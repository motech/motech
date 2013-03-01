package org.motechproject.tasks.domain;


import java.io.Serializable;
import java.util.Objects;

public class Filter implements Serializable {
    private static final long serialVersionUID = 7811400954352375064L;

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

    @Override
    public int hashCode() {
        return Objects.hash(eventParameter, negationOperator, operator, expression);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Filter other = (Filter) obj;

        return Objects.equals(this.eventParameter, other.eventParameter) &&
                Objects.equals(this.negationOperator, other.negationOperator) &&
                Objects.equals(this.operator, other.operator) &&
                Objects.equals(this.expression, other.expression);
    }

    @Override
    public String toString() {
        return String.format("Filter{eventParameter=%s, negationOperator=%s, operator='%s', expression='%s'}",
                eventParameter, negationOperator, operator, expression);
    }
}
