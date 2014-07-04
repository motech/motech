package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.Channel;

public interface ChannelsDataService extends MotechDataService<Channel> {

    @Lookup
    Channel findByModuleName(@LookupField(name = "moduleName") String moduleName);

}
