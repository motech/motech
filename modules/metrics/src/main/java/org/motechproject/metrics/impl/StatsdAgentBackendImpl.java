package org.motechproject.metrics.impl;

import org.apache.commons.io.FileUtils;
import org.motechproject.metrics.service.MetricsAgentBackend;
import org.motechproject.metrics.service.StatsdAgentBackend;
import org.motechproject.metrics.domain.ConfigProperty;
import org.motechproject.metrics.domain.PropertyType;
import org.motechproject.metrics.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A very simple metric backend that logs all metrics over UDP.
 * The intended receiver is a statsd server
 * (http://codeascraft.etsy.com/2011/02/15/measure-anything-measure-everything/)
 */
public class StatsdAgentBackendImpl implements MetricsAgentBackend, StatsdAgentBackend {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String serverHost;
    private int serverPort;
    private boolean generateHostBasedStats;
    private String graphiteUrl;

    private final String serverHostKey = "serverHost";
    private final String serverPortKey = "serverPort";
    private final String generateHostBasedStatsKey = "generateHostBasedStats";
    private final String graphiteUrlKey = "graphiteUrl";

    private InetAddress serverAddr;
    private String hostName;
    private DatagramSocket socket;
    private String configFileLocation = System.getProperty("user.home") + "/.motech/config/org." +
            "motechproject.metrics/statsdAgent.properties";

    private final String implementationName = "StatsD";

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

    private boolean send(List<String> stats) {
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

    public String getGraphiteUrl() {
        return graphiteUrl;
    }

    public void setGraphiteUrl(String url) {
        this.graphiteUrl = url;
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

    //Loads config data from properties file
    @PostConstruct
    public void loadProperties() {
        File file = new File(configFileLocation);
            if (file.exists()) {
                try (InputStream in = new FileInputStream(file)) {
                    Properties statsdAgentConfig = new Properties();
                    statsdAgentConfig.load(in);
                    in.close();
                    serverHost = statsdAgentConfig.getProperty(serverHostKey);
                    serverPort = Integer.parseInt(statsdAgentConfig.getProperty(serverPortKey));
                    generateHostBasedStats = "true".equals(statsdAgentConfig.getProperty(generateHostBasedStatsKey));
                    graphiteUrl = statsdAgentConfig.getProperty(graphiteUrlKey);
                }
                catch (IOException e) {
                    log.error("Error while loading statsdAgent configuration from " + configFileLocation, e);
                }
            }
    }

     //Saves config data to properties file
    public void saveProperties() {
        File file = new File(configFileLocation);
        if (!file.exists()) {
            try {
                FileUtils.touch(file);
            } catch (IOException e) {
                log.error("Error while saving statsdAgent config", e);
            }
        }
        try (FileOutputStream out = new FileOutputStream(file)) {
            Properties statsdAgentConfig = new Properties();
            statsdAgentConfig.setProperty("serverHost", serverHost);
            statsdAgentConfig.setProperty("serverPort", Integer.toString(serverPort));
            statsdAgentConfig.setProperty("generateHostBasedStats", String.valueOf(generateHostBasedStats));
            statsdAgentConfig.setProperty("graphiteUrl", graphiteUrl);
            statsdAgentConfig.store(out, null);
            out.close();
        }
        catch (IOException e) {
            log.error("Error while saving statsdAgent config", e);
        }
    }

    @Override
    public String getImplementationName() {
        return implementationName;
    }

    @Override
    public Map<String, ConfigProperty> getSettings() {
        Map<String, ConfigProperty> configuration = new HashMap<>();

        configuration.put(serverHostKey, new ConfigProperty("Server Host", PropertyType.UNICODE, serverHost));
        configuration.put(serverPortKey, new ConfigProperty("Server Port", PropertyType.INTEGER, Integer.toString(serverPort)));
        configuration.put(generateHostBasedStatsKey, new ConfigProperty("Generate Host Based Stats", PropertyType.BOOLEAN, String.valueOf(generateHostBasedStats)));
        configuration.put(graphiteUrlKey, new ConfigProperty("Graphite Url", PropertyType.UNICODE, graphiteUrl));

       return configuration;
    }

    @Override
    public void saveSettings(Map<String, ConfigProperty> config) {
        Map<String, String> errors = validate(config);

        if (!errors.isEmpty()) {
            throw new ValidationException(implementationName, errors);
        } else {
            serverHost = config.get(serverHostKey).getValue();
            serverPort = Integer.parseInt(config.get(serverPortKey).getValue());
            generateHostBasedStats = "true".equals(config.get(generateHostBasedStatsKey).getValue());
            graphiteUrl = config.get(graphiteUrlKey).getValue();

            //save to file
            saveProperties();
        }
    }

    private Map<String, String> validate(Map<String, ConfigProperty> config) {
        Map<String, String> errors = new HashMap<>();

        if ((config.get(serverHostKey)).getValue().isEmpty()) {
            errors.put(config.get(serverHostKey).getDisplayName(), "Server host cannot be empty");
        }

        try {
            Integer.parseInt((config.get(serverPortKey)).getValue());
        } catch (Exception e) {
            errors.put(config.get(serverPortKey).getDisplayName(), "Error while parsing port value");
        }

        return errors;
    }
}
