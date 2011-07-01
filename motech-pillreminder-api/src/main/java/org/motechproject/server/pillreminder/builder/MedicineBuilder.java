package org.motechproject.server.pillreminder.builder;

import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.domain.Medicine;

/**
 * Created by IntelliJ IDEA.
 * User: prateekk
 * Date: 7/1/11
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MedicineBuilder {
    public Medicine createFrom(MedicineRequest medicineRequest) {
        return new Medicine(medicineRequest.getName(), medicineRequest.getStartDate(), medicineRequest.getEndDate());
    }
}
