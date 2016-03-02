package org.motechproject.mds.web.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.exception.rest.RestNotSupportedException;
import org.motechproject.mds.rest.MdsRestFacade;
import org.motechproject.mds.util.ClassName;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsRestFacadeRetrieverTest {

    private static final String ENTITY_NAME = "Patient";
    private static final String MODULE_NAME = "MOTECH OpenMrs";
    private static final String NAMESPACE = "accra";

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference<MdsRestFacade> serviceRef;

    @Mock
    private MdsRestFacade mdsRestFacade;

    @InjectMocks
    private MdsRestFacadeRetriever restFacadeRetriever = new MdsRestFacadeRetriever();

    @Test
    public void shouldRetrieveRestFacadesForEudeEntities() throws InvalidSyntaxException {
        testRetrieval(ENTITY_NAME, null, null);
    }

    @Test
    public void shouldRetrieveRestFacadesForEntitiesFromModules() throws InvalidSyntaxException {
        testRetrieval(ENTITY_NAME, MODULE_NAME, null);
    }

    @Test
    public void shouldRetrieveRestFacadesForEntitiesFromModulesWithNs() throws InvalidSyntaxException {
        testRetrieval(ENTITY_NAME, MODULE_NAME, NAMESPACE);
    }

    @Test(expected = RestNotSupportedException.class)
    public void shouldThrowRestUnsupportedExceptionIfThereIsNoFacadeRef() {
        restFacadeRetriever.getRestFacade("unsupported", null, null);
    }

    private void testRetrieval(String entityName, String moduleName, String namespace) throws InvalidSyntaxException {
        String filter = filter(ClassName.restId(entityName, moduleName, namespace));
        when(bundleContext.getServiceReferences(MdsRestFacade.class, filter))
                .thenReturn(asList(serviceRef));
        when(bundleContext.getService(serviceRef)).thenReturn(mdsRestFacade);

        MdsRestFacade result = restFacadeRetriever.getRestFacade(entityName, moduleName, namespace);

        assertEquals(mdsRestFacade, result);
    }


    private String filter(String restId) {
        return String.format("(%s=%s)", "org.eclipse.gemini.blueprint.bean.name", restId);
    }
}
