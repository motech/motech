package org.motechproject.ivr.kookoo;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.service.ivr.IVRMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KookooIVRResponseBuilder {
    private boolean isHangUp;
    private int dtmfLength;
    private List<String> playTexts = new ArrayList<String>();
    private List<String> playAudios = new ArrayList<String>();
    private String sid;
    private String language;

    public KookooIVRResponseBuilder() {
    }

    public KookooIVRResponseBuilder withDefaultLanguage() {
        return language("en");
    }

    public KookooIVRResponseBuilder language(String code) {
        this.language = code;
        return this;
    }

	public KookooIVRResponseBuilder withPlayTexts(String... playTexts) {
        Collections.addAll(this.playTexts, playTexts);
        return this;
    }

	public KookooIVRResponseBuilder withPlayAudios(String... playAudios) {
        Collections.addAll(this.playAudios, playAudios);
        return this;
    }

	public KookooIVRResponseBuilder collectDtmfLength(Integer dtmfLength) {
        this.dtmfLength = dtmfLength;
        return this;
    }

	public KookooIVRResponseBuilder withHangUp() {
        this.isHangUp = true;
        return this;
    }

    public KookooIVRResponseBuilder withSid(String sid) {
        this.sid = sid;
        return this;
    }
    
	public String create(IVRMessage ivrMessage) {
        if (StringUtils.isEmpty(language)) withDefaultLanguage();
        Response response = new Response();
        if (StringUtils.isNotBlank(sid)) response.setSid(sid);

        if (isCollectDtmf()) {
            CollectDtmf collectDtmf = KookooCollectDtmfFactory.create();
            if(dtmfLength > 0) collectDtmf.setMaxDigits(dtmfLength);
            for (String playText : playTexts) collectDtmf.addPlayText(ivrMessage.getText(playText));
            for (String playAudio : playAudios) collectDtmf.addPlayAudio(ivrMessage.getWav(playAudio, language));

            response.addCollectDtmf(collectDtmf);
        } else {
            for (String playText : playTexts)
                response.addPlayText(ivrMessage.getText(playText));
            for (String playAudio : playAudios)
                response.addPlayAudio(ivrMessage.getWav(playAudio, language));
        }

        if (isHangUp) response.addHangup();
        return response.getXML();
    }

	public boolean isHangUp() {
        return isHangUp;
    }

	public boolean isCollectDtmf() {
        return dtmfLength > 0;
    }

	public List<String> getPlayTexts() {
        return playTexts;
    }

	public List<String> getPlayAudios() {
        return playAudios;
    }

    public String sid() {
        return sid;
    }
}
