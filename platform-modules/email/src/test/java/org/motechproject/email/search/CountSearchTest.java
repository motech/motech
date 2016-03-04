package org.motechproject.email.search;

import org.motechproject.email.builder.EmailRecordSearchCriteria;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class CountSearchTest extends AbstractSearchExecutionTest {

    @Override
    protected Object queryResult() {
        return 7L;
    }

    @Override
    protected void verifyImplementationDetails() {
        verify(getQuery()).setResult("count(this)");
    }

    @Override
    protected void assertResult(Object result) {
        assertEquals(7L, result);
    }

    @Override
    protected AbstractSearchExecution createExecution(EmailRecordSearchCriteria criteria) {
        return new CountSearch(criteria);
    }
}
