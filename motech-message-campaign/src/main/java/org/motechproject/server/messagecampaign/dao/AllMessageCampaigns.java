package org.motechproject.server.messagecampaign.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class AllMessageCampaigns {
    private String definitionFile;

    public AllMessageCampaigns() {
        this(System.getProperty("messagecampaign.definition.file"));
    }

    AllMessageCampaigns(String definitionFile) {
        this.definitionFile = definitionFile;
        if (definitionFile == null) throw new MessageCampaignException();
    }

    public Campaign get(String name) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(definitionFile);
        String jsonText = null;
        try {
            jsonText = IOUtils.toString(inputStream);
        } catch (IOException e) {
            LoggerFactory.getLogger(AllMessageCampaigns.class).error("Error reading message campaign definitions - " + e.getMessage());
            return null;
        }
        Type campaignListType = new TypeToken<List<Campaign>>() {}.getType();
        List<Campaign> campaigns = new Gson().fromJson(jsonText, campaignListType);

        for (Campaign campaign:campaigns) {
            if(campaign.getName().equals(name)) return campaign;
        }
        return null;
    }
}
