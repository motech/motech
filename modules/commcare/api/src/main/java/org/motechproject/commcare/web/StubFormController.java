package org.motechproject.commcare.web;

import com.google.gson.JsonParseException;
import org.motechproject.commcare.domain.FormStubJson;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller that handles the incoming stub form feed from CommCareHQ. Maps to
 * /commcare/stubforms.
 */
@Controller
public class StubFormController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private EventRelay eventRelay;

    private MotechJsonReader jsonReader;

    @Autowired
    public StubFormController(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
        jsonReader = new MotechJsonReader();
    }

    @RequestMapping({ "/stub" })
    public ModelAndView receiveFormEvent(@RequestBody String body, HttpServletResponse response) {


        FormStubJson formStub = null;

        try {
            formStub = (FormStubJson) jsonReader.readFromString(body, FormStubJson.class);
        } catch (JsonParseException e) {
            logger.warn("Unable to parse Json: " + e.getMessage());
            MotechEvent formFailEvent = new MotechEvent(EventSubjects.FORM_STUB_FAIL_EVENT);
            eventRelay.sendEventMessage(formFailEvent);
            return null;
        }

        if (formStub != null) {
            MotechEvent formEvent = new MotechEvent(EventSubjects.FORM_STUB_EVENT);

            formEvent.getParameters().put(EventDataKeys.RECEIVED_ON, formStub.getReceivedOn());
            formEvent.getParameters().put(EventDataKeys.FORM_ID, formStub.getFormId());
            formEvent.getParameters().put(EventDataKeys.CASE_IDS, formStub.getCaseIds());

            eventRelay.sendEventMessage(formEvent);
        }

        return null;
    }
}
