package org.motechproject.tasks.util;

import org.motechproject.tasks.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class ChannelRegister {
    private Resource channelResource;

    public ChannelRegister(Resource channelResource) {
        this.channelResource = channelResource;
    }

    @Autowired
    public void setChannelService(ChannelService channelService) throws IOException {
        register(channelService);
    }

    private void register(final ChannelService channelService) throws IOException {
        if (channelService != null && channelResource != null) {
            channelService.registerChannel(channelResource.getInputStream());
        }
    }
}
