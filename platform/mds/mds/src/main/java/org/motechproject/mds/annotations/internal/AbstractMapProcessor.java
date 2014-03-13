package org.motechproject.mds.annotations.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>AbstractMapProcessor</code> provides the same methods as the
 * {@link org.motechproject.mds.annotations.internal.AbstractProcessor} class and adds new
 * methods related with operations on map.
 * <p/>
 * If a developer want to create a new MDS annotation processor and the developer know that the
 * processed data will have to be collected in map, the developer should create a new class with
 * the <code>AbstractMapProcessor</code> as the base class.
 *
 * @param <A> tpe type of related annotation.
 * @param <K> the type of keys maintained by map
 * @param <V> the type of mapped values
 */
abstract class AbstractMapProcessor<A extends Annotation, K, V>
        extends AbstractProcessor<A> {

    private Map<K, V> map;

    /**
     * Removes all found data from the map.
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

    public Map<K, V> getElements() {
        if (map == null) {
            map = new HashMap<>();
        }

        return map;
    }

    public V getElement(K key) {
        return getElements().get(key);
    }

    protected void clear() {
        getElements().clear();
    }

    protected void put(K key, V value) {
        getElements().put(key, value);
    }

}
