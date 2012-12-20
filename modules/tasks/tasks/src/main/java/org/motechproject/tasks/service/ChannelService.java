package org.motechproject.tasks.service;

import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.domain.Channel;

import java.io.InputStream;
import java.util.List;

public interface ChannelService {

    void registerChannel(InputStream stream);

    List<Channel> getAllChannels();

    Channel getChannel(String displayName, String moduleName, String moduleVersion);

    BundleIcon getChannelIcon(String moduleName, String version);

}
