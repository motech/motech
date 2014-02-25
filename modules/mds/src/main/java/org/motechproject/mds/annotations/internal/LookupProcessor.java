package org.motechproject.mds.annotations.internal;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.AnnotationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    protected List<? extends AnnotatedElement> getProcessElements() {
        return AnnotationsUtil.getMethods(getAnnotationType(), getBundle());
    }

    @Override
    protected void process(AnnotatedElement annotatedElement) {
        Method method = (Method) annotatedElement;

        Class returnType = method.getReturnType();
        String returnClassName = returnType.getName();

        boolean singleObjectReturn = true;

        //Our entity will never be an interface, therefore we can assume the return type is
        // a collection if that's the case
        if (returnType.isArray() || returnType.isInterface()) {
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
        Lookup annotation = AnnotationsUtil.findAnnotation(method, Lookup.class);
        String lookupName = generateLookupName(annotation.name(), method.getName());
        List<String> lookupFields = findLookupFields(method);

        LookupDto lookup = new LookupDto();
        lookup.setSingleObjectReturn(singleObjectReturn);
        lookup.setLookupName(lookupName);
        lookup.setFieldNames(lookupFields);
        lookup.setReadOnly(true);

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

    private List<String> findLookupFields(Method method) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        List<String> lookupFields = new ArrayList<>();
        List<String> methodParameterNames = new ArrayList<>();

        methodParameterNames.addAll(Arrays.asList(paranamer.lookupParameterNames(method)));

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation.annotationType().equals(LookupField.class)) {
                    LookupField fieldAnnotation = (LookupField) annotation;

                    if (StringUtils.isBlank(fieldAnnotation.name())) {
                        //no name defined in annotation - get lookup field name from parameter name
                        lookupFields.add(methodParameterNames.get(i));
                    } else {
                        //name defined in annotation - get lookup field name from annotation
                        lookupFields.add(fieldAnnotation.name());
                    }

                    break;
                }
            }
        }

        // No LookupFields annotation? Then add all the fields.
        if (lookupFields.isEmpty()) {
            lookupFields.addAll(methodParameterNames);
        }

        return lookupFields;
    }

    private String determineGenericClass(String clazz) {
        return clazz.substring(clazz.indexOf('<') + 1, clazz.lastIndexOf('>'));
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

}
