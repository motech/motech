package org.motechproject.security.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.motechproject.security.repository.MotechURLSecurityRuleDataService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.security.constants.PermissionNames.MANAGE_ROLE_AND_PERMISSION_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MANAGE_URL_PERMISSION;
import static org.motechproject.security.constants.PermissionNames.MANAGE_USER_PERMISSION;
import static org.motechproject.server.osgi.util.PlatformConstants.SECURITY_SYMBOLIC_NAME;
import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.RESOLVED;
import static org.osgi.framework.Bundle.UNINSTALLED;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public abstract class BaseIT extends BasePaxIT {
    private static final String SECURITY_BUNDLE_NAME = "motech-platform-web-security";

    @Rule
    public TestName testName = new TestName();

    @Inject
    private BundleContext bundleContext;

    @Inject
    private MotechURLSecurityRuleDataService securityRuleDataService;

    private ApplicationContext context;

    @Before
    public void setUp() throws Exception {
        getLogger().info("Starting test: " + testName.getMethodName());
        context = ServiceRetriever.getWebAppContext(bundleContext, SECURITY_SYMBOLIC_NAME);
    }

    @After
    public void tearDown() throws Exception {
        getLogger().info("Finishing test: " + testName.getMethodName());
    }

    protected <T> T getFromContext(Class<T> service) {
        return context.getBean(service);
    }

    protected <T> T getFromContext(Class<T> service, String name) {
        return context.getBean(name, service);
    }

    protected Bundle[] getBundles() {
        return bundleContext.getBundles();
    }

    protected MotechURLSecurityRuleDataService getSecurityRuleDataService() {
        return securityRuleDataService;
    }

    protected void restartSecurityBundle() throws BundleException, InterruptedException, IOException {
        getLogger().info("Restarting web security bundle");

        Bundle securityBundle = getBundle(SECURITY_BUNDLE_NAME);

        securityBundle.stop();
        waitForBundleState(securityBundle, RESOLVED);

        securityBundle.start();
        waitForBundleState(securityBundle, ACTIVE);

        context = ServiceRetriever.getWebAppContext(bundleContext, SECURITY_SYMBOLIC_NAME);

        getLogger().info("Restarted web security bundle");
    }

    protected Bundle getBundle(String symbolicName) {
        Bundle testBundle = null;

        for (Bundle bundle : getBundles()) {
            if (null != bundle.getSymbolicName() && bundle.getSymbolicName().contains(symbolicName)
                    && UNINSTALLED != bundle.getState()) {
                testBundle = bundle;
                break;
            }
        }

        assertNotNull("Bundle not found: " + symbolicName, testBundle);

        return testBundle;
    }

    protected void waitForBundleState(final Bundle bundle, final int state) throws InterruptedException {
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return state == bundle.getState();
            }
        }, 2000).start();
        assertEquals(state, bundle.getState());
    }

    protected void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    protected String[] getPermissions() {
        return new String[] {MANAGE_USER_PERMISSION, MANAGE_ROLE_AND_PERMISSION_PERMISSION,
                MANAGE_URL_PERMISSION};
    }

}
