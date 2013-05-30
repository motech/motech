package org.motechproject.commcare.builder;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CommcareFormBuilderTest {

    CommcareFormBuilder builder;

    @Before
    public void setup() throws Exception {
        builder = new CommcareFormBuilder();
    }

    @Test
    public void shouldBuildCommcareFormFromMotechEvent() {
        String elementName = "myform";
        MotechEvent motechEvent = new MotechEvent();

        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("xmlns", "myNs");
        attributes.put("uiVersion", "myUiVersion");
        attributes.put("version", "myVersion");
        Multimap<String, Map<String, Object>> subElements = new LinkedHashMultimap<>();
        Map<String, Object> metaElement = new HashMap<>();
        Multimap<String, Map<String, Object>> metaSubElements = new LinkedHashMultimap<>();

        motechEvent.getParameters().put(EventDataKeys.ELEMENT_NAME, elementName);
        motechEvent.getParameters().put(EventDataKeys.ATTRIBUTES, attributes);

        metaElement.put(EventDataKeys.ELEMENT_NAME, "meta");


        final String meta1 = "meta1";
        final String metaValue1 = "metaValue1";
        metaSubElements.put(meta1, new HashMap<String, Object>() {{
            put(EventDataKeys.ELEMENT_NAME, meta1);
            put(EventDataKeys.VALUE, metaValue1);
        }});

        final String meta2 = "meta2";
        final String metaValue2 = "metaValue2";

        metaSubElements.put(meta2, new HashMap<String, Object>() {{
            put(EventDataKeys.ELEMENT_NAME, meta2);
            put(EventDataKeys.VALUE, metaValue2);
        }});

        final String instanceIdMetaElementName = "instanceID";
        final String expectedInstanceId = "myInstanceId";
        metaSubElements.put(instanceIdMetaElementName, new HashMap<String, Object>() {{
            put(EventDataKeys.ELEMENT_NAME, instanceIdMetaElementName);
            put(EventDataKeys.VALUE, expectedInstanceId);
        }});

        metaElement.put(EventDataKeys.SUB_ELEMENTS, metaSubElements);
        subElements.put("meta", metaElement);
        motechEvent.getParameters().put(EventDataKeys.SUB_ELEMENTS, subElements);

        CommcareForm actualForm = builder.buildFrom(motechEvent);

        FormValueElement rootElement = actualForm.getForm();
        assertEquals(elementName, rootElement.getElementName());
        assertEquals(attributes, rootElement.getAttributes());
        assertEquals(1, rootElement.getSubElements().size());
        assertNotNull(rootElement.getElement("meta1").getSubElements());
        assertEquals(3, actualForm.getMetadata().size());
        assertEquals(metaValue1, actualForm.getMetadata().get(meta1));
        assertEquals(metaValue2, actualForm.getMetadata().get(meta2));
        assertEquals("myUiVersion", actualForm.getUiversion());
        assertEquals("myVersion", actualForm.getVersion());
        assertEquals("myInstanceId", actualForm.getId());
    }

    @Test
    public void shouldDefaultToEmptyIfEventHasNullAsAttributeAndSubElements() {
        String elementName = "myform";
        MotechEvent motechEvent = new MotechEvent();

        motechEvent.getParameters().put(EventDataKeys.ELEMENT_NAME, elementName);
        motechEvent.getParameters().put(EventDataKeys.ATTRIBUTES, null);
        motechEvent.getParameters().put(EventDataKeys.SUB_ELEMENTS, null);

        CommcareForm commcareForm = builder.buildFrom(motechEvent);

        Multimap<String,FormValueElement> subElements = commcareForm.getForm().getSubElements();
        assertEquals(0, subElements.size());

        Map<String, String> metadata = commcareForm.getMetadata();
        assertEquals(0, metadata.size());

        Map<String, String> attributes = commcareForm.getForm().getAttributes();
        assertEquals(0, attributes.size());
    }

    @Test
    public void shouldDefaultToEmptyIfEventDoesNotHaveAttributesAndSubElement() {
        String elementName = "myform";
        MotechEvent motechEvent = new MotechEvent();

        motechEvent.getParameters().put(EventDataKeys.ELEMENT_NAME, elementName);

        CommcareForm commcareForm = builder.buildFrom(motechEvent);

        Multimap<String,FormValueElement> subElements = commcareForm.getForm().getSubElements();
        assertEquals(0, subElements.size());

        Map<String, String> metadata = commcareForm.getMetadata();
        assertEquals(0, metadata.size());

        Map<String, String> attributes = commcareForm.getForm().getAttributes();
        assertEquals(0, attributes.size());
    }

    @Test
    public void shouldDefaultRootNameToFormIfItsNotPresent() {
        MotechEvent motechEvent = new MotechEvent();
        motechEvent.getParameters().put(EventDataKeys.ELEMENT_NAME, null);
        CommcareForm commcareForm = builder.buildFrom(motechEvent);

        assertEquals("form", commcareForm.getForm().getElementName());

        motechEvent = new MotechEvent();
        commcareForm = builder.buildFrom(motechEvent);

        assertEquals("form", commcareForm.getForm().getElementName());
    }

    @Test
    public void shouldDefaultSubElementsAndAttributesToEmptyListIfNotPresent() {
        MotechEvent motechEvent = new MotechEvent();
        FormValueElementMapBuilder elementMapBuilder = new FormValueElementMapBuilder(motechEvent);
        elementMapBuilder.addSubElement(new FormValueElementMapBuilder().withElementName("child").addAttribute("attribute1Name", "attribute1Value").withValue("value1").build());
        elementMapBuilder.addSubElement(new FormValueElementMapBuilder().withElementName("child").addSubElement(new FormValueElementMapBuilder().withElementName("secondLevelChild").build()).build());

        CommcareForm commcareForm = builder.buildFrom(motechEvent);


        List<FormValueElement> childElements = commcareForm.getForm().getChildElements("child");
        assertEquals(2, childElements.size());

        FormValueElement firstChildElement = childElements.get(0);
        assertEquals(0, firstChildElement.getSubElements().size());
        assertEquals("attribute1Value", firstChildElement.getAttributes().get("attribute1Name"));
        assertEquals("value1", firstChildElement.getValue());

        FormValueElement secondChildElement = childElements.get(1);
        assertEquals(1, secondChildElement.getSubElements().size());
        assertEquals(0, secondChildElement.getAttributes().size());
        assertEquals(1, secondChildElement.getChildElements("secondLevelChild").size());
    }

    private class FormValueElementMapBuilder {
        private final Map<String, Object> map;

        public FormValueElementMapBuilder(MotechEvent motechEvent) {
            this.map = motechEvent.getParameters();
        }

        public FormValueElementMapBuilder() {
            this.map = new HashMap<>();
        }

        public FormValueElementMapBuilder withValue(String value) {
            map.put(EventDataKeys.VALUE, value);
            return this;
        }

        public FormValueElementMapBuilder withElementName(String name) {
            map.put(EventDataKeys.ELEMENT_NAME, name);
            return this;
        }

        public FormValueElementMapBuilder withAttributes(Map<String, String> attributes) {
            map.put(EventDataKeys.ATTRIBUTES, attributes);
            return this;
        }

        public FormValueElementMapBuilder addAttribute(String name, String value) {
            Map<String, String> attributes = (Map<String, String>) map.get(EventDataKeys.ATTRIBUTES);
            if(attributes == null) {
                attributes = new HashMap<>();
                map.put(EventDataKeys.ATTRIBUTES, attributes);
            }
            attributes.put(name, value);
            return this;
        }

        public FormValueElementMapBuilder withSubElements(Multimap<String, Map<String, Object>> subElements) {
            map.put(EventDataKeys.SUB_ELEMENTS, subElements);
            return this;
        }

        public FormValueElementMapBuilder addSubElement(Map<String, Object> subElement, String elementName) {
            Multimap<String, Map<String, Object>> subElements = (Multimap<String, Map<String, Object>>) map.get(EventDataKeys.SUB_ELEMENTS);
            if(subElements == null) {
                subElements = new LinkedHashMultimap<>();
                map.put(EventDataKeys.SUB_ELEMENTS, subElements);
            }

            if(elementName == null) {
                elementName = (String) subElement.get(EventDataKeys.ELEMENT_NAME);
            }

            subElements.put(elementName, subElement);
            return this;
        }

        public FormValueElementMapBuilder addSubElement(Map<String, Object> subElement) {
            return addSubElement(subElement, null);
        }

        public Map<String, Object> build() {
            return map;
        }
    }
}
