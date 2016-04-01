package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.List;

/**
 * Responsible for fetching and counting static triggers.
 */
public interface TriggerEventsDataService extends MotechDataService<TriggerEvent> {

    String MODULE_NAME = "channel.moduleName";
    String TRIGGER_SUBJECT = "subject";

    @Lookup
    List<TriggerEvent> byChannelModuleName(@LookupField(name = MODULE_NAME) String moduleName,
                                           QueryParams queryParams);

    @Lookup
    TriggerEvent byChannelModuleNameAndSubject(@LookupField(name = MODULE_NAME) String moduleName,
                                               @LookupField(name = TRIGGER_SUBJECT) String subject);

    @Lookup
    TriggerEvent bySubject(@LookupField(name = TRIGGER_SUBJECT) String subject);

    long countByChannelModuleName(@LookupField(name = MODULE_NAME) String moduleName);

    long countByChannelModuleNameAndSubject(@LookupField(name = MODULE_NAME) String moduleName,
                                            @LookupField(name = TRIGGER_SUBJECT) String subject);
}
