package org.motechproject.server.service.ivr;

public interface IVRRequest {
	public static enum CallDirection {
		Inbound, Outbound
	};

	public String getSessionId();

	public void setSid(String sid);

	public String getCallerId();

	public void setCid(String cid);

	public String getEvent();

	public void setEvent(String event);

	public String getData();
	public String getInput();

	public void setData(String data);

	public IVREvent callEvent();
	public boolean hasNoData();
	
	public String getParameter(String key);
	public void setParameter(String key, String value);
	
	/*public void setTamaData(String tamaData);
	public Map getTamaParams();
	public boolean hasNoTamaData(); */
	public CallDirection getCallDirection();
}
