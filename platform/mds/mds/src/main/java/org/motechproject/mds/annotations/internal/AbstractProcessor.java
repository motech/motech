package org.motechproject.mds.annotations.internal;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * The <code>AbstractProcessor</code> is a base abstract class with implement the
 * {@link org.motechproject.mds.annotations.internal.Processor} interface. It provides default
 * implementation of the {@link #execute(org.osgi.framework.Bundle)} method. Also it defines
 * several new methods that have to be implemented by the inherited classes.
 *
 * @param <A> the type of related annotation.
 */
abstract class AbstractProcessor<A extends Annotation> implements Processor<A> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);

    private Bundle bundle;

    /**
     * Returns a list of element on which the actions inside the
     * {@link #process(java.lang.reflect.AnnotatedElement)} method should be maintain.
     *
     * @return a list of elements to be processed.
     */
    protected abstract List<? extends AnnotatedElement> getProcessElements();

    /**
     * Executes the specific actions on an single found element.
     *
     * @param element single element from a list from the {@link #getProcessElements()} method.
     */
    protected abstract void process(AnnotatedElement element);

    /**
     * Defines what actions should be maintain before processing each found element.
     */
    protected abstract void beforeExecution();

    /**
     * Defines what actions should be maintain after processing each found element.
     */
    protected abstract void afterExecution();

    /**
     * Executes the {@link #execute(org.osgi.framework.Bundle)} method with the {@value null}
     * parameter.
     */
    public void execute() {
        execute(null);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Developer have to specify what actions have to be done before and after processing each
     * found element.
     */
    @Override
    public void execute(Bundle bundle) {
        this.bundle = bundle;
        Class<A> annotation = getAnnotationType();
        List<? extends AnnotatedElement> elements = getProcessElements();

        beforeExecution();

        for (AnnotatedElement element : elements) {
            LOGGER.debug("Processing: Annotation: {} Object: {}", annotation.getName(), element);

            process(element);

            LOGGER.debug("Processed: Annotation: {} Object: {}", annotation.getName(), element);
        }

        afterExecution();
    }


    protected Bundle getBundle() {
        return bundle;
    }

    void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
