package org.motechproject.mobileforms.api.domain;

import org.junit.Test;
import org.motechproject.MotechException;
import org.motechproject.mobileforms.api.validator.TestFormBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FormBeanGroupTest {
    @Test
    public void shouldSortBasedOnDependents() {
        final TestFormBean formBean1 = new TestFormBean(null, "item1", null, null, null, Arrays.asList("item2", "item3"), null, null);
        final TestFormBean formBean2 = new TestFormBean(null, "item2", null, null, null, Arrays.asList("item4"), null, null);
        final TestFormBean formBean3 = new TestFormBean(null, "item3", null, null, null, Collections.<String>emptyList(), null, null);
        final TestFormBean formBean4 = new TestFormBean(null, "item4", null, null, null, null, null, null);
        final TestFormBean formBean5 = new TestFormBean(null, "item5", null, null, null, null, null, null);

        FormBeanGroup group = new FormBeanGroup(Arrays.<FormBean>asList(formBean1, formBean2, formBean3, formBean4, formBean5));

        List<FormBean> sortedList = group.sortByDependency();

        assertTrue(sortedList.indexOf(formBean2) < sortedList.indexOf(formBean1));
        assertTrue(sortedList.indexOf(formBean3) < sortedList.indexOf(formBean1));
        assertTrue(sortedList.indexOf(formBean4) < sortedList.indexOf(formBean2));
        assertTrue(sortedList.contains(formBean1));
        assertTrue(sortedList.contains(formBean2));
        assertTrue(sortedList.contains(formBean3));
        assertTrue(sortedList.contains(formBean4));
        assertTrue(sortedList.contains(formBean5));

        // just with one form
        group = new FormBeanGroup(Arrays.<FormBean>asList(formBean1));
        assertThat(group.sortByDependency(), is(equalTo(Arrays.<FormBean>asList(formBean1))));

        // more than one form but all dependents on in the list
        group = new FormBeanGroup(Arrays.<FormBean>asList(formBean1, formBean3));
        assertThat(group.sortByDependency(), is(equalTo(Arrays.<FormBean>asList(formBean3, formBean1))));
    }

    @Test(expected = MotechException.class)
    public void shouldDetectCyclicDependency() {
        final TestFormBean formBean1 = new TestFormBean(null, "item1", null, null, null, Arrays.asList("item2"), null, null);
        final TestFormBean formBean2 = new TestFormBean(null, "item2", null, null, null, Arrays.asList("item1"), null, null);
        final TestFormBean formBean3 = new TestFormBean(null, "item3", null, null, null, null, null, null);

        FormBeanGroup group = new FormBeanGroup(Arrays.<FormBean>asList(formBean1, formBean2, formBean3));
        group.sortByDependency();
    }

    @Test
    public void shouldReturnAllFormBeansWithNoErrors() {
        TestFormBean formBean1 = new TestFormBean();
        TestFormBean formBean2 = new TestFormBean();
        formBean1.addFormError(new FormError("", ""));

        assertThat(new FormBeanGroup(Arrays.<FormBean>asList(formBean1, formBean2)).validForms(), is(equalTo(Arrays.<FormBean>asList(formBean2))));
    }

}
