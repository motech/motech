package org.motechproject.mds.repository;

import org.motechproject.mds.domain.TypeSetting;
import org.springframework.stereotype.Repository;

/**
 * The <code>AllTypeSettings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeSetting}.
 */
@Repository
public class AllTypeSettings extends MotechDataRepository<TypeSetting> {

    public AllTypeSettings() {
        super(TypeSetting.class);
    }

}
