package org.motechproject.security.service.authentication;

import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.UserStatus;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.repository.MotechUsersDao;
import org.motechproject.security.service.AuthoritiesService;
import org.motechproject.security.service.MotechRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation class for @AuthenticationUserDetailsService. Retrieves user details given OpenId
 * authentication
 */
public class MotechOpenIdUserDetailsService implements AuthenticationUserDetailsService<OpenIDAuthenticationToken> {
    private MotechRoleService motechRoleService;
    private MotechUsersDao motechUsersDao;
    private AuthoritiesService authoritiesService;

    /**
     * Adds user for given OpenId to {@link MotechUsersDao}
     * and return his {@link org.springframework.security.core.userdetails.UserDetails}
     *
     * @param token for OpenId
     * @return details of added user
     */
    @Override
    @Transactional
    public UserDetails loadUserDetails(OpenIDAuthenticationToken token) {
        MotechUser user = motechUsersDao.findUserByOpenId(token.getName());
        if (user == null) {
            List<String> roles = new ArrayList<>();
            if (motechUsersDao.getOpenIdUsers().isEmpty()) {
                for (RoleDto role : motechRoleService.getRoles()) {
                    roles.add(role.getRoleName());
                }
            }
            user = new MotechUser(getAttribute(token.getAttributes(), "Email"), "", getAttribute(token.getAttributes(), "Email"), "", roles, token.getName(), Locale.getDefault());
            motechUsersDao.addOpenIdUser(user);
        }

        return new User(user.getUserName(), user.getPassword(), user.isActive(), true, !UserStatus.MUST_CHANGE_PASSWORD.equals(user.getUserStatus()),
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
    public void setMotechRoleService(MotechRoleService motechRoleService) {
        this.motechRoleService = motechRoleService;
    }

    @Autowired
    public void setMotechUsersDao(MotechUsersDao motechUsersDao) {
        this.motechUsersDao = motechUsersDao;
    }

    @Autowired
    public void setAuthoritiesService(AuthoritiesService authoritiesService) {
        this.authoritiesService = authoritiesService;
    }
}
