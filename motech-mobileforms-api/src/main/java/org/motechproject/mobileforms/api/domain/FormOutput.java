package org.motechproject.mobileforms.api.domain;

import org.fcitmuk.epihandy.ResponseHeader;
import org.motechproject.mobileforms.api.vo.Study;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class FormOutput {
    private Map<FormBean, List<FormError>> errorMap;

    public FormOutput() {
        errorMap = new TreeMap<FormBean, List<FormError>>(new FormBeanComparator());
    }

    public void add(FormBean formBean, List<FormError> errors) {
        errorMap.put(formBean, errors);
    }

    public void populate(DataOutputStream dataOutput) throws IOException {
        int success = 0;
        int failures = 0;
        Map<String, Study> studyMap = new HashMap<String, Study>();
        for (FormBean formBean : errorMap.keySet()) {
            if (errorMap.get(formBean).isEmpty()) {
                success++;
            } else {
                String studyName = formBean.getStudyName();
                if (!studyMap.containsKey(studyName)) studyMap.put(studyName, new Study(studyName));
                studyMap.get(studyName).addForm(formBean.getXmlContent());
                failures++;
            }
        }
        dataOutput.writeByte(ResponseHeader.STATUS_SUCCESS);
        dataOutput.writeInt(success);
        dataOutput.writeInt(failures);

        int studyCount = 0;
        for (String studyName : studyMap.keySet()) {
            dataOutput.write((byte) studyCount);
            int formCount = 0;
            for (String formXML : studyMap.get(studyName).forms()) {
                dataOutput.writeShort((short) formCount);
                dataOutput.writeUTF(formXML);
                formCount++;
            }
            studyCount++;
        }
    }

    private class FormBeanComparator implements Comparator<FormBean> {
        @Override
        public int compare(FormBean formBean, FormBean formBean1) {
            int comparison = formBean.getStudyName().compareToIgnoreCase(formBean1.getStudyName());
            return comparison != 0 ? comparison : formBean.getFormname().compareToIgnoreCase(formBean1.getFormname());
        }
    }
}
