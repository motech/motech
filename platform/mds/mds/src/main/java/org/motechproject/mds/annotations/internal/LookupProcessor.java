package org.motechproject.mds.annotations.internal;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.IllegalLookupException;
import org.motechproject.mds.ex.LookupWrongParameterTypeException;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * The <code>LookupProcessor</code> class is responsible for processing public methods, acting like
 * lookups. The Entity looked for, is resolved based on the return type of the lookup method. The
 * lookup fields are determined either basing on the LookupField annotations, or if no such
 * annotation is found in method parameters, on all method parameter names.
 *
 * @see org.motechproject.mds.annotations.Lookup
 * @see org.motechproject.mds.annotations.LookupField
 */
@Component
class LookupProcessor extends AbstractMapProcessor<Lookup, Long, List<LookupDto>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LookupProcessor.class);

    private Paranamer paranamer = new BytecodeReadingParanamer();
    private EntityService entityService;

    @Override
    public Class<Lookup> getAnnotationType() {
        return Lookup.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElementsToProcess() {
        return ReflectionsUtil.getMethods(getAnnotationType(), getBundle());
    }

    @Override
    protected void process(AnnotatedElement annotatedElement) {
        Method method = (Method) annotatedElement;

        Class returnType = method.getReturnType();
        String returnClassName = returnType.getName();

        boolean singleObjectReturn = true;

        if (returnType.isArray() || Collection.class.isAssignableFrom(returnType)) {
            singleObjectReturn = false;
            returnClassName = determineGenericClass(method.getGenericReturnType().toString());
        }

        EntityDto entity = entityService.getEntityByClassName(returnClassName);

        if (entity == null) {
            LOGGER.error("There's no matching entity for the resolved return type of the lookup" +
                    "method: {}; Resolved return type: {}", method.getName(), returnClassName);
            return;
        }

        LOGGER.debug(
                "Found entity class by the return type of lookup method: {}",
                entity.getName()
        );

        Long entityId = entity.getId();
        Lookup annotation = ReflectionsUtil.findAnnotation(method, Lookup.class);
        String lookupName = generateLookupName(annotation.name(), method.getName());
        List<LookupFieldDto> lookupFields = findLookupFields(method, entity);

        verifyLookupParameters(method, entityId, lookupName, lookupFields, method.getParameterTypes());

        LookupDto lookup = new LookupDto();
        lookup.setSingleObjectReturn(singleObjectReturn);
        lookup.setLookupName(lookupName);
        lookup.setLookupFields(lookupFields);
        lookup.setReadOnly(true);
        lookup.setMethodName(method.getName());

        if (!getElements().containsKey(entityId)) {
            put(entityId, new ArrayList<LookupDto>());
        }

        getElement(entityId).add(lookup);
    }

    @Override
    protected void afterExecution() {
        for (Map.Entry<Long, List<LookupDto>> entry : getElements().entrySet()) {
            entityService.addLookups(entry.getKey(), entry.getValue());
        }
    }

    private void verifyLookupParameters(Method method, Long entityId, String lookupName, List<LookupFieldDto> lookupFields, Class<?>[] parameterTypes) {
        List<String> parametersNames = findParametersNames(method);
        for (LookupFieldDto lookupFieldDto : lookupFields) {
            if (lookupFieldDto.getType() == LookupFieldDto.Type.VALUE) {
                FieldDto fieldDto = entityService.findEntityFieldByName(entityId, lookupFieldDto.getName());
                int position = parametersNames.indexOf(lookupFieldDto.getName());

                if (fieldDto != null && fieldDto.getType() != null) {
                    String fieldType = fieldDto.getType().getTypeClass();
                    ComboboxHolder comboboxHolder = new ComboboxHolder(fieldDto);

                    // check for combobox with user supplied option enabled and String as parameter type
                    if (comboboxHolder.isAllowUserSupplied()) {
                        continue;
                    }

                    if (!parameterTypes[position].getName().equals(fieldType)) {
                        StringBuilder sb = new StringBuilder("Wrong type of argument ");
                        sb.append(position).append(" \"").append(parametersNames.get(position));
                        sb.append("\" in lookup \"").append(lookupName);
                        sb.append("\" - should be ").append(fieldType);
                        sb.append(" but is ").append(parameterTypes[position].getName());
                        throw new LookupWrongParameterTypeException(sb.toString());
                    }
                }
            }
        }
    }

    private List<String> findParametersNames(Method method) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        List<String> methodParameterNames = new ArrayList<>();

        try {
            methodParameterNames.addAll(Arrays.asList(paranamer.lookupParameterNames(method)));
        } catch (Exception e) {
            LOGGER.warn("Unable to read method {} names using paranamer", method.toString());
            LOGGER.debug("Paranamer stacktrace", e);
        }

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation.annotationType().equals(LookupField.class)) {
                    LookupField fieldAnnotation = (LookupField) annotation;
                    String name = isBlank(fieldAnnotation.name())
                            ? methodParameterNames.get(i)
                            : fieldAnnotation.name();
                    if (i >= methodParameterNames.size()) {
                        methodParameterNames.add(name);
                    } else {
                        methodParameterNames.set(i, name);
                    }
                }
            }
        }

        return methodParameterNames;
    }

    private String generateLookupName(String lookupDisplayName, String methodName) {
        String lookupName;

        if (StringUtils.isNotBlank(lookupDisplayName)) {
            lookupName = lookupDisplayName;
        } else {
            String[] splitName = StringUtils.splitByCharacterTypeCamelCase(methodName);
            StringBuilder stringBuilder = new StringBuilder();
            String prefix = "";

            for (String word : splitName) {
                String capitalize = StringUtils.capitalize(word);
                stringBuilder.append(prefix).append(capitalize);

                if (StringUtils.isEmpty(prefix)) {
                    prefix = " ";
                }
            }

            lookupName = stringBuilder.toString();
        }

        return lookupName;
    }

    private List<LookupFieldDto> findLookupFields(Method method, EntityDto entity) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        List<LookupFieldDto> lookupFields = new ArrayList<>();
        List<String> methodParameterNames = new ArrayList<>();
        List<Class<?>> methodParameterTypes = new ArrayList<>();

        methodParameterTypes.addAll(Arrays.asList(method.getParameterTypes()));

        try {
            methodParameterNames.addAll(Arrays.asList(paranamer.lookupParameterNames(method)));
        } catch (Exception e) {
            LOGGER.warn("Unable to read method {} names using paranamer", method.toString());
            LOGGER.debug("Paranamer stacktrace", e);
        }

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation.annotationType().equals(LookupField.class)) {
                    LookupField fieldAnnotation = (LookupField) annotation;

                    Class<?> methodParameterType = methodParameterTypes.get(i);

                    //no name defined in annotation - get lookup field name from parameter name
                    //name defined in annotation - get lookup field name from annotation
                    String name = isBlank(fieldAnnotation.name())
                            ? methodParameterNames.get(i)
                            : fieldAnnotation.name();

                    LookupFieldDto.Type type = determineLookupType(methodParameterType);

                    LookupFieldDto lookupField = new LookupFieldDto(null, name, type);

                    setCustomOperator(fieldAnnotation, lookupField);
                    setUseGenericParam(entity, methodParameterType, lookupField);

                    lookupFields.add(lookupField);

                    break;
                }
            }
        }

        // No LookupFields annotation? Then add all the fields.
        if (lookupFields.isEmpty()) {
            for (int i = 0; i < methodParameterNames.size(); i++) {
                String name = methodParameterNames.get(i);
                Class<?> type = methodParameterTypes.get(i);

                lookupFields.add(new LookupFieldDto(null, name, determineLookupType(type)));
            }
        }

        return lookupFields;
    }

    private void setUseGenericParam(EntityDto entity, Class<?> methodParameterType, LookupFieldDto lookupField) {
        FieldDto field = entityService.findEntityFieldByName(entity.getId(), lookupField.getName());
        TypeDto fieldType = field.getType();

        if (fieldType.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);
            boolean isCollection = holder.isEnumList() || holder.isStringList();
            boolean isCollectionParam = Collection.class.isAssignableFrom(methodParameterType);

            lookupField.setUseGenericParam(isCollection && !isCollectionParam);
        }
    }

    private void setCustomOperator(LookupField fieldAnnotation, LookupFieldDto lookupField) {
        if (StringUtils.isNotBlank(fieldAnnotation.customOperator())) {
            if (lookupField.getType() != LookupFieldDto.Type.VALUE) {
                String msg = String.format(
                        "Custom operator found on lookup field %s. Custom operators are not supported"
                                + " for %s lookups", fieldAnnotation.name(), lookupField.getType());

                throw new IllegalLookupException(msg);
            }

            lookupField.setCustomOperator(fieldAnnotation.customOperator());
        }
    }

    private String determineGenericClass(String clazz) {
        return clazz.substring(clazz.indexOf('<') + 1, clazz.lastIndexOf('>'));
    }

    private LookupFieldDto.Type determineLookupType(Class<?> methodParameterClass) {
        if (Range.class.isAssignableFrom(methodParameterClass)) {
            return LookupFieldDto.Type.RANGE;
        } else if (Set.class.isAssignableFrom(methodParameterClass)) {
            return LookupFieldDto.Type.SET;
        } else {
            return LookupFieldDto.Type.VALUE;
        }
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

}
