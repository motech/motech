package org.motechproject.commcare.service.impl;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.util.CommCareAPIHttpClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommcareFormServiceImplTest {

    private CommcareFormServiceImpl formService;
    @Mock
    private CommCareAPIHttpClient commcareHttpClient;

    @Before
    public void setUp() {
        initMocks(this);
        formService = new CommcareFormServiceImpl(commcareHttpClient);
    }

    @Test
    public void testFormOne() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonRegistrationForm());

        CommcareForm form = formService.retrieveForm("testForm1");

        assertEquals(form.getId(), "4432638b-8f77-42f5-8c65-9fc18b26fb09");
        assertEquals(form.getMd5(), "75cb64ec8ead19684895e0822a960149");
        assertEquals(form.getResourceUri(), "");
        assertEquals(form.getType(), "data");
        assertEquals(form.getUiversion(), "1");
        assertEquals(form.getVersion(), "1");

        Map<String, String> metaData = form.getMetadata();

        assertNotNull(metaData);
        assertEquals(metaData.size(), 9);

        assertEquals(metaData.get("@xmlns"), "http://openrosa.org/jr/xforms");
        assertEquals(
                metaData.get("appVersion"),
                "@xmlns:http://commcarehq.org/xforms, #text:CommCare ODK, version \"2.0\"(1090). CommCare Version 2.0. Build #1090, built on: May-23-2012");
        assertEquals(metaData.get("deviceID"), "A000002A46308E");
        assertEquals(metaData.get("instanceID"), "4432638b-8f77-42f5-8c65-9fc18b26fb09");
        assertEquals(metaData.get("timeEnd"), "2012-06-22T14:26:54");
        assertEquals(metaData.get("timeStart"), "2012-06-22T14:26:01");
        assertEquals(metaData.get("userID"), "abc707434d4ec780967fa65b7167cc58");
        assertEquals(metaData.get("username"), "test");
        assertNull(metaData.get("deprecatedID"));

        FormValueElement rootElement = form.getForm();

        Map<String, String> topLevelAttributes = rootElement.getAttributes();

        assertEquals(topLevelAttributes.size(), 4);
        assertEquals(rootElement.getValue(), "data");

        List<FormValueElement> elements = rootElement.getElementsByAttribute("concept_id", "5596");
        assertEquals(elements.size(), 1);

        assertEquals("form", rootElement.getElementName());

        Multimap<String, FormValueElement> subElements = rootElement.getSubElements();
        assertNotNull(subElements.get("case"));
        assertEquals(subElements.size(), 15);

        Collection<FormValueElement> childElement = subElements.get("family_planning_acceptance");
        FormValueElement firstElement = childElement.iterator().next();
        assertNotNull(firstElement);
        assertEquals(firstElement.getSubElements().size(), 3);
    }

    @Test
    public void testFormTwo() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonFormTwo());

        CommcareForm form = formService.retrieveForm("testForm2");

        FormValueElement rootElement = form.getForm();

        List<FormValueElement> elementsByName = rootElement.getAllElements("top_level_value_1");
        assertEquals(2, elementsByName.size());

        elementsByName = rootElement.getChildElements("value3");
        assertEquals(1, elementsByName.size());
        assertEquals("no", elementsByName.get(0).getValue());


        elementsByName = rootElement.getAllElements("value3");
        assertEquals(3, elementsByName.size());

        List<String> restrictedElements = new ArrayList<>();
        restrictedElements.add("registration");
        elementsByName = rootElement.getAllElements("value3", restrictedElements);
        assertEquals(2, elementsByName.size());
        assertEquals("no", elementsByName.get(0).getValue());
        assertEquals("innervalue", elementsByName.get(1).getValue());

        elementsByName = rootElement.getAllElements("noName");
        assertEquals(0, elementsByName.size());

        elementsByName = rootElement.getAllElements("case");
        assertEquals(2, elementsByName.size());

        FormValueElement caseElementOne = elementsByName.get(0);

        FormValueElement caseElementTwo = elementsByName.get(1);

        assertEquals(1, caseElementOne.getSubElements().size());

        assertEquals(3, caseElementTwo.getSubElements().size());

        assertNull(caseElementOne.getElement("create"));

        assertNotNull(caseElementTwo.getElement("create"));
    }

    @Test
    public void shouldSearchFromStartElement() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonTestForm());

        CommcareForm form = formService.retrieveForm("testForm");

        FormValueElement rootElement = form.getForm();

        List<String> restrictedElements = new ArrayList<>();
        restrictedElements.add("restricted");
        restrictedElements.add("anotherRestricted");
        FormValueElement value = rootElement.getElement("value", restrictedElements);
        assertEquals("1", value.getValue());

        FormValueElement subelement1 = rootElement.getElement("subelement1");
        FormValueElement valueInsideSublement1 = subelement1.getElement("value", restrictedElements);
        assertEquals("4", valueInsideSublement1.getValue());

        FormValueElement valueWithoutRestriction = subelement1.getElement("value");
        assertEquals("2", valueWithoutRestriction.getValue());

    }

    @Test
    public void shouldSearchFromRootPath() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonTestForm());
        CommcareForm form = formService.retrieveForm("testForm");
        FormValueElement rootElement = form.getForm();

        FormNode elementByPath = rootElement.searchFirst("//subelement1/value");

        assertEquals("4", elementByPath.getValue());

    }

    @Test
    public void shouldSearchFromRootPathWithoutRestrictedElements() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonTestForm());
        CommcareForm form = formService.retrieveForm("testForm");
        FormValueElement rootElement = form.getForm();

        FormNode elementByPath = rootElement.searchFirst("/form/restricted");

        assertNull(elementByPath);

    }

    @Test
    public void shouldSearchByCurrentPath() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonTestForm());
        CommcareForm form = formService.retrieveForm("testForm");
        FormValueElement rootElement = form.getForm().getElement("subelement1");

        FormNode elementByCurrentPath = rootElement.searchFirst("//validElement/value");

        assertEquals("5", elementByCurrentPath.getValue());
    }

    @Test
    public void shouldSearchForAttributes() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonRegistrationForm());
        CommcareForm form = formService.retrieveForm("testForm");
        FormValueElement rootElement = form.getForm();

        FormNode elementByPath = rootElement.searchFirst("//case/@case_id");

        assertEquals("6bc2f8f6-b1da-4be2-98d4-1cb2d557a329", elementByPath.getValue());

    }

    @Test
    public void shouldSearchForValue() {
        when(commcareHttpClient.formRequest(Matchers.anyString())).thenReturn(jsonRegistrationForm());
        CommcareForm form = formService.retrieveForm("testForm");
        FormValueElement rootElement = form.getForm();

        FormNode elementByPath = rootElement.searchFirst("//#");

        assertEquals("data", elementByPath.getValue());

    }

    private String jsonRegistrationForm() {
        return "{\"form\":{\"#type\":\"data\",\"@name\":\"Register Pregnancy\",\"@uiVersion\":\"1\",\"@version\":\"1\",\"@xmlns\":\"http://openrosa.org/formdesigner/882FC273-E436-4BA1-B8CC-9CA526FFF8C2\",\"case\":{\"@case_id\":\"6bc2f8f6-b1da-4be2-98d4-1cb2d557a329\",\"@date_modified\":\"2012-06-22T14:26:54\",\"@user_id\":\"abc707434d4ec780967fa65b7167cc58\",\"@xmlns\":\"http://commcarehq.org/case/transaction/v2\",\"create\":{\"case_name\":\"Russell Gillen\",\"case_type\":\"pregnancy\",\"owner_id\":\"abc707434d4ec780967fa65b7167cc58\"},\"update\":{\"dob\":\"1996-06-22\",\"dob_calc\":\"1996-06-22\",\"dob_known\":\"yes\",\"edd_calc\":\"2012-12-23\",\"edd_known\":\"no\",\"external_id\":\"Reee\",\"first_name\":\"Russell\",\"full_name\":\"Russell Gillen\",\"health_id\":\"Reee\",\"household_head_health_id\":\"Fee\",\"mobile_phone_number\":\"2525252222\",\"pregnancy_month\":\"3\",\"surname\":\"Gillen\"}},\"dob\":\"1996-06-22\",\"dob_calc\":\"1996-06-22\",\"dob_known\":\"yes\",\"family_planning_acceptance\":{\"new_acceptors\":\"2\",\"repeat_acceptors\":{\"sublevel1\":{\"sublevel2\":[{\"#text\":\"4\",\"@conceptId\":\"4321\",\"@field\":\"blah\",\"@field2\":\"blah2\"},{\"#text\":\"3\",\"@conceptId\":\"1234\"}]}},\"total_acceptors\":\"2\"},\"edd_calc\":{\"#text\":\"2012-12-23\",\"@concept_id\":\"5596\"},\"edd_known\":\"no\",\"first_name\":\"Russell\",\"full_name\":\"Russell Gillen\",\"health_id\":\"Reee\",\"household_head_health_id\":\"Fee\",\"meta\":{\"@xmlns\":\"http://openrosa.org/jr/xforms\",\"appVersion\":{\"#text\":\"CommCare ODK, version \\\"2.0\\\"(1090). CommCare Version 2.0. Build #1090, built on: May-23-2012\",\"@xmlns\":\"http://commcarehq.org/xforms\"},\"deviceID\":\"A000002A46308E\",\"instanceID\":\"4432638b-8f77-42f5-8c65-9fc18b26fb09\",\"timeEnd\":\"2012-06-22T14:26:54\",\"timeStart\":\"2012-06-22T14:26:01\",\"userID\":\"abc707434d4ec780967fa65b7167cc58\",\"username\":\"test\"},\"mobile_phone_number\":{\"#text\":\"2525252222\",\"@concept_id\":\"159635\"},\"pregnancy_month\":\"3\",\"surname\":\"Gillen\"},\"id\":\"4432638b-8f77-42f5-8c65-9fc18b26fb09\",\"md5\":\"75cb64ec8ead19684895e0822a960149\",\"metadata\":{\"@xmlns\":\"http://openrosa.org/jr/xforms\",\"appVersion\":\"@xmlns:http://commcarehq.org/xforms, #text:CommCare ODK, version \\\"2.0\\\"(1090). CommCare Version 2.0. Build #1090, built on: May-23-2012\",\"deprecatedID\":null,\"deviceID\":\"A000002A46308E\",\"instanceID\":\"4432638b-8f77-42f5-8c65-9fc18b26fb09\",\"timeEnd\":\"2012-06-22T14:26:54\",\"timeStart\":\"2012-06-22T14:26:01\",\"userID\":\"abc707434d4ec780967fa65b7167cc58\",\"username\":\"test\"},\"resource_uri\":\"\",\"type\":\"data\",\"uiversion\":\"1\",\"version\":\"1\"}";
    }

    private String jsonFormTwo() {
        return "{\"form\":{\"#type\":\"data\",\"@name\":\"Test form\",\"@uiVersion\":\"1\",\"@version\":\"100\",\"@xmlns\":\"http://openrosa.org/formdesigner/1\",\"case\":{\"@case_id\":\"case1\",\"@date_modified\":\"2013-01-29T13:08:23\",\"@user_id\":\"ABC\",\"@xmlns\":\"http://commcarehq.org/case/transaction/v2\",\"update\":{\"date_of_visit\":\"2013-01-29\",\"total_children\":\"\",\"visit_type\":\"registration\"}},\"top_level_value_1\":\"2013-01-29\",\"top_level_value_2\":\"123\",\"meta\":{\"@xmlns\":\"http://openrosa.org/jr/xforms\",\"appVersion\":{\"#text\":\"v2.3.0 (6dcc56-6c6a74-unvers-2.1.0-Nokia/S40-generic) build 881 App #881 b:2012-Dec-17 r:2013-Jan-25\",\"@xmlns\":\"http://commcarehq.org/xforms\"},\"deviceID\":\"ABCDEF\",\"instanceID\":\"eee444fffgg\",\"timeEnd\":\"2013-01-29T13:08:23\",\"timeStart\":\"2013-01-29T13:06:33\",\"userID\":\"ABC\",\"username\":\"Motech\"},\"value3\":\"no\",\"registration\":{\"value3\":\"yes\"},\"subcase_0\":{\"case\":{\"@case_id\":\"case2\",\"@date_modified\":\"2013-01-29T13:08:23\",\"@user_id\":\"ABC\",\"@xmlns\":\"http://commcarehq.org/case/transaction/v2\",\"create\":{\"total_children\":\"5\",\"value3\":\"innervalue\"},\"index\":{\"parent\":{\"#text\":\"case1\",\"@case_type\":\"mother\"}},\"update\":{\"updateField\":\"updateValue\",\"top_level_value_1\":\"5\"}}},\"value_4\":\"\",\"top_level_value_1\":\"\"},\"id\":\"2645\",\"md5\":\"24634634\",\"metadata\":{\"@xmlns\":\"http://openrosa.org/jr/xforms\",\"appVersion\":\"@xmlns:http://commcarehq.org/xforms, #text:v2.3.0 (6dcc56-6c6a74-unvers-2.1.0-Nokia/S40-generic) build 881 App #881 b:2012-Dec-17 r:2013-Jan-25\",\"deprecatedID\":null,\"deviceID\":\"BCDEF\",\"instanceID\":\"werwer\",\"timeEnd\":\"2013-01-29T13:08:23\",\"timeStart\":\"2013-01-29T13:06:33\",\"userID\":\"ABC\",\"username\":\"marie\"},\"received_on\":\"2013-01-29T13:08:30\",\"resource_uri\":\"\",\"type\":\"data\",\"uiversion\":\"1\",\"version\":\"111\"}";
    }

    private String jsonTestForm() {
        return "{\"form\":{\"value\":\"1\",\"subelement1\":{\"restricted\":{\"value\":\"2\"},\"anotherRestricted\":{\"value\":\"3\"},\"value\":\"4\",\"validElement\":{\"value\":\"5\"}}}}";
    }
}
