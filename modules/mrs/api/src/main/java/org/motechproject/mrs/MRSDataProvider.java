package org.motechproject.mrs;

import org.motechproject.commons.api.AbstractDataProvider;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MRSDataProvider extends AbstractDataProvider {
    private static final String SUPPORT_FIELD = "id";

    private List<MRSPatientAdapter> patientAdapters;
    private List<MRSFacilityAdapter> facilityAdapters;
    private List<MRSPersonAdapter> personAdapters;

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

                if (MRSPatient.class.isAssignableFrom(cls)) {
                    obj = getPatient(id);
                } else if (MRSPerson.class.isAssignableFrom(cls)) {
                    obj = getPerson(id);
                } else if (MRSFacility.class.isAssignableFrom(cls)) {
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
        return Arrays.asList(MRSPerson.class, MRSPatient.class, MRSFacility.class);
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.mrs.domain";
    }

    public void setPatientAdapters(List<MRSPatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }

    public void setFacilityAdapters(List<MRSFacilityAdapter> facilityAdapters) {
        this.facilityAdapters = facilityAdapters;
    }

    public void setPersonAdapters(List<MRSPersonAdapter> personAdapters) {
        this.personAdapters = personAdapters;
    }

    private Object getPatient(String patientId) {
        Object obj = null;

        if (patientAdapters != null && !patientAdapters.isEmpty()) {
            for (MRSPatientAdapter adapter : patientAdapters) {
                obj = adapter.getPatient(patientId);
            }
        }

        return obj;
    }

    private MRSFacility getFacility(String facilityId) {
        MRSFacility facility = null;

        if (facilityAdapters != null && !facilityAdapters.isEmpty()) {
            for (MRSFacilityAdapter adapter : facilityAdapters) {
                facility = adapter.getFacility(facilityId);
            }
        }

        return facility;
    }

    private MRSPerson getPerson(String personId) {
        MRSPerson person = null;

        if (personAdapters != null && !personAdapters.isEmpty()) {
            for (MRSPersonAdapter adapter : personAdapters) {
                List<? extends MRSPerson> byPersonId = adapter.findByPersonId(personId);
                person = byPersonId.isEmpty() ? null : byPersonId.get(0);
            }
        }

        return person;
    }
}
