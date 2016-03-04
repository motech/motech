package org.motechproject.mds.test.service.relationshipswithhistory;

import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.relationshipswithhistory.Language;
import org.motechproject.mds.util.Constants;

import java.util.List;

public interface LanguageDataService extends MotechDataService<Language> {

    @Lookup
    List<Language> findByDistrictName(@LookupField(name = "districts.name") String districtName);

    @Lookup
    List<Language> findByDistrictSerialNumber(@LookupField(name = "districts.serialNumber", customOperator = Constants.Operators.EQ) Long serialNumber);

    @Lookup
    List<Language> findByDistrictNameAndSerialNumber(@LookupField(name = "districts.name", customOperator = Constants.Operators.STARTS_WITH) String districtName,
                                                     @LookupField(name = "districts.serialNumber") Range<Long> serialNumber);
}
