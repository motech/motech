package org.motechproject.ivr.kookoo;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KookooRequest implements IVRRequest {
	
	private Logger log = Logger.getLogger(KookooRequest.class);

    private static final String POUND_SYMBOL = "%23";
    private String sid;
    private String cid;
    private String event;
    private String data;
    private Map<String, String> dataMap = new HashMap<String, String>();

    public KookooRequest() {
    }

    public KookooRequest(String sid, String cid, String event, String data) {
        this.sid = sid;
        this.cid = cid;
        this.event = event;
        this.data = data;
    }

    @Override
    public String getCid() {
        return cid;
    }

    @Override
    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String getParameter(String key) {
        return dataMap.get(key);
    }

    @Override
    public void setParameter(String key, String value) {
        dataMap.put(key, value);
    }

    @Override
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSid() {
        return sid;
    }

    @Override
    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getData() {
        return data;
    }

    public String getInput() {
        return data.replace(POUND_SYMBOL, "");
    }

    public void setData(String data) {
        this.data = data;
    }

    public IVREvent callEvent() {
        return IVREvent.keyOf(this.event);
    }

    public CallDirection getCallDirection() {
        return dataMap!=null && "true".equals(dataMap.get(IVRSession.IVRCallAttribute.IS_OUTBOUND_CALL)) ? CallDirection.Outbound: CallDirection.Inbound;
    }
    
    public void setDataMap(String jsonDataMap){
    	try {
    		JSONObject jsonObject = new JSONObject(jsonDataMap);
    	for (Iterator<String> i = jsonObject.keys(); i.hasNext();) {
    		String key = i.next();
    		dataMap.put(key, jsonObject.getString(key));
    	}
    	} catch (Exception ignore) {
    		log.warn("Not able to read json data", ignore);
		}
    }
    

}
