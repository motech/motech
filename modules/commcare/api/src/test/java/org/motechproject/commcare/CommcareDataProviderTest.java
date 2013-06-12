package org.motechproject.commcare;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.MotechObject;
import org.springframework.core.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.CommcareFixture;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.impl.CommcareCaseServiceImpl;
import org.motechproject.commcare.service.impl.CommcareFixtureServiceImpl;
import org.motechproject.commcare.service.impl.CommcareFormServiceImpl;
import org.motechproject.commcare.service.impl.CommcareUserServiceImpl;


public class CommcareDataProviderTest {
    private static final String FIELD_KEY = "id";
    private static final String FIELD_VALUE = "12345";

    private static Map<String, String> lookupFields;

    @Mock
    private CommcareUser commcareUser;

    @Mock
    private CommcareFixture commcareFixture;

    @Mock
    private CommcareForm commcareForm;

    @Mock
    private CaseInfo caseInfo;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private CommcareFormServiceImpl commcareFormService;

    @Mock
    private CommcareUserServiceImpl commcareUserService;

    @Mock
    private CommcareFixtureServiceImpl commcareFixtureService;

    @Mock
    private CommcareCaseServiceImpl commcareCaseService;



    private CommcareDataProvider provider;

    @BeforeClass
    public static void setLookupFields() {
        lookupFields = new HashMap<>();
        lookupFields.put(FIELD_KEY, FIELD_VALUE);
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(resourceLoader.getResource("task-data-provider.json")).thenReturn(null);

        provider = new CommcareDataProvider(resourceLoader);
        provider.setCommcareUserService(commcareUserService);
        provider.setCommcareFormService(commcareFormService);
        provider.setCommcareCaseService(commcareCaseService);
        provider.setCommcareFixtureService(commcareFixtureService);
    }

    @Test
    public void shouldReturnNullWhenClassIsNotSupported() {
        // given
        String clazz = MotechObject.class.getSimpleName();

        // when
        Object object = provider.lookup(clazz, lookupFields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenMapNotContainsSupportedField() {
        // given
        String clazz = CommcareUser.class.getSimpleName();
        HashMap<String, String> fields = new HashMap<>();

        // when
        Object object = provider.lookup(clazz, fields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenListIsNull() {
        // given
        String commcareUserClass = CommcareUser.class.getSimpleName();
        String commcareFormClass = CommcareForm.class.getSimpleName();
        String commcareFixtureClass = CommcareFixture.class.getSimpleName();
        String caseInfoClass = CaseInfo.class.getSimpleName();


        // when
        Object userContent = provider.lookup(commcareUserClass, lookupFields);
        Object formContent = provider.lookup(commcareFormClass, lookupFields);
        Object fixtureContent = provider.lookup(commcareFixtureClass, lookupFields);
        Object caseContent = provider.lookup(caseInfoClass, lookupFields);

        // then
        assertNull(userContent);
        assertNull(formContent);
        assertNull(fixtureContent);
        assertNull(caseContent);
    }

    @Test
    public void shouldReturnNullWhenListIsEmpty() {
        // given
        String commcareUserClass = CommcareUser.class.getSimpleName();
        String commcareFormClass = CommcareForm.class.getSimpleName();
        String commcareFixtureClass = CommcareFixture.class.getSimpleName();
        String caseInfoClass = CaseInfo.class.getSimpleName();


        // when
        Object userContent = provider.lookup(commcareUserClass, lookupFields);
        Object formContent = provider.lookup(commcareFormClass, lookupFields);
        Object fixtureContent = provider.lookup(commcareFixtureClass, lookupFields);
        Object caseContent = provider.lookup(caseInfoClass, lookupFields);

        // then
        assertNull(userContent);
        assertNull(formContent);
        assertNull(fixtureContent);
        assertNull(caseContent);
    }

    @Test
    public void shouldReturnObject() {
        // given
        String commcareUserClass = CommcareUser.class.getSimpleName();
        String commcareFormClass = CommcareForm.class.getSimpleName();
        String commcareFixtureClass = CommcareFixture.class.getSimpleName();
        String caseInfoClass = CaseInfo.class.getSimpleName();

        when(commcareUserService.getCommcareUserById(FIELD_VALUE)).thenReturn(commcareUser);
        when(commcareFixtureService.getCommcareFixtureById(FIELD_VALUE)).thenReturn(commcareFixture);
        when(commcareFormService.retrieveForm(FIELD_VALUE)).thenReturn(commcareForm);
        when(commcareCaseService.getCaseByCaseId(FIELD_VALUE)).thenReturn(caseInfo);

        // when
        CommcareUser commcareUser1 = (CommcareUser) provider.lookup(commcareUserClass, lookupFields);
        CommcareForm commcareForm1 = (CommcareForm) provider.lookup(commcareFormClass, lookupFields);
        CommcareFixture commcareFixture1 = (CommcareFixture) provider.lookup(commcareFixtureClass, lookupFields);
        CaseInfo caseInfo1 = (CaseInfo) provider.lookup(caseInfoClass, lookupFields);

        // then
        assertEquals(this.commcareUser, commcareUser1);
        assertEquals(this.commcareForm, commcareForm1);
        assertEquals(this.commcareFixture, commcareFixture1);
        assertEquals(this.caseInfo, caseInfo1);
    }
}
