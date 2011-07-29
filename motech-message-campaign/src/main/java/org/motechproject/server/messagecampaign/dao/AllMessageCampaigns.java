package org.motechproject.server.messagecampaign.dao;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.server.messagecampaign.builder.CampaignBuilder;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Properties;

public class AllMessageCampaigns {

    public static final String MESSAGECAMPAIGN_DEFINITION_FILE = "messagecampaign.definition.file";

    @Qualifier("messageCampaignProperties")
    private Properties properties;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public AllMessageCampaigns(Properties properties, MotechJsonReader motechJsonReader) {
        this.properties = properties;
        this.motechJsonReader = motechJsonReader;
    }

    public Campaign get(String name) {
        List<CampaignBuilder> campaigns =
                (List<CampaignBuilder>) motechJsonReader.readFromFile(definitionFile(),
                new TypeToken<List<CampaignBuilder>>() {}.getType());

        for (CampaignBuilder campaign : campaigns) {
            if (campaign.name().equals(name)) return campaign.build();
        }
        return null;
    }

    private String definitionFile() {
        return this.properties.getProperty(MESSAGECAMPAIGN_DEFINITION_FILE);
    }
}