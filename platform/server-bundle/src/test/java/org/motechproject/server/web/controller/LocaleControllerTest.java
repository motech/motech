package org.motechproject.server.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.ui.LocaleService;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class LocaleControllerTest {

    @Mock
    private LocaleService localeService;

    @InjectMocks
    private LocaleController localeController = new LocaleController();

    private MockMvc controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = MockMvcBuilders.standaloneSetup(localeController).build();
    }

    @Test
    public void shouldReturnMessages() throws Exception {
        Map<String, String> msgs = new LinkedHashMap<>();
        msgs.put("key1", "msg1");
        msgs.put("key2", "msg2");

        when(localeService.getMessages(any(HttpServletRequest.class))).thenReturn(msgs);

        controller.perform(
                get("/lang/locate")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().string("{\"key1\":\"msg1\",\"key2\":\"msg2\"}")
        );
    }
}
