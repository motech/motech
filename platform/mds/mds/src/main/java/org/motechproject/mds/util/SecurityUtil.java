package org.motechproject.mds.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>SecurityUtil</code> class provides helper methods
 * to retrieve logged user details, such as username or roles
 */
public final class SecurityUtil {

    private SecurityUtil() {

    }

    public static String getUsername() {
        String username = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            User user = (User) auth.getPrincipal();
            if (user != null) {
                username = user.getUsername();
            }
        }

        return username;
    }

    public static List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = new ArrayList<>();

        if (null != authentication) {
            Object details = authentication.getDetails();
            Object value = PropertyUtil.safeGetProperty(details, "roles");

            if (value instanceof Collection) {
                roles = (List<String>) value;
            }
        }

        return roles;
    }

}
