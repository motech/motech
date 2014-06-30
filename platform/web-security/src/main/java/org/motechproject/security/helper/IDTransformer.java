package org.motechproject.security.helper;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public final class IDTransformer implements Transformer {
    public static final IDTransformer INSTANCE = new IDTransformer();

    private static final Logger LOGGER = LoggerFactory.getLogger(IDTransformer.class);
    private static final String ID = "id";

    private IDTransformer() {
    }

    @Override
    public Object transform(Object input) {
        Object value = -1;

        try {
            if (null != input && PropertyUtils.isReadable(input, ID)) {
                value = PropertyUtils.getProperty(input, ID);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOGGER.error("There was a problem with get id value from bean: {}", input);
            LOGGER.error("Because of: ", e);
        }

        return value;
    }

}
