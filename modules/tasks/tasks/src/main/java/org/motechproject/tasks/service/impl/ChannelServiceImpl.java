package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.commons.couchdb.dao.BusinessIdNotUniqueException;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.service.ChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

@Service("channelService")
public class ChannelServiceImpl implements ChannelService {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelServiceImpl.class);

    private AllChannels allChannels;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public ChannelServiceImpl(final AllChannels allChannels) {
        this.allChannels = allChannels;
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public void registerChannel(final InputStream stream) {
        Type type = new TypeToken<Channel>() {
        }.getType();
        Channel channel = (Channel) motechJsonReader.readFromStream(stream, type);
        LOG.debug("Read channel definition from json file.");

        try {
            allChannels.addOrUpdate(channel);
            LOG.info(String.format("Saved channel: %s", channel.getDisplayName()));
        } catch (BusinessIdNotUniqueException e) {
            LOG.error("Cant save channel: ", e);
        }
    }

    @Override
    public List<Channel> getAllChannels() {
        return allChannels.getAll();
    }

    @Override
    public Channel getChannel(final String displayName, final String moduleName, final String moduleVersion) {
        return allChannels.byChannelInfo(displayName, moduleName, moduleVersion);
    }
}
