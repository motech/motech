package org.motechproject.mds.annotations.internal;


import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.annotations.*;
import org.motechproject.mds.domain.MdsEntity;
import org.motechproject.mds.domain.MdsVersionedEntity;
import org.motechproject.mds.dto.*;
import org.motechproject.mds.exception.entity.EntityDoesNotExtendEntityException;
import org.motechproject.mds.exception.entity.EntityDoesNotExtendMDSEntityException;
import org.motechproject.mds.helper.EntityDefaultFieldsHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.SecurityMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.jdo.annotations.Version;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.motechproject.mds.util.Constants.AnnotationFields.MAX_FETCH_DEPTH;
import static org.motechproject.mds.util.Constants.AnnotationFields.HISTORY;
import static org.motechproject.mds.util.Constants.AnnotationFields.NON_EDITABLE;


@Component
public class EntityExtensionProcessor extends AbstractListProcessor<EntityExtension, EntityDto>{
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityExtensionProcessor.class);

    private FieldProcessor fieldProcessor;
    private UIFilterableProcessor uiFilterableProcessor;
    private UIDisplayableProcessor uiDisplayableProcessor;
    private RestIgnoreProcessor restIgnoreProcessor;
    private RestOperationsProcessor restOperationsProcessor;
    private CrudEventsProcessor crudEventsProcessor;
    private NonEditableProcessor nonEditableProcessor;
    private List<MDSProcessorOutput> entityProcessingResult;
    private EntityProcessorOutput extendedEntityProcessorOutput;

    private boolean incompleteEntity;

    private boolean isClassValid(AnnotatedElement element) {

        Class clazz = (Class) element;
        Class<EntityExtension> extAnn = ReflectionsUtil.getAnnotationClass(clazz, EntityExtension.class);
        Annotation annotationExtension = AnnotationUtils.findAnnotation(clazz, extAnn);

        boolean found = false;

        if(clazz.getSuperclass().getClass().getName().equals(Object.class.getName())) {
            LOGGER.error("class {} annotated by EntityExtension does not extend any class", clazz.getName());
            throw new EntityDoesNotExtendEntityException(clazz);
        }

        if(annotationExtension == null) {
            LOGGER.debug("did not found Entity or EntityExtension annotations in class: {}", clazz.getName());
            return false;
        }

        for(MDSProcessorOutput processorOutput : entityProcessingResult) {
            if(processorOutput.getEntityProcessorOutputByClassName(clazz.getSuperclass().getName()) != null){
                extendedEntityProcessorOutput = processorOutput.getEntityProcessorOutputByClassName(clazz.getSuperclass().getName());
                return true;
            }
        }

        throw new EntityDoesNotExtendMDSEntityException(clazz.getSuperclass());
    }

    @Override
    protected Set<? extends AnnotatedElement> getElementsToProcess() {
        return ReflectionsUtil.getClasses(getAnnotationType(), getBundle());
    }

    @Override
    protected void process(AnnotatedElement element) {
        EntityDto extendedEntity;
        Collection<FieldDto> existingFields;
        Collection<FieldDto> newFields;
        Class clazz = (Class) element;
        Class<EntityExtension> extAnn = ReflectionsUtil.getAnnotationClass(clazz, EntityExtension.class);
        Class<Entity> ann = ReflectionsUtil.getAnnotationClass(clazz, Entity.class);
        Annotation annotationExtension = AnnotationUtils.findAnnotation(clazz, extAnn);
        Annotation annotation = AnnotationUtils.findAnnotation(clazz, ann);

        if(isClassValid(element)) {

            boolean recordHistory = Boolean.parseBoolean(ReflectionsUtil.getAnnotationValue(annotation, HISTORY));
            boolean nonEditable = Boolean.parseBoolean(ReflectionsUtil.getAnnotationValue(annotation, NON_EDITABLE));
            String className = clazz.getName();
            String bundleSymbolicName = getBundle().getSymbolicName();

            RestOptionsDto restOptions;
            TrackingDto tracking;

                LOGGER.debug("DDE for {} exists, updating if necessary", clazz.getName());

                extendedEntity = extendedEntityProcessorOutput.getEntityProcessingResult();
                restOptions = extendedEntityProcessorOutput.getRestProcessingResult();
                tracking = extendedEntityProcessorOutput.getTrackingProcessingResult();
                existingFields = extendedEntityProcessorOutput.getFieldProcessingResult();
            //}


            if (!tracking.isModifiedByUser()) {
                tracking.setRecordHistory(recordHistory);
                tracking.setNonEditable(nonEditable);
            }

            setSecurityOptions(element, extendedEntity);

            // per entity maxFetchDepth that will be passed to the Persistence Manager
            setMaxFetchDepth(extendedEntity, annotation);

            extendedEntityProcessorOutput.setEntityProcessingResult(extendedEntity);
            newFields = findFields(clazz, extendedEntity, existingFields);

            String versionField = getVersionFieldName(clazz);
            addVersionMetadata(newFields, versionField);
            addDefaultFields(extendedEntity, newFields);

            restOptions = processRestOperations(clazz, restOptions);
            restOptions = findRestFields(clazz, restOptions, newFields);

            updateUiChangedFields(newFields, clazz.getName());
            updateResults(extendedEntityProcessorOutput, clazz, newFields, restOptions, tracking, versionField);

            add(extendedEntity);


            for(MDSProcessorOutput processingOutput: entityProcessingResult) {
                for(EntityProcessorOutput entityProcessorOutput : processingOutput.getEntityProcessorOutputs()) {
                    if(entityProcessorOutput.getEntityProcessingResult().getClassName().equals(clazz.getSuperclass().getName())) {
                        processingOutput.getEntityProcessorOutputs().remove(entityProcessorOutput);
                        processingOutput.getEntityProcessorOutputs().add(extendedEntityProcessorOutput);
                    }
                }
            }

            MotechClassPool.registerDDE(clazz.getName());
        }
    }

    public void setEntityProcessingResult(List<MDSProcessorOutput> entityProcessingResult) {
        this.entityProcessingResult = entityProcessingResult;
    }

    private void addVersionMetadata(Collection<FieldDto> fields, String versionField) {
        if (StringUtils.isNotBlank(versionField)) {
            for (FieldDto fieldDto : fields) {
                if (fieldDto.getBasic().getName().equals(versionField)) {
                    fieldDto.addMetadata(new MetadataDto(Constants.MetadataKeys.VERSION_FIELD, Constants.Util.TRUE));
                }
            }
        }
    }

    private String getVersionFieldName(Class clazz) {
        if (MdsVersionedEntity.class.getName().equalsIgnoreCase(clazz.getSuperclass().getName())) {
            return "instanceVersion";
        }

        Class<Version> verAnn = ReflectionsUtil.getAnnotationClass(clazz, Version.class);
        Version versionAnnotation = AnnotationUtils.findAnnotation(clazz, verAnn);
        if (versionAnnotation != null && versionAnnotation.extensions().length !=0 && versionAnnotation.extensions()[0].key().equals("field-name")) {
            return versionAnnotation.extensions()[0].value();
        }

        return null;
    }

    private void setMaxFetchDepth(EntityDto entity, Annotation annotation) {
        int maxFetchDepth = Integer.parseInt(ReflectionsUtil.getAnnotationValue(annotation, MAX_FETCH_DEPTH));
        if (maxFetchDepth != Constants.FetchDepth.MDS_DEFAULT) {
            entity.setMaxFetchDepth(maxFetchDepth);
        }
    }

    private void addDefaultFields(EntityDto entity, Collection<FieldDto> fields) {
        if (!MdsEntity.class.getName().equalsIgnoreCase(entity.getSuperClass()) &&
                !MdsVersionedEntity.class.getName().equalsIgnoreCase(entity.getSuperClass())) {
            fields.addAll(EntityDefaultFieldsHelper.defaultFields(getSchemaHolder()));
        }
    }

    private void updateResults(EntityProcessorOutput entityProcessorOutput, Class<?> clazz, Collection<FieldDto> fields,
                               RestOptionsDto restOptions, TrackingDto tracking, String versionField) {
        entityProcessorOutput.setFieldProcessingResult(fields);

        entityProcessorOutput.setUiFilterableProcessingResult(findFilterableFields(clazz));
        entityProcessorOutput.setUiDisplayableProcessingResult(findDisplayedFields(clazz));
        entityProcessorOutput.setTrackingProcessingResult(processCrudEvents(clazz, tracking));

        entityProcessorOutput.setRestProcessingResult(restOptions);

        Map<String, Boolean> nonEditableFields = findNonEditableFields(clazz);
        //we must set non editable for version field
        if (StringUtils.isNotBlank(versionField)) {
            nonEditableFields.put(versionField, true);
        }
        entityProcessorOutput.setNonEditableProcessingResult(nonEditableFields);
    }

    private void updateUiChangedFields(Collection<FieldDto> fieldsToUpdate, String entityClassName) {
        if (getSchemaHolder().getEntityByClassName(entityClassName) != null) {
            List<FieldDto> currentFields = getSchemaHolder().getFields(entityClassName);
            for (FieldDto field : fieldsToUpdate) {
                FieldDto currentField = getCurrentField(currentFields, field.getBasic().getName());
                if (currentField != null && currentField.isUiChanged()) {
                    field.setUiFilterable(currentField.isUiFilterable());
                    field.setUiChanged(currentField.isUiChanged());
                }
            }
        }
    }

    private FieldDto getCurrentField(List<FieldDto> currentFields, String fieldName) {
        for (FieldDto field : currentFields) {
            if (field.getBasic().getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    @Override
    protected void afterExecution() {
        LOGGER.debug("Execution complete for bundle {}", getBundle().getSymbolicName());
    }

    private RestOptionsDto processRestOperations(Class clazz, RestOptionsDto restOptions) {
        restOperationsProcessor.setClazz(clazz);
        restOperationsProcessor.setRestOptions(restOptions);
        restOperationsProcessor.execute(getBundle(), getSchemaHolder());
        return restOperationsProcessor.getProcessingResult();
    }

    private TrackingDto processCrudEvents(Class clazz, TrackingDto tracking) {
        crudEventsProcessor.setClazz(clazz);
        crudEventsProcessor.setTrackingDto(tracking);
        crudEventsProcessor.execute(getBundle(), getSchemaHolder());
        return crudEventsProcessor.getProcessingResult();
    }

    private Collection<String> findFilterableFields(Class clazz) {
        uiFilterableProcessor.setClazz(clazz);
        uiFilterableProcessor.execute(getBundle(), getSchemaHolder());
        return uiFilterableProcessor.getProcessingResult();
    }

    private Collection<FieldDto> findFields(Class clazz, EntityDto entity, Collection<FieldDto> existingFields) {
        fieldProcessor.setClazz(clazz);
        fieldProcessor.setEntity(entity);
        fieldProcessor.setExistingFields(existingFields);
        fieldProcessor.execute(getBundle(), getSchemaHolder());
        return fieldProcessor.getProcessingResult();
    }

    private Map<String, Long> findDisplayedFields(Class clazz) {
        uiDisplayableProcessor.setClazz(clazz);
        uiDisplayableProcessor.execute(getBundle(), getSchemaHolder());
        return uiDisplayableProcessor.getProcessingResult();
    }

    private RestOptionsDto findRestFields(Class clazz, RestOptionsDto restOptions, Collection<FieldDto> fields) {
        restIgnoreProcessor.setClazz(clazz);
        restIgnoreProcessor.setRestOptions(restOptions);
        restIgnoreProcessor.setFields(new ArrayList<>(fields));
        restIgnoreProcessor.execute(getBundle(), getSchemaHolder());
        return restIgnoreProcessor.getProcessingResult();
    }

    private Map<String, Boolean> findNonEditableFields(Class clazz) {
        nonEditableProcessor.setClazz(clazz);
        nonEditableProcessor.execute(getBundle(), getSchemaHolder());
        return nonEditableProcessor.getProcessingResult();
    }

    private void setSecurityOptions(AnnotatedElement element, EntityDto entity) {
        Access access = element.getAnnotation(Access.class);
        ReadAccess readAccess = element.getAnnotation(ReadAccess.class);
        if (null != access && !entity.isSecurityOptionsModified()) {
            Set<String> securityMembers = returnSecurityMembersForSecurityMode(access.value(), access.members(), "Access");
            entity.setSecurityMode(access.value());
            entity.setSecurityMembers(securityMembers);
            if(null == readAccess) {
                entity.setReadOnlySecurityMode(SecurityMode.NO_ACCESS);
            }
        }

        if(null != readAccess && !entity.isSecurityOptionsModified()) {
            Set<String> readOnlySecurityMembers = returnSecurityMembersForSecurityMode(readAccess.value(), readAccess.members(), "ReadAccess");
            entity.setReadOnlySecurityMode(readAccess.value());
            if(entity.getSecurityMode() == SecurityMode.EVERYONE) {
                entity.setSecurityMode(SecurityMode.NO_ACCESS);
            }
            entity.setReadOnlySecurityMembers(readOnlySecurityMembers);
        }
    }

    private Set<String> returnSecurityMembersForSecurityMode(SecurityMode securityMode, String[] securityMembersArray, String annotationName) {
        Boolean hasMembers = securityMembersArray != null && securityMembersArray.length > 0;
        Set<String> securityMembers;
        if (securityMode == SecurityMode.USERS || securityMode == SecurityMode.PERMISSIONS) {
            if (hasMembers) {
                securityMembers = new HashSet<String>(Arrays.asList(securityMembersArray));
            } else {
                throw new IllegalArgumentException(
                        "Failed to process " + annotationName+ " annotation: the security mode is set to "
                                + securityMode + " but there are no members specified."
                );
            }
        } else {
            securityMembers = new HashSet<String>();
            if (hasMembers) {
                throw new IllegalArgumentException(
                        "Failed to process " + annotationName + " annotation: the members attribute can be only used with USERS or PERMISSIONS security mode."
                );
            }
        }
        return securityMembers;
    }

    @Override
    public Class<EntityExtension> getAnnotationType() {
        return EntityExtension.class;
    }

    @Override
    public Object getProcessingResult() {
        return null;
    }


    @Autowired
    public void setFieldProcessor(FieldProcessor fieldProcessor) {
        this.fieldProcessor = fieldProcessor;
    }

    @Autowired
    public void setUIFilterableProcessor(UIFilterableProcessor uiFilterableProcessor) {
        this.uiFilterableProcessor = uiFilterableProcessor;
    }

    @Autowired
    public void setUIDisplayableProcessor(UIDisplayableProcessor uiDisplayableProcessor) {
        this.uiDisplayableProcessor = uiDisplayableProcessor;
    }

    @Autowired
    public void setRestIgnoreProcessor(RestIgnoreProcessor restIgnoreProcessor) {
        this.restIgnoreProcessor = restIgnoreProcessor;
    }

    @Autowired
    public void setRestOperationsProcessor(RestOperationsProcessor restOperationsProcessor) {
        this.restOperationsProcessor = restOperationsProcessor;
    }

    @Autowired
    public void setCrudEventsProcessor(CrudEventsProcessor crudEventsProcessor) {
        this.crudEventsProcessor = crudEventsProcessor;
    }

    @Autowired
    public void setNonEditableProcessor(NonEditableProcessor nonEditableProcessor) {
        this.nonEditableProcessor = nonEditableProcessor;
    }
}