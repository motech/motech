package org.motechproject.server.pillreminder.contract;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: prateekk
 * Date: 7/1/11
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MedicineRequest {
    private String name;
    private Date startDate;
    private Date endDate;

    public MedicineRequest(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
