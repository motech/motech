package org.motechproject.commcare.parser;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.motechproject.commcare.exception.CaseParserException;
import org.motechproject.commcare.response.OpenRosaResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

public class OpenRosaResponseParser {

    public OpenRosaResponse parseResponse(String response)
            throws CaseParserException {
        DOMParser parser = new DOMParser();

        OpenRosaResponse openRosaResponse = new OpenRosaResponse();

        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(response));

        try {
            parser.parse(inputSource);
        } catch (IOException ex) {
            throw new CaseParserException("Could not parse: IOException");
        } catch (SAXException ex) {
            throw new CaseParserException("Could not parse: SAXException");
        }

        Document document = parser.getDocument();

        Element openRosaElement = (Element) document.getElementsByTagName(
                "OpenRosaResponse").item(0);

        if (openRosaElement == null) {
            return null;
        }

        Element messageElement = (Element) document.getElementsByTagName(
                "message").item(0);

        if (messageElement != null) {
            String messageNature = messageElement.getAttribute("nature");
            String messageText = messageElement.getTextContent();

            openRosaResponse.setMessageNature(messageNature);
            openRosaResponse.setMessageText(messageText);
        }

        return openRosaResponse;
    }
}
