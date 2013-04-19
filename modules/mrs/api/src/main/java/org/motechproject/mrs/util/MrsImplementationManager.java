package org.motechproject.mrs.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSImplReqAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("mrsImplementationManager")
public class MrsImplementationManager implements OsgiServiceLifecycleListener {

    private String currentImplName;

    private Map<String, MRSPatientAdapter> patientAdapterMap = new HashMap<>();
    private Map<String, MRSFacilityAdapter> facilityAdapterMap = new HashMap<>();
    private Map<String, MRSPersonAdapter> personAdapterMap = new HashMap<>();
    private Map<String, MRSImplReqAdapter> implReqAdapterMap = new HashMap<>();

    public String getCurrentImplName() {
        return currentImplName;
    }

    public List<String>  getReqFieldsNames() throws ImplementationException {
        return getImplReqAdapter().getRequired();
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

        if (service instanceof MRSPatientAdapter) {
            patientAdapterMap.put(bundleSymbolicName, (MRSPatientAdapter) service);
        } else if (service instanceof MRSFacilityAdapter) {
            facilityAdapterMap.put(bundleSymbolicName, (MRSFacilityAdapter) service);
        } else if (service instanceof MRSPersonAdapter) {
            personAdapterMap.put(bundleSymbolicName, (MRSPersonAdapter) service);
        } else if (service instanceof MRSImplReqAdapter) {
            implReqAdapterMap.put(bundleSymbolicName, (MRSImplReqAdapter) service);
        }
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        String bundleSymbolicName = serviceProperties.get("Bundle-SymbolicName").toString();

        if (service instanceof MRSPatientAdapter) {
            patientAdapterMap.remove(bundleSymbolicName);
        } else if (service instanceof MRSFacilityAdapter) {
            facilityAdapterMap.remove(bundleSymbolicName);
        } else if (service instanceof MRSPersonAdapter) {
            personAdapterMap.remove(bundleSymbolicName);
        } else if (service instanceof MRSImplReqAdapter) {
            implReqAdapterMap.remove(bundleSymbolicName);
        }
    }

    public MRSPatientAdapter getPatientAdapter() throws ImplementationException {
        MRSPatientAdapter patientAdapter = patientAdapterMap.get(currentImplName);

        if (patientAdapter == null) {
            changeToNextImpl();
            throw implException();
        }

        return patientAdapter;
    }

    public MRSFacilityAdapter getFacilityAdapter() throws ImplementationException {
        MRSFacilityAdapter facilityAdapter = facilityAdapterMap.get(currentImplName);

        if (facilityAdapter == null) {
            changeToNextImpl();
            throw implException();
        }

        return facilityAdapter;
    }

    public MRSImplReqAdapter getImplReqAdapter() throws ImplementationException {
        MRSImplReqAdapter implReqAdapter = implReqAdapterMap.get(currentImplName);

        if (implReqAdapter == null) {
            changeToNextImpl();
            throw implException();
        }

        return implReqAdapter;
    }

    public MRSPersonAdapter getPersonAdapter() throws ImplementationException {
        MRSPersonAdapter personAdapter = personAdapterMap.get(currentImplName);

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
