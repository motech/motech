package org.motechproject.mds.docs.swagger.model;

/**
 * Represents where the parameter will be used.
 */
public enum ParameterType {
    /**
     * The param will be included in the path, example /items/{id}
     */
    PATH,
    /**
     * The param will be included in the query as a request param, example /items?id={id}
     */
    QUERY,
    /**
     * The param will be included in the headers of the request, for HTTP schema this will be HTTP headers
     */
    HEADER,
    /**
     * This param will be the payload of the body. Since there can only be one payload, there can only be one body parameter.
     * Since Form parameters are also in the payload, body and form parameters cannot exist together for the same operation.
     */
    BODY,
    /**
     * Used to describe the payload of an HTTP request when either application/x-www-form-urlencoded or multipart/form-data are used as the content type of the request
     */
    FORM
}
