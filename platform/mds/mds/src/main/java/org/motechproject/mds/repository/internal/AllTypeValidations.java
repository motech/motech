package org.motechproject.mds.repository.internal;

import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.repository.MotechDataRepository;
import org.springframework.stereotype.Repository;

/**
 * The <code>AllTypeValidations</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeValidation}.
 */
@Repository
public class AllTypeValidations extends MotechDataRepository<TypeValidation> {

    public AllTypeValidations() {
        super(TypeValidation.class);
    }

}
