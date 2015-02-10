package org.motechproject.admin.domain;

/**
 * Represents a JMS queue. Holds information about the queue statistics.
 * This is information is retrieved using JMX.
 */
public class QueueMBean {

    private String destination;
    private long enqueueCount;
    private long dequeueCount;
    private long inflightCount;
    private long expiredCount;
    private long consumerCount;
    private long queueSize;

    /**
     * @param destination the name of the queue
     */
    public QueueMBean(String destination) {
        this.destination = destination;
    }

    /**
     * @return the name of the queue
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the name of the queue
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the total number of messages sent to the queue since the last restart
     */
    public long getEnqueueCount() {
        return enqueueCount;
    }

    /**
     * @param enqueueCount the total number of messages sent to the queue since the last restart
     */
    public void setEnqueueCount(long enqueueCount) {
        this.enqueueCount = enqueueCount;
    }

    /**
     * @return the total number of messages removed from the queue (ack'd by consumer) since last restart
     */
    public long getDequeueCount() {
        return dequeueCount;
    }

    /**
     * @param dequeueCount the total number of messages removed from the queue (ack'd by consumer) since last restart
     */
    public void setDequeueCount(long dequeueCount) {
        this.dequeueCount = dequeueCount;
    }

    /**
     * @return the number of messages sent to a consumer session that have not received an ack
     */
    public long getInflightCount() {
        return inflightCount;
    }

    /**
     * @param inflightCount the number of messages sent to a consumer session that have not received an ack
     */
    public void setInflightCount(long inflightCount) {
        this.inflightCount = inflightCount;
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

    /**
     * @return number of consumers for this queue (most likely MOTECH instances)
     */
    public long getConsumerCount() {
        return consumerCount;
    }

    /**
     * @param consumerCount number of consumers for this queue (most likely MOTECH instances)
     */
    public void setConsumerCount(long consumerCount) {
        this.consumerCount = consumerCount;
    }

    /**
     * Returns the total number of messages in the queue/store that have not been ack'd by a consumer.
     * This can become confusing at times when compared to the Enqueue Count because the Enqueue Count is a
     * count over a period of time (since the last broker restart) while the Queue Size is not dependent on a period of
     * time but instead on the actual number of messages in the store.
     *
     * @return the total number of messages in the queue/store that have not been ack'd by a consumer,
     * not dependent on a period of time
     */
    public long getQueueSize() {
        return queueSize;
    }

    /**
     * Sets the total number of messages in the queue/store that have not been ack'd by a consumer.
     * This can become confusing at times when compared to the Enqueue Count because the Enqueue Count is a
     * count over a period of time (since the last broker restart) while the Queue Size is not dependent on a period of
     * time but instead on the actual number of messages in the store.
     *
     * @param queueSize the total number of messages in the queue/store that have not been ack'd by a consumer,
     * not dependent on a period of time
     */
    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }
}
