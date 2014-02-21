package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;

/**
 * Represents an order by in a query
 */
public class Order {

    private final String field;
    private final Direction direction;

    public Order(String field) {
        this(field, Direction.ASC);
    }

    public Order(String field, String direction) {
        this(field, Direction.fromString(direction));
    }

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

    public static enum Direction {
        ASC, DESC;

        public static Direction fromString(String str) {
            if (StringUtils.isBlank(str)) {
                // ascending is default
                return ASC;
            } else if (StringUtils.equalsIgnoreCase(str, "asc") || StringUtils.equalsIgnoreCase(str, "ascending")) {
                return ASC;
            } else if (StringUtils.equalsIgnoreCase(str, "desc") || StringUtils.equalsIgnoreCase(str, "descending")) {
                return DESC;
            } else {
                throw new  IllegalArgumentException("Ordering direction can be either ASC or DESC");
            }
        }

        @Override
        public String toString() {
            return (this == ASC) ? "ascending" : "descending";
        }
    }
}
