package org.motechproject.http.agent.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.http.agent.service.HttpAgent;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HttpAgentBundleIT extends BasePaxIT {

    @Inject
    private HttpAgent httpAgent;

    @Test
    public void testHttpAgentLoads()  {
        assertNotNull(httpAgent);
    }
}
