package org.motechproject.security.service;

import org.motechproject.security.domain.MotechUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Service interface to retrieve authorities (permissions) for a given {@link MotechUser}
 */
public interface AuthoritiesService {

    /**
     * Gets list of {@link org.springframework.security.core.GrantedAuthority}
     * for given user
     *
     * @param user for whom we want to get list
     * @return list that contains {@link org.springframework.security.core.GrantedAuthority}
     */
    List<GrantedAuthority> authoritiesFor(MotechUser user);
}
