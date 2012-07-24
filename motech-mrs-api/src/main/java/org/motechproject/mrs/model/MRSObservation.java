package org.motechproject.mrs.model;

import ch.lambdaj.Lambda;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * Maintains patient's data collected during visits (Encounters)
 *
 * @param <T> Type of the observation's value
 */
public class MRSObservation<T> {

    private String id;
    private Date date;
    private String conceptName;
    private T value;
    private Set<MRSObservation> dependantObservations;

    /**
     * Creates an observation object with the given details
     *
     * @param date        Date of the observation
     * @param conceptName Name of the concept
     * @param value       Value of the observation
     */
    public MRSObservation(Date date, String conceptName, T value) {
        this.date = date;
        this.conceptName = conceptName;
        this.value = value;
    }

    /**
     * Creates an observation object with the given details
     *
     * @param id          Observation id
     * @param date        Date of the observation
     * @param conceptName Name of the concept
     * @param value       Value of the observation
     */
    public MRSObservation(String id, Date date, String conceptName, T value) {
        this(date, conceptName, value);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getConceptName() {
        return conceptName;
    }

    public T getValue() {
        return value;
    }

    public Set<MRSObservation> getDependantObservations() {
        return dependantObservations;
    }

    public void setDependantObservations(Set<MRSObservation> dependantObservations) {
        this.dependantObservations = dependantObservations;
    }

    public void addDependantObservation(MRSObservation mrsObservation) {
        if (this.dependantObservations == null) {
            dependantObservations = new HashSet<MRSObservation>();
        }
        //to remove duplicate observation
        List<MRSObservation> existingObservationList = Lambda.filter(having(on(MRSObservation.class).getConceptName(), is(equalTo(mrsObservation.getConceptName()))), dependantObservations);
        if (!existingObservationList.isEmpty()) {
            dependantObservations.remove(existingObservationList.get(0));
        }
        dependantObservations.add(mrsObservation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MRSObservation)) {
            return false;
        }
        MRSObservation that = (MRSObservation) o;
        if (conceptName != null ? !conceptName.equals(that.conceptName) : that.conceptName != null) {
            return false;
        }
        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (dependantObservations != null ? !dependantObservations.equals(that.dependantObservations) : that.dependantObservations != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (conceptName != null ? conceptName.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (dependantObservations != null ? dependantObservations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MRSObservation{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", conceptName='" + conceptName + '\'' +
                ", value=" + value +
                '}';
    }
}
