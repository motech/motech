package org.motechproject.mds.ex.rest;

/**
 * Signals that the entity does not support rest.
 */
public class RestNotSupportedException extends RuntimeException {

    private static final long serialVersionUID = 4367270872061433404L;

    private final String msg;

    public RestNotSupportedException(String entityName, String moduleName, String namespace) {
        StringBuilder sb = new StringBuilder("Rest is not supported for entity ").append(entityName);
        if (moduleName != null) {
            sb.append(" from module ").append(moduleName);
        }
        if (namespace != null) {
            sb.append(" from namespace ").append(namespace);
        }
        msg = sb.toString();
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
