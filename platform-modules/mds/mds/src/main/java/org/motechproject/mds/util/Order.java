package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Represents an order in a query
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 8786361061292224134L;

    private final String field;
    private final Direction direction;

    /**
     * Creates order, with ascending direction.
     *
     * @param field field to order results by
     */
    public Order(String field) {
        this(field, Direction.ASC);
    }

    /**
     * Creates order.
     *
     * @param field field to order results by
     * @param direction {@link java.lang.String} representation of a direction
     */
    public Order(String field, String direction) {
        this(field, Direction.fromString(direction));
    }

    /**
     * Creates order.
     *
     * @param field field to order results by
     * @param direction direction of the order
     */
    public Order(String field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("%s %s", field, direction.toString());
    }

    /**
     * Represents a direction of the order.
     */
    public static enum Direction {
        /**
         * Ascending direction order (eg. 1, 2, 3...)
         */
        ASC,

        /**
         * Descending direction order (eg. 100, 99, 98...)
         */
        DESC;

        /**
         * Creates direction from the given {@link java.lang.String}. Throws {@link java.lang.IllegalArgumentException}
         * if conversion from {@link java.lang.String} to direction failed.
         *
         * @param str String representation of a direction; one of the following: asc, ascending, desc, descending
         * @return a direction value for the provided string
         */
        public static Direction fromString(String str) {
            if (StringUtils.isBlank(str)) {
                // ascending is default
                return ASC;
            } else if (StringUtils.equalsIgnoreCase(str, "asc") || StringUtils.equalsIgnoreCase(str, "ascending")) {
                return ASC;
            } else if (StringUtils.equalsIgnoreCase(str, "desc") || StringUtils.equalsIgnoreCase(str, "descending")) {
                return DESC;
            } else {
                throw new IllegalArgumentException("Ordering direction can be either ASC or DESC");
            }
        }

        /**
         * @return String representation; Either ascending or descending
         */
        @Override
        public String toString() {
            return (this == ASC) ? "ascending" : "descending";
        }
    }
}
