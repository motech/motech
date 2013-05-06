package org.motechproject.tasks.service;

import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.domain.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ChannelService {

    void registerChannel(InputStream stream);

    void addOrUpdate(Channel channel);

    List<Channel> getAllChannels();

    Channel getChannel(String moduleName);

    BundleIcon getChannelIcon(String moduleName) throws IOException;

}
