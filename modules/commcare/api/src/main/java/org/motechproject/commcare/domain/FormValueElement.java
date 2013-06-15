package org.motechproject.commcare.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;


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

    /**
     * Returns first element found  of all descendant elements with a given element name. The lookup happens in a preorder traversal of this element tree.
     * Lookup starts with this element and hence this element will be returned if it matches the given element name.
     *
     * <p>
     *      <b>Note:</b> This method will return null if no matching node is found.
     * </p>
     *
     * @param elementName The name of the element to match on.
     * @return First element matching the given name.
     */
    public FormValueElement getElement(String elementName) {
        return getElement(elementName, new ArrayList<String>());
    }

    /**
     * Returns first element found  of all descendant elements with a given element name. The lookup happens in a preorder traversal of this element tree.
     * Lookup starts with this element and hence this element will be part of the returned list if it matches the given element name.
     *
     * <p>
     *      Lookup within an element and its descendants is skipped if it is part of {@code restrictedElements} list.
     * </p>
     *
     * <p>
     *      <b>Note:</b> This method will return null if no matching node is found.
     * </p>
     *
     * @param elementName The name of the element to match on.
     * @param restrictedElements The list containing all restricted elements.
     * @return First element matching the given name.
     */
    public FormValueElement getElement(String elementName, List<String> restrictedElements) {
        List<FormValueElement> elements = getAllElements(elementName, restrictedElements, true);
        return (elements.size() > 0 ? elements.get(0) : null);
    }

    /**
     * Returns a list of all descendant elements with a given element name, in the order in which they are encountered in a preorder traversal of this element tree.
     * Lookup starts with this element and hence this element will be part of the returned list if it matches the given element name.
     *
     * <p>
     *      <b>Note:</b> This method will return an empty list if no matching node is found.
     * </p>
     *
     * @param elementName The name of the element to match on.
     * @return A list containing matching elements.
     */
    public List<FormValueElement> getAllElements(String elementName) {
        return getAllElements(elementName, new ArrayList<String>());
    }

    /**
     * Returns a list of all descendant elements with a given element name, in the order in which they are encountered in a preorder traversal of this element tree.
     * Lookup starts with this element and hence this element will be part of the returned list if it matches the given element name.
     *
     * <p>
     *      Lookup within an element and its descendants is skipped if it is part of {@code restrictedElements} list.
     * </p>
     *
     * <p>
     *      <b>Note:</b> This method will return an empty list if no matching node is found.
     * </p>
     *
     * @param elementName The name of the element to match on.
     * @param restrictedElements The list containing all restricted elements.
     * @return A list containing matching elements.
     */
    public List<FormValueElement> getAllElements(String elementName, List<String> restrictedElements) {
        return getAllElements(elementName, restrictedElements, false);
    }

    private List<FormValueElement> getAllElements(String elementName, List<String> restrictedElements, boolean breakOnFirst) {
        List<FormValueElement> elements = new ArrayList<>();

        if (restrictedElements != null && restrictedElements.contains(this.getElementName())) {
            return elements;
        }

        if (this.elementName.equals(elementName)) {
            elements.add(this);
            if (breakOnFirst) {
                return elements;
            }
        }

        for (Entry<String, FormValueElement> entry : subElements.entries()) {
            if (restrictedElements == null || !restrictedElements.contains(entry.getKey())) {
                elements.addAll(entry.getValue().getAllElements(elementName, restrictedElements, breakOnFirst));
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

    public FormValueElement getChildElement(String elementName) {
        for (Entry<String, FormValueElement> entry : subElements.entries()) {
            if (entry.getKey().equals(elementName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public FormValueElement getElementByAttribute(String attribute, String value) {
        return getElementByAttribute(attribute, value, null);
    }

    public FormValueElement getElementByAttribute(String attribute, String value, List<String> restrictedElements) {
        List<FormValueElement> elementsByAttribute = getElementsByAttribute(attribute, value, restrictedElements, true);
        return elementsByAttribute.size() > 0 ? elementsByAttribute.get(0) : null;
    }

    public List<FormValueElement> getElementsByAttribute(String attribute, String value) {
        return getElementsByAttribute(attribute, value, null);
    }

    public List<FormValueElement> getElementsByAttribute(String attribute, String value, List<String> restrictedElements) {
        return getElementsByAttribute(attribute, value, restrictedElements, false);
    }

    public List<FormValueElement> getElementsByAttribute(String attribute, String value, List<String> restrictedElements, boolean breakOnFirst) {
        List<FormValueElement> elements = new ArrayList<>();

        if (restrictedElements != null && restrictedElements.contains(this.getElementName())) {
            return elements;
        }

        if (value.equals(this.getAttributes().get(attribute))) {
            elements.add(this);
            if (breakOnFirst) {
                return elements;
            }
        }

        for (Entry<String, FormValueElement> entry : subElements.entries()) {
            List<FormValueElement> subElementList = entry.getValue().getElementsByAttribute(attribute, value, restrictedElements);
            elements.addAll(subElementList);
            if (elements.size() > 0 && breakOnFirst) {
                break;
            }
        }
        return elements;
    }

    /**
     * Returns first node (elements and attributes) found at the given path, in the order in which they are encountered in a preorder traversal of this element tree.
     * Lookup starts with this element and hence this element will be part of the returned list if it matches the given path.
     *
     * <p>
     *      For usage and other information see {@link #search(String)}.
     * </p>
     *
     * <p>
     *      <b>Note:</b> This method will return null if no matching node is found.
     * </p>
     *
     * @param path Lookup path.
     * @return First node found at lookup path.
     */
    public FormNode searchFirst(String path) {
        List<FormNode> results = new ArrayList<>();
        search(splitPath(path), true, results);
        return results.size() > 0 ? results.get(0) : null;
    }

    /**
     * Returns a list of all descendant nodes (elements and attributes) found at the given path, in the order in which they are encountered in a preorder traversal of this element tree.
     * Lookup starts with this element and hence this element will be part of the returned list if it matches the given path.
     *
     * <p>
     *      Path should start with "//".
     * </p>
     *
     * <p><b>Example:</b>
     * <br>For FormValueElement similar to following XML:
     * <pre><code>
     *      &lt;this thisAttribute="thisAttribute"&gt;
     *          &lt;foo&gt;
     *              &lt;bar barAttribute="barAttribute1"&gt;
     *                  &lt;child&gt;child1&lt;/child&gt;
     *                  &lt;child&gt;child2&lt;/child&gt;
     *              &lt;/bar&gt;
     *              &lt;child&gt;child3&lt;/child&gt;
     *          &lt;/foo&gt;
     *          &lt;foo&gt;
     *              &lt;bar barAttribute="barAttribute2" otherBarAttribute="otherBarAttribute"&gt;
     *                  &lt;child&gt;child4&lt;/child&gt;
     *                  &lt;child&gt;child5&lt;/child&gt;
     *                  &lt;otherChild&gt;otherChild&lt;/otherChild&gt;
     *              &lt;/bar&gt;
     *          &lt;/foo&gt;
     *      &lt;/this&gt;
     *     </code></pre>
     * </p>
     * Search for <b>//foo</b> will return both foo elements
     * <br>
     * Search for <b>//foo/bar/child</b> will return elements having values <b>child1</b>, <b>child2</b>, <b>child4</b>, <b>child5</b>.
     * <br>
     * Search for <b>//foo/child</b> will return one element having value as <b>child3</b>.
     * <br>
     * Search for <b>//foo/bar/@barAttribute</b> will return two attribute nodes having values as <b>barAttribute1</b> and <b>barAttribute2</b>.
     * <br>
     * Search for <b>//foo/bar/@otherBarAttribute</b> will return one attribute element having value as <b>otherBarAttribute</b>.
     * <br>
     * Search for <b>//</b> will return <b>this</b> element.
     * <br>
     * Search for <b>//@thisAttribute</b> will return "thisAttribute</b> node on <b>this</b> element.
     * <br>
     * Paths //foo/bar/#anyValue will return same result as //foo/bar or //foo/bar/#anyOtherValue.
     *
     * <p>
     * <b>Note:</b> This method will return an empty list if no matching node is found.
     * </p>
     *
     * @param path Lookup path.
     * @return A list containing matching nodes.
     */
    public List<FormNode> search(String path) {
        List<FormNode> results = new ArrayList<>();
        search(splitPath(path), false, results);
        return results;
    }

    private List<String> splitPath(String path) {
        List<String> pathToTraverse = new Vector<>();
        String  trimmedPath = path.replaceFirst("^" + PREFIX_SEARCH_RELATIVE, "");

        if (StringUtils.isEmpty(trimmedPath)) {
            return pathToTraverse;
        }

        pathToTraverse.addAll(Arrays.asList(trimmedPath.split("/")));
        return pathToTraverse;
    }

    private void search(List<String> pathToTraverse, boolean breakOnFirst, List<FormNode> results) {
        if (pathToTraverse.isEmpty()) {
            results.add(this);
            return;
        }

        String currentSearchSegment = pathToTraverse.remove(0);

        if (currentSearchSegment.startsWith(PREFIX_ATTRIBUTE)) {
            results.add(getAttributeAsNode(currentSearchSegment.replace(PREFIX_ATTRIBUTE, "")));
            return;
        }
        if (currentSearchSegment.startsWith(PREFIX_VALUE)) {
            results.add(this);
        }

        List<FormValueElement> childElements = getChildElements(currentSearchSegment);
        for (FormValueElement childElement: childElements) {
            childElement.search(new ArrayList<>(pathToTraverse), breakOnFirst, results);
            if (breakOnFirst && results.size() > 0) {
                return;
            }
        }
    }

    private FormValueAttribute getAttributeAsNode(String name) {
        return new FormValueAttribute(attributes.get(name));
    }
}
