package org.motechproject.mobileforms.api.parser;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.motechproject.mobileforms.api.domain.FormData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FormDataParser {
    private final Logger log = LoggerFactory.getLogger(FormDataParser.class);

    public FormData parse(String xml) {
        Map formData = new HashMap();
        try {
            InputStream in = new ByteArrayInputStream(xml.getBytes());
            Document doc = new SAXBuilder().build(in);
            List children = doc.getRootElement().getChildren();
            for (Object o : children) {
                Element child = (Element) o;
                formData.put(child.getName(), child.getText());
            }
        } catch (Exception e) {
            log.error("Error in parsing form xml", e);
        }
        return new FormData(formData);
    }

}


