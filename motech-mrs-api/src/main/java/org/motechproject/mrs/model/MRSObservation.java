package org.motechproject.mrs.model;

import java.util.Date;

public class MRSObservation<T>{

    private String id;
    private Date date;
    private String conceptName;
    private T value;
    
    public MRSObservation(Date date, String conceptName, T value) {
        this.date = date;
        this.conceptName = conceptName;
        this.value = value;
    }

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