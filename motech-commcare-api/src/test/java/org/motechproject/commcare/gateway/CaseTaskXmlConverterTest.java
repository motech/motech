package org.motechproject.commcare.gateway;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CaseTask;
import org.motechproject.commcare.domain.CloseTask;
import org.motechproject.commcare.domain.CreateTask;
import org.motechproject.commcare.domain.IndexTask;
import org.motechproject.commcare.domain.UpdateTask;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.request.IndexSubElement;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseTaskXmlConverterTest {

    private CaseTaskXmlConverter caseConverter;

    @Mock
    private EventRelay eventRelay;

    @Before
    public void setUp() {
        initMocks(this);
        caseConverter = new CaseTaskXmlConverter(eventRelay);
    }

    @Test
    public void testShouldRaiseExceptionEventWhenIndexIsMalformed()
            throws FileNotFoundException, CaseParserException {

        CaseTask task = new CaseTask();

        List<IndexSubElement> subElements = new ArrayList<IndexSubElement>();

        subElements.add(new IndexSubElement(null, null, null));

        IndexTask indexTask = new IndexTask(subElements);

        task.setIndexTask(indexTask);

        ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor
                .forClass(MotechEvent.class);

        String xml = caseConverter.convertToCaseXml(task);

        verify(eventRelay).sendEventMessage(
                motechEventCaptor.capture());

        MotechEvent motechEvent = motechEventCaptor.getValue();

        Assert.assertEquals(motechEvent.getSubject(),
                EventSubjects.MALFORMED_CASE_EXCEPTION);

        Assert.assertNull(xml);
    }

    @Test
    public void testShouldRaiseExceptionEventWhenCaseTypeIsMissing()
            throws FileNotFoundException, CaseParserException {

        CaseTask task = new CaseTask();

        CreateTask createTask = new CreateTask();

        createTask.setOwnerId("OWNER_ID");

        createTask.setCaseName("CASE_NAME");

        task.setCreateTask(createTask);

        ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor
                .forClass(MotechEvent.class);

        String xml = caseConverter.convertToCaseXml(task);

        verify(eventRelay).sendEventMessage(
                motechEventCaptor.capture());

        MotechEvent motechEvent = motechEventCaptor.getValue();

        Assert.assertEquals(motechEvent.getSubject(),
                EventSubjects.MALFORMED_CASE_EXCEPTION);

        Assert.assertNull(xml);
    }

    @Test
    public void testShouldRaiseExceptionEventWhenCaseNameIsMissing()
            throws FileNotFoundException, CaseParserException {

        CaseTask task = new CaseTask();

        CreateTask createTask = new CreateTask();

        createTask.setOwnerId("OWNER_ID");

        createTask.setCaseType("CASE_TYPE");

        task.setCreateTask(createTask);

        ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor
                .forClass(MotechEvent.class);

        String xml = caseConverter.convertToCaseXml(task);

        verify(eventRelay).sendEventMessage(
                motechEventCaptor.capture());

        MotechEvent motechEvent = motechEventCaptor.getValue();

        Assert.assertEquals(motechEvent.getSubject(),
                EventSubjects.MALFORMED_CASE_EXCEPTION);

        Assert.assertNull(xml);
    }

    @Test
    public void testCreateCaseXml() throws FileNotFoundException,
            CaseParserException {

        CaseTask task = new CaseTask();

        task.setCaseId("CASE_ID");
        task.setUserId("USER_ID");
        task.setXmlns("XMLNS");
        task.setDateModified("DATE_MODIFIED");

        CreateTask createTask = new CreateTask();

        createTask.setCaseName("CASE_NAME");
        createTask.setCaseType("CASE_TYPE");
        createTask.setOwnerId("OWNER_ID");

        task.setCreateTask(createTask);

        String xml = caseConverter.convertToCaseXml(task);

        Assert.assertTrue(xml.contains("xmlns=\"XMLNS\""));
        Assert.assertTrue(xml.contains("case_id=\"CASE_ID\""));
        Assert.assertTrue(xml.contains("date_modified=\"DATE_MODIFIED\""));
        Assert.assertTrue(xml.contains("user_id=\"USER_ID\""));
        Assert.assertTrue(xml.contains("<create>"));
        Assert.assertTrue(xml.contains("</create>"));
        Assert.assertTrue(xml.contains("<case_type>CASE_TYPE</case_type>"));
        Assert.assertTrue(xml.contains("<case_name>CASE_NAME</case_name>"));
        Assert.assertTrue(xml.contains("<owner_id>OWNER_ID</owner_id>"));
    }

    @Test
    public void testUpdateCaseXml() throws FileNotFoundException,
            CaseParserException {

        CaseTask task = new CaseTask();

        task.setCaseId("CASE_ID");
        task.setUserId("USER_ID");
        task.setXmlns("XMLNS");
        task.setDateModified("DATE_MODIFIED");

        UpdateTask updateTask = new UpdateTask();

        Map<String, String> fieldValues = new HashMap<String, String>();

        fieldValues.put("KEY1", "VALUE1");
        fieldValues.put("KEY2", "VALUE2");
        fieldValues.put("KEY3", "VALUE3");

        updateTask.setCaseName("CASE_NAME");
        updateTask.setCaseType("CASE_TYPE");
        updateTask.setDateOpened("DATE_OPENED");
        updateTask.setFieldValues(fieldValues);
        updateTask.setOwnerId("OWNER_ID");

        task.setUpdateTask(updateTask);

        String xml = caseConverter.convertToCaseXml(task);

        Assert.assertTrue(xml.contains("xmlns=\"XMLNS\""));
        Assert.assertTrue(xml.contains("case_id=\"CASE_ID\""));
        Assert.assertTrue(xml.contains("date_modified=\"DATE_MODIFIED\""));
        Assert.assertTrue(xml.contains("user_id=\"USER_ID\""));
        Assert.assertTrue(xml.contains("<update>"));
        Assert.assertTrue(xml.contains("</update>"));
        Assert.assertTrue(xml.contains("<case_type>CASE_TYPE</case_type>"));
        Assert.assertTrue(xml.contains("<case_name>CASE_NAME</case_name>"));
        Assert.assertTrue(xml.contains("<owner_id>OWNER_ID</owner_id>"));
        Assert.assertTrue(xml.contains("<KEY2>VALUE2</KEY2>"));
        Assert.assertTrue(xml.contains("<KEY1>VALUE1</KEY1>"));
        Assert.assertTrue(xml.contains("<KEY3>VALUE3</KEY3>"));
    }

    @Test
    public void testCloseCaseXml() throws FileNotFoundException,
            CaseParserException {

        CaseTask task = new CaseTask();

        task.setCloseTask(new CloseTask(true));

        task.setCaseId("CASE_ID");

        String xml = caseConverter.convertToCaseXml(task);

        Assert.assertTrue(xml.contains("<close/>"));
    }

    @Test
    public void testCorrectIndicesCaseXml() throws FileNotFoundException,
            CaseParserException {

        CaseTask task = new CaseTask();

        List<IndexSubElement> subElements = new ArrayList<IndexSubElement>();

        subElements.add(new IndexSubElement("caseId1", "type1",
                "indexNodeName1"));
        subElements.add(new IndexSubElement("caseId2", "type2",
                "indexNodeName2"));
        subElements.add(new IndexSubElement("caseId3", "type3",
                "indexNodeName3"));

        IndexTask indexTask = new IndexTask(subElements);

        task.setIndexTask(indexTask);

        String xml = caseConverter.convertToCaseXml(task);

        Assert.assertTrue(xml
                .contains("<indexNodeName1 case_type=\"type1\">caseId1</indexNodeName1>"));
        Assert.assertTrue(xml
                .contains("<indexNodeName2 case_type=\"type2\">caseId2</indexNodeName2>"));
        Assert.assertTrue(xml
                .contains("<indexNodeName3 case_type=\"type3\">caseId3</indexNodeName3>"));
    }

    @Test
    public void testNoIndexElementIncludedWhenNoIndices()
            throws FileNotFoundException, CaseParserException {

        CaseTask task = new CaseTask();

        List<IndexSubElement> subElements = new ArrayList<IndexSubElement>();

        IndexTask indexTask = new IndexTask(subElements);

        task.setIndexTask(indexTask);

        String xml = caseConverter.convertToCaseXml(task);

        Assert.assertFalse(xml.contains("index"));

    }
}
