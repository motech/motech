package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.motechproject.mds.annotations.internal.PredicateUtil.nonEditable;

/**
 * The <code>NonEditableProcessor</code> provides a mechanism to finding fields or methods with
 * the {@link org.motechproject.mds.annotations.NonEditable} annotation inside the class with the
 * {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.NonEditable
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class NonEditableProcessor extends AbstractMapProcessor<NonEditable, String, Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NonEditableProcessor.class);

    private Class clazz;

    @Override
    public Class<NonEditable> getAnnotationType() {
        return NonEditable.class;
    }

    @Override
    public Map<String, Boolean> getProcessingResult() {
        return getElements();
    }

    @Override
    protected void process(AnnotatedElement element) {
        Class<?> classType = MemberUtil.getCorrectType(element);

        if (null != classType) {
            NonEditable annotation = ReflectionsUtil.getAnnotationClassLoaderSafe(element, classType, NonEditable.class);

            if (null != annotation) {
                String fieldName = MemberUtil.getFieldName(element);
                put(fieldName, annotation.display());
            }
        } else {
            LOGGER.warn("Field type is unknown in: {}", element);
        }
    }

    @Override
    protected List<? extends AnnotatedElement> getElementsToProcess() {
        List<Member> members = ReflectionsUtil.getFilteredMembers(clazz, nonEditable());
        List<AnnotatedElement> elements = new ArrayList<>(members.size());
        for (Member member : members) {
            if (member instanceof AnnotatedElement) {
                elements.add((AnnotatedElement) member);
            }
        }
        return elements;
    }

    @Override
    protected void afterExecution() {
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

}
