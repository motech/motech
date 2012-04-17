package org.motechproject.demo.commcare.util;

import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.motechproject.commcare.domain.Case;
import org.motechproject.commcare.parser.CommcareCaseParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CommcareParserUtil {

	DocumentBuilderFactory dbFactory;
	InputSource inputSource;
	DocumentBuilder  documentBuilder;
	Document document;


	public CommcareParserUtil(String xml) throws ParserConfigurationException {
		inputSource = new InputSource();
		dbFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = dbFactory.newDocumentBuilder();
		document = null;
		initialize(xml);
	}


	private void initialize(String xml) {
		inputSource.setCharacterStream(new StringReader(xml));
		try {
			document = documentBuilder.parse(inputSource);
		} catch (Exception e) {
		}
	}

	public String findAttributeByElement(String element, String attribute) {
		if (document != null) {
			NodeList dataNodeList = document.getElementsByTagName(element);
			Element dataElement = (Element) dataNodeList.item(0);
			if (dataElement != null) {
				String xmlnsAttribute = dataElement.getAttribute(attribute);
				if (xmlnsAttribute.length() > 0) {
					return xmlnsAttribute;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	public String getValueByElement(String element) {
		if (document != null) {
			NodeList dataNodeList = document.getElementsByTagName(element);
			Element dataElement = (Element) dataNodeList.item(0);
			if (dataElement != null) {
				String elementValue = dataElement.getNodeValue();
				if (dataElement.hasChildNodes()) {
					NodeList innerNodeList = dataElement.getChildNodes();
					return innerNodeList.item(0).getNodeValue();
				} else {
					return elementValue;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	public Case parseCaseFromForm(String formXml) {
		int caseOpenIndex = formXml.indexOf("<case");
		int caseClosedIndex = formXml.indexOf("</case>");
		if (caseOpenIndex > 0 && caseClosedIndex > 0) {
			formXml = formXml.substring(caseOpenIndex, caseClosedIndex + 7);
			System.out.println("Chopped down case: " + formXml);
		}
		try {
			CommcareCaseParser<Case> parser = new CommcareCaseParser<Case>(Case.class, formXml.trim());
			Case caseInstance = parser.parseCase();
			System.out.println("Have the case: ");
			System.out.println("Case id: " + caseInstance.getCase_id());
			System.out.println("Case userId: " + caseInstance.getUser_id());
			System.out.println("Case action: " + caseInstance.getAction());
			System.out.println("Case name: " + caseInstance.getCase_name());
			System.out.println("Date modified: " + caseInstance.getDate_modified());
			System.out.println("Owner id: " + caseInstance.getOwner_id());
			Map<String, String> fieldValues = caseInstance.getFieldValues();
			System.out.println("Field values in case: ");
			System.out.println("----------------------");

			for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
				System.out.println(entry.getKey() + "   :  " + entry.getValue());
			}
			System.out.println("----------------------");
			return caseInstance;
		} catch (Exception e) {
			return null;
		}


	}
}
