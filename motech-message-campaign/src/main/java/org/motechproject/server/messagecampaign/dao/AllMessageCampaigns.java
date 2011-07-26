package org.motechproject.server.messagecampaign.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AllMessageCampaigns {

    public static final String MESSAGECAMPAIGN_DEFINITION_FILE = "messagecampaign.definition.file";

    @Qualifier("messageCampaignProperties")
    private Properties properties;

    @Autowired
    public AllMessageCampaigns(Properties properties) {
        this.properties = properties;
    }

    public Campaign get(String name) {
        List<Campaign> campaigns = getAll();

        for (Campaign campaign : campaigns) {
            if (campaign.getName().equals(name)) return campaign;
        }
        return null;
    }

    private List<Campaign> getAll() {
        String jsonText = getJSON();
        return getFromJSON(jsonText);
    }

    private List<Campaign> getFromJSON(String jsonText) {
        Type campaignListType = new TypeToken<List<Campaign>>() {}.getType();

        List<Campaign> allCampaigns = (new Gson()).fromJson(jsonText, campaignListType);

        if(allCampaigns == null) return new ArrayList<Campaign>();

        return allCampaigns;
    }

    private String getJSON() {
        String jsonText = "";

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(definitionFile());
        if (inputStream == null) return jsonText;

        try {
            jsonText = IOUtils.toString(inputStream);
        } catch (IOException e) {
            LoggerFactory.getLogger(AllMessageCampaigns.class).error("Error reading message campaign definitions - " + e.getMessage());
        }
        return jsonText;
    }

    private String definitionFile() {
        return this.properties.getProperty(MESSAGECAMPAIGN_DEFINITION_FILE);
    }
}
