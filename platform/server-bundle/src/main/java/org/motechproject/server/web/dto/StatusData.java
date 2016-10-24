package org.motechproject.server.web.dto;

import org.joda.time.DateTime;

/**
 * Class holding metadata about the current server
 */
public class StatusData {
    private DateTime time;
    private DateTime uptime;
    private String nodeName;
    private boolean inboundChannelActive;

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public DateTime getUptime() {
        return uptime;
    }

    public void setUptime(DateTime uptime) {
        this.uptime = uptime;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean isInboundChannelActive() {
        return inboundChannelActive;
    }

    public void setInboundChannelActive(boolean inboundChannelActive) {
        this.inboundChannelActive = inboundChannelActive;
    }
}
