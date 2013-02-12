package org.motechproject.messagecampaign.loader;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.messagecampaign.web.model.CampaignDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CampaignJsonLoader {

    private String messageCampaignsJsonFile = "message-campaigns.json";

    private SettingsFacade settings;

    private AllMessageCampaigns allMessageCampaigns;

    private MotechJsonReader motechJsonReader;

    public CampaignJsonLoader() {
        this(new MotechJsonReader());
    }

    public CampaignJsonLoader(MotechJsonReader motechJsonReader) {
        this.motechJsonReader = motechJsonReader;
    }

    public List<CampaignRecord> loadCampaigns(String filename) {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename)) {
            return loadCampaigns(in);
        } catch (IOException e) {
            throw new MotechException("Error while loading json file", e);
        }
    }

    public List<CampaignRecord> loadCampaigns(InputStream in) {
        List<CampaignDto> dtoList = (List<CampaignDto>) motechJsonReader.readFromStream(
                in, new TypeToken<List<CampaignDto>>() { } .getType()
        );

        List<CampaignRecord> records = new ArrayList<>(dtoList.size());
        for (CampaignDto dto : dtoList) {
            records.add(dto.toCampaignRecord());
        }

        return records;
    }

    public CampaignRecord loadSingleCampaign(InputStream in) {
        CampaignDto campaignDto = (CampaignDto) motechJsonReader.readFromStream(
                in, new TypeToken<CampaignDto>() { } .getType()
        );
        return campaignDto.toCampaignRecord();
    }

    public CampaignRecord loadSingleCampaign(String filename) {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename)) {
            return loadSingleCampaign(in);
        } catch (IOException e) {
            throw new MotechException("Error while loading json file", e);
        }
    }

    public void loadAferInit() {
        List<CampaignRecord> records = loadCampaigns(settings.getRawConfig(messageCampaignsJsonFile));
        for (CampaignRecord record : records) {
            allMessageCampaigns.saveOrUpdate(record);
        }
    }

    public String getMessageCampaignsJsonFile() {
        return messageCampaignsJsonFile;
    }

    public void setMessageCampaignsJsonFile(String messageCampaignsJsonFile) {
        this.messageCampaignsJsonFile = messageCampaignsJsonFile;
    }

    public void setSettings(SettingsFacade settings) {
        this.settings = settings;
    }

    public void setAllMessageCampaigns(AllMessageCampaigns allMessageCampaigns) {
        this.allMessageCampaigns = allMessageCampaigns;
    }
}
