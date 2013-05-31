package org.motechproject.commcare.builder;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class CommcareFormBuilder {


    private static final String DEFAULT_ROOT_ELEMENT = "form";

    private static final String UIVERSION_ATTRIBUTE_NAME = "uiVersion";
    private static final String VERSION_ATTRIBUTE_NAME = "version";
    private static final String INSTANCE_ID_ATTRIBUTE_NAME = "instanceID";

    public CommcareForm buildFrom(MotechEvent motechEvent) {
        FormValueElement rootElement = toFormValueElement(motechEvent.getParameters());
        fixRootElementName(rootElement);

        CommcareForm form = new CommcareForm();
        form.setForm(rootElement);
        populateFormFields(rootElement, form);
        return form;
    }

    private void populateFormFields(FormValueElement rootElement, CommcareForm form) {
        Map<String, String> meta = getFormMeta(rootElement);
        form.setMetadata(meta);
        form.setUiversion(rootElement.getAttributes().get(UIVERSION_ATTRIBUTE_NAME));
        form.setVersion(rootElement.getAttributes().get(VERSION_ATTRIBUTE_NAME));
        form.setId(meta.get(INSTANCE_ID_ATTRIBUTE_NAME));
    }


    private void fixRootElementName(FormValueElement rootElement) {
        if (rootElement.getElementName() ==  null) {
            rootElement.setElementName(DEFAULT_ROOT_ELEMENT);
        }
    }

    private Multimap<String, FormValueElement> toFormValueElements(Multimap<String, Map<String, Object>> elements) {
        if (elements == null) {
            return new LinkedHashMultimap<>();
        }

        LinkedHashMultimap<String, FormValueElement> formValueElements = new LinkedHashMultimap<>();

        for (Map.Entry<String, Map<String, Object>> entry: elements.entries()) {
            formValueElements.put(entry.getKey(), toFormValueElement(entry.getValue()));
        }

        return formValueElements;
    }

    private FormValueElement toFormValueElement(Map<String, Object> element) {
        FormValueElement formValueElement = new FormValueElement();

        formValueElement.setElementName((String) element.get(EventDataKeys.ELEMENT_NAME));
        formValueElement.setValue((String) element.get(EventDataKeys.VALUE));

        Map<String, String> attributes = (Map<String, String>) element.get(EventDataKeys.ATTRIBUTES);
        if (attributes != null) {
            formValueElement.setAttributes(attributes);
        }
        formValueElement.setSubElements(toFormValueElements((Multimap<String, Map<String, Object>>) element.get(EventDataKeys.SUB_ELEMENTS)));

        return formValueElement;
    }

    private Map<String, String> getFormMeta(FormValueElement rootElement) {
        Map<String, String> formMeta = new HashMap<>();
        FormValueElement metaElement = rootElement.getChildElement("meta");

        if (metaElement == null) {
            return new HashMap<>();
        }

        for (Map.Entry<String, FormValueElement> metaEntry : metaElement.getSubElements().entries()) {
            formMeta.put(metaEntry.getKey(), metaEntry.getValue().getValue());
        }
        return formMeta;
    }
}
