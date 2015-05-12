package org.motechproject.security.authentication;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.constants.SecurityConfigConstants;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * A custom AccessDecisionVoter for voting on whether
 * a specific user has access to a particular URL. For example,
 * a security rule can specify that the users motech and admin
 * have access to a particular URL. This loads the metadata source
 * with attributes for ACCESS_motech and ACCESS_admin. When a user
 * invokes that URL, an affirmative based voting system will check
 * whether or not the user is motech or admin. If not, they are denied
 * permission, otherwise they are granted access.
 */
public class MotechAccessVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return StringUtils.startsWith(attribute.getAttribute(), SecurityConfigConstants.USER_ACCESS_PREFIX);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    /**
     * Checks if given user has access to given URL.
     * If authentication details are not instance of MotechUserProfile or
     * ConfigAttributes are empty then return ACCESS_ABSTAIN.
     * If attribute is supported but User is not allowed then return
     * ACCESS_DENIED, otherwise return ACCESS_GRANTED
     *
     * @param authentication to be used for check
     * @param attributes that contains information about access for users
     * @return ACCESS_ABSTAIN, ACCESS_DENIED or ACCESS_GRANTED
     */
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                result = ACCESS_DENIED;

                if (StringUtils.equalsIgnoreCase(SecurityConfigConstants.USER_ACCESS_PREFIX + authentication.getName(),
                        attribute.getAttribute())) {
                    return ACCESS_GRANTED;
                }
            }
        }

        return result;
    }
}
