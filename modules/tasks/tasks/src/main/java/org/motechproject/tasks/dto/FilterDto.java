package org.motechproject.tasks.dto;

import org.motechproject.tasks.domain.enums.ParameterType;

import java.util.List;

public class FilterDto {

    private String displayName;
    private String key;
    private ParameterType type;
    private String operator;
    private boolean negationOperator;
    private String expression;
    private List<String> manipulations;

    public FilterDto() {
    }

    public FilterDto(String displayName, String key, ParameterType type, String operator, boolean negationOperator, String expression,
                     List<String> manipulations) {
        this.displayName = displayName;
        this.key = key;
        this.type = type;
        this.operator = operator;
        this.negationOperator = negationOperator;
        this.expression = expression;
        this.manipulations = manipulations;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isNegationOperator() {
        return negationOperator;
    }

    public void setNegationOperator(boolean negationOperator) {
        this.negationOperator = negationOperator;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<String> getManipulations () {
        return manipulations;
    }

    public void setManipulations (List<String> manipulations) {
        this.manipulations = manipulations;
    }
}
