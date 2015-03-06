package org.motechproject.commons.api;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * Base class for every data provider.
 */
public abstract class AbstractDataProvider implements DataProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String body;

    /**
     * Returns list of classes supported by this data provider.
     *
     * @return the list of supported classes
     */
    public abstract List<Class<?>> getSupportClasses();

    /**
     * Returns root package for this data provider.
     *
     * @return root package
     */
    public abstract String getPackageRoot();

    @Override
    public String toJSON() {
        return getBody();
    }

    @Override
    public boolean supports(String type) {
        boolean support;

        try {
            support = isAssignable(getClassForType(type), getSupportClasses());
        } catch (ClassNotFoundException e) {
            logger.error("Class {} not found by provider {}", type, getName(), e);
            support = false;
        }

        return support;
    }

    protected Class<?> getClassForType(String type) throws ClassNotFoundException {
        return getClass().getClassLoader().loadClass(String.format("%s.%s", getPackageRoot(), type));
    }

    protected boolean isAssignable(Class<?> check, List<Class<?>> classes) {
        boolean r = false;

        for (Class<?> main : classes) {
            r = r || main.isAssignableFrom(check);
        }

        return r;
    }

    protected String getBody() {
        return body;
    }

    protected void setBody(String body) {
        this.body = body;
    }

    protected void setBody(final Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cant be null");
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalArgumentException("Resource not exists or can not be read");
        }

        StringWriter writer = new StringWriter();
        InputStream is = null;

        try {
            is = resource.getInputStream();

            IOUtils.copy(is, writer);
            body = writer.toString().replaceAll("(^\\s+|\\s+$)", "");
        } catch (IOException e) {
            throw new MotechException("Can't read data from resource", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
    }

    protected Logger getLogger() {
        return logger;
    }
}
