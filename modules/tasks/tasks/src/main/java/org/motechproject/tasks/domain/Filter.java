package org.motechproject.tasks.domain;

import org.motechproject.mds.annotations.Entity;

import java.io.Serializable;
import java.util.Objects;

import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;

@Entity
public class Filter implements Serializable {
    private static final long serialVersionUID = 7811400954352375064L;

    private String displayName;
    private String key;
    private ParameterType type;
    private boolean negationOperator;
    private String operator;
    private String expression;

    public Filter() {
        this(null, null, null, false, null, null);
    }

    public Filter(EventParameter eventParameter, boolean negationOperator, String operator,
                  String expression) {
        this.negationOperator = negationOperator;
        this.operator = operator;
        this.expression = expression;

        if (eventParameter != null) {
            this.displayName = String.format("%s (Trigger)", eventParameter.getDisplayName());
            this.key = String.format("%s.%s", TRIGGER_PREFIX, eventParameter.getEventKey());
            this.type = eventParameter.getType();
        }
    }

    public Filter(String displayName, String key, ParameterType type, boolean negationOperator,
                  String operator, String expression) {
        this.displayName = displayName;
        this.key = key;
        this.type = type;
        this.negationOperator = negationOperator;
        this.operator = operator;
        this.expression = expression;
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

    public void setType(final ParameterType type) {
        this.type = type;
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
        return Objects.hash(displayName, key, type, negationOperator, operator, expression);
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

        return Objects.equals(this.displayName, other.displayName) &&
               Objects.equals(this.key, other.key) &&
               Objects.equals(this.type, other.type) &&
               Objects.equals(this.negationOperator, other.negationOperator) &&
               Objects.equals(this.operator, other.operator) &&
               Objects.equals(this.expression, other.expression);
    }

    @Override
    public String toString() {
        return String.format(
                "Filter{displayName=%s, key=%s, type=%s, negationOperator=%s, operator='%s', expression='%s'}",
                displayName, key, type, negationOperator, operator, expression
        );
    }
}
