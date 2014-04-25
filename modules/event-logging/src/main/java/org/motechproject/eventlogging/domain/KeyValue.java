package org.motechproject.eventlogging.domain;

public class KeyValue {

    private String startKey;
    private Object startValue;
    private String endKey;
    private Object endValue;
    private boolean isOptional;

    public KeyValue(String startKey, Object startValue, String endKey, Object endValue, boolean isOptional) {
        this.startKey = startKey;
        this.startValue = startValue;
        this.endKey = endKey;
        this.endValue = endValue;
        this.isOptional = isOptional;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    public String getStartKey() {
        return startKey;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }

    public Object getStartValue() {
        return startValue;
    }

    public void setStartValue(Object startValue) {
        this.startValue = startValue;
    }

    public String getEndKey() {
        return endKey;
    }

    public void setEndKey(String endKey) {
        this.endKey = endKey;
    }

    public Object getEndValue() {
        return endValue;
    }

    public void setEndValue(Object endValue) {
        this.endValue = endValue;
    }

}
