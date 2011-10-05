package org.motechproject.server.service.ivr;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

public class CallEvent {

    private String name;

    private DateTime timeStamp;

    private Map<String, String> data;

    private CallEvent(){
    }

    public CallEvent(String name, Map<String, String> data) {
        this(name, DateUtil.now(), data);
    }

    private CallEvent(String name, DateTime timeStamp, Map<String, String> data) {
        this.name = name;
        this.timeStamp = timeStamp;
        this.data = data;
    }

    public static CallEvent newDialEvent() {
        return new CallEvent("Dial", DateUtil.now(), new HashMap<String, String>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
