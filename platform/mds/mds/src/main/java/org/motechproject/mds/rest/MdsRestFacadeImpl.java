package org.motechproject.mds.rest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.DtoHelper;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.ex.rest.RestBadBodyFormatException;
import org.motechproject.mds.ex.rest.RestEntityNotFoundException;
import org.motechproject.mds.ex.rest.RestInternalException;
import org.motechproject.mds.ex.rest.RestLookupExecutionForbbidenException;
import org.motechproject.mds.ex.rest.RestLookupNotFoundException;
import org.motechproject.mds.ex.rest.RestNoLookupResultException;
import org.motechproject.mds.ex.rest.RestOperationNotSupportedException;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.BlobDeserializer;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This {@link org.motechproject.mds.rest.MdsRestFacade} implementation
 * retrieves REST related metadata on initialization. It uses an instance of
 * {@link org.motechproject.mds.service.MotechDataService} for operations and
 * the jackson JSON library for parsing InputStreams.
 * @param <T>
 */
public class MdsRestFacadeImpl<T> implements MdsRestFacade<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MotechDataService<T> dataService;
    private AllEntities allEntities;
    private Class<T> entityClass;

    private Map<String, LookupExecutor> lookupExecutors = new HashMap<>();
    private Set<String> forbiddenLookupMethodNames = new HashSet<>();

    private List<String> restFields;
    private List<String> blobFields;

    private RestOptionsDto restOptions;

    static {
        SimpleModule module = new SimpleModule("Deserializers", Version.unknownVersion());
        module.addDeserializer(Byte[].class, new BlobDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    @PostConstruct
    public void init() {
        entityClass = dataService.getClassType();
        Entity entity = allEntities.retrieveByClassName(entityClass.getName());

        readRestOptions(entity);

        Map<String, FieldDto> fieldMap = DtoHelper.asFieldMapByName(entity.getFieldDtos());

        readLookups(entity);
        readFieldsExposedByRest(fieldMap);
        readBlobFieldsExposedByRest(fieldMap);
    }

    @Override
    public List<RestProjection> get(QueryParams queryParams, boolean includeBlob) {
        if (!restOptions.isRead()) {
            throw operationNotSupportedEx("READ");
        }
        List<T> values = dataService.retrieveAll(queryParams);

        if (includeBlob) {
            for (T value : values) {
                getBlobs(value);
            }
        }

        return RestProjection.createProjectionCollection(values, restFields, blobFields);
    }

    @Override
    public RestProjection get(Long id, boolean includeBlob) {
        if (!restOptions.isRead()) {
            throw operationNotSupportedEx("READ");
        }
        T value = dataService.findById(id);

        if (includeBlob) {
            getBlobs(value);
        }

        if(value != null) {
            return RestProjection.createProjection(value, restFields, blobFields);
        } else {
            throw new RestEntityNotFoundException("id", id.toString());
        }
    }

    @Override
    public RestProjection create(InputStream instanceBody) {
        if (!restOptions.isCreate()) {
            throw operationNotSupportedEx("CREATE");
        }

        try {
            T instance = OBJECT_MAPPER.readValue(instanceBody, entityClass);

            T filteredInstance = entityClass.newInstance();
            PropertyUtil.copyProperties(filteredInstance, instance, null, new HashSet<>(restFields));

            return RestProjection.createProjection(dataService.create(filteredInstance), restFields, blobFields);
        } catch (IOException e) {
            throw badBodyFormatException(e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RestInternalException("Unable to create a new instance of " + entityClass.getName(), e);
        }
    }

    @Override
    public RestProjection update(InputStream instanceBody) {
        if (!restOptions.isUpdate()) {
            throw operationNotSupportedEx("UPDATE");
        }

        try {
            T instance = OBJECT_MAPPER.readValue(instanceBody, entityClass);

            T result = dataService.updateFromTransient(instance, fieldsToUpdate());

            return RestProjection.createProjection(result, restFields, blobFields);
        } catch (IOException e) {
            throw badBodyFormatException(e);
        }
    }

    @Override
    public void delete(Long id) {
        if (!restOptions.isDelete()) {
            throw operationNotSupportedEx("DELETE");
        }

        dataService.deleteById(id);
    }

    @Override
    public Object executeLookup(String lookupName, Map<String, String> lookupMap, QueryParams queryParams, boolean includeBlob) {
        if (lookupExecutors.containsKey(lookupName)) {
            LookupExecutor executor = lookupExecutors.get(lookupName);
            Object result = executor.execute(lookupMap, queryParams);
            if (result instanceof Collection) {
                if (includeBlob) {
                    for (T value : ((Collection<T>) result)) {
                        getBlobs(value);
                    }
                }
                return RestProjection.createProjectionCollection((Collection) result, restFields, blobFields);
            } else {
                if (result == null) {
                    throw new RestNoLookupResultException("No result for lookup:" + lookupName);
                }
                if (includeBlob) {
                    getBlobs((T) result);
                }
                return RestProjection.createProjection(result, restFields, blobFields);
            }
        } else if (forbiddenLookupMethodNames.contains(lookupName)) {
            throw new RestLookupExecutionForbbidenException(lookupName);
        } else {
            throw new RestLookupNotFoundException(lookupName);
        }
    }

    private void getBlobs(T value) {
        for (String field : blobFields) {
            PropertyUtil.safeSetProperty(value, field, dataService.getDetachedField(value, field));
        }
    }

    private RestOperationNotSupportedException operationNotSupportedEx(String operation) {
        return new RestOperationNotSupportedException(String.format("%s operation not supported for entity: %s",
                operation, entityClass));
    }

    private RestBadBodyFormatException badBodyFormatException(IOException ioException) {
        return new RestBadBodyFormatException("Unable to parse provided body to "
                + dataService.getClassType().getName(), ioException);
    }

    public void setDataService(MotechDataService<T> dataService) {
        this.dataService = dataService;
    }

    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    private void readFieldsExposedByRest(Map<String, FieldDto> fieldMap) {
        restFields = new ArrayList<>(restOptions.getFieldNames().size());
        for (String restFieldName : restOptions.getFieldNames()) {
            FieldDto field = fieldMap.get(restFieldName);
            if (null != field) {
                restFields.add(field.getBasic().getName());
            }
        }
    }

    private void readLookups(Entity entity) {
        for (LookupDto lookup : entity.getLookupDtos()) {
            Map<String, FieldDto> fieldMap = getLookupFieldsMapping(entity, lookup);
            String lookuMethodpName = lookup.getMethodName();
            if (lookup.isExposedViaRest()) {
                // we create executors for exposed lookups
                LookupExecutor executor = new LookupExecutor(dataService, lookup, fieldMap);
                lookupExecutors.put(lookuMethodpName, executor);
            } else {
                // we keep a list of forbidden lookups in order to print the appropriate error
                forbiddenLookupMethodNames.add(lookuMethodpName);
            }
        }
    }

    private Map<String, FieldDto> getLookupFieldsMapping(Entity entity, LookupDto lookup) {
        Map<String, FieldDto> fieldMap = new HashMap<>();
        for (LookupFieldDto lookupField : lookup.getLookupFields()) {
            Field field;
            if (StringUtils.isNotBlank(lookupField.getRelatedName())) {
                Entity relatedEntity = allEntities.retrieveByClassName(new RelationshipHolder(entity.getField(lookupField.getName())).getRelatedClass());
                field = relatedEntity.getField(lookupField.getRelatedName());
            } else {
                field = entity.getField(lookupField.getName());
            }
            fieldMap.put(lookupField.getLookupFieldName(), field.toDto());
        }
        return fieldMap;

    }

    private void readRestOptions(Entity entity) {
        RestOptions restOptsFromDb = entity.getRestOptions();

        if (restOptsFromDb == null) {
            restOptions = new RestOptionsDto();
        } else {
            restOptions = restOptsFromDb.toDto();
        }
    }

    private void readBlobFieldsExposedByRest(Map<String, FieldDto> fieldMap) {
        blobFields = new ArrayList<>(restOptions.getFieldNames().size());
        for (String restFieldName : restOptions.getFieldNames()) {
            FieldDto field = fieldMap.get(restFieldName);
            if (null != field && field.getType().isBlob()) {
                blobFields.add(field.getBasic().getName());
            }
        }
    }

    private Set<String> fieldsToUpdate() {
        // we don't want to be updating auto generated fields
        Set<String> fields = new HashSet<>(restFields);
        fields.removeAll(Arrays.asList(Constants.Util.GENERATED_FIELD_NAMES));
        return fields;
    }
}
