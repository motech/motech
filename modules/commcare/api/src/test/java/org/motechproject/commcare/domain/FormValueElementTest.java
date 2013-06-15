package org.motechproject.commcare.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FormValueElementTest {

    @Test
    public void shouldFindElementsByAttributeValue() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("firstLevelChild1").withAttribute("attr1", "value1");
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("attr1", "value1").build());
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("attr1", "value1").build());
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild2").withAttribute("attr1", "someothervalue").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("firstLevelChild2");
        childElementBuilder2.withSubElement(new FormValueElementBuilder("secondLevelChild3").withAttribute("attr1", "value1").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement").withAttribute("attr1", "value1");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build());
        rootElementBuilder.withSubElement(new FormValueElementBuilder("firstLevelChild1").withAttribute("attr1", "someothervalue").build());

        FormValueElement rootElement = rootElementBuilder.build();

        assertEquals(0, rootElement.getElementsByAttribute("attr1", "doesnotexist").size());

        List<FormValueElement> elementsByAttribute = rootElement.getElementsByAttribute("attr1", "value1");
        assertEquals(5, elementsByAttribute.size());
        assertEquals("rootElement", elementsByAttribute.get(0).getElementName());
        assertEquals("firstLevelChild1", elementsByAttribute.get(1).getElementName());
        assertEquals("secondLevelChild1", elementsByAttribute.get(2).getElementName());
        assertEquals("secondLevelChild1", elementsByAttribute.get(3).getElementName());
        assertEquals("secondLevelChild3", elementsByAttribute.get(4).getElementName());
    }

    @Test
    public void shouldIgnoreRestrictedElementsWhileFindingElementsByAttributeValue() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("firstLevelChild1").withAttribute("attr1", "value1");
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("attr1", "value1").build());
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("attr1", "value1").build());
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild2").withAttribute("attr1", "someothervalue").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("firstLevelChild2");
        childElementBuilder2.withSubElement(new FormValueElementBuilder("secondLevelChild3").withAttribute("attr1", "value1").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement").withAttribute("attr1", "value1");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build());
        rootElementBuilder.withSubElement(new FormValueElementBuilder("firstLevelChild1").withAttribute("attr1", "someothervalue").build());

        FormValueElement rootElement = rootElementBuilder.build();

        List<FormValueElement> foundElements = rootElement.getElementsByAttribute("attr1", "value1", Arrays.asList("rootElement"));
        assertEquals(0, foundElements.size());

        foundElements = rootElement.getElementsByAttribute("attr1", "value1", Arrays.asList("firstLevelChild1"));
        assertEquals(2, foundElements.size());
        assertEquals("rootElement", foundElements.get(0).getElementName());
        assertEquals("secondLevelChild3", foundElements.get(1).getElementName());

        foundElements = rootElement.getElementsByAttribute("attr1", "value1", Arrays.asList("firstLevelChild1", "firstLevelChild2"));
        assertEquals(1, foundElements.size());
        assertEquals("rootElement", foundElements.get(0).getElementName());

        foundElements = rootElement.getElementsByAttribute("attr1", "value1", Arrays.asList("secondLevelChild1"));
        assertEquals(3, foundElements.size());
        assertEquals("rootElement", foundElements.get(0).getElementName());
        assertEquals("firstLevelChild1", foundElements.get(1).getElementName());
        assertEquals("secondLevelChild3", foundElements.get(2).getElementName());
    }

    @Test
    public void shouldFindFirstElementsByAttributeValue() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("firstLevelChild1").withAttribute("attr1", "value1");
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("attr1", "value1").build());
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("attr1", "value1").build());
        childElementBuilder1.withSubElement(new FormValueElementBuilder("secondLevelChild2").withAttribute("attr1", "someothervalue").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("firstLevelChild2");
        childElementBuilder2.withSubElement(new FormValueElementBuilder("secondLevelChild3").withAttribute("attr1", "value1").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build());
        rootElementBuilder.withSubElement(new FormValueElementBuilder("firstLevelChild1").withAttribute("attr1", "someothervalue").build());

        FormValueElement rootElement = rootElementBuilder.build();

        FormValueElement foundElement = rootElement.getElementByAttribute("attr1", "value1", Arrays.asList("firstLevelChild1"));
        assertEquals("secondLevelChild3", foundElement.getElementName());

        foundElement = rootElement.getElementByAttribute("attr1", "doesnotexist", Arrays.asList("firstLevelChild1"));
        assertNull(foundElement);
    }

    @Test
    public void shouldSearchForTheFirstAttribute(){
        FormValueElementBuilder childElementBuilder = new FormValueElementBuilder("firstLevelChild")
                .withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("Attribute1", "Value1").withAttribute("Attribute2", null).build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withAttribute("Attribute1", "Value2").withAttribute("Attribute2", null).build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withAttribute("Attribute1", "Value3").build());
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement");
        rootElementBuilder.withSubElement(childElementBuilder.build());
        FormValueElement rootElement = rootElementBuilder.build();

        FormNode searchedElement = rootElement.searchFirst("//firstLevelChild/secondLevelChild/@Attribute1");
        assertEquals("Value2", searchedElement.getValue());

        assertNull(rootElement.searchFirst("//firstLevelChild/secondLevelChild/@Attribute2").getValue());
        assertNull(rootElement.searchFirst("//firstLevelChild/secondLevelChild/@Attribute3").getValue());
    }

    @Test
    public void shouldSearchForAllAttributes(){
        FormValueElementBuilder childElementBuilder = new FormValueElementBuilder("firstLevelChild")
                .withSubElement(new FormValueElementBuilder("secondLevelChild1").withAttribute("Attribute1", "Value1").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withAttribute("Attribute1", "Value2").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withAttribute("Attribute1", "Value3").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withAttribute("Attribute1", null).build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withAttribute("Attribute2", "Value1").build());
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement");
        rootElementBuilder.withSubElement(childElementBuilder.build());
        FormValueElement rootElement = rootElementBuilder.build();

        List<FormNode> searchedElements = rootElement.search("//firstLevelChild/secondLevelChild/@Attribute1");

        assertEquals(4, searchedElements.size());
        assertEquals("Value2", searchedElements.get(0).getValue());
        assertEquals("Value3", searchedElements.get(1).getValue());
        assertNull(searchedElements.get(2).getValue());
        assertNull(searchedElements.get(3).getValue());
    }

    @Test
    public void shouldSearchForTheFirstValue(){
        FormValueElementBuilder childElementBuilder = new FormValueElementBuilder("firstLevelChild")
                .withSubElement(new FormValueElementBuilder("secondLevelChild1").withValue("Value1").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value2").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value3").build());
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement");
        rootElementBuilder.withSubElement(childElementBuilder.build());
        FormValueElement rootElement = rootElementBuilder.build();

        FormNode searchedElement = rootElement.searchFirst("//firstLevelChild/secondLevelChild/#anything");
        assertEquals("Value2",searchedElement.getValue());

        assertNull(rootElement.searchFirst("//firstLevelChild/doesnotexist/#anything"));
    }

    @Test
    public void shouldSearchForAllValues(){
        FormValueElementBuilder childElementBuilder = new FormValueElementBuilder("firstLevelChild")
                .withSubElement(new FormValueElementBuilder("secondLevelChild1").withValue("Value1").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value2").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value3").build());
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement");
        rootElementBuilder.withSubElement(childElementBuilder.build());
        FormValueElement rootElement = rootElementBuilder.build();

        List<FormNode> searchedElements = rootElement.search("//firstLevelChild/secondLevelChild/#anything");

        assertEquals(2, searchedElements.size());
        assertEquals("Value2", searchedElements.get(0).getValue());
        assertEquals("Value3", searchedElements.get(1).getValue());

        assertNull(rootElement.searchFirst("//firstLevelChild/doesnotexist/#anything"));
    }

    @Test
    public void shouldSearchForTheFirstElement(){
        FormValueElementBuilder childElementBuilder = new FormValueElementBuilder("firstLevelChild")
                .withSubElement(new FormValueElementBuilder("secondLevelChild1").withValue("Value1").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value2").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value3").build());
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement");
        rootElementBuilder.withSubElement(childElementBuilder.build());
        FormValueElement rootElement = rootElementBuilder.build();

        FormNode searchedElement = rootElement.searchFirst("//firstLevelChild/secondLevelChild");
        assertEquals("Value2", searchedElement.getValue());

        assertNull(rootElement.searchFirst("//firstLevelChild/doesnotexist"));
    }

    @Test
    public void shouldSearchForAllElements(){
        FormValueElementBuilder childElementBuilder = new FormValueElementBuilder("firstLevelChild")
                .withSubElement(new FormValueElementBuilder("secondLevelChild1").withValue("Value1").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value2").build())
                .withSubElement(new FormValueElementBuilder("secondLevelChild").withValue("Value3").build());
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("rootElement");
        rootElementBuilder.withSubElement(childElementBuilder.build());
        FormValueElement rootElement = rootElementBuilder.build();

        List<FormNode> searchedElements = rootElement.search("//firstLevelChild/secondLevelChild/");

        assertEquals(2, searchedElements.size());
        assertEquals("Value2", searchedElements.get(0).getValue());
        assertEquals("Value3", searchedElements.get(1).getValue());
    }

    /**
     * This test asserts that while looking up for all matching elements with a name, the search should start with the current element and it should be part of the returned list.
     */
    @Test
    public void shouldSearchWithCurrentElementWhenLookingForAllElementsWithName() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("child1")
                .withSubElement(new FormValueElementBuilder("child").withValue("value1").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("child2")
                .withSubElement(new FormValueElementBuilder("child").withValue("value2").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("child");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build()).withValue("value");
        FormValueElement rootElement = rootElementBuilder.build();

        List<FormValueElement> foundElements = rootElement.getAllElements("child");
        assertEquals(3, foundElements.size());
        assertEquals("value", foundElements.get(0).getValue());
        assertEquals("value1", foundElements.get(1).getValue());
        assertEquals("value2", foundElements.get(2).getValue());
    }

    /**
     * This test asserts that while looking up for a matching element with a name, the search should start with the current element and it should be returned if has matching name.
     */
    @Test
    public void shouldSearchWithCurrentElementWhenLookingForElementWithName() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("child1")
                .withSubElement(new FormValueElementBuilder("child").withValue("value1").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("child2")
                .withSubElement(new FormValueElementBuilder("child").withValue("value2").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("child");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build()).withValue("value");
        FormValueElement rootElement = rootElementBuilder.build();

        FormValueElement foundElement = rootElement.getElement("child");
        assertEquals(rootElement, foundElement);
    }

    /**
     * This test asserts that while looking up for a matching element with a name, all the restricted elements and their descendants should be ignored.
     */
    @Test
    public void shouldConsiderRestrictedElementsWhileSearchingForElementByName() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("child1")
                .withSubElement(new FormValueElementBuilder("child").withValue("value1").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("child2")
                .withSubElement(new FormValueElementBuilder("child").withValue("value2").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("root");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build());
        FormValueElement rootElement = rootElementBuilder.build();

        FormValueElement foundElement = rootElement.getElement("root", Arrays.asList("root"));
        assertNull(foundElement);

        rootElement.setElementName("root");
        foundElement = rootElement.getElement("child", Arrays.asList("child1"));
        assertEquals("value2", foundElement.getValue());
    }

    /**
     * This test asserts that while looking up for matching elements with a name, all the restricted elements and their descendants should be ignored.
     */
    @Test
    public void shouldConsiderRestrictedElementsWhileSearchingForElementsByName() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("child1")
                .withSubElement(new FormValueElementBuilder("child").withValue("value1").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("child2")
                .withSubElement(new FormValueElementBuilder("child").withValue("value2").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("child");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build()).withValue("value");
        FormValueElement rootElement = rootElementBuilder.build();

        List<FormValueElement> foundElements = rootElement.getAllElements("child", Arrays.asList("child"));
        assertTrue(foundElements.isEmpty());

        foundElements = rootElement.getAllElements("child", Arrays.asList("child1"));
        assertEquals(2, foundElements.size());
        assertEquals("value", foundElements.get(0).getValue());
        assertEquals("value2", foundElements.get(1).getValue());
    }

    /**
     * This test asserts search with "//" path should return this element.
     */
    @Test
    public void shouldReturnCurrentElementWhenSearchPathIsSelf() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("child1")
                .withSubElement(new FormValueElementBuilder("child").withValue("value1").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("child2")
                .withSubElement(new FormValueElementBuilder("child").withValue("value2").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("child");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build()).withValue("value");
        FormValueElement rootElement = rootElementBuilder.build();

        FormNode foundElement = rootElement.searchFirst("//");
        assertEquals(rootElement, foundElement);

        List<FormNode> foundElements = rootElement.search("//");
        assertEquals(1, foundElements.size());
        assertEquals(rootElement, foundElements.get(0));
    }

    /**
     * This test asserts search with "//#foo" path should return this element.
     */
    @Test
    public void shouldReturnCurrentElementWhenSearchPathIsSelfValue() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("child1")
                .withSubElement(new FormValueElementBuilder("child").withValue("value1").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("child2")
                .withSubElement(new FormValueElementBuilder("child").withValue("value2").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("child");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build()).withValue("value");
        FormValueElement rootElement = rootElementBuilder.build();

        FormNode foundElement = rootElement.searchFirst("//#something");
        assertEquals(rootElement, foundElement);

        List<FormNode> foundElements = rootElement.search("//#something");
        assertEquals(1, foundElements.size());
        assertEquals(rootElement, foundElements.get(0));
    }

    /**
     * This test asserts search with "//@foo" path should return the attribute node from this element.
     */
    @Test
    public void shouldReturnAttrubuteValueFromCurrentElementWhenSearchPathIsSelf() {
        FormValueElementBuilder childElementBuilder1 = new FormValueElementBuilder("child1")
                .withSubElement(new FormValueElementBuilder("child").withValue("value1").build());

        FormValueElementBuilder childElementBuilder2 = new FormValueElementBuilder("child2")
                .withSubElement(new FormValueElementBuilder("child").withValue("value2").build());

        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("child");
        rootElementBuilder.withSubElement(childElementBuilder1.build()).withSubElement(childElementBuilder2.build()).withValue("value");
        rootElementBuilder.withAttribute("attribute1", "value1");
        FormValueElement rootElement = rootElementBuilder.build();

        FormNode foundAttribute = rootElement.searchFirst("//@attribute1");
        assertEquals("value1", foundAttribute.getValue());

        List<FormNode> foundAttributes = rootElement.search("//@attribute1");
        assertEquals(1, foundAttributes.size());
        assertEquals("value1", foundAttributes.get(0).getValue());
    }

    private class FormValueElementBuilder {

        private FormValueElement formValueElement;

        public FormValueElementBuilder(String elementName) {
            formValueElement = new FormValueElement();
            formValueElement.setElementName(elementName);
        }

        public FormValueElementBuilder withAttribute(String name, String value) {
            formValueElement.getAttributes().put(name, value);
            return this;
        }

        public FormValueElementBuilder withSubElement(FormValueElement subElement) {
            formValueElement.getSubElements().put(subElement.getElementName(), subElement);
            return this;
        }

        public FormValueElementBuilder withValue(String value) {
            formValueElement.setValue(value);
            return this;
        }

        public FormValueElement build() {
            return formValueElement;
        }
    }
}
