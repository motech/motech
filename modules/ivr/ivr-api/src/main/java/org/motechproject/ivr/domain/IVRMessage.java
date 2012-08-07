package org.motechproject.ivr.domain;

public interface IVRMessage {

    String getText(String key);

    String getWav(String key, String preferredLangCode);
}
