package org.motechproject.mds.repository;

import org.motechproject.mds.domain.UserPreferences;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllUserPreferences extends MotechDataRepository<UserPreferences> {

    protected AllUserPreferences() {
        super(UserPreferences.class);
    }

    public UserPreferences retrieveByClassNameAndUsername(String entityClassName, String username) {
        return retrieve(new String[] {"className", "username"}, new Object[] {entityClassName, username});
    }

    public List<UserPreferences> retrieveByClassName(String entityClassName) {
        return retrieveAll("className", entityClassName);
    }
}
