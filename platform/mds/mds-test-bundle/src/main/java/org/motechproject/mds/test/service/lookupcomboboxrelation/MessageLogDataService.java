package org.motechproject.mds.test.service.lookupcomboboxrelation;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.lookupcomboboxrelation.LogAttribute;
import org.motechproject.mds.test.domain.lookupcomboboxrelation.LogStatus;
import org.motechproject.mds.test.domain.lookupcomboboxrelation.MessageLog;

import java.util.List;
import java.util.Set;

public interface MessageLogDataService extends MotechDataService<MessageLog> {

    @Lookup
    List<MessageLog> findByPatameterStatus(@LookupField(name = "mainParameter.paramStatus") LogStatus status);

    @Lookup
    List<MessageLog> findByPatameterStatusSet(@LookupField(name = "mainParameter.paramStatus") Set<LogStatus> status);

    @Lookup
    List<MessageLog> findByPatameterValue(@LookupField(name = "mainParameter.values") String value);

    @Lookup
    List<MessageLog> findByPatametersStatus(@LookupField(name = "parameters.paramStatus") LogStatus status);

    @Lookup
    List<MessageLog> findByPatametersAttributes(@LookupField(name = "parameters.logAttribute") LogAttribute attribute);

    @Lookup
    List<MessageLog> findByPatametersSetAttributes(@LookupField(name = "parameters.logAttribute") Set<LogAttribute> attribute);
}
