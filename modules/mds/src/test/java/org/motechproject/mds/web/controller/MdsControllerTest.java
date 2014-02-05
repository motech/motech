package org.motechproject.mds.web.controller;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.ex.EntityInfrastructureException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.ex.MdsException;

public class MdsControllerTest {
    private MdsController controller = new MdsController() {

        @Override
        public String toString() {
            return "MdsControllerTest";
        }

    };

    private MdsException[] exceptions = {
            new EntityAlreadyExistException(),
            new EntityNotFoundException(),
            new EntityReadOnlyException(),
            new EntityCreationException(new RuntimeException()),
            new EntityInfrastructureException(new RuntimeException()),
            new MdsException("test.key")
    };

    @Test
    public void shouldHandleMdsExceptions() throws Exception {
        for (MdsException exception : exceptions) {
            Assert.assertEquals(
                    String.format("key:%s", exception.getMessageKey()),
                    controller.handleMdsException(exception)
            );
        }
    }
}
