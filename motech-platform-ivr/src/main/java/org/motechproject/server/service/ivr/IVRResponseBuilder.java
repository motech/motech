package org.motechproject.server.service.ivr;

import java.util.List;


public interface IVRResponseBuilder {

    public abstract IVRResponseBuilder withPlayTexts(String... playTexts);

    public abstract IVRResponseBuilder withPlayAudios(String... playAudios);

    public abstract IVRResponseBuilder collectDtmf(Integer dtmfLength);

    public abstract IVRResponseBuilder withHangUp();

    IVRResponseBuilder withNextUrl(String nextUrl);

    public abstract String create(IVRMessage ivrMessage, String sessionId, String languageCode);

    public abstract String createWithDefaultLanguage(IVRMessage ivrMessage, String sessionId);

    public abstract boolean isHangUp();

    public abstract boolean isCollectDtmf();

    public abstract List<String> getPlayTexts();

    public abstract List<String> getPlayAudios();

}