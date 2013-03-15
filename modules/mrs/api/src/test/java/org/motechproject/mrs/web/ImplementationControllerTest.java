package org.motechproject.mrs.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mrs.util.MrsImplementationManager;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;

public class ImplementationControllerTest {
    private MockMvc controller;

    @Mock
    private MrsImplementationManager mrsImplementationManager;

    @InjectMocks
    private ImplementationController implementationController = new ImplementationController();

    @Before
    public void setUp() {
        initMocks(this);
        controller = MockMvcBuilders.standaloneSetup(implementationController).build();
    }

    @Test
    public void shouldGetDefaultAdapter () throws Exception{

        when(mrsImplementationManager.getCurrentImplName()).thenReturn("test_mrs");

        controller.perform(
                get("/impl/adapters/default")
        ).andExpect(
                status().is(HttpStatus.OK.value())
        ).andExpect(
                content().string("test_mrs")
        );

        verify(mrsImplementationManager).getCurrentImplName();
    }

    @Test
    public void shouldSetActiveAdapter () throws Exception{

        controller.perform(
                post("/impl/adapters")
                        .body("test_mrs".getBytes("UTF-8"))
        ).andExpect(
                status().is(HttpStatus.OK.value())
        );

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mrsImplementationManager).setCurrentImplName(captor.capture());
        assertEquals("test_mrs", captor.getValue());
    }
}
