package org.motechproject.event.aggregation.service.impl;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.service.AggregationRule;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class AggregationRuleRequest implements AggregationRule {

    @NotNull(message = "Name must be present")
    @Size(min = 1, message = "Name must be present")
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @NotNull(message = "Must subscribe to an event")
    @Size(min = 1, message = "Must subscribe to an event")
    @JsonProperty
    private String subscribedTo;

    @NotNull(message = "Must define some at least one field over which to aggregate events")
    @Size(min = 1, message = "Must define some at least one field over which to aggregate events")
    @JsonProperty
    private List<String> fields;

    @NotNull
    @Valid
    @JsonProperty
    private AggregationScheduleRequest aggregationSchedule;

    @NotNull(message = "Must specify aggregated event subject")
    @Size(min = 1, message = "Must specify aggregated event subject")
    @JsonProperty
    private String publishAs;

    @JsonProperty
    @NotNull(message = "Must specify aggregation state")
    private AggregationState state;

    public AggregationRuleRequest() {
    }

    public AggregationRuleRequest(String name, String description, String subscribedTo, List<String> fields, AggregationScheduleRequest aggregationSchedule, String publishAs, AggregationState state) {
        this.name = name;
        this.description = description;
        this.subscribedTo = subscribedTo;
        this.fields = fields;
        this.aggregationSchedule = aggregationSchedule;
        this.publishAs = publishAs;
        this.state = state;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return name;
    }

    @JsonIgnore
    @Override
    public String getDescription() {
        return description;
    }

    @JsonIgnore
    @Override
    public String getSubscribedTo() {
        return subscribedTo;
    }

    @JsonIgnore
    @Override
    public List<String> getFields() {
        return fields;
    }

    @JsonIgnore
    @Override
    public AggregationScheduleRequest getAggregationSchedule() {
        return aggregationSchedule;
    }

    @JsonIgnore
    @Override
    public String getPublishAs() {
        return publishAs;
    }

    @Override
    @JsonIgnore
    public AggregationState getState() {
        return state;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public void setSubscribedTo(String subscribedTo) {
        this.subscribedTo = subscribedTo;
    }

    @JsonIgnore
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @JsonIgnore
    public void setAggregationSchedule(AggregationScheduleRequest aggregationSchedule) {
        this.aggregationSchedule = aggregationSchedule;
    }

    @JsonIgnore
    public void setPublishAs(String publishAs) {
        this.publishAs = publishAs;
    }

    @JsonIgnore
    public void setState(AggregationState state) {
        this.state = state;
    }
}
