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
import org.motechproject.mds.config.ModuleSettings;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.domain.BundleFailureReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.repository.AllBundleFailureReports;
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

    private static final String SAMPLE_SYMBOLIC_NAME = "org.motechproject.ebodac";
    private static final String FAILURE_MESSAGE_1 = "Application context initialization for 'org.motechproject.ebodac' has timed out waiting for (|(objectClass=org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService)";
    private static final String FAILURE_MESSAGE_2 = "Error creating bean with name 'sendSmsEventHandler' defined in URL [bundle://50.0:1/org/motechproject/sms/event/SendSmsEventHandler.class]: Unsatisfied dependency expressed through constructor argument with index 0";

    @Mock
    private AllBundleFailureReports allBundleFailureReports;

    @Mock
    private JdoTransactionManager transactionManager;

    @Mock
    private Bundle bundle;

    @Mock
    private Throwable throwable;

    @Mock
    private ApplicationContext source;

    @Mock
    private BundleFailureReport report;

    @Mock
    private TransactionStatus transactionStatus;

    @Mock
    private OsgiEventProxy osgiEventProxy;

    @Mock
    SettingsService settingsService;

    @InjectMocks
    private MdsOsgiBundleApplicationContextListener mdsOsgiBundleApplicationContextListener = new MdsOsgiBundleApplicationContextListener();

    @Captor
    ArgumentCaptor<BundleFailureReport> bundleFailsReportArgumentCaptor;

    @Captor
    ArgumentCaptor<Map<String, Object>> paramsArgumentCaptor;

    @Before
    public void setUp() {
        ModuleSettings moduleSettings = new ModuleSettings();
        moduleSettings.setRestartModuleAfterTimeout(true);
        when(settingsService.getModuleSettings()).thenReturn(moduleSettings);
        when(settingsService.isRefreshModuleAfterTimeout()).thenReturn(true);
        when(bundle.getSymbolicName()).thenReturn(SAMPLE_SYMBOLIC_NAME);
        when(throwable.getMessage()).thenReturn(FAILURE_MESSAGE_1);
        when(allBundleFailureReports.getLastInProgressReport(anyString(), eq(SAMPLE_SYMBOLIC_NAME))).thenReturn(report);
        transactionManager = PowerMockito.mock(JdoTransactionManager.class);
        PowerMockito.when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
        mdsOsgiBundleApplicationContextListener.clearBundlesSet();
    }

    @Test
    public void shouldRestartBundle() throws BundleException {
        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        verify(allBundleFailureReports).create(bundleFailsReportArgumentCaptor.capture());
        BundleFailureReport failsReport = bundleFailsReportArgumentCaptor.getValue();
        assertEquals(SAMPLE_SYMBOLIC_NAME, failsReport.getBundleSymbolicName());
        assertEquals(FAILURE_MESSAGE_1, failsReport.getErrorMessage());
        assertEquals(BundleRestartStatus.IN_PROGRESS, failsReport.getBundleRestartStatus());

        verify(bundle).start();
        verify(bundle).stop();

        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextRefreshedEvent(source, bundle));

        verify(report).setBundleRestartStatus(BundleRestartStatus.SUCCESS);
        verify(allBundleFailureReports).update(report);
        verify(osgiEventProxy).sendEvent(eq(MdsOsgiBundleApplicationContextListener.MESSAGE_SUBJECT), paramsArgumentCaptor.capture());

        Map<String, Object> params = paramsArgumentCaptor.getValue();
        assertEquals("WARN", params.get(MdsOsgiBundleApplicationContextListener.LEVEL_KEY));
        assertEquals("mds", params.get(MdsOsgiBundleApplicationContextListener.MODULE_NAME_KEY));
    }

    @Test
    public void shouldSetErrorStatus() throws BundleException {
        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        verify(allBundleFailureReports).create(bundleFailsReportArgumentCaptor.capture());
        BundleFailureReport failsReport = bundleFailsReportArgumentCaptor.getValue();
        assertEquals(SAMPLE_SYMBOLIC_NAME, failsReport.getBundleSymbolicName());
        assertEquals(FAILURE_MESSAGE_1, failsReport.getErrorMessage());
        assertEquals(BundleRestartStatus.IN_PROGRESS, failsReport.getBundleRestartStatus());

        verify(bundle).start();
        verify(bundle).stop();

        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        verify(report).setBundleRestartStatus(BundleRestartStatus.ERROR);
        verify(allBundleFailureReports).update(report);
        verify(osgiEventProxy).sendEvent(eq(MdsOsgiBundleApplicationContextListener.MESSAGE_SUBJECT), paramsArgumentCaptor.capture());

        Map<String, Object> params = paramsArgumentCaptor.getValue();
        assertEquals("CRITICAL", params.get(MdsOsgiBundleApplicationContextListener.LEVEL_KEY));
        assertEquals("mds", params.get(MdsOsgiBundleApplicationContextListener.MODULE_NAME_KEY));
    }

    @Test
    public void shouldNotSendStatusMessage() throws BundleException {
        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextRefreshedEvent(source, bundle));

        Set<String> modules = mdsOsgiBundleApplicationContextListener.getRestartedBundles();
        assertEquals(0, modules.size());

        verify(allBundleFailureReports, never()).create(bundleFailsReportArgumentCaptor.capture());
        verify(allBundleFailureReports, never()).update(bundleFailsReportArgumentCaptor.capture());
        verify(osgiEventProxy, never()).sendEvent(eq(MdsOsgiBundleApplicationContextListener.MESSAGE_SUBJECT), paramsArgumentCaptor.capture());
    }

    @Test
    public void shouldDoNothingWhenProblemIsOtherThanTimeout() throws BundleException {
        when(throwable.getMessage()).thenReturn(FAILURE_MESSAGE_2);

        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        assertEquals(0, mdsOsgiBundleApplicationContextListener.getRestartedBundles().size());
        verify(allBundleFailureReports, never()).create(bundleFailsReportArgumentCaptor.capture());
        verify(bundle, never()).start();
        verify(bundle, never()).stop();
        verify(report, never()).setBundleRestartStatus(BundleRestartStatus.ERROR);
        verify(allBundleFailureReports, never()).update(report);
        verify(osgiEventProxy, never()).sendEvent(eq(MdsOsgiBundleApplicationContextListener.MESSAGE_SUBJECT), paramsArgumentCaptor.capture());
    }

    @Test
    public void shouldDoNothingWhenSettingIsDisabled() throws BundleException {
        when(settingsService.getModuleSettings()).thenReturn(new ModuleSettings());

        mdsOsgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextFailedEvent(source, bundle, throwable));

        assertEquals(0, mdsOsgiBundleApplicationContextListener.getRestartedBundles().size());
        verify(allBundleFailureReports, never()).create(bundleFailsReportArgumentCaptor.capture());
        verify(bundle, never()).start();
        verify(bundle, never()).stop();
        verify(report, never()).setBundleRestartStatus(BundleRestartStatus.ERROR);
        verify(allBundleFailureReports, never()).update(report);
        verify(osgiEventProxy, never()).sendEvent(eq(MdsOsgiBundleApplicationContextListener.MESSAGE_SUBJECT), paramsArgumentCaptor.capture());
    }
}
