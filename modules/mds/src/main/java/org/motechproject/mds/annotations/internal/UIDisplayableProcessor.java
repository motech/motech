package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * The <code>UIDisplayableProcessor</code> provides a mechanism to finding fields or methods with
 * the {@link org.motechproject.mds.annotations.UIDisplayable} annotation inside the class with the
 * {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.UIDisplayable
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class UIDisplayableProcessor extends AbstractMapProcessor<UIDisplayable, String, Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIDisplayableProcessor.class);
    private static final Long DEFAULT_VALUE = -1L;

    private EntityService entityService;

    private EntityDto entity;
    private Class clazz;

    @Override
    public Class<UIDisplayable> getAnnotationType() {
        return UIDisplayable.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getProcessElements() {
        return AnnotationsUtil.getAnnotatedMembers(
                getAnnotationType(), clazz, new MethodPredicate(), new FieldPredicate(this)
        );
    }

    @Override
    protected void process(AnnotatedElement element) {
        Class<?> classType = MemberUtil.getCorrectType(element);

        if (null != classType) {
            UIDisplayable annotation = AnnotationsUtil.getAnnotation(element, UIDisplayable.class);

            if (null != annotation) {
                String fieldName = MemberUtil.getFieldName(element);
                Long position = annotation.position();

                if (DEFAULT_VALUE.equals(position)) {
                    position = (long) getElements().size();
                }

                if (getElements().containsValue(position)) {
                    LOGGER.error("The annotation has the position value which is already used");
                } else {
                    put(fieldName, position);
                }
            }
        } else {
            LOGGER.warn("Field type is unknown in: {}", element);
        }
    }

    @Override
    protected void afterExecution() {
        entityService.addDisplayedFields(entity, getElements());
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
