package org.motechproject.email.domain;

import java.util.Comparator;

/**
 * The <code>EmailRecordComparator</code> class is an implementation of the Comparator interface,
 * that allows callers to compare {@link org.motechproject.email.domain.EmailRecord} objects by a single field.
 */

public class EmailRecordComparator implements Comparator<EmailRecord> {
    private String compareField = "subject";
    private Boolean ascending = true;

    /**
     * Creates a new <code>EmailRecordComparator</code> that supports comparison based
     * on the specified field.
     *
     * @param ascending  boolean indicating whether comparisons should be ascending or descending
     * @param compareField  the field for which comparisons should be performed
     */
    public EmailRecordComparator(Boolean ascending, String compareField) {
        this.compareField = compareField;
        this.ascending = ascending;
    }

    /**
     * Compares its two arguments for order. If ascending is <code>true</code>, returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     * If ascending is <code>false</code>, returns a positive integer, zero, or negative integer as the first
     * argument is less than, equal to, or greater than the second.
     *
     * @param o1  the first <code>EmailRecord</code> to be compared
     * @param o2  the second <code>EmailRecord</code> to be compared
     * @return a positive integer, zero, or negative integer indicating the result of comparing the objects
     */
    @Override
    public int compare(EmailRecord o1, EmailRecord o2) {
        int ret;

        switch (compareField) {
            case "fromAddress":
                ret = (o1.getFromAddress()).compareToIgnoreCase(o2.getFromAddress());
                break;
            case "toAddress":
                ret = (o1.getToAddress()).compareToIgnoreCase(o2.getToAddress());
                break;
            case "message":
                ret = o1.getMessage().compareTo(o2.getMessage());
                break;
            case "deliveryStatus":
                ret = o1.getDeliveryStatus().compareTo(o2.getDeliveryStatus());
                break;
            case "subject":
                ret = (o1.getSubject()).compareToIgnoreCase(o2.getSubject());
                break;
            case "deliveryTime": // Fall thruough to the default value if no field is specified
            default:
                ret = o1.getDeliveryTime().compareTo(o2.getDeliveryTime());
                break;
        }

        return (ascending) ? ret : -ret;
    }

}
