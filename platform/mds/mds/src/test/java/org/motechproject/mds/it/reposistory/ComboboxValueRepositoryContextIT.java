package org.motechproject.mds.it.reposistory;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Autowired
    private ComboboxValueRepository cbValueRepository;

    @Autowired
    private PersistenceManagerFactory persistenceManagerFactory;

    @Override
    protected String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    protected List<FieldDto> getEntityFields() {
        // combobox with multiple selection - string collection field
        FieldDto cbFieldMulti = new FieldDto(CB_FIELD_MULTI_NAME, CB_FIELD_MULTI_NAME, TypeDto.COLLECTION, false,
                null, "tooltip", "placeholder");
        cbFieldMulti.addSetting(new SettingDto(Constants.Settings.ALLOW_USER_SUPPLIED, Constants.Util.TRUE));
        cbFieldMulti.addSetting(new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, Constants.Util.TRUE));

        // combobox without multiple selection - string field
        FieldDto cbFieldSingle = new FieldDto(CB_FIELD_SINGLE_NAME, CB_FIELD_SINGLE_NAME, TypeDto.COLLECTION, false,
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
    }

    @Test
    public void shouldRetrieveComboboxMultiSelectValuesFromDb() {
        // This service is normally taken from the generated entities bundle
        MetadataService metadataService = new MetadataServiceImpl();
        ReflectionTestUtils.setField(metadataService, "persistenceManagerFactory", persistenceManagerFactory);

        String cbTableName = metadataService.getComboboxTableName(ClassName.getEntityClassName(ENTITY_NAME), CB_FIELD_MULTI_NAME);

        List<String> values = cbValueRepository.getComboboxValuesForCollection(cbTableName);

        assertNotNull(values);
        assertEquals(asList("one", "two", "four", "five", "three"), values);
    }

    @Test
    public void shouldRetrieveComboboxSingleSelectValuesFromDb() {
        Entity entity = getAllEntities().retrieveByClassName(ClassName.getEntityClassName(ENTITY_NAME));
        Field cbField = entity.getField(CB_FIELD_SINGLE_NAME);

        List<String> values = cbValueRepository.getComboboxValuesForStringField(entity, cbField);

        assertNotNull(values);
        assertEquals(asList("one", "two", "four", "five"), values);
    }

    private void setUpTestData() throws Exception {
        MotechDataService service = getService();

        service.create(objectInstance("one", asList("one", "two")));
        service.create(objectInstance("two", asList("two", "one", "four")));
        service.create(objectInstance("four", asList("four", "four", "four")));
        service.create(objectInstance("five", singletonList("five")));
        service.create(objectInstance("one", asList("one", "two", "four", "three")));
        service.create(objectInstance(null, null));
        service.create(objectInstance(null, singletonList(null)));
        service.create(objectInstance("", singletonList("")));

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
