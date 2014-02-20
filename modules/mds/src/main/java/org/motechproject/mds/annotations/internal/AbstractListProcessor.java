package org.motechproject.mds.annotations.internal;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;

/**
 * The <code>AbstractListProcessor</code> provides the same methods as the
 * {@link org.motechproject.mds.annotations.internal.AbstractProcessor} class and adds new
 * methods related with operations on collection (not map).
 * <p/>
 * If a developer want to create a new MDS annotation processor and the developer know that the
 * processed data will have to be collected in collection (not in map), the developer should
 * create a new class with the <code>AbstractListProcessor</code> as the base class.
 *
 * @param <A> type of related annotation.
 * @param <E> type of element in collection.
 */
abstract class AbstractListProcessor<A extends Annotation, E> extends AbstractProcessor<A> {

    private Collection<E> collection;

    /**
     * Removes all found data from the collection.
     */
    @Override
    protected void beforeExecution() {
        clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasFound() {
        return !getElements().isEmpty();
    }

    public Collection<E> getElements() {
        if (collection == null) {
            collection = new HashSet<>();
        }

        return collection;
    }

    protected void clear() {
        getElements().clear();
    }

    protected void add(E value) {
        getElements().add(value);
    }


}
