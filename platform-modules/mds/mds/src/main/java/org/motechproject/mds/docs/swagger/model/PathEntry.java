package org.motechproject.mds.docs.swagger.model;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an operation in the API under a given path and method (for example POST /api/items).
 * These will be represented in the Swagger UI as expandable tiles, under which the user will be able to execute
 * the call represented by this element.
 */
public class PathEntry implements Serializable {

    private static final long serialVersionUID = -402421870021219022L;

    private String description;
    private String operationId;
    private List<String> produces;
    private List<String> tags;
    private List<Parameter> parameters;
    private Map<Integer, Response> responses;

    /**
     * @return human readable description of this operation
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description human readable description of this operation
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the unique id of this operation, won't be shown to the user
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * @param operationId the unique id of this operation, won't be shown to the user
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    /**
     * @return the list of mime types produced by this endpoint
     */
    public List<String> getProduces() {
        return produces;
    }

    /**
     * @param produces the list of mime types produced by this endpoint
     */
    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    /**
     * @return the list of parameters used for this api call
     * @see org.motechproject.mds.docs.swagger.model.Parameter
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @param parameters  the list of parameters used for this api call
     * @see org.motechproject.mds.docs.swagger.model.Parameter
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return responses returned by this endpoint, keys are status codes
     */
    public Map<Integer, Response> getResponses() {
        return responses;
    }

    /**
     * @param responses responses returned by this endpoint, keys are status codes
     */
    public void setResponses(Map<Integer, Response> responses) {
        this.responses = responses;
    }

    /**
     * Tags will be used for grouping operations on the UI. We group by entity class names.
     * @return tags of this element
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Tags will be used for grouping operations on the UI. We group by entity class names.
     * @param tags  tags of this element
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Adds a parameter to this path entry.
     * @param parameter the parameter to be added
     */
    public void addParameter(Parameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(parameter);
    }

    /**
     * Adds a response to this path entry.
     * @param httpStatus the status for which this response is returned
     * @param response the response model
     */
    public void addResponse(HttpStatus httpStatus, Response response) {
        if (responses == null) {
            responses = new HashMap<>();
        }
        responses.put(httpStatus.value(), response);
    }

    /**
     * Adds a tag to this path entry.
     * @param tag the tag value
     */
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }
}
