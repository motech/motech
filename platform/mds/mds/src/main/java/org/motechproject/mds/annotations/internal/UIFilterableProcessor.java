package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIFilterable;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.type.TypeNotFoundException;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.annotations.internal.PredicateUtil.uiFilterable;
import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.DATE;
import static org.motechproject.mds.dto.TypeDto.DATETIME;
import static org.motechproject.mds.dto.TypeDto.LIST;
import static org.motechproject.mds.dto.TypeDto.LOCAL_DATE;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;

/**
 * The <code>UIFilterableProcessor</code> provides a mechanism to finding fields or methods with
 * the {@link org.motechproject.mds.annotations.UIFilterable} annotation inside the class with the
 * {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.UIFilterable
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class UIFilterableProcessor extends AbstractListProcessor<UIFilterable, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIFilterableProcessor.class);
    private static final TypeDto[] SUPPORT_TYPES = {BOOLEAN, DATE, DATETIME, LOCAL_DATE, LIST};

    private TypeService typeService;
    private Class clazz;

    @Override
    public Class<UIFilterable> getAnnotationType() {
        return UIFilterable.class;
    }

    @Override
    public Collection<String> getProcessingResult() {
        return getElements();
    }

    @Override
    protected List<? extends AnnotatedElement> getElementsToProcess() {
        List<Member> members = ReflectionsUtil.getFilteredMembers(clazz, uiFilterable());
        List<AnnotatedElement> elements = new ArrayList<>(members.size());
        for (Member member : members) {
            if (member instanceof AnnotatedElement) {
                elements.add((AnnotatedElement) member);
            }
        }
        return elements;
    }

    @Override
    protected void process(AnnotatedElement element) {
        Class<?> classType = MemberUtil.getCorrectType(element);

        if (null != classType) {
            UIFilterable annotation = ReflectionsUtil.getAnnotationClassLoaderSafe(element, classType, UIFilterable.class);

            if (null != annotation) {
                if (isCorrectType(classType)) {
                    Field fieldAnnotation = ReflectionsUtil.getAnnotationClassLoaderSafe(element, classType, Field.class);
                    String fieldName = MemberUtil.getFieldName(element);
                    String field = ReflectionsUtil.getAnnotationValue(
                            fieldAnnotation, NAME, fieldName
                    );

                    add(field);
                } else {
                    LOGGER.warn("@UIFilterable found on field of type {}, filters on this type are not supported",
                            classType.getName());
                }
            }
        } else {
            LOGGER.warn("Field type is unknown in: {}", element);
        }
    }

    @Override
    protected void afterExecution() {
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
