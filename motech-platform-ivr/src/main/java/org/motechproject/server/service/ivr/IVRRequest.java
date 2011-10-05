package org.motechproject.server.service.ivr;

public interface IVRRequest {

    public static enum CallDirection {
		Inbound, Outbound;
    };

	public String getSid();

    public void setSid(String sid);

	public String getCid();

    public void setCid(String cid);

	public String getEvent();

	public void setEvent(String event);

	public String getData();

    public void setData(String data);

	public String getInput();

	public IVREvent callEvent();

	public String getParameter(String key);

	public void setParameter(String key, String value);
	
	public CallDirection getCallDirection();
}
