package org.motechproject.security.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Class responsible for password encoding
 */
@Component
@Qualifier("passwordEncoder")
public class MotechPasswordEncoder extends BCryptPasswordEncoder {

    /**
     * Encodes given password using BCrypt hashing
     *
     * @param rawPassword password to be encoded
     * @return encoded password
     */
    public String encodePassword(String rawPassword) {
        return super.encode(rawPassword);
    }

    /**
     * Encodes rawPassword and checks if it's the same
     * as encoded one
     *
     * @param encPassword encoded password
     * @param rawPassword not encoded password
     * @return true if passwords are the same, false otherwise
     */
    public boolean isPasswordValid(String encPassword, String rawPassword) {
        return super.matches(rawPassword, encPassword);
    }
}
