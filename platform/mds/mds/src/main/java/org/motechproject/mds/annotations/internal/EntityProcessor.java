package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.EntityExtension;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.motechproject.mds.util.Constants.AnnotationFields.HISTORY;
import static org.motechproject.mds.util.Constants.AnnotationFields.MODULE;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAMESPACE;
import static org.motechproject.mds.util.Constants.AnnotationFields.NON_EDITABLE;
import static org.motechproject.mds.util.Constants.AnnotationFields.TABLE_NAME;

/**
 * The <code>EntityProcessor</code> provides a mechanism, allowing adding public classes from other
 * modules as entities in the MDS module. When the entity is successfully added into MDS database,
 * related processors are starting to process the class definitions in order to add other
 * information into the MDS database.
 *
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class EntityProcessor extends EntityAnnotationProcessor<Entity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityProcessor.class);

    private List<EntityProcessorOutput> processingResult;
    private FieldProcessor fieldProcessor;

    @Override
    public Class<Entity> getAnnotationType() {
        return Entity.class;
    }

    @Override
    public List<EntityProcessorOutput> getProcessingResult() {
        return processingResult;
    }

    @Override
    protected Set<? extends AnnotatedElement> getElementsToProcess() {
        return ReflectionsUtil.getClasses(getAnnotationType(), getBundle());
    }

    private boolean isClassValid(AnnotatedElement element)  {
        Class clazz = (Class) element;
        Class<Entity> ann = ReflectionsUtil.getAnnotationClass(clazz, Entity.class);
        Class<EntityExtension> extAnn = ReflectionsUtil.getAnnotationClass(clazz, EntityExtension.class);
        Annotation annotationExtension = AnnotationUtils.findAnnotation(clazz, extAnn);
        Annotation annotation = AnnotationUtils.findAnnotation(clazz, ann);

        if (annotation != null && annotationExtension == null) {
            return true;
        }
        return false;
    }

    @Override
    protected void process(AnnotatedElement element) {
        BundleHeaders bundleHeaders = new BundleHeaders(getBundle());

        EntityProcessorOutput entityProcessorOutput = new EntityProcessorOutput();

        Class clazz = (Class) element;
        Class<Entity> ann = ReflectionsUtil.getAnnotationClass(clazz, Entity.class);

        Annotation annotation = AnnotationUtils.findAnnotation(clazz, ann);

        if (isClassValid(element)) {
            String className = clazz.getName();

            String name = ReflectionsUtil.getAnnotationValue(
                    annotation, NAME, ClassName.getSimpleName(className)
            );
            String module = ReflectionsUtil.getAnnotationValue(
                    annotation, MODULE, bundleHeaders.getName(), bundleHeaders.getSymbolicName()
            );
            String bundleSymbolicName = getBundle().getSymbolicName();
            String namespace = ReflectionsUtil.getAnnotationValue(annotation, NAMESPACE);
            String tableName = ReflectionsUtil.getAnnotationValue(annotation, TABLE_NAME);

            boolean recordHistory = Boolean.parseBoolean(ReflectionsUtil.getAnnotationValue(annotation, HISTORY));
            boolean nonEditable = Boolean.parseBoolean(ReflectionsUtil.getAnnotationValue(annotation, NON_EDITABLE));

            EntityDto entity = getSchemaHolder().getEntityByClassName(className);
            RestOptionsDto restOptions = new RestOptionsDto();
            TrackingDto tracking = new TrackingDto();

            if (entity == null) {
                LOGGER.debug("Creating DDE for {}", className);

                entity = new EntityDto(
                        null, className, name, module, namespace, tableName, recordHistory,
                        SecurityMode.EVERYONE, null, null, null, clazz.getSuperclass().getName(),
                        Modifier.isAbstract(clazz.getModifiers()), false, bundleSymbolicName
                );
            } else {
                LOGGER.debug("DDE for {} already exists, updating if necessary", className);

                AdvancedSettingsDto advancedSettings = getSchemaHolder().getAdvancedSettings(className);
                restOptions = advancedSettings.getRestOptions();
                tracking = advancedSettings.getTracking();
                entity.setBundleSymbolicName(bundleSymbolicName);
                entity.setModule(module);

                if(!className.equals(entity.getClassName())) {
                    entity.setClassName(className);
                }

            }


            if (!tracking.isModifiedByUser()) {
                tracking.setRecordHistory(recordHistory);
                tracking.setNonEditable(nonEditable);
            }

            setSecurityOptions(element, entity);

            // per entity maxFetchDepth that will be passed to the Persistence Manager
            setMaxFetchDepth(entity, annotation);

            entityProcessorOutput.setEntityProcessingResult(entity);

            Collection<FieldDto> fields = findFields(clazz, entity);

            String versionField = getVersionFieldName(clazz);
            addVersionMetadata(fields, versionField);
            addDefaultFields(entity, fields);

            restOptions = processRestOperations(clazz, restOptions);
            restOptions = findRestFields(clazz, restOptions, fields);

            updateUiChangedFields(fields, className);
            updateResults(entityProcessorOutput, clazz, fields, restOptions, tracking, versionField);

            add(entity);
            processingResult.add(entityProcessorOutput);
            MotechClassPool.registerDDE(entity.getClassName());
        } else {
                LOGGER.debug("Did not find Entity annotation or found EntityExtension annotation in class: {}", clazz.getName());
        }
    }

    @Override
    protected void beforeExecution() {
        processingResult = new ArrayList<>();
        super.beforeExecution();
    }

    @Override
    protected void afterExecution() {
        LOGGER.debug("Execution complete for bundle {}", getBundle().getSymbolicName());
    }

    private Collection<FieldDto> findFields(Class clazz, EntityDto entity) {
        fieldProcessor.setClazz(clazz);
        fieldProcessor.setEntity(entity);
        fieldProcessor.execute(getBundle(), getSchemaHolder());
        return fieldProcessor.getProcessingResult();
    }

    @Autowired
    public void setFieldProcessor(FieldProcessor fieldProcessor) {
        this.fieldProcessor = fieldProcessor;
    }
}
