package org.motechproject.commcare.domain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FormValueElement {

    private String elementName;
    private Multimap<String, FormValueElement> subElements = ArrayListMultimap.create();
    private Map<String, String> attributes = new HashMap<String, String>();
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addFormValueElement(String key, FormValueElement element) {
        subElements.put(key, element);
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public Multimap<String, FormValueElement> getSubElements() {
        return subElements;
    }

    public void setSubElements(Multimap<String, FormValueElement> subElements) {
        this.subElements = subElements;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public FormValueElement getElementByName(String elementName) {
        if ("case".equals(this.elementName)) {
            return null;
        }
        if (elementName.equals(this.elementName)) {
            return this;
        } else {
            for (Entry<String, FormValueElement> entry : subElements.entries()) {
                FormValueElement subElement = entry.getValue().getElementByName(elementName);
                if (subElement != null) {
                    return subElement;
                }
            }
        }
        return null;
    }

    public FormValueElement getElementByNameIncludeCase(String elementName) {
        if (elementName.equals(this.elementName)) {
            return this;
        } else {
            for (Entry<String, FormValueElement> entry : subElements.entries()) {
                FormValueElement subElement = entry.getValue().getElementByNameIncludeCase(elementName);
                if (subElement != null) {
                    return subElement;
                }
            }
        }
        return null;
    }

    public List<FormValueElement> getElementsByAttribute(String attribute, String value) {
        List<FormValueElement> elements = new ArrayList<FormValueElement>();
        if (value.equals(this.getAttributes().get(attribute))) {
            elements.add(this);
        }
        for (Entry<String, FormValueElement> entry : subElements.entries()) {
            List<FormValueElement> subElementList = entry.getValue().getElementsByAttribute(attribute, value);
            elements.addAll(subElementList);
        }
        return elements;
    }
}
