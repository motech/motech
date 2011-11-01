package org.motechproject.mobileforms.api.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderImpl implements PasswordEncoder{

    public String sha(String pass, String salt){
        return new String(Hex.encodeHex(DigestUtils.sha(pass + salt)));
    }
}
