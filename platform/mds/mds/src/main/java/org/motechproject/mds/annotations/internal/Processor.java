package org.motechproject.mds.annotations.internal;

import org.osgi.framework.Bundle;

import java.lang.annotation.Annotation;

/**
 * The <code>Processor</code> is an base interface for every processor that would like to perform
 * some actions on objects with the given annotation. The name of a processor should start with
 * the name of an annotation. For example if there is an annotation 'A', the processor name should
 * be equal to 'AProcessor' and the {@link #getAnnotationType()} method should return class
 * definition of the 'A' annotation.
 *
 * @param <A> the type of related annotation.
 */
public interface Processor<A extends Annotation> {

    /**
     * Returns the annotation type of the given annotation.
     *
     * @return annotation class.
     */
    Class<A> getAnnotationType();

    /**
     * Executes the specific actions on data with the given annotation.
     *
     * @param bundle instance of bundle that represent the given module.
     */
    void execute(Bundle bundle);

    /**
     * Checks if the processor found some data.
     *
     * @return true if the processor found data; otherwise false.
     */
    boolean hasFound();

}
