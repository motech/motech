package org.motechproject.security.authentication;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserCouchdbImpl;
import org.motechproject.security.helper.SecurityHelper;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import java.util.ArrayList;
import java.util.List;

public class MotechOpenIdUserDetailsService implements AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

    @Autowired
    private AllMotechRoles allMotechRoles;

    @Autowired
    private AllMotechUsers allMotechUsers;

    @Override
    public UserDetails loadUserDetails(OpenIDAuthenticationToken token) throws UsernameNotFoundException {
        MotechUser user = allMotechUsers.findUserByOpenId(token.getName());
        if (user == null) {
            List<String> roles = new ArrayList<String>();
            if (allMotechUsers.getOpenIdUsers().isEmpty()) {
                for (MotechRole role : allMotechRoles.getRoles()) {
                    roles.add(role.getRoleName());
                }
            }
            user = new MotechUserCouchdbImpl(getAttribute(token.getAttributes(), "Email"), "", getAttribute(token.getAttributes(), "Email"), "", roles, token.getName());
            allMotechUsers.addOpenIdUser(user);
        }

        return new User(user.getUserName(), user.getPassword(), user.isActive(), true, true, true, SecurityHelper.getAuthorities(user.getRoles(), allMotechRoles));
    }

    private String getAttribute(List<OpenIDAttribute> attributes, String attributeName) {
        String attributeValue = "";
        for (OpenIDAttribute attribute : attributes) {
            if (attribute.getName() != null && (attribute.getName().equals("ax" + attributeName) || attribute.getName().equals("ae" + attributeName))) {
                attributeValue = attribute.getValues().get(0);
            }
        }
        return attributeValue;
    }
}
