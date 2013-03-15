package org.motechproject.mrs.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.PersonAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component("mrsImplementationManager")
public class MrsImplementationManager implements OsgiServiceLifecycleListener {

    private String currentImplName;

    private Map<String, PatientAdapter> patientAdapterMap = new HashMap<>();
    private Map<String, FacilityAdapter> facilityAdapterMap = new HashMap<>();
    private Map<String, PersonAdapter> personAdapterMap = new HashMap<>();

    public String getCurrentImplName() {
        return currentImplName;
    }

    public void setCurrentImplName(String currentImplName) {
        this.currentImplName = currentImplName;
    }

    public Set<String> getAvailableAdapters() {
        return patientAdapterMap.keySet();
    }

    @Override
    public void bind(Object service, Map serviceProperties) {
        String bundleSymbolicName = serviceProperties.get("Bundle-SymbolicName").toString();

        if (StringUtils.isBlank(currentImplName)) {
            currentImplName = bundleSymbolicName;
        }

        if (service instanceof PatientAdapter) {
            patientAdapterMap.put(bundleSymbolicName, (PatientAdapter) service);
        } else if (service instanceof FacilityAdapter) {
            facilityAdapterMap.put(bundleSymbolicName, (FacilityAdapter) service);
        } else if (service instanceof PersonAdapter) {
            personAdapterMap.put(bundleSymbolicName, (PersonAdapter) service);
        }
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        String bundleSymbolicName = serviceProperties.get("Bundle-SymbolicName").toString();

        if (service instanceof PatientAdapter) {
            patientAdapterMap.remove(bundleSymbolicName);
        } else if (service instanceof FacilityAdapter) {
            facilityAdapterMap.remove(bundleSymbolicName);
        } else if (service instanceof PersonAdapter) {
            personAdapterMap.remove(bundleSymbolicName);
        }
    }

    public PatientAdapter getPatientAdapter() throws ImplementationException {
        PatientAdapter patientAdapter = patientAdapterMap.get(currentImplName);

        if (patientAdapter == null) {
            changeToNextImpl();
            throw implException();
        }

        return patientAdapter;
    }

    public FacilityAdapter getFacilityAdapter() throws ImplementationException {
        FacilityAdapter facilityAdapter = facilityAdapterMap.get(currentImplName);

        if (facilityAdapter == null) {
            changeToNextImpl();
            throw implException();
        }

        return facilityAdapter;
    }

    public PersonAdapter getPersonAdapter() throws ImplementationException {
        PersonAdapter personAdapter = personAdapterMap.get(currentImplName);

        if (personAdapter == null) {
            changeToNextImpl();
            throw implException();
        }

        return personAdapter;
    }

    private void changeToNextImpl() {
        if (patientAdapterMap.size() > 0) {
            setCurrentImplName(patientAdapterMap.keySet().iterator().next());
        }
    }

    private ImplementationException implException() {
        return (getAvailableAdapters().isEmpty()) ? new NoImplementationsAvailableException() :
                new ImplementationNotAvailableException(currentImplName);
    }
}
