package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.MotechConfigurationException;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.startup.StartupManager;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StartupManager.class})
public class BootstrapControllerTest {
    @Mock
    private StartupManager startupManager;
    @Mock
    private ConfigurationService configService;

    @InjectMocks
    private BootstrapController bootstrapController = new BootstrapController();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(StartupManager.class);

        initMocks(this);

        StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(bootstrapController);
        mockMvc = mockMvcBuilder.build();
    }

    @Test
    public void shouldReturnViewWithBootstrapFlagSet() throws Exception {
        when(startupManager.isBootstrapConfigRequired()).thenReturn(true);

        mockMvc.perform(get("/bootstrap"))
                .andExpect(status().isOk())
                .andExpect(view().name("bootstrapconfig")).andReturn();
    }

    @Test
    public void shouldRedirectToHomePageIfBootstrapConfigIsAlreadyLoaded() throws Exception {
        when(startupManager.isBootstrapConfigRequired()).thenReturn(false);

        mockMvc.perform(get("/bootstrap"))
                .andExpect(status().isOk()).andExpect(view().name("redirect:home"));
    }

    @Test
    public void shouldSaveBootstrapConfig() throws Exception {
        mockMvc.perform(post("/bootstrap")
                .param("dbUrl", "http://www.someurl.com")
                .param("dbUsername", "some_username")
                .param("dbPassword", "some_password")
                .param("tenantId", "some_tenantId")
                .param("configSource", "UI"))
                .andExpect(status().isOk()).andExpect(view().name("redirect:home"));

        BootstrapConfig expectedConfigToSave = new BootstrapConfig(new DBConfig("http://www.someurl.com", "some_username", "some_password"), "some_tenantId", ConfigSource.valueOf("UI"));
        InOrder inOrder = inOrder(configService, startupManager);
        inOrder.verify(configService).save(expectedConfigToSave);
        inOrder.verify(startupManager).startup();
    }

    @Test
    public void shouldAddErrorOnSaveAndReturnTheSameBootstrapStartupView() throws Exception {
        doThrow(new MotechConfigurationException("Test Exception")).when(configService).save(any(BootstrapConfig.class));

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

        ModelAndView actualView = bootstrapController.submitForm(bootstrapConfigForm, bindingResult);

        assertThat(actualView.getViewName(), is("bootstrapconfig"));
        assertThat((String) ((List) actualView.getModel().get("errors")).get(0), is("server.dbUrl.error"));
    }
}
