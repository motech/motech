package org.motechproject.tasks.service;

import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.domain.mds.channel.Channel;

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
     * Unregisters the given channel with the task module.
     *
     * @param moduleName , not null
     */
    void unregisterChannel(String moduleName);

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
     * Deletes the given module.
     *
     * @param moduleName  the channel to be deleted
     */
    void delete(final String moduleName);

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
     * Checks whether the given module provides a task channel.
     *
     * @param moduleName  the name of the module
     * @return true if the module provides the task channel, false otherwise
     */
    boolean channelExists(String moduleName);
}
