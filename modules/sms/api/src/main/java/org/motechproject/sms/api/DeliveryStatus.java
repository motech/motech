package org.motechproject.sms.api;

public enum DeliveryStatus {
    UNKNOWN,
    /**
     * @deprecated As of release 0.20, replaced by {@link #DISPATCHED}
     */
    @Deprecated
    INPROGRESS,
    /**
     * @deprecated As of release 0.20, replaced by {@link #DELIVERY_CONFIRMED}
     */
    @Deprecated
    DELIVERED,
    KEEPTRYING,
    ABORTED,
    PENDING,
    RECEIVED,
    DISPATCHED,
    DELIVERY_CONFIRMED
}
