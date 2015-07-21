package org.motechproject.mds.web.controller;

import org.junit.Test;
import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.ex.entity.EntityAlreadyExistException;
import org.motechproject.mds.ex.entity.EntityCreationException;
import org.motechproject.mds.ex.entity.EntityInfrastructureException;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.ex.entity.EntityReadOnlyException;

import static org.junit.Assert.assertEquals;

public class MdsControllerTest {

    private MdsController controller = new MdsController() {

        @Override
        public String toString() {
            return "MdsControllerTest";
        }

    };

    private MdsException[] exceptions = {
            new EntityAlreadyExistException("EntityName"),
            new EntityNotFoundException("EntityName"),
            new EntityReadOnlyException("EntityName"),
            new EntityCreationException("msg", new RuntimeException()),
            new EntityInfrastructureException("className", new RuntimeException()),
            new MdsException("msg", new RuntimeException(), "test.key")
    };

    @Test
    public void shouldHandleMdsExceptions() throws Exception {
        for (MdsException exception : exceptions) {
            assertEquals("Exception class " + exception.getClass(),
                    String.format("key:%s", exception.getMessageKey()),
                    controller.handleMdsException(exception)
            );
        }
    }
}
