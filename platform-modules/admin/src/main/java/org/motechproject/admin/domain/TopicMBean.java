package org.motechproject.admin.domain;

/**
 * Represents a JMS topic. Holds information about the topic statistics.
 * This information is retrieved using JMX.
 */
public class TopicMBean {

    private String destination;
    private long consumerCount;
    private long enqueueCount;
    private long dequeueCount;
    private long expiredCount;


    /**
     * @param destination the name of the topic
     */
    public TopicMBean(String destination) {
        this.destination = destination;
    }

    /**
     * @return the name of the topic
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the name of the topic
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return number of consumers for this topic (most likely MOTECH instances)
     */
    public long getConsumerCount() {
        return consumerCount;
    }

    /**
     * @param consumerCount number of consumers for this topic (most likely MOTECH instances)
     */
    public void setConsumerCount(long consumerCount) {
        this.consumerCount = consumerCount;
    }

    /**
     * @return the total number of messages sent to the topic since the last restart
     */
    public long getEnqueueCount() {
        return enqueueCount;
    }

    /**
     * @param enqueueCount the total number of messages sent to the topic since the last restart
     */
    public void setEnqueueCount(long enqueueCount) {
        this.enqueueCount = enqueueCount;
    }

    /**
     * @return the total number of messages removed from the topic since last restart
     */
    public long getDequeueCount() {
        return dequeueCount;
    }

    /**
     * @param dequeueCount the total number of messages removed from the topic since last restart
     */
    public void setDequeueCount(long dequeueCount) {
        this.dequeueCount = dequeueCount;
    }
    /**
     * @return the number of messages that were not delivered because they were expired
     */
    public long getExpiredCount() {
        return expiredCount;
    }

    /**
     * @param expiredCount the number of messages that were not delivered because they were expired
     */
    public void setExpiredCount(long expiredCount) {
        this.expiredCount = expiredCount;
    }
}
