package org.motechproject.config.service;

import org.motechproject.mds.service.MotechDataService;
import org.motechproject.config.domain.SettingsRecord;

/**
 * Interface for settings service. Its implementation is injected by the MDS.
 */
public interface SettingService extends MotechDataService<SettingsRecord> {

}
