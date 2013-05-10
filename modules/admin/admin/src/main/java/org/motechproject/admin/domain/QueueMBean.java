package org.motechproject.admin.domain;

public class QueueMBean {

    private String destination;
    private long enqueueCount;
    private long dequeueCount;
    private long inflightCount;
    private long expiredCount;
    private long consumerCount;
    private long queueSize;

    public QueueMBean(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public long getEnqueueCount() {
        return enqueueCount;
    }

    public void setEnqueueCount(long enqueueCount) {
        this.enqueueCount = enqueueCount;
    }

    public long getDequeueCount() {
        return dequeueCount;
    }

    public void setDequeueCount(long dequeueCount) {
        this.dequeueCount = dequeueCount;
    }

    public long getInflightCount() {
        return inflightCount;
    }

    public void setInflightCount(long inflightCount) {
        this.inflightCount = inflightCount;
    }

    public long getExpiredCount() {
        return expiredCount;
    }

    public void setExpiredCount(long expiredCount) {
        this.expiredCount = expiredCount;
    }

    public long getConsumerCount() {
        return consumerCount;
    }

    public void setConsumerCount(long consumerCount) {
        this.consumerCount = consumerCount;
    }

    public long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }
}
