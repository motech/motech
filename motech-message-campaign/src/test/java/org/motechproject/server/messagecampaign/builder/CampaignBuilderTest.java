package org.motechproject.server.messagecampaign.builder;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CampaignBuilderTest {

    private CampaignBuilder campaignBuilder;
    private CampaignMessageBuilder messageBuilder;

    @Before
    public void setUp() {
        createCampaign("Campaign 1");
    }

    private void createCampaign(String name) {
        campaignBuilder = new CampaignBuilder();
        campaignBuilder.name(name);

        ArrayList<String> formats = new ArrayList<String>() {{
            add("SMS");
            add("IVR");
        }};
        ArrayList<String> languages = new ArrayList<String>() {{
            add("en");
            add("gh");
        }};
        createCampaignMessageBuilder("Message 1", "message-key", formats, languages);
    }

    private void createCampaignMessageBuilder(String name, String messageKey, ArrayList<String> formats, ArrayList<String> languages) {
        messageBuilder = new CampaignMessageBuilder();
        messageBuilder.name(name);
        messageBuilder.formats(formats);
        messageBuilder.languages(languages);
        messageBuilder.messageKey(messageKey);
        messageBuilder.date(new Date());

        campaignBuilder.messages(new ArrayList<CampaignMessageBuilder>() {{
            add(messageBuilder);
        }});
    }

    @Test
    public void testBuildAbsoluteCampaign() {
        AbsoluteCampaign campaign = (AbsoluteCampaign) campaignBuilder.build();
        assertTrue(campaign instanceof AbsoluteCampaign);
        assertEquals(campaignBuilder.name(), campaign.name());
        assertEquals(CampaignType.ABSOLUTE, campaign.type());
        List<AbsoluteCampaignMessage> messages = campaign.messages();
        assertEquals(1, messages.size());

        AbsoluteCampaignMessage message = messages.get(0);
        assertEquals(messageBuilder.name(), message.name());
        assertEquals(messageBuilder.formats(), message.formats());
        assertEquals(messageBuilder.languages(), message.languages());
        assertEquals(messageBuilder.messageKey(), message.messageKey());
        assertEquals(messageBuilder.date(), message.date());
    }

    @Test
    public void testBuildOffsetCampaign() {
        OffsetCampaign campaign = (OffsetCampaign) campaignBuilder.build();
        assertTrue(campaign instanceof OffsetCampaign);
        assertEquals(campaignBuilder.name(), campaign.name());
        assertEquals(CampaignType.OFFSET, campaign.type());
        List<OffsetCampaignMessage> messages = campaign.messages();
        assertEquals(1, messages.size());

        OffsetCampaignMessage message = messages.get(0);
        assertEquals(messageBuilder.name(), message.name());
        assertEquals(messageBuilder.formats(), message.formats());
        assertEquals(messageBuilder.languages(), message.languages());
        assertEquals(messageBuilder.messageKey(), message.messageKey());
        assertEquals(messageBuilder.timeOffset(), message.timeOffset());

    }
}
