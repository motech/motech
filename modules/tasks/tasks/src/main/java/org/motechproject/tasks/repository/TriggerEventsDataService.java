package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.TriggerEvent;

import java.util.List;

public interface TriggerEventsDataService extends MotechDataService<TriggerEvent> {

    String MODULE_NAME = "channel.moduleName";

    @Lookup
    List<TriggerEvent> byChannelModuleName(@LookupField(name = MODULE_NAME) String moduleName,
                                           QueryParams queryParams);

    @Lookup
    TriggerEvent byChannelModuleNameAndListenerSubject(@LookupField(name = MODULE_NAME) String moduleName,
                                                       @LookupField(name = "triggerListenerSubject") String triggerListenerSubject);

    @Lookup
    TriggerEvent bySubject(@LookupField(name = "triggerListenerSubject") String subject);

    long countByChannelModuleName(@LookupField(name = MODULE_NAME) String moduleName);

    long countByChannelModuleNameAndListenerSubject(@LookupField(name = MODULE_NAME) String moduleName,
                                                    @LookupField(name = "triggerListenerSubject") String subject);
}
