package org.motechproject.ivr.kookoo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;

public class KookooRequest implements IVRRequest{

	private static final String POUND_SYMBOL = "%23";
	private String sid;
    private String cid;
    private String event;
    private String data;
    Map<String, String> dataMap = new HashMap<String, String>();

	@Override
	public String getCallerId() {
			return cid;
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

    public KookooRequest() {
    }

    public KookooRequest(String sid, String cid, String event, String data) {
        this.sid = sid;
        this.cid = cid;
        this.event = event;
        this.data = data;
    }

    public String getSessionId() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }


    public void setEvent(String event) {
        this.event = event;
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

    public boolean hasNoData() {
        return StringUtils.isBlank(this.data);
    }

	public String getPayloadJson() {
		JSONObject object = new JSONObject();
		for (String key : dataMap.keySet()){
			try {
				object.put(key, dataMap.get(key));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}        
		}
		return object.toString();
    }

    public Map getPayloadParams() {
        return dataMap;
    }

    public boolean hasNoPayload() {
        return dataMap.size()==0;
    }
    public CallDirection getCallDirection() {
    	return hasNoPayload()?CallDirection.Inbound: CallDirection.Outbound;
    }
    /*
        public static final String POUND_SYMBOL = "%23";
    
    public static enum CallDirection {Inbound, Outbound};

*/}
