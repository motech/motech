package org.motechproject.admin.it;

import ch.lambdaj.Lambda;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.messages.Level;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class AdminBundleIT extends BasePaxIT {
    private static final String ERROR_MSG = "test-error";
    private static final String DEBUG_MSG = "test-debug";
    private static final String WARNING_MSG = "test-warn";
    private static final String MODULE_NAME = "test-module";
    private static final DateTime TIMEOUT = DateUtil.nowUTC().plusHours(1);
    private static final String HOST = "localhost";
    private static final int PORT = TestContext.getJettyPort();

    @Inject
    private StatusMessageService statusMessageService;

    @Inject
    private BundleContext bundleContext;

    @BeforeClass
    public static void beforeClass() throws Exception {
        createAdminUser();
        login();
    }

    @Before
    public void setUp() {
        setUpSecurityContextForDefaultUser("manageBundles", "manageMessages", "manageLogs", "manageActivemq", "manageSettings");
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

    @Test
    public void testUploadBundleFromRepository() throws IOException, InterruptedException {
        Bundle[] bundlesBeforeUpload, bundlesAfterUpload;

        String uri = String.format("http://%s:%d/admin/api/bundles/upload/", HOST, PORT);
        HttpPost httpPost = new HttpPost(uri);

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        Charset chars = Charset.forName("UTF-8");
        entity.setCharset(chars);
        entity.addTextBody("moduleSource", "Repository", ContentType.MULTIPART_FORM_DATA);
        entity.addTextBody("moduleId","org.motechproject:cms-lite:LATEST", ContentType.MULTIPART_FORM_DATA);
        entity.addTextBody("startBundle","on", ContentType.MULTIPART_FORM_DATA);
        httpPost.setEntity(entity.build());

        bundlesBeforeUpload = bundleContext.getBundles();
        HttpResponse response = getHttpClient().execute(httpPost);
        EntityUtils.consume(response.getEntity());

        bundlesAfterUpload = bundleContext.getBundles();

        assertTrue(bundlesAfterUpload.length == bundlesBeforeUpload.length+1);
    }

    private String apiGet(String path) throws IOException, InterruptedException {
        String processedPath = (path.startsWith("/")) ? path.substring(1) : path;

        return getHttpClient().execute(new HttpGet(String.format("http://localhost:%d/admin/api/", TestContext.getJettyPort())
                + processedPath), new BasicResponseHandler());
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

