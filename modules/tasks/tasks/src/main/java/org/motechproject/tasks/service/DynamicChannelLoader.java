package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.TaskTriggerInformation;
import org.motechproject.tasks.domain.TriggerEvent;

import java.util.List;

public interface DynamicChannelLoader {

    List<TriggerEvent> getDynamicTriggers(String channelModule, int page, int pageSize);

    boolean hasDynamicTriggers(String moduleName);

    Long countByChannelModuleName(String moduleName);

    TriggerEvent getTrigger(TaskTriggerInformation triggerInformation);

    boolean channelExists(String moduleName);

    boolean isValidTrigger(String moduleName, String subject);
}
