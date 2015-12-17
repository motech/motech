package org.motechproject.mds.it;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.filter.FilterValue;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.testing.utils.TimeFaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test filtering operations
 */
public class FilterContextIT extends BaseInstanceIT {

    private final Logger LOGGER = LoggerFactory.getLogger(FilterContextIT.class);
    private static final String ENTITY_NAME = "TestForFilter";
    private static final String BOOL_FIELD = "boolField";
    private static final String DATE_FIELD = "dateField";
    private static final String DATETIME_FIELD = "dateTimeField";
    private static final String STRING_FIELD = "strField";
    private static final List<String> STR_VALUES =
            asList("now", "threeDaysAgo", "eightDaysAgo", "notThisMonth", "notThisYear");

    private static final DateTime NOW = new DateTime(2014, 3, 17, 0, 0);

    @Autowired
    private EntityService entityService;

    @Autowired
    private MDSConstructor mdsConstructor;

    @Autowired
    private AllEntities allEntities;

    @Autowired
    private MetadataHolder metadataHolder;

    @Mock
    private HistoryService historyService;

    @Override
    protected String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    protected List<FieldDto> getEntityFields() {
        List<FieldDto> fields = new ArrayList<>();
        fields.add(FieldTestHelper.fieldDto(BOOL_FIELD, Boolean.class.getName()));
        fields.add(FieldTestHelper.fieldDto(DATE_FIELD, Date.class.getName()));
        fields.add(FieldTestHelper.fieldDto(DATETIME_FIELD, DateTime.class.getName()));
        fields.add(FieldTestHelper.fieldDto(STRING_FIELD, String.class.getName()));
        return fields;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        setUpForInstanceTesting();
        setUpTestData();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void shouldDoFilter() {
        MotechDataService service = getService();

        Filter trueFilter = new Filter(BOOL_FIELD, FilterValue.YES);
        List<Object> list = service.filter(new Filters(trueFilter), null);

        assertPresentByStrField(list, "now", "threeDaysAgo");
        assertEquals(2, service.countForFilters(new Filters(trueFilter)));

        Filter falseFilter = new Filter(BOOL_FIELD, FilterValue.NO);
        list = service.filter(new Filters(falseFilter), null);

        assertPresentByStrField(list, "eightDaysAgo", "notThisMonth", "notThisYear");
        assertEquals(3, service.countForFilters(new Filters(falseFilter)));

        try {
            TimeFaker.fakeNow(NOW);

            for (String field : asList(DATE_FIELD, DATETIME_FIELD)) {
                Filter todayFilter = new Filter(field, FilterValue.TODAY);
                list = service.filter(new Filters(todayFilter), null);
                assertPresentByStrField(list, "now");
                assertEquals(1, service.countForFilters(new Filters(todayFilter)));

                Filter sevenDayFilter = new Filter(field, FilterValue.PAST_7_DAYS);
                list = service.filter(new Filters(sevenDayFilter), null);
                assertPresentByStrField(list, "now", "threeDaysAgo");
                assertEquals(2, service.countForFilters(new Filters(sevenDayFilter)));

                Filter monthFilter = new Filter(field, FilterValue.THIS_MONTH);
                list = service.filter(new Filters(monthFilter), null);
                assertPresentByStrField(list, "now", "threeDaysAgo", "eightDaysAgo");
                assertEquals(3, service.countForFilters(new Filters(monthFilter)));

                Filter yearFilter = new Filter(field, FilterValue.THIS_YEAR);
                list = service.filter(new Filters(yearFilter), null);
                assertPresentByStrField(list, "now", "threeDaysAgo", "eightDaysAgo", "notThisMonth");
                assertEquals(4, service.countForFilters(new Filters(yearFilter)));
            }
        } finally {
            TimeFaker.stopFakingTime();
        }
    }

    @Test
    public void shouldDoFilters() {
        MotechDataService service = getService();

        Filter yearFilter = new Filter(DATE_FIELD, FilterValue.THIS_YEAR);
        Filter falseFilter = new Filter(BOOL_FIELD, FilterValue.NO);

        Filters filters = new Filters(new Filter[]{yearFilter, falseFilter});

        try {
            TimeFaker.fakeNow(NOW);

            List<Object> list = service.filter(filters, null);
            assertPresentByStrField(list, "eightDaysAgo", "notThisMonth");
            assertEquals(2, service.countForFilters(filters));
        } finally {
            TimeFaker.stopFakingTime();
        }
    }

    private void setUpTestData() throws Exception {
        Class<?> clazz = getEntityClass();

        DateTime threeDaysAgo = NOW.minusDays(3);
        DateTime eightDaysAgo = NOW.minusDays(8);
        DateTime notThisMonth = NOW.minusMonths(2);
        DateTime notThisYear = NOW.minusYears(2);

        MotechDataService service = getService();

        final Object instance1 = objectInstance(clazz, true, NOW.toDate(), NOW, "now");
        final Object instance2 = objectInstance(clazz, true, threeDaysAgo.toDate(), threeDaysAgo, "threeDaysAgo");
        final Object instance3 = objectInstance(clazz, false, eightDaysAgo.toDate(), eightDaysAgo, "eightDaysAgo");
        final Object instance4 = objectInstance(clazz, false, notThisMonth.toDate(), notThisMonth, "notThisMonth");
        final Object instance5 = objectInstance(clazz, false, notThisYear.toDate(), notThisYear, "notThisYear");

        service.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                service.create(instance1);
                service.create(instance2);
                service.create(instance3);
                service.create(instance4);
                service.create(instance5);
            }
        });

        assertEquals("There were issues creating test data", 5, service.count());
    }

    private Object objectInstance(Class<?> clazz, Boolean bool, Date date, DateTime dateTime, String str) throws Exception {
        Object instance = clazz.newInstance();
        PropertyUtils.setProperty(instance, BOOL_FIELD, bool);
        PropertyUtils.setProperty(instance, DATE_FIELD, date);
        PropertyUtils.setProperty(instance, DATETIME_FIELD, dateTime);
        PropertyUtils.setProperty(instance, STRING_FIELD, str);

        return instance;
    }

    private void assertPresentByStrField(List objects, String... expected) {
        for (String value : STR_VALUES) {
            Matcher hasItemMatcher = hasItem(Matchers.hasProperty(STRING_FIELD, equalTo(value)));

            if (ArrayUtils.contains(expected, value)) {
                assertThat(objects, hasItemMatcher);
            } else {
                assertThat(objects, not(hasItemMatcher));
            }
        }
    }
}
