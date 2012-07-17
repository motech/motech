package org.motechproject.openmrs.rest.impl;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.rest.HttpException;
import org.motechproject.openmrs.rest.RestClient;
import org.motechproject.openmrs.rest.model.Observation;
import org.motechproject.openmrs.rest.model.Observation.ObservationValue;
import org.motechproject.openmrs.rest.model.Observation.ObservationValueDeserializer;
import org.motechproject.openmrs.rest.model.ObservationListResult;
import org.motechproject.openmrs.rest.util.ConverterUtils;
import org.motechproject.openmrs.rest.util.JsonUtils;
import org.motechproject.openmrs.rest.util.OpenMrsUrlHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("observationAdapter")
public class MRSObservationAdapterImpl implements MRSObservationAdapter {
    private static final Logger LOGGER = Logger.getLogger(MRSObservationAdapterImpl.class);

    private final MRSPatientAdapter patientAdapter;
    private final RestClient restfulClient;
    private final OpenMrsUrlHolder urlHolder;

    @Autowired
    public MRSObservationAdapterImpl(MRSPatientAdapter patientAdapter, RestClient restfulClient,
            OpenMrsUrlHolder urlHolder) {
        this.patientAdapter = patientAdapter;
        this.restfulClient = restfulClient;
        this.urlHolder = urlHolder;
    }

    @Override
    public List<MRSObservation> findObservations(String motechId, String conceptName) {
        Validate.notEmpty(motechId, "Motech id cannot be empty");
        Validate.notEmpty(conceptName, "Concept name cannot be empty");

        List<MRSObservation> obs = new ArrayList<MRSObservation>();
        MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);
        if (patient == null) {
            return obs;
        }

        String responseJson = null;
        try {
            responseJson = restfulClient.getJson(urlHolder.getObservationsByPatient(patient.getId()));
        } catch (HttpException e) {
            LOGGER.error("Could not retrieve observations for patient with motech id: " + motechId);
            throw new MRSException(e);
        }

        Map<Type, Object> adapters = new HashMap<Type, Object>();
        adapters.put(ObservationValue.class, new ObservationValueDeserializer());
        ObservationListResult result = (ObservationListResult) JsonUtils.readJsonWithAdapters(responseJson,
                ObservationListResult.class, adapters);
        for (Observation ob : result.getResults()) {
            if (ob.hasConceptByName(conceptName)) {
                obs.add(ConverterUtils.convertObservationToMrsObservation(ob));
            }
        }

        return obs;
    }

    @Override
    public void voidObservation(MRSObservation mrsObservation, String reason, String mrsUserMotechId)
            throws ObservationNotFoundException {
        Validate.notNull(mrsObservation);
        Validate.notEmpty(mrsObservation.getId());

        URI uri = null;
        if (StringUtils.isEmpty(reason)) {
            uri = urlHolder.getObservationById(mrsObservation.getId());
        } else {
            uri = urlHolder.getObservationDeleteWithReason(mrsObservation.getId(), reason);
        }

        try {
            restfulClient.delete(uri);
        } catch (HttpException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                LOGGER.warn("No Observation found with id: " + mrsObservation.getId());
                throw new ObservationNotFoundException(mrsObservation.getId());
            }

            LOGGER.error("Could not void observation with id: " + mrsObservation.getId());
            throw new MRSException(e);
        }
    }

    @Override
    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        Validate.notEmpty(patientMotechId, "MoTeCH Id cannot be empty");
        Validate.notEmpty(conceptName, "Concept name cannot be empty");

        List<MRSObservation> observations = findObservations(patientMotechId, conceptName);
        if (observations.size() == 0) {
            return null;
        }

        return observations.get(0);
    }
}
