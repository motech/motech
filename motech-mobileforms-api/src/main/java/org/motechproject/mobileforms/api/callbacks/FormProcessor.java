package org.motechproject.mobileforms.api.callbacks;

import org.apache.commons.lang.StringUtils;
import org.fcitmuk.epihandy.DeserializationListenerAdapter;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.StudyData;
import org.motechproject.MotechException;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.parser.FormDataParser;
import org.motechproject.mobileforms.api.repository.AllMobileForms;
import org.motechproject.mobileforms.api.utils.MapToBeanConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FormProcessor extends DeserializationListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(FormProcessor.class);

    private List<FormBean> formBeans = new ArrayList<FormBean>();

    @Autowired
    private FormDataParser parser;

    @Autowired
    private AllMobileForms allMobileForms;

    @Autowired
    private MapToBeanConvertor mapToBeanConvertor;

    @Value("#{mobileFormsProperties['forms.xml.form.name']}")
    private String marker;

    @Override
    public void formProcessed(StudyData studyData, FormData formData, String formXml) {
        try {
            Map data = parser.parse(formXml);
            Form form = allMobileForms.getFormByName((String) data.get(marker));

            FormBean formBean = (FormBean) Class.forName(form.bean()).newInstance();
            formBean.setValidator(form.validator());
            formBean.setFormname(form.name());
            formBean.setStudyName(form.studyName());
            formBean.setXmlContent(formXml);

            mapToBeanConvertor.convert(formBean, handleEmptyStrings(data));
            formBeans.add(formBean);

        } catch (Exception e) {
            throw new MotechException("Exception occurred while parsing form xml", e);
        }
    }

    public List<FormBean> formBeans() {
        return formBeans;
    }

    private Map<String, String> handleEmptyStrings(Map<String, String> attributes) {
        Map<String, String> attributeWithOutEmptyStringValue = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (StringUtils.isNotEmpty(entry.getValue())) {
                attributeWithOutEmptyStringValue.put(entry.getKey(), entry.getValue());
            }
        }
        return attributeWithOutEmptyStringValue;
    }
}


