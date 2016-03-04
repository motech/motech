package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.mds.service.RestDocumentationService;
import org.motechproject.osgi.web.service.LocaleService;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class RestDocumentationControllerTest {

    @InjectMocks
    private RestDocumentationController restDocController = new RestDocumentationController();

    @Mock
    private RestDocumentationService restDocService;

    @Mock
    private LocaleService localeService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restDocController).build();
    }

    @Test
    public void shouldReturnRestDocumentation() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Writer writer = (Writer) invocation.getArguments()[0];
                String prefix = (String) invocation.getArguments()[1];

                writer.write("Rest docs with prefix: " + prefix);

                return null;
            }
        }).when(restDocService).retrieveDocumentation(any(Writer.class), anyString(), any(Locale.class));

        when(localeService.getUserLocale(any(HttpServletRequest.class))).thenReturn(new Locale("en", "US"));

        mockMvc.perform(
                get("/rest-doc?serverPrefix=/testPrefix")
        ).andExpect(status().isOk())
        .andExpect(content().string("Rest docs with prefix: /testPrefix"));
    }
}
