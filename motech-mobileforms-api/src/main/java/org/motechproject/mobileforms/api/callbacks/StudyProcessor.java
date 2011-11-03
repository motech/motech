package org.motechproject.mobileforms.api.callbacks;

import org.fcitmuk.epihandy.DeserializationListenerAdapter;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.StudyData;
import org.motechproject.mobileforms.api.vo.Study;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyProcessor extends DeserializationListenerAdapter {

    private List<Study> studies = new ArrayList<Study>();

    @Override
    public void processingStudy(StudyData studyData) {
        studies.add(new Study("study", new ArrayList<String>()));
    }

    @Override
    public void formProcessed(StudyData studyData, FormData formData, String formXml) {
        Study lastStudy = studies.get(studies.size() - 1);
        lastStudy.addForm(formXml);
    }

    public List<Study> studies() {
        return studies;
    }

    public Integer formsCount() {
        int count = 0;
        for (Study processedStudy : studies)
            count += processedStudy.forms().size();
        return count;
    }
}

