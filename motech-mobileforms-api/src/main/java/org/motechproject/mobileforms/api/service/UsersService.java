package org.motechproject.mobileforms.api.service;

import java.util.List;
/**
 * \ingroup MobileForms
 */

/**
 * User service, allows list user along with one way hashed password.
 */

public interface UsersService {
    /**
     * Get list for users in the format compatible for xforms serialization
     * @return List of users, each user detail represented by Object array,
     * in the format new Object[]{1, "userName", SHA1_for_password_with_salt, "salt"}
     */
    List<Object[]> getUsers();
}
