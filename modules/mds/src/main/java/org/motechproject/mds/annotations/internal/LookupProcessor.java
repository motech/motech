package org.motechproject.mds.annotations.internal;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The <code>LookupProcessor</code> class is responsible for processing public methods, acting like lookups.
 * The Entity looked for, is resolved based on the return type of the lookup method. The lookup fields are determined
 * either basing on the LookupField annotations, or if no such annotation is found in method parameters, on all
 * method parameter names.
 *
 * @see org.motechproject.mds.annotations.Lookup
 * @see org.motechproject.mds.annotations.LookupField
 */
@Component
public class LookupProcessor extends AbstractProcessor {

    private EntityService entityService;
    private Paranamer paranamer;
    private static final Logger LOGGER = LoggerFactory.getLogger(LookupProcessor.class);

    public LookupProcessor() {
        this.paranamer = new BytecodeReadingParanamer();
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Lookup.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return getMethods(getAnnotation());
    }

    @Override
    protected void process(AnnotatedElement annotatedElement) {
        Method method = (Method) annotatedElement;

        Class returnType = method.getReturnType();
        String returnClassName = returnType.getName();

        boolean singleObjectReturn = true;

        //Our entity will never be an interface, therefore we can assume the return type is a collection if that's the case
        if (returnType.isArray() || returnType.isInterface()) {
            singleObjectReturn = false;
            returnClassName = determineGenericClass(method.getGenericReturnType().toString());
        }

        EntityDto entity = entityService.getEntityByClassName(returnClassName);

        if (entity == null) {
            LOGGER.error("There's no matching entity for the resolved return type of the lookup method: " +
                    method.getName() + "; Resolved return type: " + returnClassName);
            return;
        }
        LOGGER.debug("Found entity class by the return type of lookup method: " + entity.getName());

        LookupDto lookup = new LookupDto();

        lookup.setSingleObjectReturn(singleObjectReturn);
        lookup.setLookupName(generateLookupName(
                AnnotationUtils.findAnnotation(method, Lookup.class).name(), method.getName()));
        lookup.setFieldList(findLookupFields(method));

        List<LookupDto> entityLookups = entityService.getAdvancedSettings(entity.getId(), true).getIndexes();

        if (!entityLookups.contains(lookup)) {
            LOGGER.debug("Attempting to add lookup to the entity " + lookup.getLookupName());
            entityService.addLookupToEntity(entity.getId(), lookup);
        }
    }

    private String generateLookupName(String lookupDisplayName, String methodName) {
        if (!"".equals(lookupDisplayName)) {
            return lookupDisplayName;
        } else {
            String[] splitName = StringUtils.splitByCharacterTypeCamelCase(methodName);
            StringBuilder stringBuilder = new StringBuilder();
            for (String word : splitName) {
                String capitalize = StringUtils.capitalize(word);
                stringBuilder.append(capitalize).append(" ");
            }
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));
            return stringBuilder.toString();
        }
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

                    if ("".equals(fieldAnnotation.name())) {
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
        return clazz.substring(clazz.toString().indexOf("<") + 1, clazz.toString().lastIndexOf(">"));
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

}
