package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.ObservationListResult;

public interface ObservationResource {

    ObservationListResult queryForObservationsByPatientId(String id) throws HttpException;

    void deleteObservation(String id, String reason) throws HttpException;

    Observation getObservationById(String id) throws HttpException;

}
