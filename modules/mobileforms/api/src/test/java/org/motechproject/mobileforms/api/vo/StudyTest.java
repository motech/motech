package org.motechproject.mobileforms.api.vo;

import org.junit.Test;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.validator.TestFormBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StudyTest {

    @Test
    public void shouldGroupFormsBasedOnTheGroupIdentifier() {
        final TestFormBean formBeanOne = new TestFormBean("study", "form1", "<xml>xml</xml>", "validator1", "formType", Collections.<String>emptyList(), "group1", "lName1");
        final TestFormBean formBeanTwo = new TestFormBean("study", "form2", "<xml>xml</xml>", "validator2", "formType", Collections.<String>emptyList(), "group2", "lName2");
        final TestFormBean formBeanThree = new TestFormBean("study", "form3", "<xml>xml</xml>", "validator3", "formType", Collections.<String>emptyList(), "group1", "lName3");
        final TestFormBean formBeanFour = new TestFormBean("study", "form4", "<xml>xml</xml>", "validator4", "formType", Collections.<String>emptyList(), null, "lName3");
        final TestFormBean formBeanFive = new TestFormBean("study", "form5", "<xml>xml</xml>", "validator5", "formType", Collections.<String>emptyList(), null, "lName3");
        final Study study = new Study("Study", Arrays.<FormBean>asList(formBeanOne, formBeanTwo, formBeanThree, formBeanFour, formBeanFive));
        assertThat(new HashSet(study.groupedForms()), is(equalTo(new HashSet(Arrays.asList(new FormBeanGroup(Arrays.<FormBean>asList(formBeanOne, formBeanThree)),
                new FormBeanGroup(Arrays.<FormBean>asList(formBeanTwo)), new FormBeanGroup(Arrays.<FormBean>asList(formBeanFour)),
                new FormBeanGroup(Arrays.<FormBean>asList(formBeanFive)))))));


        assertThat(new Study("st").groupedForms(), is(equalTo(Collections.<FormBeanGroup>emptyList())));
    }


}
