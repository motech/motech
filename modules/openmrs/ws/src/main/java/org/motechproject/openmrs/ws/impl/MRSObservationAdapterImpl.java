package org.motechproject.openmrs.ws.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.services.ObservationAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.ObservationResource;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.ObservationListResult;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("observationAdapter")
public class MRSObservationAdapterImpl implements ObservationAdapter {
    private static final Logger LOGGER = Logger.getLogger(MRSObservationAdapterImpl.class);

    private final PatientAdapter patientAdapter;
    private final ObservationResource obsResource;

    @Autowired
    public MRSObservationAdapterImpl(ObservationResource obsResource, PatientAdapter patientAdapter) {
        this.obsResource = obsResource;
        this.patientAdapter = patientAdapter;
    }

    @Override
    public List<org.motechproject.mrs.domain.Observation> findObservations(String motechId, String conceptName) {
        Validate.notEmpty(motechId, "Motech id cannot be empty");
        Validate.notEmpty(conceptName, "Concept name cannot be empty");

        List<org.motechproject.mrs.domain.Observation> obs = new ArrayList<org.motechproject.mrs.domain.Observation>();
        Patient patient = patientAdapter.getPatientByMotechId(motechId);
        if (patient == null) {
            return obs;
        }

        ObservationListResult result = null;
        try {
            result = obsResource.queryForObservationsByPatientId(patient.getPatientId());
        } catch (HttpException e) {
            LOGGER.error("Could not retrieve observations for patient with motech id: " + motechId);
            return Collections.emptyList();
        }

        for (Observation ob : result.getResults()) {
            if (ob.hasConceptByName(conceptName)) {
                obs.add(ConverterUtils.convertObservationToMrsObservation(ob));
            }
        }

        return obs;
    }

    @Override
    public void voidObservation(org.motechproject.mrs.domain.Observation mrsObservation, String reason, String mrsUserMotechId)
            throws ObservationNotFoundException {
        Validate.notNull(mrsObservation);
        Validate.notEmpty(mrsObservation.getObservationId());

        try {
            obsResource.deleteObservation(mrsObservation.getObservationId(), reason);
        } catch (HttpException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                LOGGER.warn("No Observation found with uuid: " + mrsObservation.getObservationId());
                throw new ObservationNotFoundException(mrsObservation.getObservationId(), e);
            }

            LOGGER.error("Could not void observation with uuid: " + mrsObservation.getObservationId());
        }
    }

    @Override
    public OpenMRSObservation findObservation(String patientMotechId, String conceptName) {
        Validate.notEmpty(patientMotechId, "MoTeCH Id cannot be empty");
        Validate.notEmpty(conceptName, "Concept name cannot be empty");

        List<org.motechproject.mrs.domain.Observation> observations = findObservations(patientMotechId, conceptName);
        if (observations.size() == 0) {
            return null;
        }

        return (OpenMRSObservation) observations.get(0);
    }

    @Override
    public OpenMRSObservation getObservationById(String id) {
        try {
            Observation obs = obsResource.getObservationById(id);
            return ConverterUtils.convertObservationToMrsObservation(obs);
        } catch (HttpException e) {
            return null;
        }
    }
}
