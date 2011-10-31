package org.motechproject.mobileforms.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AllMobileFormsTest {

    private AllMobileForms allMobileForms;
    private List<Form> formsOfGroupOne;
    private List<Form> formsOfGroupTwo;
    private FormGroup formGroupOne;
    private FormGroup formGroupTwo;

    @Before
    public void setup() {
        allMobileForms = new AllMobileForms(setupPropertiesWithFormConfigFileName("/dummy-forms.json"), new MotechJsonReader());
        allMobileForms.initialize();
        formsOfGroupOne = Arrays.asList(new Form("MForm-I", "DummyForm-1.xml", "<form>DummyForm1</form>"));
        formsOfGroupTwo = Arrays.asList(new Form("MForm-II", "DummyForm-2.xml", "<form>DummyForm2</form>"),
                                        new Form("MForm-III", "DummyForm-3.xml", "<form>DummyForm3</form>"));
        formGroupOne = new FormGroup("GroupNameI", formsOfGroupOne);
        formGroupTwo = new FormGroup("GroupNameII", formsOfGroupTwo);

    }

    @Test
    public void shouldInitializeByLoadingAllFormsFromFileSystem(){
        List<FormGroup> expectedFormGroups = Arrays.asList(formGroupOne, formGroupTwo);
        assertThat(allMobileForms.getAllFormGroups(), is(equalTo(expectedFormGroups)));
    }

    @Test
    public void shouldReturnTheGroupGivenTheIndexOfListOfFormGroups(){
        assertThat(allMobileForms.getGroup(0), is(equalTo(formGroupOne)));
        assertThat(allMobileForms.getGroup(1), is(equalTo(formGroupTwo)));
    }

    private Properties setupPropertiesWithFormConfigFileName(String dummyFormConfigFileName) {
        Properties properties = new Properties();
        properties.setProperty("forms.config.file", dummyFormConfigFileName);
        return properties;
    }

}
