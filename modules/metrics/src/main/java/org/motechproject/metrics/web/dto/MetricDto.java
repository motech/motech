package org.motechproject.metrics.web.dto;

/**
 * Represents a single configured selection of either a numerator or denominator from the user interface.
 */
public class MetricDto {
    private String name;
    private MetricType type;
    private RatioGaugeValue value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetricType getType() {
        return type;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public RatioGaugeValue getValue() {
        return value;
    }

    public void setValue(RatioGaugeValue value) {
        this.value = value;
    }
}
