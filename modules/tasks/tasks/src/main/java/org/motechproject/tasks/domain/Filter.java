package org.motechproject.tasks.domain;


public class Filter {
    private EventParameter eventParameter;
    private boolean negationOperator;
    private String operator;
    private String expression;

    public Filter() {
    }

    public Filter(EventParameter eventParameter, boolean negationOperator, String operator, String expression) {
        this.eventParameter = eventParameter;
        this.negationOperator = negationOperator;
        this.operator = operator;
        this.expression = expression;
    }

    public EventParameter getEventParameter() {
        return eventParameter;
    }

    public void setEventParameter(EventParameter eventParameter) {
        this.eventParameter = eventParameter;
    }

    public boolean isNegationOperator() {
        return negationOperator;
    }

    public void setNegationOperator(boolean negationOperator) {
        this.negationOperator = negationOperator;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
