package org.motechproject.mds.annotations.internal;


import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.exception.entity.EntityDoesNotExtendEntityException;
import org.motechproject.mds.exception.entity.EntityDoesNotExtendMDSEntityException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.motechproject.mds.util.Constants.AnnotationFields.HISTORY;
import static org.motechproject.mds.util.Constants.AnnotationFields.NON_EDITABLE;

/**
 * The <code>EntityExtensionProcessor</code> provides a mechanism, allowing extending existing Entities.
 *
 * @see org.motechproject.mds.annotations.EntityExtension
 */
@Component
public class EntityExtensionProcessor extends EntityAnnotationProcessor<EntityExtension> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityExtensionProcessor.class);

    private List<MDSProcessorOutput> entitiesProcessingResult;
    private EntityProcessorOutput extendedEntityProcessorOutput;
    private Set<Bundle> affectedBundles = new TreeSet<>();
    private FieldProcessor fieldProcessor;


    private boolean isClassValid(AnnotatedElement element) {

        Class clazz = (Class) element;
        Class<EntityExtension> extAnn = ReflectionsUtil.getAnnotationClass(clazz, EntityExtension.class);
        Annotation annotationExtension = AnnotationUtils.findAnnotation(clazz, extAnn);


        if (clazz.getSuperclass().getClass().getName().equals(Object.class.getName())) {
            LOGGER.error("class {} annotated by EntityExtension does not extend any class", clazz.getName());
            throw new EntityDoesNotExtendEntityException(clazz);
        }

        if (annotationExtension == null) {
            LOGGER.debug("did not found Entity or EntityExtension annotations in class: {}", clazz.getName());
            return false;
        }

        for (MDSProcessorOutput processorOutput : entitiesProcessingResult) {
            if (processorOutput.getEntityProcessorOutputByClassName(clazz.getSuperclass().getName()) != null) {
                extendedEntityProcessorOutput = processorOutput.getEntityProcessorOutputByClassName(clazz.getSuperclass().getName());
                affectedBundles.add(processorOutput.getBundle());
                return true;
            }
        }

        throw new EntityDoesNotExtendMDSEntityException(clazz.getSuperclass());
    }

    @Override
    protected void process(AnnotatedElement element) {
        EntityDto extendedEntity;
        Collection<FieldDto> existingFields;
        Collection<FieldDto> newFields;
        Class clazz = (Class) element;
        Class<Entity> ann = ReflectionsUtil.getAnnotationClass(clazz, Entity.class);
        Annotation annotation = AnnotationUtils.findAnnotation(clazz, ann);

        if (isClassValid(element)) {

            boolean recordHistory = Boolean.parseBoolean(ReflectionsUtil.getAnnotationValue(annotation, HISTORY));
            boolean nonEditable = Boolean.parseBoolean(ReflectionsUtil.getAnnotationValue(annotation, NON_EDITABLE));

            RestOptionsDto restOptions;
            TrackingDto tracking;

                LOGGER.debug("DDE for {} exists, updating if necessary", clazz.getName());

                extendedEntity = extendedEntityProcessorOutput.getEntityProcessingResult();
                restOptions = extendedEntityProcessorOutput.getRestProcessingResult();
                tracking = extendedEntityProcessorOutput.getTrackingProcessingResult();
                existingFields = extendedEntityProcessorOutput.getFieldProcessingResult();


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


            for (MDSProcessorOutput processingOutput: entitiesProcessingResult) {
                for (EntityProcessorOutput entityProcessorOutput : processingOutput.getEntityProcessorOutputs()) {
                    if (entityProcessorOutput.getEntityProcessingResult().getClassName().equals(clazz.getSuperclass().getName())) {
                        processingOutput.getEntityProcessorOutputs().remove(entityProcessorOutput);
                        processingOutput.getEntityProcessorOutputs().add(extendedEntityProcessorOutput);
                    }
                }
            }

            MotechClassPool.registerDDE(clazz.getName());
        }
    }

    public Set<Bundle> getAffectedBundles(){
        return affectedBundles;
    }

    public void setEntitiesProcessingResult(List<MDSProcessorOutput> entitiesProcessingResult) {
        this.entitiesProcessingResult = entitiesProcessingResult;
    }

    @Override
    protected void afterExecution() {
        LOGGER.debug("Execution complete for bundle {}", getBundle().getSymbolicName());
    }

    private Collection<FieldDto> findFields(Class clazz, EntityDto entity, Collection<FieldDto> existingFields) {
        fieldProcessor.setClazz(clazz);
        fieldProcessor.setEntity(entity);
        fieldProcessor.setExistingFields(existingFields);
        fieldProcessor.execute(getBundle(), getSchemaHolder());
        return fieldProcessor.getProcessingResult();
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

}
