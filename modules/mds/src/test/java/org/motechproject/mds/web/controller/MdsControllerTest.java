package org.motechproject.mds.web.controller;

import org.junit.Test;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.ex.FieldNotFoundException;
import org.motechproject.mds.ex.MdsException;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class MdsControllerTest {
    private TestMdsController controller = new TestMdsController();
    MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Test
    public void shouldHandleMdsExceptions() throws Exception {
        String[] keys = new String[]{
                "key:mds.error.entityAlreadyExist",
                "key:mds.error.entityNotFound",
                "key:mds.error.entityIsReadOnly",
                "key:mds.error.fieldNotFound",
                "key:test.key"
        };

        for (int i = 0; i < keys.length; ++i) {
            mvc.perform(get("/exception/" + i))
                    .andExpect(status().isConflict())
                    .andExpect(content().string(keys[i]));
        }
    }

    private class TestMdsController extends MdsController {
        private MdsException[] exceptions = new MdsException[]{
                new EntityAlreadyExistException(),
                new EntityNotFoundException(),
                new EntityReadOnlyException(),
                new FieldNotFoundException(),
                new MdsException("test.key")
        };

        @RequestMapping(value = "/exception/{id}")
        public void throwException(@PathVariable Integer id) {
            throw exceptions[id];
        }

    }
}
