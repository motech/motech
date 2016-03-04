package org.motechproject.mds.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NumberPredicateTest {
    private int number = 100;
    private NumberPredicate predicate = new NumberPredicate(number);

    @Test
    public void shouldReturnTrueIfCandidateIsEqual() throws Exception {
        assertTrue(predicate.evaluate(100L));
        assertTrue(predicate.evaluate((byte) 100));
        assertTrue(predicate.evaluate(new BigDecimal("100")));
    }

    @Test
    public void shouldReturnFalseIfCandidateIsDifferentOrIsNotNumber() throws Exception {
        assertFalse(predicate.evaluate(new Object()));
        assertFalse(predicate.evaluate(200));
    }

}
