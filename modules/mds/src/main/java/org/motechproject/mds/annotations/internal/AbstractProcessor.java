package org.motechproject.mds.annotations.internal;

import org.apache.commons.collections.Predicate;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.MemberUtil;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

/**
 * The <code>AbstractProcessor</code> is a base class for every processor that would like to perform
 * some actions on objects with the given annotation. The name of a processor should start with
 * the name of an annotation. For example if there is an annotation 'A', the processor name should
 * be equal to 'AProcessor' and the {@link #getAnnotation()} method should return class definition
 * of the 'A' annotation.
 */
abstract class AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);

    private Bundle bundle;

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract List<? extends AnnotatedElement> getElements();

    protected abstract void process(AnnotatedElement element);

    public void execute() {
        execute(null);
    }

    public boolean execute(Bundle bundle) {
        this.bundle = bundle;
        Class<? extends Annotation> annotation = getAnnotation();

        List<? extends AnnotatedElement> annotatedElements = getElements();

        for (AnnotatedElement element : annotatedElements) {
            LOGGER.debug("Processing: Annotation: {} Object: {}", annotation.getName(), element);

            process(element);

            LOGGER.debug("Processed: Annotation: {} Object: {}", annotation.getName(), element);
        }

        return !annotatedElements.isEmpty();
    }

    protected Bundle getBundle() {
        return bundle;
    }

    void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    protected final class MethodPredicate extends GenericPrecidate<Method> {

        protected MethodPredicate() {
            super(Method.class);
        }

        @Override
        protected boolean match(Method object) {
            boolean isNotFromObject = object.getDeclaringClass() != Object.class;
            boolean isGetter = startsWithIgnoreCase(object.getName(), MemberUtil.GETTER_PREFIX);
            boolean isSetter = startsWithIgnoreCase(object.getName(), MemberUtil.SETTER_PREFIX);
            boolean hasIgnoreAnnotation = AnnotationsUtil.hasAnnotation(object, Ignore.class);

            return (isNotFromObject && (isGetter || isSetter)) && !hasIgnoreAnnotation;
        }
    }

    protected final class FieldPredicate extends GenericPrecidate<java.lang.reflect.Field> {

        protected FieldPredicate() {
            super(java.lang.reflect.Field.class);
        }

        @Override
        public boolean match(java.lang.reflect.Field object) {
            boolean hasAnnotation = AnnotationsUtil.hasAnnotation(object, getAnnotation());
            boolean hasIgnoreAnnotation = AnnotationsUtil.hasAnnotation(object, Ignore.class);
            boolean isPublic = Modifier.isPublic(object.getModifiers());

            return (hasAnnotation || isPublic) && !hasIgnoreAnnotation;
        }
    }

    protected abstract class GenericPrecidate<T> implements Predicate {
        private Class<T> clazz;

        protected GenericPrecidate(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean evaluate(Object object) {
            return clazz.isInstance(object) && match(clazz.cast(object));
        }

        protected abstract boolean match(T object);
    }
}
