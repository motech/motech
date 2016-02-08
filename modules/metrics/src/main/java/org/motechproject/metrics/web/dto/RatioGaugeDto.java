package org.motechproject.metrics.web.dto;

/**
 * Represents an instance of a ratio gauge created in the user interface.
 */
public class RatioGaugeDto {
    private String name;
    private MetricDto numerator;
    private MetricDto denominator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetricDto getNumerator() {
        return numerator;
    }

    public void setNumerator(MetricDto numerator) {
        this.numerator = numerator;
    }

    public MetricDto getDenominator() {
        return denominator;
    }

    public void setDenominator(MetricDto denominator) {
        this.denominator = denominator;
    }
}
