package org.motechproject.mobileforms.api.utils;

public interface PasswordEncoder {
    String sha(String pass, String salt);
}
