package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.FieldValidation;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.mds.util.Constants.AnnotationFields.DISPLAY_NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.MAX;
import static org.motechproject.mds.util.Constants.AnnotationFields.MIN;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.REGEXP;
import static org.motechproject.mds.util.Constants.AnnotationFields.VALUE;

/**
 * The <code>FieldProcessor</code> provides a mechanism to finding fields or methods with the
 * {@link org.motechproject.mds.annotations.Field} annotation inside the class with the
 * {@link org.motechproject.mds.annotations.Entity} annotation.
 * <p/>
 * By default all public fields (the field is public if it has public modifier or single methods
 * called 'getter and 'setter') will be added in the MDS definition of the entity. The field type
 * will be mapped on the appropriate type in the MDS system. If the appropriate mapping does not
 * exist an {@link org.motechproject.mds.ex.TypeNotFoundException} exception will be raised.
 * <p/>
 * Fields or acceptable methods with the {@link org.motechproject.mds.annotations.Ignore}
 * annotation are ignored by the processor and they are not added into entity definition.
 *
 * @see org.motechproject.mds.annotations.Field
 * @see org.motechproject.mds.annotations.Entity
 * @see org.motechproject.mds.annotations.Ignore
 * @see org.motechproject.mds.annotations.internal.EntityProcessor
 */
@Component
class FieldProcessor extends AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldProcessor.class);

    private TypeService typeService;

    private EntityDto entity;
    private Class clazz;

    private List<FieldDto> fields = new LinkedList<>();

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Field.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return AnnotationsUtil.getMembers(
                getAnnotation(), clazz, new MethodPredicate(), new FieldPredicate()
        );
    }

    @Override
    protected void process(AnnotatedElement element) {
        AccessibleObject ac = (AccessibleObject) element;
        Class<?> classType = MemberUtil.getCorrectType(ac);

        if (null != classType) {
            Field annotation = AnnotationUtils.getAnnotation(ac, Field.class);
            String defaultName = MemberUtil.getFieldName(ac);

            TypeDto type = typeService.findType(classType);

            FieldBasicDto basic = new FieldBasicDto();
            basic.setDisplayName(AnnotationsUtil.getAnnotationValue(
                    annotation, DISPLAY_NAME, defaultName)
            );
            basic.setName(AnnotationsUtil.getAnnotationValue(
                    annotation, NAME, defaultName)
            );

            if (null != annotation) {
                basic.setRequired(annotation.required());
                basic.setDefaultValue(annotation.defaultValue());
                basic.setTooltip(annotation.tooltip());
            }

            FieldDto field = new FieldDto();
            field.setEntityId(entity.getId());
            field.setType(type);
            field.setBasic(basic);
            field.setValidation(createValidation(ac, type));

            fields.add(field);
        } else {
            LOGGER.warn("Field type is unknown in: {}", ac);
        }
    }

    @Autowired
    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    public void setEntity(EntityDto entity) {
        this.entity = entity;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public List<FieldDto> getFields() {
        return fields;
    }

    private FieldValidationDto createValidation(AccessibleObject ac, TypeDto type) {
        FieldValidationDto validationDto = null;

        for (Annotation annotation : ac.getAnnotations()) {
            List<TypeValidation> validations = typeService.findValidations(
                    type, annotation.annotationType()
            );

            if (null != validations) {
                for (TypeValidation v : validations) {
                    // we don't need information about field in here
                    // the field validation value will be set below
                    FieldValidation fieldValidation = new FieldValidation(null, v, null, true);
                    assignValidationValue(fieldValidation, annotation);

                    if (null == validationDto) {
                        validationDto = new FieldValidationDto();
                    }

                    validationDto.addCriterion(fieldValidation.toDto());
                }
            }
        }

        return validationDto;
    }

    private void assignValidationValue(FieldValidation validation, Annotation annotation) {
        if (AnnotationsUtil.hasProperty(annotation, VALUE)) {
            String value = AnnotationsUtil.getAnnotationValue(annotation, VALUE);
            validation.setValue(value);
        } else {
            if (annotation instanceof Pattern) {
                String regexp = AnnotationsUtil.getAnnotationValue(annotation, REGEXP);
                validation.setValue(regexp);
            } else if (annotation instanceof Size) {
                switch (validation.getDetails().getDisplayName()) {
                    case "mds.field.validation.minLength":
                        String min = AnnotationsUtil.getAnnotationValue(annotation, MIN);
                        validation.setValue(min);
                        break;
                    case "mds.field.validation.maxLength":
                        String max = AnnotationsUtil.getAnnotationValue(annotation, MAX);
                        validation.setValue(max);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "The @Size annotation can be used only on fields with String type."
                        );
                }
            }
        }
    }

}
