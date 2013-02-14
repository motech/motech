package org.motechproject.admin.osgi;

import ch.lambdaj.Lambda;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.security.service.MotechPermissionService;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.CoreMatchers.equalTo;

public class AdminBundleIT extends BaseOsgiIT {

    private static final String ERROR_MSG = "test-error";
    private static final String DEBUG_MSG = "test-debug";
    private static final String WARNING_MSG = "test-warn";
    private static final DateTime TIMEOUT = DateTime.now().plusHours(1);

    private static final String ADMIN_URL = "http://localhost:8080/admin/api/";
    private static final String LOGIN_URL = "http://localhost:8080/server/motech-platform-server/j_spring_security_check";

    private HttpClient httpClient = new DefaultHttpClient();

    public void testAdminBundleContext() {
        assertServicePresent(PlatformSettingsService.class);
        assertServicePresent(EventListenerRegistryService.class);
        assertServicePresent(MotechPermissionService.class);
        assertServicePresent(MotechRoleService.class);
    }

    public void testStatusMessageService() {
        StatusMessageService service = (StatusMessageService) assertServicePresent(StatusMessageService.class);

        service.error(ERROR_MSG, TIMEOUT);
        service.warn(WARNING_MSG, TIMEOUT);
        service.debug(DEBUG_MSG, TIMEOUT);

        List<StatusMessage> messages = service.getActiveMessages();
        messages = Lambda.filter(having(on(StatusMessage.class).getTimeout(), equalTo(TIMEOUT)), messages);

        assertFalse(messages.isEmpty());

        StatusMessage msg = findMsgByText(messages, ERROR_MSG);
        assertEquals(Level.ERROR, msg.getLevel());

        msg = findMsgByText(messages, WARNING_MSG);
        assertEquals(Level.WARN, msg.getLevel());

        msg = findMsgByText(messages, DEBUG_MSG);
        assertEquals(Level.DEBUG, msg.getLevel());
    }

    public void testBundleController() throws IOException {
        final String response = apiGet("bundles/");

        assertTrue(StringUtils.isNotBlank(response));
        JsonNode json = responseToJson(response);
        assertTrue("No bundles listed as active", json.size() > 0);
    }

    public void testSettingsController() throws IOException {
        final String response = apiGet("settings/platform");

        assertTrue(StringUtils.isNotBlank(response));
        JsonNode json = responseToJson(response);
        assertTrue("No settings listed", json.size() > 0);
    }

    public void testMessageController() throws IOException {
        StatusMessageService service = (StatusMessageService) assertServicePresent(StatusMessageService.class);
        service.error(ERROR_MSG, TIMEOUT);

        final String response = apiGet("messages");

        assertTrue(StringUtils.isNotBlank(response));
        JsonNode json = responseToJson(response);
        assertTrue("No messages listed", json.size() > 0);
    }

    private Object assertServicePresent(Class<?> clazz) {
        ServiceReference serviceReference = bundleContext.getServiceReference(clazz.getName());
        assertNotNull("No service refence for " + clazz.getName(), serviceReference);

        Object service = bundleContext.getService(serviceReference);
        assertNotNull("Null service for " + clazz.getName(), service);

        return service;
    }

    private String apiGet(String path) throws IOException {
        login();

        String processedPath = (path.startsWith("/")) ? path.substring(1) : path;

        return httpClient.execute(new HttpGet(ADMIN_URL + processedPath), new BasicResponseHandler());
    }

    private void login() throws IOException {
        final HttpPost loginPost = new HttpPost(LOGIN_URL);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("j_username", "motech"));
        nvps.add(new BasicNameValuePair("j_password", "motech"));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        final HttpResponse response = httpClient.execute(loginPost);
        EntityUtils.consume(response.getEntity());
    }

    private JsonNode responseToJson(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser parser = factory.createJsonParser(response);
        return mapper.readTree(parser);
    }

    private StatusMessage findMsgByText(List<StatusMessage> messages, String text) {
        List<StatusMessage> found = Lambda.filter(having(on(StatusMessage.class).getText(), equalTo(text)), messages);

        assertTrue("No matching message for: " + text, found.size() > 0);
        assertFalse("Found more then one matching message for: " + text, found.size() > 1);

        return found.get(0);
    }

    @Override
    protected void onTearDown() throws Exception {
        StatusMessageService service = (StatusMessageService) assertServicePresent(StatusMessageService.class);

        List<StatusMessage> messages = service.getAllMessages();

        for (StatusMessage msg : messages) {
            if (msg.getText().startsWith("test-") && TIMEOUT.equals(msg.getTimeout())) {
                service.removeMessage(msg);
            }
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testAdminBundleContext.xml"};
    }
}

