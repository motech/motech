package org.motechproject.commons.api;

import java.util.Objects;

public class Range<T> {

    private T min;
    private T max;

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
