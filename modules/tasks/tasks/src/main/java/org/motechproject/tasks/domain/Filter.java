package org.motechproject.tasks.domain;


public class Filter {
    private EventParameter eventParameter;
    private boolean navigationOperator;
    private String operator;
    private String expression;

    public Filter() {
    }

    public Filter(EventParameter eventParameter, boolean navigationOperator, String operator, String expression) {
        this.eventParameter = eventParameter;
        this.navigationOperator = navigationOperator;
        this.operator = operator;
        this.expression = expression;
    }

    public EventParameter getEventParameter() {
        return eventParameter;
    }

    public void setEventParameter(EventParameter eventParameter) {
        this.eventParameter = eventParameter;
    }

    public boolean isNavigationOperator() {
        return navigationOperator;
    }

    public void setNavigationOperator(boolean navigationOperator) {
        this.navigationOperator = navigationOperator;
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
