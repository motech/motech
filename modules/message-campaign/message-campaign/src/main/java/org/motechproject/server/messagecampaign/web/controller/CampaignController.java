package org.motechproject.server.messagecampaign.web.controller;

import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.server.messagecampaign.web.model.CampaignDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "web-api")
public class CampaignController {

    private static final String HAS_MANAGE_CAMPAIGNS_ROLE = "hasRole('manageCampaigns')";

    @Autowired
    private MessageCampaignService messageCampaignService;

    @RequestMapping(value = "/campaigns/{campaignName}", method = RequestMethod.GET)
    @PreAuthorize(HAS_MANAGE_CAMPAIGNS_ROLE)
    public @ResponseBody CampaignDto getCampaign(@PathVariable String campaignName) {
        CampaignRecord campaignRecord = messageCampaignService.getCampaignRecord(campaignName);

        if (campaignRecord == null) {
            throw new CampaignNotFoundException("Campaign not found: " + campaignName);
        }

        return new CampaignDto(campaignRecord);
    }

    @RequestMapping(value = "/campaigns", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(HAS_MANAGE_CAMPAIGNS_ROLE)
    public void createCampaign(@RequestBody CampaignDto campaign) {
        CampaignRecord campaignRecord = campaign.toCampaignRecord();
        messageCampaignService.saveCampaign(campaignRecord);
    }

    @RequestMapping(value = "/campaigns", method = RequestMethod.GET)
    @PreAuthorize(HAS_MANAGE_CAMPAIGNS_ROLE)
    public @ResponseBody List<CampaignDto> getAllCampaigns() {
        List<CampaignRecord> campaignRecords = messageCampaignService.getAllCampaignRecords();

        List<CampaignDto> campaignDtos = new ArrayList<>();
        for (CampaignRecord record : campaignRecords) {
            campaignDtos.add(new CampaignDto(record));
        }

        return campaignDtos;
    }

    @RequestMapping(value = "/campaigns/{campaignName}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(HAS_MANAGE_CAMPAIGNS_ROLE)
    public void deleteCampaign(@PathVariable String campaignName) {
        messageCampaignService.deleteCampaign(campaignName);
    }

    @ExceptionHandler(CampaignNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody String handleException(Exception e) {
        return e.getMessage();
    }
}
