package org.motechproject.mds.test.service;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.Movie;

public interface MovieDataService extends MotechDataService<Movie> {
    @Lookup
    Movie findByName(@LookupField(name = "name") String name);
}
