package org.motechproject.mobileforms.api.callbacks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.TestForm;
import org.motechproject.mobileforms.api.parser.FormDataParser;
import org.motechproject.mobileforms.api.repository.AllMobileForms;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormProcessorTest {
    private FormProcessor formProcessor;
    @Mock
    private FormDataParser formDataParser;
    @Mock
    private AllMobileForms allMobileForms;

    @Before
    public void setUp() {
        initMocks(this);
        formProcessor = new FormProcessor();
        ReflectionTestUtils.setField(formProcessor, "parser", formDataParser);
        ReflectionTestUtils.setField(formProcessor, "allMobileForms", allMobileForms);
        ReflectionTestUtils.setField(formProcessor, "marker", "formName");

    }

    @Test
    public void shouldMakeFormBeansOutOfFormXML() {
        Map map = new HashMap();
        map.put("formName","formName");
        map.put("country","india");
        map.put("district","katpadi");

        when(formDataParser.parse("xml")).thenReturn(map);
        Form form = mock(Form.class);
        when(form.bean()).thenReturn("org.motechproject.mobileforms.api.domain.TestForm");
        when(form.validator()).thenReturn("validator");
        when(form.name()).thenReturn("formName");
        when(allMobileForms.getFormByName("formName")).thenReturn(form);

        formProcessor.formProcessed(null, null, "xml");

        TestForm testForm  = (TestForm) formProcessor.formBeans().get(0);
        assertEquals("formName",testForm.getFormName());
        assertEquals("validator",testForm.getValidator());
        assertEquals("india",testForm.getCountry());
        assertEquals("katpadi",testForm.getDistrict());
    }

}
