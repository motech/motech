package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.mds.channel.Channel;

/**
 * Data service for channels.
 */
public interface ChannelsDataService extends MotechDataService<Channel> {

    /**
     * Returns the list of channels for the module with given name.
     *
     * @param moduleName  the name of the module, null returns null
     * @return the list of matching activities
     */
    @Lookup
    Channel findByModuleName(@LookupField(name = "moduleName") String moduleName);

    /**
     * Return the number of channels provided by the module with the given {@code moduleName}.
     *
     * @param moduleName  the name of the module
     */
    long countFindByModuleName(@LookupField(name = "moduleName") String moduleName);
}
