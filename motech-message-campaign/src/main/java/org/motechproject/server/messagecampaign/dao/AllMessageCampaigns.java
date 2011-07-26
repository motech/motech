package org.motechproject.server.messagecampaign.dao;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.messagecampaign.domain.Campaign;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;

import java.io.IOException;
import java.io.InputStream;

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
//            TODO
        }
        Gson gson = new Gson();
        Campaign campaign = gson.fromJson(jsonText, Campaign.class);
        return campaign;
    }
}
