package org.motechproject.tasks.service;

import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.domain.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Manages CRUD operations for a {@link Channel}.
 */
public interface ChannelService {

    /**
     * Registers the given channel with the task module.
     *
     * @param channelRequest  the channel request, not null
     */
    void registerChannel(ChannelRequest channelRequest);

    /**
     * Registers channel from the given stream for the given module. The input stream should contain the JSON definition
     * of the channel.
     *
     * @param stream  the channel JSON definition as a stream, not null
     * @param moduleName  the name of the module
     * @param moduleVersion  the version of the module
     */
    void registerChannel(InputStream stream, String moduleName, String moduleVersion);

    /**
     * Saves the given channel. If the channel exists it will be updated.
     *
     * @param channel  the channel to be added, not null
     */
    void addOrUpdate(final Channel channel);

    /**
     * Returns the list of all registered channels.
     *
     * @return  the list of channels
     */
    List<Channel> getAllChannels();

    /**
     * Returns the channel for the module with the given name.
     *
     * @param moduleName  the name of the module, null returns null
     * @return  the channel for the module
     */
    Channel getChannel(String moduleName);

    /**
     * Returns the icon for the channel from module with the given name.
     *
     * @param moduleName  the name of the module, null returns default icon
     * @return  the icon of the module
     * @throws IOException  when there were problems while fetching the icon
     */
    BundleIcon getChannelIcon(String moduleName) throws IOException;
}
