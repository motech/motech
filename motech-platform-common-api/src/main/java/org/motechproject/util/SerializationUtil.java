package org.motechproject.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;

public final class SerializationUtil {
    private static Base64 codec = new Base64();

    private SerializationUtil() {

    }

    public static String toString(Serializable obj) {
        if (obj == null) {
            return null;
        }
        byte[] bytes = SerializationUtils.serialize(obj);
        return new String(codec.encode(bytes));
    }

    public static Object toObject(String str) {
        return SerializationUtils.deserialize(codec.decode(str.getBytes()));
    }
}
