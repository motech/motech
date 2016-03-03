package org.motechproject.mds.it.reposistory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.it.BaseInstanceIT;
import org.motechproject.mds.repository.ComboboxValueRepository;
import org.motechproject.mds.service.MetadataService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.MetadataServiceImpl;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import javax.jdo.PersistenceManagerFactory;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ComboboxValueRepositoryContextIT extends BaseInstanceIT {

    private static final String ENTITY_NAME = "TestForCbValues";
    private static final String CB_FIELD_MULTI_NAME = "cbFieldMulti";
    private static final String CB_FIELD_SINGLE_NAME = "cbFieldSingle";
    private final Logger LOGGER = LoggerFactory.getLogger(ComboboxValueRepositoryContextIT.class);

    private ComboboxValueRepository cbValueRepository;

    @Autowired
    @Qualifier("dataPersistenceManagerFactory")
    private PersistenceManagerFactory persistenceManagerFactory;

    @Override
    protected String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    protected List<FieldDto> getEntityFields() {
        // combobox with multiple selection - string collection field
        FieldDto cbFieldMulti = new FieldDto(CB_FIELD_MULTI_NAME, CB_FIELD_MULTI_NAME, TypeDto.COLLECTION, false, false,
                null, "tooltip", "placeholder");
        cbFieldMulti.addSetting(new SettingDto(Constants.Settings.ALLOW_USER_SUPPLIED, Constants.Util.TRUE));
        cbFieldMulti.addSetting(new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, Constants.Util.TRUE));

        // combobox without multiple selection - string field
        FieldDto cbFieldSingle = new FieldDto(CB_FIELD_SINGLE_NAME, CB_FIELD_SINGLE_NAME, TypeDto.COLLECTION, false, false,
                null, "tooltip2", "placeholder2");
        cbFieldSingle.addSetting(new SettingDto(Constants.Settings.ALLOW_USER_SUPPLIED, Constants.Util.TRUE));
        cbFieldSingle.addSetting(new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, Constants.Util.FALSE));

        return asList(cbFieldMulti, cbFieldSingle);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        setUpForInstanceTesting();
        setUpTestData();

        // normally this lives in the entities bundle
        cbValueRepository = new ComboboxValueRepository();
        cbValueRepository.setPersistenceManagerFactory(persistenceManagerFactory);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void shouldRetrieveComboboxMultiSelectValuesFromDb() {
        // This service is normally taken from the generated entities bundle
        MetadataService metadataService = new MetadataServiceImpl();
        ReflectionTestUtils.setField(metadataService, "persistenceManagerFactory", persistenceManagerFactory);

        String cbTableName = metadataService.getComboboxTableName(ClassName.getEntityClassName(ENTITY_NAME), CB_FIELD_MULTI_NAME);

        List<String> values = cbValueRepository.getComboboxValuesForCollection(cbTableName);

        assertNotNull(values);
        assertEquals(asList("five", "four", "one", "three", "two"), values);
    }

    @Test
    public void shouldRetrieveComboboxSingleSelectValuesFromDb() {
        Entity entity = getAllEntities().retrieveByClassName(ClassName.getEntityClassName(ENTITY_NAME));
        Field cbField = entity.getField(CB_FIELD_SINGLE_NAME);

        List<String> values = cbValueRepository.getComboboxValuesForStringField(entity.toDto(), cbField.toDto());

        assertNotNull(values);
        assertEquals(asList("five", "four", "one", "two"), values);
    }

    private void setUpTestData() throws Exception {
        MotechDataService service = getService();

        final Object instance1 = objectInstance("one", asList("one", "two"));
        final Object instance2 = objectInstance("two", asList("two", "one", "four"));
        final Object instance3 = objectInstance("four", asList("four", "four", "four"));
        final Object instance4 = objectInstance("five", singletonList("five"));
        final Object instance5 = objectInstance("one", asList("one", "two", "four", "three"));
        final Object instance6 = objectInstance(null, null);
        final Object instance7 = objectInstance(null, singletonList(null));
        final Object instance8 = objectInstance("", singletonList(""));

        service.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                service.create(instance1);
                service.create(instance2);
                service.create(instance3);
                service.create(instance4);
                service.create(instance5);
                service.create(instance6);
                service.create(instance7);
                service.create(instance8);
            }
        });

        assertEquals("There were issues creating test data", 8, service.count());
    }

    private Object objectInstance(String singleValue, List<String> multiValues) throws Exception {
        Class clazz = getEntityClass();
        Object obj = clazz.newInstance();
        PropertyUtil.setProperty(obj, CB_FIELD_SINGLE_NAME, singleValue);
        if (multiValues != null) {
            PropertyUtil.setProperty(obj, CB_FIELD_MULTI_NAME, new ArrayList<>(multiValues));
        }

        return obj;
    }
}
