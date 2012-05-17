package org.motechproject.appointments.api.service.contract;

import org.joda.time.DateTime;
import org.motechproject.appointments.api.model.search.*;

import java.util.ArrayList;
import java.util.List;

/**
 * \ingroup Appointments
 *
 * Allows to build query to search for visits by {@link org.motechproject.appointments.api.service.AppointmentService#search(VisitsQuery)}
 */
public class VisitsQuery {
    private List<Criterion> criteria = new ArrayList<Criterion>();

    /**
     * Adds query to filter visits with due date in the given range. (Start and end time inclusive)
     *
     * @param start
     * @param end
     * @return
     */
    public VisitsQuery withDueDateIn(DateTime start, DateTime end) {
        criteria.add(new DueDateInCriterion(start, end));
        return this;
    }

    /**
     * Adds query to filter scheduled visits by missed visits.
     * Result contain all future visits. Should be used after adding {@link #withDueDateIn(DateTime, DateTime)} to avoid future visits in result
     *
     * @return
     */
    public VisitsQuery unvisited() {
        criteria.add(new UnvisitedCriterion());
        return this;
    }

    /**
     * Adds query to filter visits by user identifier (external id)
     *
     * @param externalId
     * @return
     */
    public VisitsQuery havingExternalId(String externalId) {
        criteria.add(new ExternalIdCriterion(externalId));
        return this;
    }

    /**
     * Builds query to filter visits by given metadata key value pair
     *
     * @param property Property of the metadata
     * @param value    Value of the property to be matched
     * @return currently built query
     */
    public VisitsQuery havingMetadata(String property, String value) {
        criteria.add(new MetadataCriterion(property, value));
        return this;
    }

    public List<Criterion> getCriteria() {
        return new ArrayList<Criterion>(criteria);
    }
}
