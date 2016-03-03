package org.motechproject.email.search;

import org.mockito.Mock;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.EmailRecord;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class RecordSearchTest extends AbstractSearchExecutionTest {

    @Mock
    private EmailRecord emailRecord1;

    @Mock
    private EmailRecord emailRecord2;

    @Override
    protected Object queryResult() {
        return asList(emailRecord1, emailRecord2);
    }

    @Override
    protected void verifyImplementationDetails() {
        verify(getQuery()).setRange(30, 60);
        verify(getQuery()).setOrdering("deliveryStatus ascending");
    }

    @Override
    protected void assertResult(Object result) {
        assertEquals(asList(emailRecord1, emailRecord2), result);
    }

    @Override
    protected AbstractSearchExecution createExecution(EmailRecordSearchCriteria criteria) {
        return new RecordSearch(criteria);
    }
}
