package org.motechproject.mobileforms.api.callbacks;

import org.junit.Test;
import org.motechproject.mobileforms.api.vo.Study;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class StudyProcessorTest {
    private StudyProcessor studyProcessor = new StudyProcessor();

    @Test
    public void shouldAddANewStudyOnProcessingStudyCallBack() {
        studyProcessor.processingStudy(null);
        assertEquals(1, studyProcessor.studies().size());
    }

    @Test
    public void shouldAddFormXMLToLastStudy() {
        studyProcessor.processingStudy(null);
        studyProcessor.formProcessed(null, null, "xml");
        List<Study> studies = studyProcessor.studies();
        assertEquals("xml", studies.get(0).forms().get(0));
    }

    @Test
    public void shouldReturnFormCount() {
        studyProcessor.processingStudy(null);
        studyProcessor.formProcessed(null, null, "xml");
        assertEquals(new Integer(1), studyProcessor.formsCount());
    }
}
