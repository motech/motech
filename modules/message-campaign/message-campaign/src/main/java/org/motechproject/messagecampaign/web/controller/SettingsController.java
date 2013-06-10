package org.motechproject.messagecampaign.web.controller;

import com.google.gson.JsonParseException;
import org.apache.commons.io.IOUtils;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class SettingsController {

    @Autowired
    private MessageCampaignService messageCampaignService;

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    @Qualifier("messageCampaignSettings")
    private SettingsFacade settingsFacade;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @PreAuthorize("hasRole('manageCampaigns')")
    public void saveSettings(@ModelAttribute("messageCampaigns") MultipartFile messageCampaigns) throws BundleException, IOException {
        if (messageCampaigns == null || messageCampaigns.isEmpty()) {
            throw new IllegalArgumentException("No file specified");
        }
        String oldConfig = null;
        try {
            oldConfig = IOUtils.toString(platformSettingsService.getRawConfig(settingsFacade.getSymbolicName(),
                    MessageCampaignService.MESSAGE_CAMPAIGNS_JSON_FILENAME));

            platformSettingsService.saveRawConfig(settingsFacade.getSymbolicName(),
                    MessageCampaignService.MESSAGE_CAMPAIGNS_JSON_FILENAME,
                    messageCampaigns.getInputStream());

            messageCampaignService.loadCampaigns();
        } catch (JsonParseException e) {
            //revert to previous config
            platformSettingsService.saveRawConfig(settingsFacade.getSymbolicName(),
                    MessageCampaignService.MESSAGE_CAMPAIGNS_JSON_FILENAME,
                    new ByteArrayInputStream(oldConfig.getBytes()));
            throw new IllegalArgumentException("Invalid JSON file", e);
        }
    }

}

