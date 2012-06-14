package org.motechproject.security.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Qualifier("passwordEncoder")
public class MotechPasswordEncoder extends Md5PasswordEncoder {

    @Value("${password.salt}")
    private String salt;

    public String encodePassword(String rawPassword) {
        return super.encodePassword(rawPassword, salt);
    }

    public boolean isPasswordValid(String encPassword, String rawPassword) {
        return super.isPasswordValid(encPassword, rawPassword, salt);
    }
}
