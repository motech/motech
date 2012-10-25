package org.motechproject.event.aggregation.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.event.aggregation.model.schedule.AggregationScheduleRecord;
import org.motechproject.event.aggregation.service.AggregationRule;

import java.util.List;

@TypeDiscriminator("doc.type === 'AggregationRule'")
public class AggregationRuleRecord extends MotechBaseDataObject implements AggregationRule {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private String subscribedTo;

    @JsonProperty
    private List<String> fields;

    @JsonProperty
    private AggregationScheduleRecord aggregationSchedule;

    @JsonProperty
    private String publishAs;

    @JsonProperty
    private AggregationState state;

    public AggregationRuleRecord() {
        super("AggregationRule");
        this.state = AggregationState.Running;
    }

    public AggregationRuleRecord(String name, String description, String subscribedTo, List<String> fields, AggregationScheduleRecord schedule, String publishAs, AggregationState state) {
        this();
        this.name = name;
        this.description = description;
        this.subscribedTo = subscribedTo;
        this.fields = fields;
        this.aggregationSchedule = schedule;
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
    public AggregationScheduleRecord getAggregationSchedule() {
        return aggregationSchedule;
    }

    @JsonIgnore
    @Override
    public String getPublishAs() {
        return publishAs;
    }

    @JsonIgnore
    @Override
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
    public void setAggregationSchedule(AggregationScheduleRecord aggregationSchedule) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AggregationRuleRecord that = (AggregationRuleRecord) o;

        if (!aggregationSchedule.equals(that.aggregationSchedule)) {
            return false;
        }
        if (!fields.equals(that.fields)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (!publishAs.equals(that.publishAs)) {
            return false;
        }
        if (!subscribedTo.equals(that.subscribedTo)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + subscribedTo.hashCode();
        result = 31 * result + fields.hashCode();
        result = 31 * result + aggregationSchedule.hashCode();
        result = 31 * result + publishAs.hashCode();
        return result;
    }
}
