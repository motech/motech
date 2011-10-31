package org.motechproject.mobileforms.api.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mobileforms.api.dao.AllMobileForms;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.motechproject.mobileforms.api.valueobjects.GroupNameAndForms;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MobileFormsServiceImplTest {

    private MobileFormsService mobileFormsService;

    @Mock
    private AllMobileForms allMobileForms;

    @Before
    public void setUp() {
        initMocks(this);
        mobileFormsService = new MobileFormsServiceImpl(allMobileForms);
    }

    @Test
    public void shouldFetchTheListOfFromGroupsAvailable() {

        String groupOneName = "Group-1";
        String groupTwoName = "Group-2";
        when(allMobileForms.getAllFormGroups()).thenReturn(Arrays.asList(new FormGroup(groupOneName, Arrays.asList(new Form("Form-1", "Form-1.xml"))),
                                                                         new FormGroup(groupTwoName, Arrays.asList(new Form("From-2", "Form-2.xml")))));

        List<Object[]> returnedFormGroups = mobileFormsService.getAllFormGroups();

        assertThat(returnedFormGroups.size(), is(equalTo(2)));

        assertThat((Integer)returnedFormGroups.get(0)[0], is(equalTo(0)));
        assertThat((String)returnedFormGroups.get(0)[1], is(equalTo(groupOneName)));

        assertThat((Integer)returnedFormGroups.get(1)[0], is(equalTo(1)));
        assertThat((String)returnedFormGroups.get(1)[1], is(equalTo(groupTwoName)));
    }

    @Test
    public void shouldReturnContextOfFormsThatBelongsToTheGroupGivenTheIndexOfTheGroup(){
        String formOneContent = "Form-1-content";
        String formTwoContent = "Form-2-content";
        String formThreeContent = "Form-3-content";
        String formGroupOneName = "FormGroup-1";
        String formGroupTwoName = "FormGroup-2";

        when(allMobileForms.getGroup(0)).thenReturn(new FormGroup(formGroupOneName, Arrays.asList(new Form("From-1", "Form-1.xml", formOneContent), new Form("Form-2", "Form-2.xml", formTwoContent))));
        when(allMobileForms.getGroup(1)).thenReturn(new FormGroup(formGroupTwoName, Arrays.asList(new Form("From-3", "Form-3.xml", formThreeContent))));

        assertThat(mobileFormsService.getForms(0), is(equalTo(new GroupNameAndForms(formGroupOneName, Arrays.asList(formOneContent, formTwoContent)))));
        assertThat(mobileFormsService.getForms(1), is(equalTo(new GroupNameAndForms(formGroupTwoName, Arrays.asList(formThreeContent)))));

    }
}
