package org.motechproject.mobileforms.api.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mobileforms.api.dao.AllMobileForms;
import org.motechproject.mobileforms.api.domain.FormGroup;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MobileFormsServiceImplTest {

    @Mock
    private AllMobileForms allMobileForms;

    @Before
    public void setUp() {
        allMobileForms = mock(AllMobileForms.class);
        ArrayList<FormGroup> formGroups = new ArrayList<FormGroup>();
        formGroups.add(new FormGroup("Group-1"));
        formGroups.add(new FormGroup("Group-2"));
        when(allMobileForms.getAllFormGroups()).thenReturn(formGroups);
    }

    @Test
    public void testGetAllFormGroups() {
        MobileFormsService oxdFormsService = new MobileFormsServiceImpl(allMobileForms);
        List<String> allFormGroups = oxdFormsService.getAllFormGroups();

        Assert.assertEquals(2, allFormGroups.size());
    }
}
