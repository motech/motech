package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.ClassName;
import org.motechproject.osgi.web.BundleHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static org.motechproject.mds.util.Constants.AnnotationFields.MODULE;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAMESPACE;

/**
 * The <code>EntityProcessor</code> provides a mechanism to adding public classes from other
 * modules as entities in the MDS module. When the entity is successfully added into MDS database,
 * related processors are starting to process the class definitions in order to add other
 * information into MDS database.
 *
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class EntityProcessor extends AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityProcessor.class);

    private EntityService entityService;
    private FieldProcessor fieldProcessor;
    private UIFilterableProcessor uiFilterableProcessor;
    private UIDisplayableProcessor uiDisplayableProcessor;

    private BundleHeaders bundleHeaders;

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Entity.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return AnnotationsUtil.getClasses(getAnnotation(), getBundle());
    }

    @Override
    protected void process(AnnotatedElement element) {
        if (null == bundleHeaders) {
            bundleHeaders = new BundleHeaders(getBundle());
        }

        Class clazz = (Class) element;
        Entity annotation = AnnotationUtils.findAnnotation(clazz, Entity.class);

        if (null != annotation) {
            String name = AnnotationsUtil.getAnnotationValue(
                    annotation, NAME, ClassName.getSimpleName(clazz.getName())
            );
            String module = AnnotationsUtil.getAnnotationValue(
                    annotation, MODULE, bundleHeaders.getName(), bundleHeaders.getSymbolicName()
            );
            String namespace = AnnotationsUtil.getAnnotationValue(annotation, NAMESPACE);

            try {
                EntityDto entity = new EntityDto(clazz.getName(), name, module, namespace);
                EntityDto db = entityService.createEntity(entity);

                findFields(clazz, db);
                findFilterableFields(clazz, db);
                findDisplayedFields(clazz, db);
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

    private void findFields(Class clazz, EntityDto entity) {
        fieldProcessor.setClazz(clazz);
        fieldProcessor.setEntity(entity);
        fieldProcessor.execute();

        entityService.addFields(entity, fieldProcessor.getFields());
    }

    private void findFilterableFields(Class clazz, EntityDto entity) {
        uiFilterableProcessor.setClazz(clazz);
        uiFilterableProcessor.execute();

        entityService.addFilterableFields(entity, uiFilterableProcessor.getFields());
    }

    private void findDisplayedFields(Class clazz, EntityDto entity) {
        uiDisplayableProcessor.setClazz(clazz);
        uiDisplayableProcessor.execute();

        entityService.addDisplayedFields(entity, uiDisplayableProcessor.getPositions());
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
}
