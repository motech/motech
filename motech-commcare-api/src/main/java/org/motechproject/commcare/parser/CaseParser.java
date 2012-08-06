package org.motechproject.commcare.parser;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.motechproject.commcare.domain.CaseXml;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.util.CaseMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

public class CaseParser<T> {

    private CaseMapper<T> domainMapper;
    private String xmlDoc;
    private String caseAction;

    public CaseParser(Class<T> clazz, String xmlDocument) {
        domainMapper = new CaseMapper<T>(clazz);
        this.xmlDoc = xmlDocument;
    }

    public T parseCase() throws CaseParserException {
        DOMParser parser = new DOMParser();

        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(xmlDoc));
        CaseXml ccCase;
        try {
            parser.parse(inputSource);
            ccCase = parseCase(parser.getDocument());
        } catch (IOException ex) {
            throw new CaseParserException(ex,
                    "Exception while trying to parse caseXml");
        } catch (SAXException ex) {
            throw new CaseParserException(ex,
                    "Exception while trying to parse caseXml");
        }

        return domainMapper.mapToDomainObject(ccCase);
    }

    public CaseXml parseCase(Document document) {
        Element item = (Element) document.getElementsByTagName("case").item(0);
        CaseXml ccCase = createCase(item);
        updateAction(ccCase, item);
        return ccCase;
    }

    private CaseXml createCase(Element item) {
        CaseXml ccCase = new CaseXml();

        ccCase.setCaseId(item.getAttribute("case_id"));
        ccCase.setApiKey(item.getAttribute("api_key"));
        ccCase.setDateModified(item.getAttribute("date_modified"));
        ccCase.setUserId(item.getAttribute("user_id"));
        return ccCase;
    }

    private void updateAction(CaseXml ccCase, Element item) {

        if (getMatchingChildNode(item, "create") != null) {
            setCaseAction(ccCase, "CREATE");
            populateValuesForCreation(ccCase, item);
            populateValuesFor(ccCase, item, "update");

        } else {
            if (getMatchingChildNode(item, "update") != null) {
                setCaseAction(ccCase, "UPDATE");
                populateValuesFor(ccCase, item, "update");
            } else {
                if (getMatchingChildNode(item, "close") != null) {
                    setCaseAction(ccCase, "CLOSE");
                }
            }
        }
        if (getMatchingChildNode(item, "index") != null) {
            populateValuesFor(ccCase, item, "index");
        }
    }

    private void setCaseAction(CaseXml ccCase, String action) {
        this.caseAction = action;
        ccCase.setAction(action);
    }

    private void populateValuesForCreation(CaseXml ccCase, Element item) {
        ccCase.setCaseType(getTextValue(item, "case_type"));
        ccCase.setCaseName(getTextValue(item, "case_name"));
        ccCase.setOwnerId(getTextValue(item, "owner_id"));
    }

    private void populateValuesFor(CaseXml ccCase, Element item, String tagName) {
        Node matchingNode = getMatchingNode(item, tagName);
        NodeList childNodes = matchingNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (!childNode.getNodeName().contains("text")) {
                ccCase.addFieldValue(childNode.getNodeName(),
                        childNode.getTextContent());
            }
        }
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            Node textNode = el.getFirstChild();
            if (textNode != null) {
                textVal = textNode.getNodeValue();
            }
        }
        return textVal;
    }

    private Node getMatchingChildNode(Element ele, String tagName) {
        return getMatchingNode(ele, tagName);
    }

    private Node getMatchingNode(Element ele, String tagName) {
        Node element = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            element = nl.item(0);
        }
        return element;
    }

    public String getCaseAction() {
        return caseAction;
    }
}
