package org.motechproject.commcare.osgi.it;

import com.google.gson.JsonParser;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;

import java.io.IOException;

public class CommcareBundleIT extends BaseOsgiIT {


    public void testCommcareUserService() {
        CommcareUserService service = (CommcareUserService) verifyServiceAvailable(CommcareUserService.class.getName());
        assertNotNull(service);
    }

    public void testCommcareCaseService() {
        CommcareCaseService service = (CommcareCaseService) verifyServiceAvailable(CommcareCaseService.class.getName());
        assertNotNull(service);
    }

    public void testCommcareFormService() {
        CommcareFormService service = (CommcareFormService) verifyServiceAvailable(CommcareFormService.class.getName());
        assertNotNull(service);
    }

    public void testSettingsController() throws IOException, InterruptedException {
        final String response = new PollingHttpClient(new DefaultHttpClient(), 60)
                .get("http://localhost:8080/commcare/settings", new BasicResponseHandler());
        assertNotNull(response);
        assertTrue(new JsonParser().parse(response).isJsonObject());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testCommcareBundleContext.xml"};
    }
}
