package org.motechproject.mds.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.DtoHelper;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.ex.rest.RestBadBodyFormatException;
import org.motechproject.mds.ex.rest.RestLookupExecutionForbbidenException;
import org.motechproject.mds.ex.rest.RestLookupNotFoundException;
import org.motechproject.mds.ex.rest.RestOperationNotSupportedException;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
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
    private Set<String> forbiddenLookupNames = new HashSet<>();

    private RestOptionsDto restOptions;

    @PostConstruct
    public void init() {
        entityClass = dataService.getClassType();
        Entity entity = allEntities.retrieveByClassName(entityClass.getName());

        RestOptions restOptsFromDb = entity.getRestOptions();

        if (restOptsFromDb == null) {
            restOptions = new RestOptionsDto();
        } else {
            restOptions = restOptsFromDb.toDto();
        }

        Map<Long, FieldDto> fieldMap = DtoHelper.asFieldMapById(entity.getFieldDtos());
        for (LookupDto lookup : entity.getLookupDtos()) {
            String lookupName = lookup.getLookupName();
            if (lookup.isExposedViaRest()) {
                // we create executors for exposed lookups
                LookupExecutor executor = new LookupExecutor(dataService, lookup, fieldMap);
                lookupExecutors.put(lookupName, executor);
            } else {
                // we keep a list of forbidden lookups in order to print the appropriate error
                forbiddenLookupNames.add(lookupName);
            }
        }
    }

    @Override
    public List<T> get(QueryParams queryParams) {
        if (!restOptions.isRead()) {
            throw operationNotSupportedEx("READ");
        }
        return dataService.retrieveAll(queryParams);
    }

    @Override
    public T get(Long id) {
        if (!restOptions.isRead()) {
            throw operationNotSupportedEx("READ");
        }
        return dataService.findById(id);
    }

    @Override
    public T create(InputStream instanceBody) {
        if (!restOptions.isCreate()) {
            throw operationNotSupportedEx("CREATE");
        }

        try {
            T instance = OBJECT_MAPPER.readValue(instanceBody, entityClass);
            return dataService.create(instance);
        } catch (IOException e) {
            throw badBodyFormatException(e);
        }
    }

    @Override
    public T update(InputStream instanceBody) {
        if (!restOptions.isUpdate()) {
            throw operationNotSupportedEx("UPDATE");
        }

        try {
            T instance = OBJECT_MAPPER.readValue(instanceBody, entityClass);
            return dataService.updateFromTransient(instance);
        } catch (IOException e) {
            throw badBodyFormatException(e);
        }
    }

    @Override
    public void delete(Long id) {
        if (!restOptions.isDelete()) {
            throw operationNotSupportedEx("DELETE");
        }

        dataService.delete("id", id);
    }

    @Override
    public Object executeLookup(String lookupName, Map<String, String> lookupMap, QueryParams queryParams) {
        if (lookupExecutors.containsKey(lookupName)) {
            LookupExecutor executor = lookupExecutors.get(lookupName);
            return executor.execute(lookupMap, queryParams);
        } else if (forbiddenLookupNames.contains(lookupName)) {
            throw new RestLookupExecutionForbbidenException(lookupName);
        } else {
            throw new RestLookupNotFoundException(lookupName);
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
}
