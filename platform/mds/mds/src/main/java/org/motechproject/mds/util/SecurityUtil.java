package org.motechproject.mds.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The <code>SecurityUtil</code> class provides helper methods
 * to retrieve logged user details, such as username, roles or permissions
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * Retrieves current username from the Spring security context. Returns null, if there's no
     * active authentication object.
     *
     * @return username or null, if no user is authenticated
     */
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

    /**
     * Returns authenticated user's roles. It will return empty list, in case there's no active authentication
     * or if the current user does not have any roles assigned.
     *
     * @return list of roles assigned to the current user
     */
    public static List<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = new ArrayList<>();

        if (null != authentication) {
            Object details = authentication.getDetails();

            if (null != details) {
                // to avoid dependency cycle we try to get user roles by reflection
                Object value = PropertyUtil.safeGetProperty(details, "roles");

                if (value instanceof Collection) {
                    roles = (List<String>) value;
                }
            }
        }

        return roles;
    }

    /**
     * Returns authenticated user's permissions. It will return empty set, in case there's no active authentication
     * or if the current user does not have any permissions assigned.
     *
     * @return set of permissions assigned to the current user
     */
    public static Set<String> getUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> permissions = new HashSet<>();

        if (null != authentication) {
            for (GrantedAuthority permission : authentication.getAuthorities()) {
                permissions.add(permission.getAuthority());
            }
        }

        return permissions;
    }
}
