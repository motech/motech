package org.motechproject.mds.repository.internal;

import org.motechproject.mds.domain.Type;
import org.motechproject.mds.repository.MotechDataRepository;
import org.springframework.stereotype.Repository;

/**
 * The <code>AllTypes</code> repository class allows persistence and retrieving of Field Types
 * in Data Services database.
 */
@Repository
public class AllTypes extends MotechDataRepository<Type> {

    public AllTypes() {
        super(Type.class);
    }

    public Type retrieveByClassName(String className) {
        return retrieve("typeClass", className);
    }

    public Type retrieveByDisplayName(String displayName) {
        return retrieve("displayName", displayName);
    }

}
