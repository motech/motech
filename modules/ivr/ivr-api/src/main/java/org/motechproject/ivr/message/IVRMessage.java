package org.motechproject.ivr.message;

public interface IVRMessage {

    String getText(String key);

    String getWav(String key, String preferredLangCode);
}
