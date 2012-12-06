package org.motechproject.security.authentication;

import org.motechproject.security.domain.MotechRole;
import org.motechproject.security.domain.MotechUser;
import org.motechproject.security.domain.MotechUserCouchdbImpl;
import org.motechproject.security.repository.AllMotechRoles;
import org.motechproject.security.repository.AllMotechUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
                roles.add("Admin User");
            }
            user = new MotechUserCouchdbImpl(getAttribute(token.getAttributes(), "Email"), "", "", "", roles, token.getName());
            allMotechUsers.addOpenIdUser(user);
        }

        return new User(user.getUserName(), user.getPassword(), user.isActive(), true, true, true, getAuthorities(user.getRoles()));
    }

    private List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            MotechRole motechRole = allMotechRoles.findByRoleName(role);
            for (String permission : motechRole.getPermissionNames()) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }
        return authorities;
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
