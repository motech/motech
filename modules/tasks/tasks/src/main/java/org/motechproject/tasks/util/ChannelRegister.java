package org.motechproject.tasks.util;

import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.motechproject.tasks.service.ChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;

public class ChannelRegister implements OsgiServiceRegistrationListener {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelRegister.class);
    private Resource channelResource;

    public ChannelRegister(Resource channelResource) {
        this.channelResource = channelResource;
    }

    @Override
    public void registered(Object service, Map serviceProperties) throws IOException {
        if (service instanceof ChannelService) {
            ((ChannelService) service).registerChannel(channelResource.getInputStream());
            LOG.info("Channel registered");
        }
    }

    @Override
    public void unregistered(Object service, Map serviceProperties) {
        LOG.info("ChannelService unregistered");
    }
}
