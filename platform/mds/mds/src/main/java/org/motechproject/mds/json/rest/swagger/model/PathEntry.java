package org.motechproject.mds.json.rest.swagger.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawel on 1/19/15.
 */
public class PathEntry implements Serializable {

    private static final long serialVersionUID = -402421870021219022L;

    private String description;
    private String operationId;
    private List<String> produces;
    private List<Parameter> parameters;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public List<String> getProduces() {
        return produces;
    }

    public void setProduces(List<String> produces) {
        this.produces = produces;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Parameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(parameter);
    }
}
