package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.annotations.RestOperations;
import org.motechproject.mds.domain.RestOperation;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.EntityService;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The <code>RestOperationsProcessor</code> provides a mechanism for processing
 * {@link org.motechproject.mds.annotations.RestOperations} annotation of a single
 * class with {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.RestOperations
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
public class RestOperationsProcessor implements Processor<RestOperations> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestOperationsProcessor.class);

    private EntityService entityService;

    private Class clazz;
    private EntityDto entity;

    @Override
    public Class<RestOperations> getAnnotationType() {
        return RestOperations.class;
    }

    @Override
    public void execute(Bundle bundle) {
        RestOperations annotation = ReflectionsUtil.getAnnotationClassLoaderSafe(clazz, clazz, RestOperations.class);
        RestOptionsDto restOptions = new RestOptionsDto();

        if (null != annotation) {
            RestOperation[] restOperations = annotation.value();
            if (ArrayUtils.isEmpty(restOperations)) {
                LOGGER.error("RestOperations annotation for {} is specified but its value is missing.", clazz.getName());
            } else {
                forEach:
                for (RestOperation restOperation : restOperations) {
                    switch (restOperation) {
                        case CREATE:
                            restOptions.setCreate(true);
                            break;
                        case READ:
                            restOptions.setRead(true);
                            break;
                        case UPDATE:
                            restOptions.setUpdate(true);
                            break;
                        case DELETE:
                            restOptions.setDelete(true);
                            break;
                        case ALL:
                            restOptions.setCreate(true);
                            restOptions.setRead(true);
                            restOptions.setUpdate(true);
                            restOptions.setDelete(true);
                            break forEach;
                    }
                }
            }
        }

        entityService.updateRestOptions(entity.getId(), restOptions);
    }

    @Override
    public boolean hasFound() {
        return ReflectionsUtil.hasAnnotationClassLoaderSafe(clazz, clazz, RestOperations.class);
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setEntity(EntityDto entity) {
        this.entity = entity;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }
}
