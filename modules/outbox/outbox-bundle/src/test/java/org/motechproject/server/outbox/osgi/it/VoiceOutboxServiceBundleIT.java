package org.motechproject.server.outbox.osgi.it;

import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.UUID;

public class VoiceOutboxServiceBundleIT extends BaseOsgiIT {

    private String HOST = "localhost";
    private int PORT = 8080;

    public void testVoiceOutboxService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(VoiceOutboxService.class.getName());
        assertNotNull(serviceReference);
        VoiceOutboxService voiceOutboxService = (VoiceOutboxService) bundleContext.getService(serviceReference);
        assertNotNull(voiceOutboxService);

        final String externalId = "VoiceOutboxServiceBundleIT-" + UUID.randomUUID();
        String messageId = null;
        try {
            OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
            outboundVoiceMessage.setExternalId(externalId);
            voiceOutboxService.addMessage(outboundVoiceMessage);
            messageId = outboundVoiceMessage.getId();
            OutboundVoiceMessage message = voiceOutboxService.getMessageById(messageId);
            assertNotNull(message);
            assertEquals(externalId, message.getExternalId());
        } finally {
            voiceOutboxService.removeMessage(messageId);
        }
    }

    public void testController() throws IOException, InterruptedException {
        final String response = executeHttpCall(HOST,PORT,"/outbox/vxml/outboxMessage?pId=123", new BasicResponseHandler());
        assertTrue(response.contains("There are no pending messages in your outbox"));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testOutboxBundleContext.xml"};
    }
}
