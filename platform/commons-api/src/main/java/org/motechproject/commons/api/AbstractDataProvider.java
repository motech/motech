package org.motechproject.commons.api;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

public abstract class AbstractDataProvider extends MotechObject implements DataProvider {
    private String body;

    public abstract List<Class<?>> getSupportClasses();

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
            logError(e.getMessage(), e);
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
}
