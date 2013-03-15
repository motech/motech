package org.motechproject.mrs.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.services.PatientAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class MrsImplementationManagerTest {

    private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

    @Mock
    private PatientAdapter patientAdapter;

    @Mock
    private PatientAdapter patientAdapter2;

    private Map<String, String> serviceProperties = new HashMap<>();

    private MrsImplementationManager mrsImplementationManager;

    @Before
    public void setup() {
        initMocks(this);
        mrsImplementationManager = new MrsImplementationManager();
    }

    @Test
    public void shouldBindAndUnbindImplementations() throws ImplementationException {
        serviceProperties.put(BUNDLE_SYMBOLIC_NAME, "test_mrs");
        mrsImplementationManager.bind(patientAdapter, serviceProperties);

        serviceProperties.put(BUNDLE_SYMBOLIC_NAME, "mrs2");
        mrsImplementationManager.bind(patientAdapter2, serviceProperties);

        assertEquals("test_mrs", mrsImplementationManager.getCurrentImplName());
        assertEquals(patientAdapter, mrsImplementationManager.getPatientAdapter());
        assertEquals(new HashSet<>(asList("test_mrs", "mrs2")), mrsImplementationManager.getAvailableAdapters());

        mrsImplementationManager.unbind(patientAdapter2, serviceProperties);
        assertEquals(new HashSet<>(asList("test_mrs")), mrsImplementationManager.getAvailableAdapters());

        serviceProperties.put(BUNDLE_SYMBOLIC_NAME, "test_mrs");
        mrsImplementationManager.unbind(patientAdapter, serviceProperties);
        assertTrue(mrsImplementationManager.getAvailableAdapters().isEmpty());
    }

    @Test
    public void shouldThrowExceptionForUnregisteredImplAndSwitch() throws ImplementationException {
        serviceProperties.put(BUNDLE_SYMBOLIC_NAME, "test_mrs");
        mrsImplementationManager.bind(patientAdapter, serviceProperties);

        serviceProperties.put(BUNDLE_SYMBOLIC_NAME, "mrs2");
        mrsImplementationManager.bind(patientAdapter2, serviceProperties);

        serviceProperties.put(BUNDLE_SYMBOLIC_NAME, "test_mrs");
        mrsImplementationManager.unbind(patientAdapter, serviceProperties);

        boolean exThrown = false;
        try {
            mrsImplementationManager.getPatientAdapter();
        } catch (ImplementationNotAvailableException e) {
            exThrown = true;
        }

        assertTrue(exThrown);
        assertEquals(patientAdapter2, mrsImplementationManager.getPatientAdapter());
    }

    @Test(expected = NoImplementationsAvailableException.class)
    public void shouldThrowExceptionWhenNoImplsAvailable() throws ImplementationException {
        mrsImplementationManager.getPatientAdapter();
    }

}
