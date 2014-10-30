package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.EntityService;
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
import java.util.List;

import static org.motechproject.mds.util.Constants.AnnotationFields.MODULE;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAMESPACE;
import static org.motechproject.mds.util.Constants.AnnotationFields.HISTORY;

/**
 * The <code>EntityProcessor</code> provides a mechanism to adding public classes from other
 * modules as entities in the MDS module. When the entity is successfully added into MDS database,
 * related processors are starting to process the class definitions in order to add other
 * information into MDS database.
 *
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class EntityProcessor extends AbstractListProcessor<Entity, EntityDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityProcessor.class);

    private EntityService entityService;
    private FieldProcessor fieldProcessor;
    private UIFilterableProcessor uiFilterableProcessor;
    private UIDisplayableProcessor uiDisplayableProcessor;
    private RestIgnoreProcessor restIgnoreProcessor;
    private RestOperationsProcessor restOperationsProcessor;

    @Override
    public Class<Entity> getAnnotationType() {
        return Entity.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElementsToProcess() {
        return ReflectionsUtil.getClasses(getAnnotationType(), getBundle());
    }

    @Override
    protected void process(AnnotatedElement element) {
        BundleHeaders bundleHeaders = new BundleHeaders(getBundle());

        Class clazz = (Class) element;
        Class<Entity> ann = ReflectionsUtil.getAnnotationClass(clazz, Entity.class);
        Annotation annotation = AnnotationUtils.findAnnotation(clazz, ann);

        if (null != annotation) {
            String className = clazz.getName();

            String name = ReflectionsUtil.getAnnotationValue(
                    annotation, NAME, ClassName.getSimpleName(className)
            );
            String module = ReflectionsUtil.getAnnotationValue(
                    annotation, MODULE, bundleHeaders.getName(), bundleHeaders.getSymbolicName()
            );
            String namespace = ReflectionsUtil.getAnnotationValue(annotation, NAMESPACE);

            boolean recordHistory = Boolean.parseBoolean(ReflectionsUtil.getAnnotationValue(annotation, HISTORY));

            try {
                EntityDto entity = entityService.getEntityByClassName(className);

                if (entity == null) {
                    LOGGER.debug("Creating DDE for {}", className);

                    EntityDto entityDto = new EntityDto(
                            null, className, name, module, namespace, recordHistory,
                            SecurityMode.EVERYONE, null, clazz.getSuperclass().getName(),
                            Modifier.isAbstract(clazz.getModifiers())
                    );
                    entity = entityService.createEntity(entityDto);
                } else {
                    LOGGER.debug("DDE for {} already exists, updating if necessary", className);
                }

                processRestOperations(clazz, entity);

                findFields(clazz, entity);
                findFilterableFields(clazz, entity);
                findDisplayedFields(clazz, entity);
                findRestFields(clazz, entity);

                add(entity);

                MotechClassPool.registerDDE(entity.getClassName());
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to create an entity for class {} from bundle {}",
                        clazz.getName(), getBundle().getSymbolicName()
                );
                LOGGER.error("because of: ", e);
            }
        } else {
            LOGGER.debug("Did not find Entity annotation in class: {}", clazz.getName());
        }
    }

    @Override
    protected void afterExecution() {
        LOGGER.debug("Execution complete for bundle {}", getBundle().getSymbolicName());
    }

    private void processRestOperations(Class clazz, EntityDto entity) {
        restOperationsProcessor.setClazz(clazz);
        restOperationsProcessor.setEntity(entity);
        restOperationsProcessor.execute(getBundle());
    }

    private void findFields(Class clazz, EntityDto entity) {
        fieldProcessor.setClazz(clazz);
        fieldProcessor.setEntity(entity);
        fieldProcessor.execute(getBundle());
    }

    private void findFilterableFields(Class clazz, EntityDto entity) {
        uiFilterableProcessor.setClazz(clazz);
        uiFilterableProcessor.setEntity(entity);
        uiFilterableProcessor.execute(getBundle());
    }

    private void findDisplayedFields(Class clazz, EntityDto entity) {
        uiDisplayableProcessor.setClazz(clazz);
        uiDisplayableProcessor.setEntity(entity);
        uiDisplayableProcessor.execute(getBundle());
    }

    private void findRestFields(Class clazz, EntityDto entity) {
        restIgnoreProcessor.setClazz(clazz);
        restIgnoreProcessor.setEntity(entity);
        restIgnoreProcessor.execute(getBundle());
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
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
}
