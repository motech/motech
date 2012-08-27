package org.motechproject.event.metrics.impl;

import org.motechproject.event.metrics.MetricsAgentBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

/**
 * A very simple metric backend that logs all metrics over UDP.
 * The intended receiver is a statsd server
 * (http://codeascraft.etsy.com/2011/02/15/measure-anything-measure-everything/)
 */
public class StatsdAgentBackendImpl implements MetricsAgentBackend {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String serverHost;
    private int serverPort;
    private boolean generateHostBasedStats;

    private InetAddress serverAddr;
    private String hostName;
    private DatagramSocket socket;

    public StatsdAgentBackendImpl() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        }
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // This is ok it just means host specific metrics will not be published
            log.error("Unable to get local hostname", e);
        }
    }


    /**
     * Reports an occurrence of metric, incrementing it's count. Ignores parameters
     *
     * @param metric     The metric being recorded
     * @param parameters Ignored. Silently dropped.  Actually this implementation laughs a little at you
     */
    @Override
    public void logEvent(String metric, Map<String, String> parameters) {
        logEvent(metric);
    }

    /**
     * Reports an occurrence of metric, incrementing it's count.
     *
     * @param metric The metric being recorded
     */
    @Override
    public void logEvent(String metric) {
        ArrayList<String> stats = new ArrayList<String>();
        stats.add(String.format("%s:1|c", metric));

        if (generateHostBasedStats && hostName != null) {
            stats.add(String.format("%s:1|c", String.format("%s.%s", hostName, metric)));
        }

        send(stats);
    }

    /**
     * Reports an occurrence of metric in milliseconds
     *
     * @param metric The metric being recorded
     * @param time   The execution time of this event in milliseconds
     */
    @Override
    public void logTimedEvent(String metric, long time) {
        ArrayList<String> stats = new ArrayList<String>();
        stats.add(String.format("%s:%d|ms", metric, time));

        if (generateHostBasedStats && hostName != null) {
            stats.add(String.format("%s.%s:%d|ms", hostName, metric, time));
        }

        send(stats);
    }

    private boolean send(ArrayList<String> stats) {
        if (socket == null) {
            return false;
        }
        boolean retval = false; // didn't send anything
        for (String stat : stats) {
            if (doSend(socket, stat)) {
                retval = true;
            }
        }

        return retval;
    }

    private boolean doSend(DatagramSocket sock, String stat) {
        if (serverAddr == null) {
            try {
                serverAddr = InetAddress.getByName(serverHost);
            } catch (UnknownHostException e) {
                log.error(e.getMessage());
                return false;
            }
        }

        try {
            byte[] data = stat.getBytes();
            sock.send(new DatagramPacket(data, data.length, serverAddr, serverPort));
            return true;
        } catch (IOException e) {
            log.error(String.format("Could not send stat %s to host %s:%d", stat, serverHost, serverPort), e);
        }
        return false;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public boolean isGenerateHostBasedStats() {
        return generateHostBasedStats;
    }

    public void setGenerateHostBasedStats(boolean generateHostBasedStats) {
        this.generateHostBasedStats = generateHostBasedStats;
    }
}
