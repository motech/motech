package org.motechproject.admin.osgi;

import ch.lambdaj.Lambda;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.TestContext;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AdminBundleIT extends BasePaxIT {

    private static final String ERROR_MSG = "test-error";
    private static final String DEBUG_MSG = "test-debug";
    private static final String WARNING_MSG = "test-warn";
    private static final String MODULE_NAME = "test-module";
    private static final DateTime TIMEOUT = DateUtil.nowUTC().plusHours(1);

    @Inject
    private ConfigurationService configurationService;
    @Inject
    private EventListenerRegistryService eventListenerRegistryService;
    @Inject
    private StatusMessageService statusMessageService;

    @Override
    protected boolean startHttpServer() {
        return true;
    }

    @Test
    public void testAdminBundleContext() {
        assertNotNull(configurationService);
        assertNotNull(eventListenerRegistryService);
    }

    @Test
    public void testStatusMessageService() {
        statusMessageService.error(ERROR_MSG, MODULE_NAME, TIMEOUT);
        statusMessageService.warn(WARNING_MSG, MODULE_NAME, TIMEOUT);
        statusMessageService.debug(DEBUG_MSG, MODULE_NAME, TIMEOUT);

        List<StatusMessage> messages = statusMessageService.getActiveMessages();
        messages = Lambda.filter(having(on(StatusMessage.class).getTimeout(), equalTo(TIMEOUT)), messages);

        assertFalse(messages.isEmpty());

        StatusMessage msg = findMsgByText(messages, ERROR_MSG);
        assertEquals(Level.ERROR, msg.getLevel());
        assertEquals(MODULE_NAME, msg.getModuleName());

        msg = findMsgByText(messages, WARNING_MSG);
        assertEquals(Level.WARN, msg.getLevel());
        assertEquals(MODULE_NAME, msg.getModuleName());

        msg = findMsgByText(messages, DEBUG_MSG);
        assertEquals(Level.DEBUG, msg.getLevel());
        assertEquals(MODULE_NAME, msg.getModuleName());
    }

    @Test
    public void testBundleController() throws IOException, InterruptedException {
        final String response = apiGet("bundles/");

        assertTrue(StringUtils.isNotBlank(response));
        JsonNode json = responseToJson(response);
        assertTrue("No bundles listed as active", json.size() > 0);
    }

    @Test
    public void testSettingsController() throws IOException, InterruptedException {
        final String response = apiGet("settings/platform");

        assertTrue(StringUtils.isNotBlank(response));
        JsonNode json = responseToJson(response);
        assertTrue("No settings listed", json.size() > 0);
    }

    @Test
    public void testMessageController() throws IOException, InterruptedException {
        statusMessageService.error(ERROR_MSG, MODULE_NAME, TIMEOUT);

        final String response = apiGet("messages");

        assertTrue(StringUtils.isNotBlank(response));
        JsonNode json = responseToJson(response);
        assertTrue("No messages listed", json.size() > 0);
    }

    private String apiGet(String path) throws IOException, InterruptedException {
        login();

        String processedPath = (path.startsWith("/")) ? path.substring(1) : path;

        return getHttpClient().execute(new HttpGet(String.format("http://localhost:%d/admin/api/", TestContext.getJettyPort())
                + processedPath), new BasicResponseHandler());
    }

    private void login() throws IOException, InterruptedException {
        final HttpPost loginPost = new HttpPost(
                String.format("http://localhost:%d/server/motech-platform-server/j_spring_security_check", TestContext.getJettyPort()));

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", "motech"));
        nvps.add(new BasicNameValuePair("j_password", "motech"));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        final HttpResponse response = getHttpClient().execute(loginPost);
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

    @After
    public void tearDown() throws Exception {
        List<StatusMessage> messages = statusMessageService.getAllMessages();

        for (StatusMessage msg : messages) {
            if (msg.getText().startsWith("test-") && TIMEOUT.equals(msg.getTimeout())) {
                statusMessageService.removeMessage(msg);
            }
        }
    }
}

