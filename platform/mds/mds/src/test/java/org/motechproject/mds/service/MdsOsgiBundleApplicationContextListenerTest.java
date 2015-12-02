package org.motechproject.mds.service;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.mds.domain.BundleFailsReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.repository.AllBundleFailsReports;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JdoTransactionManager.class)
public class MdsOsgiBundleApplicationContextListenerTest {

    private static final String SAMPLE_SYMBOLIC_NAME = "sample-symbolic-name";
    private static final String FAILURE_MESSAGE = "failure-message";

    @Mock
    private AllBundleFailsReports allBundleFailsReports;

    @Mock
    private JdoTransactionManager transactionManager;

    @Mock
    private Bundle bundle;

    @Mock
    private Throwable throwable;

    @Mock
    private ApplicationContext source;

    @Mock
    private BundleFailsReport report;

    @Mock
    private TransactionStatus transactionStatus;

    @Mock
    private OsgiEventProxy osgiEventProxy;

    @InjectMocks
    private MdsOsgiBundleApplicationContextListener mdsOsgiBundleApplicationContextListener = new MdsOsgiBundleApplicationContextListener();

    @Captor
    ArgumentCaptor<BundleFailsReport> bundleFailsReportArgumentCaptor;

    @Captor
    ArgumentCaptor<Map<String, Object>> paramsArgumentCaptor;

    @Before
    public void setUp() {
        when(bundle.getSymbolicName()).thenReturn(SAMPLE_SYMBOLIC_NAME);
        when(throwable.getMessage()).thenReturn(FAILURE_MESSAGE);
        when(allBundleFailsReports.getLastInProgressReport(anyString(), eq(SAMPLE_SYMBOLIC_NAME))).thenReturn(report);
        transactionManager = PowerMockito.mock(JdoTransactionManager.class);
        PowerMockito.when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
        mdsOsgiBundleApplicationContextListener.clearBundlesSet();
    }

    @Test
    public void shouldRestartBundle() throws BundleException {
        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        verify(allBundleFailsReports).create(bundleFailsReportArgumentCaptor.capture());
        BundleFailsReport failsReport = bundleFailsReportArgumentCaptor.getValue();
        assertEquals(SAMPLE_SYMBOLIC_NAME, failsReport.getBundleSymbolicName());
        assertEquals(FAILURE_MESSAGE, failsReport.getErrorMessage());
        assertEquals(BundleRestartStatus.IN_PROGRESS, failsReport.getBundleRestartStatus());

        verify(bundle).start();
        verify(bundle).stop();

        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextRefreshedEvent(source, bundle));

        verify(report).setBundleRestartStatus(BundleRestartStatus.SUCCESS);
        verify(allBundleFailsReports).update(report);
        verify(osgiEventProxy).sendEvent(eq("org.motechproject.message"), paramsArgumentCaptor.capture());

        Map<String, Object> params = paramsArgumentCaptor.getValue();
        assertEquals("WARN", params.get("level"));
        assertEquals("mds", params.get("moduleName"));
    }

    @Test
    public void shouldSetErrorStatus() throws BundleException {
        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        verify(allBundleFailsReports).create(bundleFailsReportArgumentCaptor.capture());
        BundleFailsReport failsReport = bundleFailsReportArgumentCaptor.getValue();
        assertEquals(SAMPLE_SYMBOLIC_NAME, failsReport.getBundleSymbolicName());
        assertEquals(FAILURE_MESSAGE, failsReport.getErrorMessage());
        assertEquals(BundleRestartStatus.IN_PROGRESS, failsReport.getBundleRestartStatus());

        verify(bundle).start();
        verify(bundle).stop();

        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        verify(report).setBundleRestartStatus(BundleRestartStatus.ERROR);
        verify(allBundleFailsReports).update(report);
        verify(osgiEventProxy).sendEvent(eq("org.motechproject.message"), paramsArgumentCaptor.capture());

        Map<String, Object> params = paramsArgumentCaptor.getValue();
        assertEquals("CRITICAL", params.get("level"));
        assertEquals("mds", params.get("moduleName"));
    }

    @Test
    public void shouldNotSendStatusMessage() throws BundleException {
        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextRefreshedEvent(source, bundle));

        Set<String> modules = mdsOsgiBundleApplicationContextListener.getRestartedBundles();
        assertEquals(0, modules.size());

        verify(allBundleFailsReports, never()).create(bundleFailsReportArgumentCaptor.capture());
        verify(allBundleFailsReports, never()).update(bundleFailsReportArgumentCaptor.capture());
        verify(osgiEventProxy, never()).sendEvent(eq("org.motechproject.message"), paramsArgumentCaptor.capture());
    }
}
