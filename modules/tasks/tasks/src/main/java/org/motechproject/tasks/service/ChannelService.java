package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Channel;

import java.io.InputStream;
import java.util.List;

public interface ChannelService {

    void registerChannel(InputStream stream);

    List<Channel> getAllChannels();

    Channel getChannel(String displayName, String moduleName, String moduleVersion);
}
