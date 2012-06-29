package org.motechproject.security.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Qualifier("passwordEncoder")
public class MotechPasswordEncoder extends BCryptPasswordEncoder {

    public String encodePassword(String rawPassword) {
        return super.encode(rawPassword);
    }

    public boolean isPasswordValid(String encPassword, String rawPassword) {
        return super.matches(rawPassword, encPassword);
    }
}
