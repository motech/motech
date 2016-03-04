package org.motechproject.mds.it;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.SecurityUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDto;
import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;

public class AutoGenerationContextIT extends BaseInstanceIT {
    private static final String DUKE = "Duke";
    private static final String VALUE_FIELD = "value";

    @Override
    protected String getEntityName() {
        return DUKE;
    }

    @Override
    protected List<FieldDto> getEntityFields() {
        List<FieldDto> fields = new ArrayList<>();
        fields.add(fieldDto(VALUE_FIELD, String.class.getName()));
        return fields;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        setUpForInstanceTesting();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void shouldGenerateValues() throws Exception {
        Class<?> definition = getEntityClass();
        final Object instance = definition.newInstance();

        String createUsername = StringUtils.defaultIfBlank(SecurityUtil.getUsername(), "");
        DateTime create = DateTime.now();
        double hour = 60 * 60 * 1000;

        Object instanceFromDb = getService().doInTransaction(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                return getService().create(instance);
            }
        });

        assertEquals(createUsername, PropertyUtil.safeGetProperty(instanceFromDb, CREATOR_FIELD_NAME));
        assertEquals(createUsername, PropertyUtil.safeGetProperty(instanceFromDb, OWNER_FIELD_NAME));
        assertEquals(createUsername, PropertyUtil.safeGetProperty(instanceFromDb, MODIFIED_BY_FIELD_NAME));
        assertEquals(create.getMillis(), ((DateTime) PropertyUtil.safeGetProperty(instanceFromDb, CREATION_DATE_FIELD_NAME)).getMillis(), hour);
        assertEquals(create.getMillis(), ((DateTime) PropertyUtil.safeGetProperty(instanceFromDb, MODIFICATION_DATE_FIELD_NAME)).getMillis(), hour);

        Thread.sleep(TimeUnit.SECONDS.toMillis(15));

        String updateUsername = StringUtils.defaultIfBlank(SecurityUtil.getUsername(), "");
        DateTime update = DateTime.now();
        PropertyUtil.safeSetProperty(instance, VALUE_FIELD, "nukem");

        instanceFromDb = getService().doInTransaction(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                return getService().update(instance);
            }
        });

        assertEquals(createUsername, PropertyUtil.safeGetProperty(instanceFromDb, CREATOR_FIELD_NAME));
        assertEquals(createUsername, PropertyUtil.safeGetProperty(instanceFromDb, OWNER_FIELD_NAME));
        assertEquals(updateUsername, PropertyUtil.safeGetProperty(instanceFromDb, MODIFIED_BY_FIELD_NAME));
        assertEquals(create.getMillis(), ((DateTime) PropertyUtil.safeGetProperty(instanceFromDb, CREATION_DATE_FIELD_NAME)).getMillis(), hour);
        assertEquals(update.getMillis(), ((DateTime) PropertyUtil.safeGetProperty(instanceFromDb, MODIFICATION_DATE_FIELD_NAME)).getMillis(), hour);
    }
}
