package org.motechproject.commcare.web;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.motechproject.commcare.domain.CaseXml;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commcare.events.CaseEvent;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.parser.CaseParser;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles the incoming case feed from CommCareHQ. Maps to
 * /commcare/cases.
 */
@Controller
public class CommcareController {
    @Value("#{case_event_strategy['case.events.send.with.all.data']}")
    private String caseEventStrategy;
    private static final String FULL_DATA_EVENT = "full";
    private static final String PARTIAL_DATA_EVENT = "partial";

    @Autowired
    private EventRelay eventRelay;

    private String getRequestBodyAsString(HttpServletRequest request)
            throws IOException {
        BufferedReader reader = request.getReader();
        boolean end = false;
        String forwardedRequest = "";
        while (!end) {
            String line = reader.readLine();
            if (line == null) {
                end = true;
            } else {
                forwardedRequest = forwardedRequest + line;
            }
        }

        return forwardedRequest;
    }

    @RequestMapping({ "/cases" })
    public ModelAndView testCases(HttpServletRequest request,
            HttpServletResponse response) {

        String caseXml = "";

        try {
            caseXml = getRequestBodyAsString(request);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        CaseParser<CaseXml> parser = new CaseParser<CaseXml>(CaseXml.class,
                caseXml);

        CaseXml caseInstance = null;
        try {
            caseInstance = (CaseXml) parser.parseCase();
        } catch (CaseParserException e) {
            MotechEvent motechEvent = new MotechEvent(
                    EventSubjects.MALFORMED_CASE_EXCEPTION);
            motechEvent.getParameters().put(EventDataKeys.MESSAGE,
                    "Incoming case xml did not parse correctly");
            eventRelay.sendEventMessage(motechEvent);
        }

        if (caseInstance != null) {
            CaseEvent caseEvent = new CaseEvent(caseInstance.getCaseId());

            MotechEvent motechCaseEvent = null;

            if (caseEventStrategy.equals(FULL_DATA_EVENT)) {
                caseEvent = caseEvent.eventFromCase(caseInstance);
                motechCaseEvent = caseEvent.toMotechEventWithData();
                motechCaseEvent.getParameters().put("fieldValues",
                        caseEvent.getFieldValues());
            }
            if (caseEventStrategy.equals(PARTIAL_DATA_EVENT)) {
                motechCaseEvent = caseEvent.toMotechEventWithData();
            } else {
                motechCaseEvent = caseEvent.toMotechEventWithoutData();
            }

            eventRelay.sendEventMessage(motechCaseEvent);
        }

        return null;
    }

    public void setEventRelay(
            EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }
}
