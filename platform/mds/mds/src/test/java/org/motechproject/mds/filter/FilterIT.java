package org.motechproject.mds.filter;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.BaseInstanceIT;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.builder.MDSConstructor;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.MetadataHolder;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.testing.utils.TimeFaker;
import org.springframework.beans.factory.annotation.Autowired;

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
public class FilterIT extends BaseInstanceIT {

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
        setUpForInstanceTesting();
        setUpTestData();
    }

    @Test
    public void shouldDoFilters() {
        MotechDataService service = getService();

        Filter trueFiter = new Filter(BOOL_FIELD, FilterType.YES);
        List<Object> list = service.filter(trueFiter);

        assertPresentByStrField(list, "now", "threeDaysAgo");
        assertEquals(2, service.countForFilter(trueFiter));

        Filter falseFilter = new Filter(BOOL_FIELD, FilterType.NO);
        list = service.filter(falseFilter);

        assertPresentByStrField(list, "eightDaysAgo", "notThisMonth", "notThisYear");
        assertEquals(3, service.countForFilter(falseFilter));

        try {
            TimeFaker.fakeNow(NOW);

            for (String field : asList(DATE_FIELD, DATETIME_FIELD)) {
                Filter todayFilter = new Filter(field, FilterType.TODAY);
                list = service.filter(todayFilter);
                assertPresentByStrField(list, "now");
                assertEquals(1, service.countForFilter(todayFilter));

                Filter sevenDayFilter = new Filter(field, FilterType.PAST_7_DAYS);
                list = service.filter(sevenDayFilter);
                assertPresentByStrField(list, "now", "threeDaysAgo");
                assertEquals(2, service.countForFilter(sevenDayFilter));

                Filter monthFilter = new Filter(field, FilterType.THIS_MONTH);
                list = service.filter(monthFilter);
                assertPresentByStrField(list, "now", "threeDaysAgo", "eightDaysAgo");
                assertEquals(3, service.countForFilter(monthFilter));

                Filter yearFilter = new Filter(field, FilterType.THIS_YEAR);
                list = service.filter(yearFilter);
                assertPresentByStrField(list, "now", "threeDaysAgo", "eightDaysAgo", "notThisMonth");
                assertEquals(4, service.countForFilter(yearFilter));
            }
        } finally {
            TimeFaker.stopFakingTime();
        }
    }

    private void setUpTestData() throws Exception {
        Class<?> clazz = MDSClassLoader.getInstance().loadClass(getEntityClass());

        DateTime threeDaysAgo = NOW.minusDays(3);
        DateTime eightDaysAgo = NOW.minusDays(8);
        DateTime notThisMonth = NOW.minusMonths(2);
        DateTime notThisYear = NOW.minusYears(2);

        MotechDataService service = getService();

        service.create(objectInstance(clazz, true, NOW.toDate(), NOW, "now"));
        service.create(objectInstance(clazz, true, threeDaysAgo.toDate(), threeDaysAgo, "threeDaysAgo"));
        service.create(objectInstance(clazz, false, eightDaysAgo.toDate(), eightDaysAgo, "eightDaysAgo"));
        service.create(objectInstance(clazz, false, notThisMonth.toDate(), notThisMonth, "notThisMonth"));
        service.create(objectInstance(clazz, false, notThisYear.toDate(), notThisYear, "notThisYear"));

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
