package org.motechproject.mrs;

import org.motechproject.commons.api.AbstractDataProvider;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.PersonAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MRSDataProvider extends AbstractDataProvider {
    private static final String SUPPORT_FIELD = "id";

    private List<PatientAdapter> patientAdapters;
    private List<FacilityAdapter> facilityAdapters;
    private List<PersonAdapter> personAdapters;

    @Autowired
    public MRSDataProvider(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");

        if (resource != null) {
            setBody(resource);
        }
    }

    @Override
    public String getName() {
        return "MRS";
    }

    @Override
    public Object lookup(String type, Map<String, String> lookupFields) {
        Object obj = null;

        if (supports(type) && lookupFields.containsKey(SUPPORT_FIELD)) {
            String id = lookupFields.get(SUPPORT_FIELD);

            try {
                Class<?> cls = getClassForType(type);

                if (Patient.class.isAssignableFrom(cls)) {
                    obj = getPatient(id);
                } else if (Person.class.isAssignableFrom(cls)) {
                    obj = getPerson(id);
                } else if (Facility.class.isAssignableFrom(cls)) {
                    obj = getFacility(id);
                }
            } catch (ClassNotFoundException e) {
                logError(e.getMessage(), e);
            }
        }

        return obj;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        return Arrays.asList(Person.class, Patient.class, Facility.class);
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.mrs.domain";
    }

    public void setPatientAdapters(List<PatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }

    public void setFacilityAdapters(List<FacilityAdapter> facilityAdapters) {
        this.facilityAdapters = facilityAdapters;
    }

    public void setPersonAdapters(List<PersonAdapter> personAdapters) {
        this.personAdapters = personAdapters;
    }

    private Object getPatient(String patientId) {
        Object obj = null;

        if (patientAdapters != null && !patientAdapters.isEmpty()) {
            for (PatientAdapter adapter : patientAdapters) {
                obj = adapter.getPatient(patientId);
            }
        }

        return obj;
    }

    private Facility getFacility(String facilityId) {
        Facility facility = null;

        if (facilityAdapters != null && !facilityAdapters.isEmpty()) {
            for (FacilityAdapter adapter : facilityAdapters) {
                facility = adapter.getFacility(facilityId);
            }
        }

        return facility;
    }

    private Person getPerson(String personId) {
        Person person = null;

        if (personAdapters != null && !personAdapters.isEmpty()) {
            for (PersonAdapter adapter : personAdapters) {
                List<? extends Person> byPersonId = adapter.findByPersonId(personId);
                person = byPersonId.isEmpty() ? null : byPersonId.get(0);
            }
        }

        return person;
    }
}
