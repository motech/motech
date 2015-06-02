package org.motechproject.security.service.authentication;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.motechproject.security.service.AuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation class for @AuthenticationUserDetailsService. Retrieves user details given OpenId
 * authentication
 */
public class MotechOpenIdUserDetailsService implements AuthenticationUserDetailsService<OpenIDAuthenticationToken> {
    private AllMotechRoles allMotechRoles;
    private AllMotechUsers allMotechUsers;
    private AuthoritiesService authoritiesService;

    /**
     * Adds user for given OpenId to {@link org.motechproject.security.repository.AllMotechUsers}
     * and return his {@link org.springframework.security.core.userdetails.UserDetails}
     *
     * @param token for OpenId
     * @return details of added user
     */
    @Override
    public UserDetails loadUserDetails(OpenIDAuthenticationToken token) {
        MotechUser user = allMotechUsers.findUserByOpenId(token.getName());
        if (user == null) {
            List<String> roles = new ArrayList<String>();
            if (allMotechUsers.getOpenIdUsers().isEmpty()) {
                for (MotechRole role : allMotechRoles.getRoles()) {
                    roles.add(role.getRoleName());
                }
            }
            user = new MotechUser(getAttribute(token.getAttributes(), "Email"), "", getAttribute(token.getAttributes(), "Email"), "", roles, token.getName(), Locale.getDefault());
            allMotechUsers.addOpenIdUser(user);
        }

        return new User(user.getUserName(), user.getPassword(), user.isActive(), true, true,
                !UserStatus.BLOCKED.equals(user.getUserStatus()), authoritiesService.authoritiesFor(user));
    }

    /**
     * Looks for attribute with given name in given list
     *
     * @param attributes list of OpenId attributes
     * @param attributeName of attribute we're looking for
     * @return string with attribute value if attribute with given
     * name exists, otherwise return empty string
     */
    private String getAttribute(List<OpenIDAttribute> attributes, String attributeName) {
        String attributeValue = "";
        for (OpenIDAttribute attribute : attributes) {
            if (attribute.getName() != null && (attribute.getName().equals("ax" + attributeName) || attribute.getName().equals("ae" + attributeName))) {
                attributeValue = attribute.getValues().get(0);
            }
        }
        return attributeValue;
    }

    @Autowired
    public void setAllMotechRoles(AllMotechRoles allMotechRoles) {
        this.allMotechRoles = allMotechRoles;
    }

    @Autowired
    public void setAllMotechUsers(AllMotechUsers allMotechUsers) {
        this.allMotechUsers = allMotechUsers;
    }

    @Autowired
    public void setAuthoritiesService(AuthoritiesService authoritiesService) {
        this.authoritiesService = authoritiesService;
    }
}
