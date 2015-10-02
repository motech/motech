package org.motechproject.security.velocity;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.security.ex.VelocityTemplateParsingException;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.testing.utils.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VelocityTemplateParserTest {

    private static final String CORRECT_TEMPLATE = "correctTemplate.vm";
    private static final String INCORRECT_TEMPLATE = "incorrectTemplate.vm";
    private static final String EXPECTED = "expected";

    @Mock
    private SettingsFacade settingsFacade;

    private VelocityTemplateParser parser;
    private Map<String, Object> params;

    @Before
    public void setUp() {
        initMocks(this);
        prepareParams();
        prepareParser();
    }

    @Test
    public void shouldMergeTemplateIntoString() throws Exception {
        when(settingsFacade.getRawConfig(CORRECT_TEMPLATE)).thenReturn(loadFile(CORRECT_TEMPLATE));

        String result = parser.mergeTemplateIntoString(CORRECT_TEMPLATE, params);

        verify(settingsFacade, times(1)).getRawConfig(CORRECT_TEMPLATE);

        verifyResult(result);
    }

    @Test(expected = VelocityTemplateParsingException.class)
    public void shouldThrowExceptionIfTemplateIsMalformed() throws Exception {
        when(settingsFacade.getRawConfig(INCORRECT_TEMPLATE)).thenReturn(loadFile(INCORRECT_TEMPLATE));

        try {
            parser.mergeTemplateIntoString(INCORRECT_TEMPLATE, params);
        } finally {
            verify(settingsFacade, times(1)).getRawConfig(INCORRECT_TEMPLATE);
        }
    }

    private void verifyResult(String result) throws Exception {
        try (InputStream is = loadFile(EXPECTED)) {
            assertEquals(IOUtils.toString(is), result);
        }
    }

    private FileInputStream loadFile(String filename) throws Exception {
        File file = FileHelper.getResourceFile("emailtest/" + filename);
        return file != null ? new FileInputStream(file) : null;
    }

    private void prepareParams() {
        params = new HashMap<>();
        params.put("param1", "param1val");
        params.put("param2", "param2val");
        params.put("param3", new Object[] { "param3val1", "param3val2", "param3val3"});
        params.put("param4", 5);
    }

    private void prepareParser() {
        parser = new VelocityTemplateParser();
        parser.setSettingsFacade(settingsFacade);
    }
}