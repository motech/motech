package org.motechproject.commons.api;

import java.util.Objects;

/**
 * Class representing range between two objects of same type.
 * @param <T>
 */
public class Range<T> {

    private T min;
    private T max;

    /**
     * Constructor.
     * @param min  minimum value for range
     * @param max  maximum value for range
     */
    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Range)) {
            return false;
        }

        Range range = (Range) o;

        return Objects.equals(range.min, min) && Objects.equals(range.max, max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}
