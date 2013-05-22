package org.motechproject.commcare.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;


public class FormValueElement implements FormNode {

    private String elementName;
    private Multimap<String, FormValueElement> subElements = new LinkedHashMultimap<>();
    private Map<String, String> attributes = new HashMap<>();
    private String value;

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addFormValueElement(String key, FormValueElement element) {
        subElements.put(key, element);
    }

    public boolean containsAttribute(String key) {
        return attributes.containsKey(key);
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

    public FormValueElement getElement(String elementName) {
        return getElement(elementName, new ArrayList<String>());
    }

    public FormValueElement getElement(String elementName, List<String> restrictedElements) {
        if (restrictedElements != null && restrictedElements.contains(this.elementName)) {
            return null;
        }
        if (this.elementName.equals(elementName)) {
            return this;
        }

        List<FormValueElement> elements = getAllElements(elementName, restrictedElements, true);
        return (elements.size() > 0 ? elements.get(0) : null);
    }

    public List<FormValueElement> getAllElements(String elementName) {
        return getAllElements(elementName, new ArrayList<String>());
    }

    public List<FormValueElement> getAllElements(String elementName, List<String> restrictedElements) {
        return getAllElements(elementName, restrictedElements, false);
    }

    private List<FormValueElement> getAllElements(String elementName, List<String> restrictedElements, boolean breakOnFirst) {
        List<FormValueElement> elements = new ArrayList<>();
        for (Entry<String, FormValueElement> entry : subElements.entries()) {
            if (restrictedElements == null || !restrictedElements.contains(entry.getKey())) {
                if (entry.getKey().equals(elementName)) {
                    elements.add(entry.getValue());
                    if (breakOnFirst) {
                        return elements;
                    }
                }
                elements.addAll(entry.getValue().getAllElements(elementName, restrictedElements));
                if (breakOnFirst && elements.size() > 0) {
                    return elements;
                }
            }
        }
        return elements;
    }

    public List<FormValueElement> getChildElements(String elementName) {
        return new ArrayList<>(subElements.get(elementName));
    }

    private FormValueElement getChildElement(String elementName) {
        for (Entry<String, FormValueElement> entry : subElements.entries()) {
            if (entry.getKey().equals(elementName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public List<FormValueElement> getElementsByAttribute(String attribute, String value) {
        List<FormValueElement> elements = new ArrayList<>();
        if (value.equals(this.getAttributes().get(attribute))) {
            elements.add(this);
        }
        for (Entry<String, FormValueElement> entry : subElements.entries()) {
            List<FormValueElement> subElementList = entry.getValue().getElementsByAttribute(attribute, value);
            elements.addAll(subElementList);
        }
        return elements;
    }

    public FormNode search(String path, List<String> restrictedElements) {
        List<String> pathToTraverse = new Vector<>();
        pathToTraverse.addAll(Arrays.asList(path.replaceFirst("^" + PREFIX_SEARCH_RELATIVE, "").split("/")));
        return search(pathToTraverse, restrictedElements);
    }

    private FormNode search(List<String> pathToTraverse, List<String> restrictedElements) {
        FormValueElement returnElement = this;
        while (pathToTraverse.size() > 0 && returnElement != null) {
            String currentSearchSegment = pathToTraverse.remove(0);
            if (currentSearchSegment.startsWith(PREFIX_ATTRIBUTE)) {
                return returnElement.getAttributeAsNode(currentSearchSegment.replace(PREFIX_ATTRIBUTE, ""));
            }
            if (currentSearchSegment.startsWith(PREFIX_VALUE)) {
                return returnElement;
            }
            if (restrictedElements != null && restrictedElements.contains(currentSearchSegment)) {
                return null;
            }
            returnElement = returnElement.getChildElement(currentSearchSegment);

        }
        return returnElement;
    }

    private FormValueAttribute getAttributeAsNode(String name) {
        return new FormValueAttribute(attributes.get(name));
    }
}
