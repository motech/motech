package org.motechproject.mobileforms.api.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class Encoder {
    public String sha(String pass, String salt) {
        return rawToHex(DigestUtils.sha(pass + salt));
    }

    // retaining the below function with bug (leading zero missing for single digit hex) to be consistent with the mobile client code
    // can be replaced with Hex.encodeHex once the mobile client code is fixed
    private String rawToHex(byte[] b) {
        if (b == null || b.length < 1) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        final int mask = 0xFF;
        for (byte aB : b) {
            s.append(Integer.toHexString(aB & mask));
        }
        return s.toString();
    }
}

