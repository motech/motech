package org.motechproject.email.search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.internal.matchers.VarargMatcher;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.SecurityUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jdo.Query;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtil.class)
public abstract class AbstractSearchExecutionTest {

    private static final LocalDateTime PAST = DateUtil.javaTimeNow().minusYears(20);
    private static final LocalDateTime FUTURE = DateUtil.javaTimeNow().plusYears(15);

    protected abstract Object queryResult();
    protected abstract void verifyImplementationDetails();
    protected abstract void assertResult(Object result);
    protected abstract AbstractSearchExecution createExecution(EmailRecordSearchCriteria criteria);

    @Mock
    private Query query;

    @Mock
    private InstanceSecurityRestriction restriction;

    private QueryParams queryParams = new QueryParams(2, 30, new Order("deliveryStatus", Order.Direction.ASC));

    @Before
    public void setUp() {
        PowerMockito.mockStatic(SecurityUtil.class);
        when(SecurityUtil.getUsername()).thenReturn("myuser");
    }

    @Test
    public void shouldSetCorrectParams() {
        when(restriction.isEmpty()).thenReturn(false);
        when(restriction.isByCreator()).thenReturn(true);
        when(query.executeWithArray(anyVararg())).thenReturn(queryResult());

        EmailRecordSearchCriteria criteria = new EmailRecordSearchCriteria()
                .withFromAddress("from@address.com").withMessageTimeRange(new Range<>(PAST, FUTURE))
                .withMessage("text to search").withDeliveryStatuses(DeliveryStatus.RECEIVED, DeliveryStatus.ERROR)
                .withQueryParams(queryParams);

        AbstractSearchExecution execution = createExecution(criteria);

        Object result = execution.execute(query, restriction);

        verify(query).setFilter("deliveryTime>=param0lb && deliveryTime<=param0ub && " +
                "(deliveryStatus == param1_0 || deliveryStatus == param1_1) && (fromAddress.matches(param2) || "+
                "message.matches(param3)) && creator == param4");
        verify(query).declareParameters("java.time.LocalDateTime param0lb, java.time.LocalDateTime param0ub, " +
                DeliveryStatus.class.getName() + " param1_0, " + DeliveryStatus.class.getName() + " param1_1, " +
                "java.lang.String param2, java.lang.String param3, java.lang.String param4");

        verify(query).executeWithArray(argThat(new QueryExecutionMatcher()));

        verifyImplementationDetails();
        assertResult(result);
    }

    protected Query getQuery() {
        return query;
    }

    /**
     * We must match varargs using this matcher, because the arguments coming from delivery status sets
     * will have random ordering. Argument captors don't work for varags.
     */
    private static class QueryExecutionMatcher extends ArgumentMatcher<Object[]> implements VarargMatcher {

        private static final long serialVersionUID = 2455128820319808158L;

        @Override
        public boolean matches(Object argument) {
            Object[] values = (Object[]) argument;

            assertNotNull(values);
            assertEquals(7, values.length);
            assertEquals(PAST, values[0]);
            assertEquals(FUTURE, values[1]);
            assertEquals(".*from@address.com.*", values[4]);
            assertEquals(".*text to search.*", values[5]);
            assertEquals("myuser", values[6]);

            Set<DeliveryStatus> expected = new HashSet<>(asList(DeliveryStatus.RECEIVED, DeliveryStatus.ERROR));
            Set actual = new HashSet(asList(values[2], values[3]));

            assertEquals(expected, actual);

            return true;
        }
    }
}
