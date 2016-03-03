package org.motechproject.mds.test.service.relationshipswithhistory;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.relationshipswithhistory.District;
import org.motechproject.mds.util.Constants;

import java.util.List;
import java.util.Set;

public interface DistrictDataService extends MotechDataService<District> {

    String LANGUAGE_NAME = "language.name";

    @Lookup
    District findByName(@LookupField(name = "name") String name);

    @Lookup
    List<District> findByLanguage(@LookupField(name = LANGUAGE_NAME) String languageName);

    @Lookup
    List<District> findByNameAndLanguage(@LookupField(name = "name") String name,
                                             @LookupField(name = LANGUAGE_NAME) String languageName);

    @Lookup
    List<District> findByNameAndLanguageWithOperators(@LookupField(name = "name", customOperator = Constants.Operators.STARTS_WITH) String name,
                                                      @LookupField(name = LANGUAGE_NAME, customOperator = Constants.Operators.ENDS_WITH) String languageName);

    @Lookup
    List<District> findByNameLanguageAndState(@LookupField(name = LANGUAGE_NAME) Set<String> languageNames,
                                              @LookupField(name = "state.name") String stateName);

}
