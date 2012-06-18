package org.motechproject.commcare.web;

import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CaseTask;
import org.motechproject.commcare.domain.CaseXml;
import org.motechproject.commcare.domain.CloseTask;
import org.motechproject.commcare.domain.CreateTask;
import org.motechproject.commcare.domain.IndexTask;
import org.motechproject.commcare.domain.UpdateTask;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.events.CaseEvent;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.parser.CaseParser;
import org.motechproject.commcare.request.IndexSubElement;
import org.motechproject.commcare.response.OpenRosaResponse;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.motechproject.scheduler.domain.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CommcareController  {

    @Value("#{case_event_strategy['case.events.send.with.all.data']}")
    private String caseEventStrategy;

    private static final String FULL_DATA_EVENT = "full";
    private static final String PARTIAL_DATA_EVENT = "partial";

    @Autowired
    private CommcareCaseService commcareCaseService;

    private OutboundEventGateway outboundEventGateway;

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    private String getRequestBodyAsString(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        boolean end = false;
        String forwardedRequest = "";
        while (!end) {
            String line = reader.readLine();
            if (line == null) { end = true; } else {
                forwardedRequest += line;
            }
        }

        return forwardedRequest;
    }

    /**
     * For testing/demonstration only
     */
    @RequestMapping("/testcase")
    public ModelAndView testSubmission(HttpServletRequest request, HttpServletResponse response) {

        String now = DateTime.now().toString();

        CaseTask caseTask = new CaseTask();
        caseTask.setCaseId("CASEID3");
        caseTask.setDateModified(now);
        caseTask.setUserId("e6f25c2928a54d1cf51a39263e1a4e10");
        caseTask.setXmlns("XMLNSHERE");

        CreateTask createTask = new CreateTask();
        createTask.setCaseName("CASENAME");
        createTask.setCaseType("CASETYPE");
        createTask.setOwnerId("OWNERID");

        //caseTask.setCreateTask(createTask);

        UpdateTask updateTask = new UpdateTask();
        updateTask.setCaseName("UPDATEDNAME");
        updateTask.setCaseType("UPDATEDTYPE");
        updateTask.setDateOpened(now);
        updateTask.setOwnerId("UPDATEDOWNERID");

        Map<String, String> fieldValues = new HashMap<String, String>();

        fieldValues.put("fieldONE", "valueOne");
        fieldValues.put("fieldTWO", "valueTwo");
        fieldValues.put("fieldThREE", "valueThree");

        updateTask.setFieldValues(fieldValues);

        //		caseTask.setUpdateTask(updateTask);

        List<IndexSubElement> indices = new ArrayList<IndexSubElement>();

        IndexSubElement elementOne = new IndexSubElement("elementOne", "caseTypeOne", "idOne");
        IndexSubElement elementTwo = new IndexSubElement("elementTwo", "caseTypeTwo", "idTwo");
        IndexSubElement elementThree = new IndexSubElement("elementThree", "caseTypeThree", "idThree");

        //		indices.add(elementOne);
        //		indices.add(elementTwo);
        //		indices.add(elementThree);

        IndexTask indexTask = new IndexTask(indices);

        caseTask.setIndexTask(indexTask);

        CloseTask closeTask = new CloseTask(true);

        caseTask.setCloseTask(closeTask);

        OpenRosaResponse openRosaResponse = commcareCaseService.uploadCase(caseTask);

        System.out.println(openRosaResponse.getStatus());
        System.out.println(openRosaResponse.getMessageNature());
        System.out.println(openRosaResponse.getMessageText());

        return null;
    }

    @RequestMapping("/cases")
    public ModelAndView testCases(HttpServletRequest request, HttpServletResponse response) {
        String caseXml = "";

        try {
            caseXml = getRequestBodyAsString(request);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class, caseXml);


        CaseXml caseInstance = null;

        try {
            caseInstance = (CaseXml) parser.parseCase();
        } catch (CaseParserException e) {

        }

        if (caseInstance != null) {

            CaseEvent caseEvent = new CaseEvent(caseInstance.getCase_id());

            MotechEvent motechCaseEvent = null;

            if (caseEventStrategy.equals(FULL_DATA_EVENT)) {
                caseEvent = caseEvent.eventFromCase(caseInstance);
                motechCaseEvent = caseEvent.toMotechEventWithData();
                motechCaseEvent.getParameters().put(EventDataKeys.FIELD_VALUES, caseEvent.getFieldValues());
            } if (caseEventStrategy.equals(PARTIAL_DATA_EVENT)) {
                motechCaseEvent = caseEvent.toMotechEventWithData();
            } else {
                motechCaseEvent = caseEvent.toMotechEventWithoutData();
            }

            outboundEventGateway.sendEventMessage(motechCaseEvent);
        }

        return null;
    }

    public void setOutboundEventGateway(OutboundEventGateway outboundEventGateway) {
        this.outboundEventGateway = outboundEventGateway;
    }




}