package org.motechproject.ivr.kookoo;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRResponseBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class KookooIVRResponseBuilder implements IVRResponseBuilder {
    private boolean isHangUp;
    private boolean collectDtmf;
    private int dtmfLength;
    private List<String> playTexts = new ArrayList<String>();
    private List<String> playAudios = new ArrayList<String>();
    private String nextUrl;

    public KookooIVRResponseBuilder() {
    }

    @Override
	public KookooIVRResponseBuilder withPlayTexts(String... playTexts) {
        for (String playText : playTexts)
            this.playTexts.add(playText);
        return this;
    }

    @Override
	public KookooIVRResponseBuilder withPlayAudios(String... playAudios) {
        for (String playAudio : playAudios)
            this.playAudios.add(playAudio);
        return this;
    }

    public KookooIVRResponseBuilder collectDtmf() {
        collectDtmf = true;
        return this;
    }

    @Override
	public KookooIVRResponseBuilder collectDtmf(Integer dtmfLength) {
        collectDtmf = true;
        this.dtmfLength = dtmfLength;
        return this;
    }

    @Override
	public KookooIVRResponseBuilder withHangUp() {
        this.isHangUp = true;
        return this;
    }

    @Override
    public IVRResponseBuilder withNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
        return this;
    }

    @Override
    public String createWithDefaultLanguage(IVRMessage ivrMessage, String sessionId) {
    	return create(ivrMessage, sessionId, "en");
    }
    
    @Override
	public String create(IVRMessage ivrMessage, String sid, String preferredLangCode) {
        Response response = KookooResponseFactory.create();
        if (StringUtils.isNotBlank(sid)) response.setSid(sid);

        if (collectDtmf) {
            CollectDtmf collectDtmf = KookooCollectDtmfFactory.create();
            if(dtmfLength > 0) collectDtmf.setMaxDigits(dtmfLength);
            for (String playText : playTexts) collectDtmf.addPlayText(ivrMessage.getText(playText));
            for (String playAudio : playAudios) collectDtmf.addPlayAudio(ivrMessage.getWav(playAudio, preferredLangCode));

            response.addCollectDtmf(collectDtmf);
        } else {
            for (String playText : playTexts) response.addPlayText(ivrMessage.getText(playText));
            for (String playAudio : playAudios) response.addPlayAudio(ivrMessage.getWav(playAudio, preferredLangCode));
        }

        if (StringUtils.isNotEmpty(nextUrl)) {
            response.addGotoNEXTURL(nextUrl);
        }
        
        if (isHangUp) response.addHangup();
        return response.getXML();
    }

    @Override
	public boolean isHangUp() {
        return isHangUp;
    }

    @Override
	public boolean isCollectDtmf() {
        return collectDtmf;
    }

    @Override
	public List<String> getPlayTexts() {
        return playTexts;
    }

    @Override
	public List<String> getPlayAudios() {
        return playAudios;
    }
}
