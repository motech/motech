package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;
import static org.springframework.util.ReflectionUtils.FieldCallback;
import static org.springframework.util.ReflectionUtils.FieldFilter;
import static org.springframework.util.ReflectionUtils.MethodCallback;
import static org.springframework.util.ReflectionUtils.MethodFilter;

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
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final Integer FIELD_NAME_START_IDX = 3;

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
        List<AnnotatedElement> list = new ArrayList<>();

        Callback callback = new Callback(list);
        Filter filter = new Filter();

        ReflectionUtils.doWithFields(clazz, callback, filter);
        ReflectionUtils.doWithMethods(clazz, callback, filter);

        return list;
    }

    @Override
    protected void process(AnnotatedElement element) {
        AccessibleObject ac = (AccessibleObject) element;
        Class<?> classType = getCorrectType(ac);

        if (null != classType) {
            Field annotation = AnnotationUtils.getAnnotation(ac, Field.class);
            String defaultName = getFieldName(ac);

            TypeDto type = typeService.findType(classType);

            FieldBasicDto basic = new FieldBasicDto();
            basic.setDisplayName(getDisplayName(annotation, defaultName));
            basic.setName(getName(annotation, defaultName));

            if (null != annotation) {
                basic.setRequired(annotation.required());
                basic.setDefaultValue(annotation.defaultValue());
                basic.setTooltip(annotation.tooltip());
            }

            FieldDto field = new FieldDto();
            field.setEntityId(entity.getId());
            field.setType(type);
            field.setBasic(basic);

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

    private String getDisplayName(Field annotation, String defaultName) {
        return null != annotation
                ? defaultIfBlank(annotation.displayName(), defaultName)
                : defaultName;
    }

    private String getName(Field annotation, String defaultName) {
        return null != annotation
                ? defaultIfBlank(annotation.name(), defaultName)
                : defaultName;
    }

    private Class<?> getCorrectType(AnnotatedElement object) {
        Class<?> classType = null;

        if (object instanceof Method) {
            Method method = (Method) object;

            if (startsWithIgnoreCase(method.getName(), GETTER_PREFIX)) {
                classType = method.getReturnType();
            } else if (startsWithIgnoreCase(method.getName(), SETTER_PREFIX)) {
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (ArrayUtils.isNotEmpty(parameterTypes)) {
                    classType = parameterTypes[0];
                }
            }
        } else if (object instanceof java.lang.reflect.Field) {
            java.lang.reflect.Field field = (java.lang.reflect.Field) object;

            classType = field.getType();
        }

        return classType;
    }

    private String getFieldName(AnnotatedElement object) {
        String name = null;

        if (object instanceof Method) {
            Method method = (Method) object;

            if (startsWithIgnoreCase(method.getName(), GETTER_PREFIX)
                    || startsWithIgnoreCase(method.getName(), SETTER_PREFIX)) {
                name = method.getName().substring(FIELD_NAME_START_IDX);
                name = Introspector.decapitalize(name);
            }
        } else if (object instanceof java.lang.reflect.Field) {
            java.lang.reflect.Field field = (java.lang.reflect.Field) object;

            name = field.getName();
        }

        return name;
    }

    private final class Callback implements MethodCallback, FieldCallback {
        private List<AnnotatedElement> list;

        private Callback(List<AnnotatedElement> list) {
            this.list = list;
        }

        @Override
        public void doWith(Method method) {
            add(method);
        }

        @Override
        public void doWith(java.lang.reflect.Field field) {
            add(field);
        }

        private void add(AnnotatedElement candidate) {
            Iterator<AnnotatedElement> iterator = list.iterator();
            Class<Field> type = Field.class;
            boolean found = false;

            while (iterator.hasNext()) {
                AnnotatedElement element = iterator.next();
                String candidateName = getFieldName(candidate);
                String elementName = getFieldName(element);

                if (equalsIgnoreCase(candidateName, elementName)) {
                    Field candidateAnnotation = AnnotationUtils.getAnnotation(candidate, type);
                    Field elementAnnotation = AnnotationUtils.getAnnotation(element, type);

                    found = !(candidateAnnotation != null && elementAnnotation == null);

                    if (!found) {
                        iterator.remove();
                    }
                }
            }

            if (!found) {
                list.add(candidate);
            }
        }

    }

    private final class Filter implements MethodFilter, FieldFilter {

        @Override
        public boolean matches(Method method) {
            boolean isNotFromObject = method.getDeclaringClass() != Object.class;
            boolean isGetter = startsWithIgnoreCase(method.getName(), GETTER_PREFIX);
            boolean isSetter = startsWithIgnoreCase(method.getName(), SETTER_PREFIX);
            boolean hasIgnoreAnnotation = hasAnnotation(method, Ignore.class);

            return (isNotFromObject && (isGetter || isSetter)) && !hasIgnoreAnnotation;
        }

        @Override
        public boolean matches(java.lang.reflect.Field field) {
            boolean hasFieldAnnotation = hasAnnotation(field, Field.class);
            boolean hasIgnoreAnnotation = hasAnnotation(field, Ignore.class);
            boolean isPublic = Modifier.isPublic(field.getModifiers());

            return (hasFieldAnnotation || isPublic) && !hasIgnoreAnnotation;
        }

        private boolean hasAnnotation(AccessibleObject obj, Class annotationClass) {
            return AnnotationUtils.getAnnotation(obj, annotationClass) != null;
        }

    }

}
