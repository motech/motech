package org.motechproject.mds.util;

import org.motechproject.security.domain.MotechUserProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
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
        List<String> roles = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            roles.addAll(((MotechUserProfile) authentication.getDetails()).getRoles());
        }

        return roles;
    }

}
