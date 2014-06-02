package org.motechproject.mds.jdo;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.store.objectvaluegenerator.ObjectValueGenerator;
import org.motechproject.mds.util.PropertyUtil;

/**
 * Base class for other generator classes. It takes value of property (see
 * {@link #getPropertName()} method) from object and modify it depending on the implementation.
 * If the modified value is null then the {@link java.lang.IllegalStateException} is thrown.
 *
 * @param <T> type of property
 */
public abstract class AbstractObjectValueGenerator<T> implements ObjectValueGenerator {

    @Override
    public Object generate(ExecutionContext ec, Object obj, ExtensionMetaData[] extensions) {
        T value = (T) PropertyUtil.safeGetProperty(obj, getPropertName());
        value = modify(value);

        if (null == value) {
            throw new IllegalStateException("The value for property [" + getPropertName() + "] should not be null");
        }

        return value;
    }

    protected abstract String getPropertName();

    /**
     * Modifies the given value. This method cannot return null value.
     *
     * @param value the given value related with property.
     * @return modified value.
     */
    protected abstract T modify(T value);
}
