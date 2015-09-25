package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIFilterable;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.motechproject.mds.annotations.internal.PredicateUtil.uiFilterable;
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
    private static final Class[] SUPPORTED_CLASSES = {Date.class, DateTime.class, LocalDate.class,
                                                      Boolean.class, Collection.class, boolean.class};

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
    protected Set<? extends AnnotatedElement> getElementsToProcess() {
        List<Member> members = ReflectionsUtil.getFilteredMembers(clazz, uiFilterable());
        Set<AnnotatedElement> elements = new LinkedHashSet<>(members.size());
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
            UIFilterable annotation = ReflectionsUtil.getAnnotationSelfOrAccessor(element, UIFilterable.class);

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

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    private boolean isCorrectType(Class<?> clazz) {
        return ArrayUtils.contains(SUPPORTED_CLASSES, clazz) || clazz.isEnum();
    }

}
