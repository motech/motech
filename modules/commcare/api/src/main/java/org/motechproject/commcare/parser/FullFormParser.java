package org.motechproject.commcare.parser;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.parsers.DOMParser;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.exception.FullFormParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

public class FullFormParser {
    private String xmlDoc;

    public FullFormParser(String xmlDoc) {
        this.xmlDoc = xmlDoc;
    }


    public FormValueElement parse() throws FullFormParserException {
        DOMParser parser = new DOMParser();

        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xmlDoc));
        FormValueElement root;

        try {
            parser.parse(inputSource);

            Document document = parser.getDocument();
            Node item = document.getElementsByTagName("data").item(0);

            root = new FormValueElement();
            root.setElementName("form");
            root.setValue("data");
            addAttributes(root, item.getAttributes());
            addSubElements(root, item.getChildNodes());
        } catch (SAXException | IOException ex) {
            throw new FullFormParserException(ex, "Exception while trying to parse formXml");
        }

        return root;
    }

    private void addAttributes(FormValueElement element, NamedNodeMap attributes) {
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attr = attributes.item(i);
            String key = attr.getNodeName();

            if (key.startsWith("xmlns:")) {
                key = key.substring(0, 5);
            }

            if (!element.containsAttribute(key)) {
                element.addAttribute(key, attr.getNodeValue());
            }
        }
    }

    private void addSubElements(FormValueElement element, NodeList children) {
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                FormValueElement childElement = new FormValueElement();
                childElement.setElementName(child.getLocalName());

                String value = getTextValue((Element) child);

                if (StringUtils.isNotBlank(value)) {
                    childElement.setValue(value);
                } else {
                    addAttributes(childElement, child.getAttributes());
                    addSubElements(childElement, child.getChildNodes());
                }

                element.addFormValueElement(childElement.getElementName(), childElement);
            }
        }
    }

    private String getTextValue(Element ele) {
        Node textNode = ele.getFirstChild();
        String textVal = null;

        if (textNode != null) {
            textVal = textNode.getNodeValue();
        }

        return textVal;
    }
}
