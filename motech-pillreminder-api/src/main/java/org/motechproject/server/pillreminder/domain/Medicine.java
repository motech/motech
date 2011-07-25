package org.motechproject.server.pillreminder.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Medicine {
    private String name;
    private List<Status> statuses = new ArrayList<Status>();
    private Date startDate;
    private Date endDate;

    public static final String MEDICINE_END_DATE_CANNOT_BE_BEFORE_START_DATE = "Medicine end-date cannot be before start-date";

    public Medicine() {
    }

    public Medicine(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        if (!name.equals(medicine.name)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void validate() {
        if (getEndDate() != null && getStartDate().after(getEndDate()))
             throw(new ValidationException(MEDICINE_END_DATE_CANNOT_BE_BEFORE_START_DATE));
    }
}
