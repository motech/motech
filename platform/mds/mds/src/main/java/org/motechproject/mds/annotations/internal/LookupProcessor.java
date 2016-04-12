package org.motechproject.mds.annotations.internal;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.annotations.RestExposed;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.lookup.IllegalLookupException;
import org.motechproject.mds.exception.lookup.LookupWrongParameterTypeException;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.LookupName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * The <code>LookupProcessor</code> class is responsible for processing public methods, acting as
 * lookups. The Entity looked for, is resolved based on the return type of the lookup method. The
 * lookup fields are determined either basing on the LookupField annotations, or if no such
 * annotation is found in method parameters, on all method parameter names.
 *
 * @see org.motechproject.mds.annotations.Lookup
 * @see org.motechproject.mds.annotations.LookupField
 */
@Component
class LookupProcessor extends AbstractMapProcessor<Lookup, String, List<LookupDto>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LookupProcessor.class);

    private Paranamer paranamer = new BytecodeReadingParanamer();
    private List<EntityProcessorOutput> entityProcessorOutputs;

    @Override
    public Class<Lookup> getAnnotationType() {
        return Lookup.class;
    }

    @Override
    public Map<String, List<LookupDto>> getProcessingResult() {
        return getElements();
    }

    @Override
    protected Set<? extends AnnotatedElement> getElementsToProcess() {
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

        EntityDto entity = findEntityByClassName(returnClassName);

        if (entity == null) {
            LOGGER.error("There's no matching entity for the resolved return type of the lookup" +
                    "method: {}; Resolved return type: {}", method.getName(), returnClassName);
            return;
        }

        LOGGER.debug(
                "Found entity class by the return type of lookup method: {}",
                entity.getName()
        );

        Lookup annotation = ReflectionsUtil.findAnnotation(method, Lookup.class);
        String lookupName = generateLookupName(annotation.name(), method.getName());
        List<LookupFieldDto> lookupFields = findLookupFields(method, entity);
        boolean restExposed = processRestExposed(method);
        boolean indexRequired = annotation.indexRequired();

        verifyLookupParameters(method, returnClassName, lookupName, lookupFields, method.getParameterTypes());

        LookupDto lookup = new LookupDto();
        lookup.setSingleObjectReturn(singleObjectReturn);
        lookup.setLookupName(lookupName);
        lookup.setLookupFields(lookupFields);
        lookup.setReadOnly(true);
        lookup.setMethodName(method.getName());
        lookup.setIndexRequired(indexRequired);

        if (!restOptionsModifiedByUser(entity)) {
            lookup.setExposedViaRest(restExposed);
        }

        if (!getElements().containsKey(returnClassName)) {
            put(returnClassName, new ArrayList<>());
        }

        getElement(returnClassName).add(lookup);
    }

    private EntityDto findEntityByClassName(String returnClassName) {
        for (EntityProcessorOutput entityProcessorOutput : entityProcessorOutputs) {
            if (entityProcessorOutput.getEntityProcessingResult().getClassName().equals(returnClassName)) {
                return entityProcessorOutput.getEntityProcessingResult();
            }
        }

        return null;
    }

    private boolean processRestExposed(Method method) {
        return ReflectionsUtil.hasAnnotationClassLoaderSafe(method, method.getDeclaringClass(), RestExposed.class);
    }

    @Override
    protected void afterExecution() {
    }

    private void verifyLookupParameters(Method method, String entityClassName, String lookupName, List<LookupFieldDto> lookupFields, Class<?>[] parameterTypes) {
        List<String> parametersNames = findParametersNames(method);
        for (LookupFieldDto lookupFieldDto : lookupFields) {
            if (lookupFieldDto.getType() == LookupFieldType.VALUE) {
                FieldDto fieldDto = findEntityFieldByName(entityClassName, lookupFieldDto.getLookupFieldName());
                int position = parametersNames.indexOf(lookupFieldDto.getLookupFieldName());

                if (fieldDto != null && fieldDto.getType() != null) {
                    TypeDto type = fieldDto.getType();

                    // check if field is a Combobox or a TextArea
                    if (type.isCombobox() || (type.isTextArea() && "java.lang.String".equals(parameterTypes[position].getName()))) {
                        continue;
                    }

                    if (!parameterTypes[position].getName().equals(type.getTypeClass())) {
                        StringBuilder sb = new StringBuilder("Wrong type of argument ");
                        sb.append(position).append(" \"").append(parametersNames.get(position));
                        sb.append("\" in lookup \"").append(lookupName);
                        sb.append("\" - should be ").append(type.getTypeClass());
                        sb.append(" but is ").append(parameterTypes[position].getName());
                        throw new LookupWrongParameterTypeException(sb.toString());
                    }
                }
            }
        }
    }

    private FieldDto findEntityFieldByName(String className, String name) {
        for (EntityProcessorOutput entityProcessorOutput : entityProcessorOutputs) {
            if (entityProcessorOutput.getEntityProcessingResult().getClassName().equals(className)) {
                for (FieldDto field : entityProcessorOutput.getFieldProcessingResult()) {
                    if (field.getBasic().getName().equals(name)) {
                        return field;
                    }
                }
            }
        }

        return null;
    }

    private List<String> findParametersNames(Method method) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        List<String> methodParameterNames = new ArrayList<>();

        try {
            methodParameterNames.addAll(Arrays.asList(paranamer.lookupParameterNames(method)));
        } catch (RuntimeException e) {
            logParanamerError(method.toString(), e);
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
        } catch (RuntimeException e) {
            logParanamerError(method.toString(), e);
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

                    LookupFieldType type = determineLookupType(methodParameterType);

                    LookupFieldDto lookupField = new LookupFieldDto(null, LookupName.getFieldName(name), type);
                    lookupField.setRelatedName(LookupName.getRelatedFieldName(name));
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
        FieldDto field = findEntityFieldByName(entity.getClassName(), lookupField.getName());
        TypeDto fieldType = field.getType();
        EntityDto relatedEntity = null;
        if (fieldType.isRelationship()) {
            relatedEntity = findEntityByClassName(field.getMetadata(Constants.MetadataKeys.RELATED_CLASS).getValue());
            field = findEntityFieldByName(field.getMetadata(Constants.MetadataKeys.RELATED_CLASS).getValue(), lookupField.getRelatedName());
        }

        if (fieldType.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(relatedEntity == null ? entity : relatedEntity, field);
            boolean isCollection = holder.isCollection();
            boolean isCollectionParam = Collection.class.isAssignableFrom(methodParameterType);

            lookupField.setUseGenericParam(isCollection && !isCollectionParam);
        }
    }

    private void setCustomOperator(LookupField fieldAnnotation, LookupFieldDto lookupField) {
        if (StringUtils.isNotBlank(fieldAnnotation.customOperator())) {
            if (lookupField.getType() != LookupFieldType.VALUE) {
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

    private LookupFieldType determineLookupType(Class<?> methodParameterClass) {
        if (Range.class.isAssignableFrom(methodParameterClass)) {
            return LookupFieldType.RANGE;
        } else if (Set.class.isAssignableFrom(methodParameterClass)) {
            return LookupFieldType.SET;
        } else {
            return LookupFieldType.VALUE;
        }
    }

    private boolean restOptionsModifiedByUser(EntityDto entity) {
        AdvancedSettingsDto advancedSettings = getSchemaHolder().getAdvancedSettings(entity.getClassName());
        if (advancedSettings == null) {
            return false;
        } else {
            RestOptionsDto restOptionsDto = advancedSettings.getRestOptions();
            return restOptionsDto.isModifiedByUser();
        }
    }

    private void logParanamerError(String methodName, Exception exception) {
        if (LOGGER.isTraceEnabled()) {
            // Print with stacktrace
            LOGGER.trace("Unable to read method {} parameter names using Paranamer", methodName, exception);
        } else {
            LOGGER.debug("Unable to read method {} parameter names using Paranamer", methodName);
        }
    }

    public void setEntityProcessingResult(List<EntityProcessorOutput> entityProcessorOutput) {
        this.entityProcessorOutputs = entityProcessorOutput;
    }
}
