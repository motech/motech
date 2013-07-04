package org.motechproject.tasks.service;

import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isBlank;

public class ActionParameterRequest implements Comparable<ActionParameterRequest> {

    private static final String UNICODE = "UNICODE";

    private Integer order;
    private String key;
    private String displayName;
    private String type;

    private ActionParameterRequest() {
        this.type = UNICODE;
    }

    public ActionParameterRequest(String key, String displayName, Integer order, String type) {
        this.key = key;
        this.displayName = displayName;
        this.order = order;
        this.type = isBlank(type) ? UNICODE : type;
    }

    public ActionParameterRequest(String key, String displayName, Integer order) {
        this(key, displayName, order, null);
    }

    public ActionParameterRequest(String key, String displayName) {
        this(key, displayName, null, null);
    }

    public Integer getOrder() {
        return order;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(ActionParameterRequest o) {
        return Integer.compare(this.order, o.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, key);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }


        final ActionParameterRequest other = (ActionParameterRequest) obj;

        return Objects.equals(this.order, other.order) &&
                Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        return String.format("ActionParameter{order=%d, key='%s'}", order, key);
    }
}
