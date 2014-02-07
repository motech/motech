package org.motechproject.mds.util;

import org.apache.commons.collections.Predicate;

import java.math.BigDecimal;

public class NumberPredicate implements Predicate {
    private Number element;

    public NumberPredicate(Number element) {
        this.element = element;
    }

    @Override
    public boolean evaluate(Object candidate) {
        boolean match = candidate instanceof Number;

        if (match) {
            BigDecimal dec1 = new BigDecimal(candidate.toString());
            BigDecimal dec2 = new BigDecimal(element.toString());

            match = dec1.equals(dec2);
        }

        return match;
    }

}
