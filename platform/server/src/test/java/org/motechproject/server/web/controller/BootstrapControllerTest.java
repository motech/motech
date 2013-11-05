package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.server.web.form.BootstrapConfigForm;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.test.web.server.setup.StandaloneMockMvcBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.web.controller.Constants.REDIRECT_HOME;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WebApplicationContextUtils.class})
public class BootstrapControllerTest {

    @Mock
    private CoreConfigurationService coreConfigurationService;

    private BootstrapController bootstrapController = new BootstrapController();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        initMocks(this);
        bootstrapController.setCoreConfigurationService(coreConfigurationService);
        StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(bootstrapController);
        mockMvc = mockMvcBuilder.build();
    }

    @Test
    public void shouldReturnViewWithBootstrapFlagSet() throws Exception {
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(null);

        mockMvc.perform(get("/bootstrap"))
                .andExpect(status().isOk())
                .andExpect(view().name("bootstrapconfig")).andReturn();
    }

    @Test
    public void shouldRedirectToHomePageIfBootstrapConfigIsAlreadyLoaded() throws Exception {
        final DBConfig dbConfig = new DBConfig("http://motech.heroku.com", "user", "pass");
        final BootstrapConfig bootstrapConfig = new BootstrapConfig(dbConfig, "tenant-id", ConfigSource.FILE);
        when(coreConfigurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);

        PowerMockito.mockStatic(WebApplicationContextUtils.class);

        mockMvc.perform(get("/bootstrap"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_HOME));
    }

    @Test
    public void shouldSaveBootstrapConfig() throws Exception {
        PowerMockito.mockStatic(WebApplicationContextUtils.class);
        mockMvc.perform(post("/bootstrap")
                .param("dbUrl", "http://www.someurl.com")
                .param("dbUsername", "some_username")
                .param("dbPassword", "some_password")
                .param("tenantId", "some_tenantId")
                .param("configSource", "UI"))
                .andExpect(status().isOk()).andExpect(view().name(REDIRECT_HOME));

        final DBConfig dbConfig = new DBConfig("http://www.someurl.com", "some_username", "some_password");
        final BootstrapConfig expectedConfigToSave = new BootstrapConfig(dbConfig, "some_tenantId", ConfigSource.UI);
        verify(coreConfigurationService).saveBootstrapConfig(expectedConfigToSave);
    }

    @Test
    public void shouldAddErrorOnSaveAndReturnTheSameBootstrapStartupView() throws Exception {
        doThrow(new MotechConfigurationException("Test Exception")).when(coreConfigurationService).saveBootstrapConfig(any(BootstrapConfig.class));

        MvcResult mvcResult = mockMvc.perform(post("/bootstrap")
                .param("dbUrl", "http://www.someurl.com")
                .param("dbUsername", "some_username")
                .param("dbPassword", "some_password")
                .param("tenantId", "some_tenantId")
                .param("configSource", "UI"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView actualView = mvcResult.getModelAndView();
        assertThat(actualView.getViewName(), is("bootstrapconfig"));
        assertThat((String) ((List) actualView.getModel().get("errors")).get(0), is("server.error.bootstrap.save"));
    }

    @Test
    public void shouldAddErrorsOnValidationFailure() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(new ObjectError("dbUrl", new String[]{"server.dbUrl.error"}, null, null)));

        BootstrapConfigForm bootstrapConfigForm = new BootstrapConfigForm();
        bootstrapConfigForm.setDbUrl("http://www.dburl.com");

        ModelAndView actualView = bootstrapController.submitForm(bootstrapConfigForm, null, bindingResult);

        assertThat(actualView.getViewName(), is("bootstrapconfig"));
        assertThat((String) ((List) actualView.getModel().get("errors")).get(0), is("server.dbUrl.error"));
    }
}
