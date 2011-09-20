package org.motechproject.ivr.kookoo;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRResponseBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KookooIVRResponseBuilder implements IVRResponseBuilder {
    private boolean isHangUp;
    private boolean collectDtmf;
    private int dtmfLength;
    private List<String> playTexts = new ArrayList<String>();
    private List<String> playAudios = new ArrayList<String>();

    public KookooIVRResponseBuilder() {
    }

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#withPlayTexts(java.lang.String)
	 */
    @Override
	public KookooIVRResponseBuilder withPlayTexts(String... playTexts) {
        for (String playText : playTexts)
            this.playTexts.add(playText);
        return this;
    }

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#withPlayAudios(java.lang.String)
	 */
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

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#collectDtmf(int)
	 */
    @Override
	public KookooIVRResponseBuilder collectDtmf(int dtmfLength) {
        collectDtmf = true;
        this.dtmfLength = dtmfLength;
        return this;
    }

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#withHangUp()
	 */
    @Override
	public KookooIVRResponseBuilder withHangUp() {
        this.isHangUp = true;
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
        if (isHangUp) response.addHangup();
        return response.getXML();
    }

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#isHangUp()
	 */
    @Override
	public boolean isHangUp() {
        return isHangUp;
    }

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#isCollectDtmf()
	 */
    @Override
	public boolean isCollectDtmf() {
        return collectDtmf;
    }

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#getPlayTexts()
	 */
    @Override
	public List<String> getPlayTexts() {
        return playTexts;
    }

    /* (non-Javadoc)
	 * @see org.motechproject.ivr.kookoo.IVRMessageBuilder#getPlayAudios()
	 */
    @Override
	public List<String> getPlayAudios() {
        return playAudios;
    }
}
