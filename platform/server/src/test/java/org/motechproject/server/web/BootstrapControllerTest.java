package org.motechproject.server.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.server.impl.OsgiListener;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.result.MockMvcResultMatchers;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.test.web.server.setup.StandaloneMockMvcBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doThrow;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OsgiListener.class})
public class BootstrapControllerTest {
    @Mock
    private MessageSource messageSource;
    @Mock
    private LocaleResolver localeResolver;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private BootstrapController bootstrapController = new BootstrapController();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(OsgiListener.class);

        initMocks(this);

        StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(bootstrapController);
        mockMvc = mockMvcBuilder.build();
    }

    @Test
    public void shouldReturnViewWithBootstrapFlagSet() throws Exception {
        when(OsgiListener.isBootstrapPresent()).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("bootstrapconfig")).andReturn();
    }

    @Test
    public void shouldRedirectToHomePageIfBootstrapConfigIsAlreadyLoaded() throws Exception {
        when(OsgiListener.isBootstrapPresent()).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("redirect:.."));
    }

    @Test
    public void shouldSaveBootstrapConfig() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/")
                .param("dbUrl", "http://www.someurl.com")
                .param("dbUsername", "some_username")
                .param("dbPassword", "some_password")
                .param("tenantId", "some_tenantId")
                .param("configSource", "UI"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.view().name("bootstrapconfig"))
                .andExpect(MockMvcResultMatchers.model().attribute("redirect", true));

        BootstrapConfig expectedConfigToSave = new BootstrapConfig(new DBConfig("http://www.someurl.com", "some_username", "some_password"), "some_tenantId", ConfigSource.valueOf("UI"));

        PowerMockito.verifyStatic(times(1));
        OsgiListener.saveBootstrapConfig(expectedConfigToSave);
    }

    @Test
    public void shouldAddErrorOnSaveAndReturnTheSameBootstrapStartupView() throws Exception {
        when(localeResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);
        when(messageSource.getMessage("server.error.bootstrap.save", null, Locale.ENGLISH)).thenReturn("errMsg");
        doThrow(new MotechConfigurationException("Test Exception")).when(OsgiListener.class);
        OsgiListener.saveBootstrapConfig(any(BootstrapConfig.class));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/")
                .param("dbUrl", "http://www.someurl.com")
                .param("dbUsername", "some_username")
                .param("dbPassword", "some_password")
                .param("tenantId", "some_tenantId")
                .param("configSource", "UI"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        ModelAndView actualView = mvcResult.getModelAndView();
        assertThat(actualView.getViewName(), is("bootstrapconfig"));
        assertThat((String) ((List) actualView.getModel().get("errors")).get(0), is("errMsg"));
    }

    @Test
    public void shouldAddErrorsOnValidationFailure() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(new ObjectError("dbUrl", new String[]{"server.dbUrl.error"}, null, null)));

        BootstrapConfigForm bootstrapConfigForm = new BootstrapConfigForm();
        bootstrapConfigForm.setDbUrl("http://www.dburl.com");

        ModelAndView actualView = bootstrapController.submitForm(bootstrapConfigForm, bindingResult, request);

        assertThat(actualView.getViewName(), is("bootstrapconfig"));
        assertThat((String) ((List) actualView.getModel().get("errors")).get(0), is("server.dbUrl.error"));
    }
}
