package org.motechproject.mobileforms.api.parser;

import org.apache.commons.beanutils.BeanUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.motechproject.mobileforms.api.domain.FormBean;
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

    public FormBean parse(String xml, String beanClass) {
        try {
            Map dataMap = new HashMap();
            FormBean bean = (FormBean) Class.forName(beanClass).newInstance();

            InputStream in = new ByteArrayInputStream(xml.getBytes());
            Document doc = new SAXBuilder().build(in);
            List children = doc.getRootElement().getChildren();
            for (Object o : children) {
                Element child = (Element) o;
                dataMap.put(child.getName(), child.getText());
            }
            BeanUtils.populate(bean, dataMap);
            return bean;

        } catch (Exception e) {
            log.error("Error in parsing form xml", e);
        }
        return null;
    }

}


