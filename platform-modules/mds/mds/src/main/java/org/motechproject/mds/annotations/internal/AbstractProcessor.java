package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.dto.SchemaHolder;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

/**
 * The <code>AbstractProcessor</code> is a base abstract class, that implements the
 * {@link org.motechproject.mds.annotations.internal.Processor} interface. It provides default
 * implementation of the {@link #execute(org.osgi.framework.Bundle, SchemaHolder)} method. It also defines
 * several new methods that have to be implemented by the inherited classes.
 *
 * @param <A> the type of related annotation.
 */
abstract class AbstractProcessor<A extends Annotation> implements Processor<A> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);

    private Bundle bundle;
    private SchemaHolder schemaHolder;

    /**
     * Returns a list of element on which the actions inside the
     * {@link #process(java.lang.reflect.AnnotatedElement)} method should be maintain.
     *
     * @return a list of elements to be processed.
     */
    protected abstract Set<? extends AnnotatedElement> getElementsToProcess();

    /**
     * Executes the specific actions on an single found element.
     *
     * @param element single element from a list from the {@link #getElementsToProcess()} method.
     */
    protected abstract void process(AnnotatedElement element);

    /**
     * Defines what actions should be taken, before processing each found element.
     */
    protected abstract void beforeExecution();

    /**
     * Defines what actions should be taken, after processing each found element.
     */
    protected abstract void afterExecution();

    /**
     * {@inheritDoc}
     * <p/>
     * Developer have to specify what actions have to be done before and after processing each
     * found element.
     */
    @Override
    public void execute(Bundle bundle, SchemaHolder schemaHolder) {
        this.bundle = bundle;
        this.schemaHolder = schemaHolder;

        Class<A> annotation = getAnnotationType();
        Set<? extends AnnotatedElement> elements = getElementsToProcess();

        beforeExecution();

        for (AnnotatedElement element : elements) {
            LOGGER.debug("Processing: Annotation: {} Object: {}", annotation.getName(), element);

            try {
                process(element);
            } catch (RuntimeException e) {
                LOGGER.error("An error occurred while processing annotated element in Bundle: {}, in Element: {} because of:",
                        bundle.getSymbolicName(), element, e);
            }

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

    protected SchemaHolder getSchemaHolder() {
        return schemaHolder;
    }

    void setSchemaHolder(SchemaHolder schemaHolder) {
        this.schemaHolder = schemaHolder;
    }
}
