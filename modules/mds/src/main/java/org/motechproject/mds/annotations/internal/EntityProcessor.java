package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.service.EntityService;
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

import static org.apache.commons.lang.StringUtils.defaultIfBlank;

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

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Entity.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return getClasses(getAnnotation());
    }

    @Override
    protected void process(AnnotatedElement element) {
        Class clazz = (Class) element;
        Entity annotation = AnnotationUtils.findAnnotation(clazz, Entity.class);

        if (null != annotation) {
            String name = getName(annotation, clazz);
            String module = getModule(annotation);
            String namespace = getNamespace(annotation);

            try {
                EntityDto entity = new EntityDto(clazz.getName(), name, module, namespace);
                EntityDto db = entityService.createEntity(entity);

                findFields(clazz, db);
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

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setFieldProcessor(FieldProcessor fieldProcessor) {
        this.fieldProcessor = fieldProcessor;
    }

    private String getName(Entity annotation, Class clazz) {
        return defaultIfBlank(annotation.name(), ClassName.getSimpleName(clazz.getName()));
    }

    private String getModule(Entity annotation) {
        BundleHeaders headers = new BundleHeaders(getBundle());

        String module = defaultIfBlank(annotation.module(), headers.getName());
        module = defaultIfBlank(module, headers.getSymbolicName());
        module = defaultIfBlank(module, null);

        return module;
    }

    private String getNamespace(Entity annotation) {
        return defaultIfBlank(annotation.namespace(), null);
    }
}
