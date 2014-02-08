package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIFilterable;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.TypeNotFoundException;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.DATE;
import static org.motechproject.mds.dto.TypeDto.LIST;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;

/**
 * The <code>UIFilterableProcessor</code> provides a mechanism to finding fields or methods with
 * the {@link org.motechproject.mds.annotations.UIFilterable} annotation inside the class with the
 * {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.UIFilterable
 * @see org.motechproject.mds.annotations.Entity
 * @see org.motechproject.mds.annotations.internal.EntityProcessor
 */
@Component
class UIFilterableProcessor extends AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIFilterableProcessor.class);
    private static final TypeDto[] SUPPORT_TYPES = {BOOLEAN, DATE, LIST};

    private TypeService typeService;

    private Class clazz;

    private List<String> fields = new LinkedList<>();

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return UIFilterable.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return AnnotationsUtil.getMembers(
                getAnnotation(), clazz, new MethodPredicate(), new FieldPredicate()
        );
    }

    @Override
    protected void process(AnnotatedElement element) {
        Class<?> classType = MemberUtil.getCorrectType(element);

        if (null != classType) {
            UIFilterable annotation = AnnotationsUtil.getAnnotation(element, UIFilterable.class);

            if (null != annotation) {
                if (isCorrectType(classType)) {
                    Field fieldAnnotation = AnnotationsUtil.getAnnotation(element, Field.class);
                    String fieldName = MemberUtil.getFieldName(element);
                    String field = AnnotationsUtil.getAnnotationValue(
                            fieldAnnotation, NAME, fieldName
                    );

                    fields.add(field);
                } else {
                    LOGGER.warn("The annotation can be added only on fields of type:" +
                            "Date, Boolean or List");
                }
            }
        } else {
            LOGGER.warn("Field type is unknown in: {}", element);
        }
    }

    public List<String> getFields() {
        return fields;
    }

    @Autowired
    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    private boolean isCorrectType(Class<?> clazz) {
        try {
            TypeDto type = typeService.findType(clazz);

            return ArrayUtils.contains(SUPPORT_TYPES, type);
        } catch (TypeNotFoundException e) {
            LOGGER.error("Not found type with given name: {}", clazz.getName());
            return false;
        }
    }

}
