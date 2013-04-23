package org.motechproject.commcare.web;

import com.google.common.collect.Multimap;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.exception.FullFormParserException;
import org.motechproject.commcare.parser.FullFormParser;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.commcare.events.constants.EventDataKeys.ATTRIBUTES;
import static org.motechproject.commcare.events.constants.EventDataKeys.ELEMENT_NAME;
import static org.motechproject.commcare.events.constants.EventDataKeys.SUB_ELEMENTS;
import static org.motechproject.commcare.events.constants.EventDataKeys.VALUE;
import static org.motechproject.commcare.events.constants.EventSubjects.FORMS_EVENT;
import static org.motechproject.commcare.events.constants.EventSubjects.FORMS_FAIL_EVENT;

/**
 * Controller that handles the incoming full form feed from CommCareHQ.
 */
@Controller
public class FullFormController {
    private EventRelay eventRelay;

    @Autowired
    public FullFormController(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    @RequestMapping(value = "/forms")
    @ResponseStatus(HttpStatus.OK)
    public void receiveForm(@RequestBody String body) {
        FullFormParser parser = new FullFormParser(body);
        FormValueElement formValueElement = null;

        try {
            formValueElement = parser.parse();
        } catch (FullFormParserException e) {
            eventRelay.sendEventMessage(new MotechEvent(FORMS_FAIL_EVENT));
        }

        if (formValueElement != null) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(ATTRIBUTES, formValueElement.getAttributes());
            parameters.put(SUB_ELEMENTS, convertToMap(formValueElement.getSubElements()));

            eventRelay.sendEventMessage(new MotechEvent(FORMS_EVENT, parameters));
        }
    }

    private Map<String, Map<String, Object>> convertToMap(Multimap<String, FormValueElement> subElements) {
        Map<String, Map<String, Object>> elements = new HashMap<>();

        for (Map.Entry<String, FormValueElement> entry : subElements.entries()) {
            Map<String, Object> elementAsMap = new HashMap<>(3);
            FormValueElement formValueElement = entry.getValue();

            elementAsMap.put(ELEMENT_NAME, formValueElement.getElementName());
            elementAsMap.put(SUB_ELEMENTS, convertToMap(formValueElement.getSubElements()));
            elementAsMap.put(ATTRIBUTES, formValueElement.getAttributes());
            elementAsMap.put(VALUE, formValueElement.getValue());

            elements.put(entry.getKey(), elementAsMap);
        }

        return elements;
    }
}
