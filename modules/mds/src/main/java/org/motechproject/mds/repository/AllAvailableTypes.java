package org.motechproject.mds.repository;

import org.motechproject.mds.domain.AvailableType;
import org.springframework.stereotype.Repository;

/**
 * The <code>AllAvailableTypes</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.AvailableType}.
 */
@Repository
public class AllAvailableTypes extends MotechDataRepository<AvailableType> {

    public AllAvailableTypes() {
        super(AvailableType.class);
    }

}
