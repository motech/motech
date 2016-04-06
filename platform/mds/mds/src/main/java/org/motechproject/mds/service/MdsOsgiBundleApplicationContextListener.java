package org.motechproject.mds.service;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.domain.BundleFailureReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.repository.internal.AllBundleFailureReports;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The <code>MdsOsgiBundleApplicationContextListener</code> acts as an listener for Blueprint events to get notified about
 * modules being started or failing. When the module failing then it will be automatically restarted.
 */
@Service("mdsOsgiBundleApplicationContextListener")
public class MdsOsgiBundleApplicationContextListener implements OsgiBundleApplicationContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsOsgiBundleApplicationContextListener.class);
    private static final String RESTART_SUCCESS_WARN_MSG= "%s failed to start but it was successfully restarted.";
    private static final String RESTART_FAILURE_CRIT_MSG= "%s failed to start and the MDS module was unable to restart it.";

    public static final String MESSAGE_SUBJECT = "org.motechproject.message";
    public static final String MESSAGE_KEY = "message";
    public static final String LEVEL_KEY = "level";
    public static final String MODULE_NAME_KEY = "moduleName";
    public static final String TIMEOUT_EXCEPTION_MESSAGE = "Application context initialization for '%s' has timed out waiting for";

    private Set<String> restartedBundles = new HashSet<>();
    private AllBundleFailureReports allBundleFailureReports;
    private JdoTransactionManager transactionManager;
    private OsgiEventProxy osgiEventProxy;
    private SettingsService settingsService;

    @Override
    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {

        if (!settingsService.getModuleSettings().isRestartModuleAfterTimeout()) {
            return;
        }

        String symbolicName = event.getBundle().getSymbolicName();
        if (event instanceof OsgiBundleContextFailedEvent) {
            if (!restartedBundles.contains(symbolicName)) { // We want refresh bundles only one time
                OsgiBundleContextFailedEvent failedEvent = (OsgiBundleContextFailedEvent) event;
                final String failureCauseMsg = failedEvent.getFailureCause().getMessage();
                Bundle bundle = failedEvent.getBundle();

                // we will restart bundle only after timeout
                // this is temporary solution until we don't fix problems with context
                if (!failureCauseMsg.startsWith(String.format(TIMEOUT_EXCEPTION_MESSAGE, symbolicName))) {
                    return;
                }

                LOGGER.error("Received context failed event {} from {}", event, symbolicName);
                LOGGER.error("{} failed to start due to {}", symbolicName, failureCauseMsg);

                logFailReport(symbolicName, failureCauseMsg, BundleRestartStatus.IN_PROGRESS);
                restartedBundles.add(symbolicName);

                LOGGER.info("Trying to restart {} bundle", symbolicName);
                try {
                    bundle.stop();
                    bundle.start();
                } catch (BundleException e) {
                    LOGGER.error("Cannot restart {} bundle due to {}", symbolicName, e);
                }
            } else {
                LOGGER.error("cannot restart {} bundle since it we already attempted it.", symbolicName);
                sendMessage("CRITICAL", String.format(RESTART_FAILURE_CRIT_MSG, symbolicName));
                updateReportStatus(symbolicName, BundleRestartStatus.ERROR);
            }
        } else if (event instanceof OsgiBundleContextRefreshedEvent && restartedBundles.contains(symbolicName) ) {
            sendMessage("WARN", String.format(RESTART_SUCCESS_WARN_MSG, symbolicName));
            updateReportStatus(symbolicName, BundleRestartStatus.SUCCESS);
            restartedBundles.remove(symbolicName);
        }
    }

    public void clearBundlesSet() {
        restartedBundles.clear();
    }

    public Set<String>  getRestartedBundles() {
        return restartedBundles;
    }

    private void sendMessage(String level, String message) {
        Map<String, Object> params = new HashMap<>();

        params.put(LEVEL_KEY, level);
        params.put(MESSAGE_KEY, message);
        params.put(MODULE_NAME_KEY, "mds");

        osgiEventProxy.sendEvent(MESSAGE_SUBJECT, params);
    }

    private void logFailReport(final String symbolicName, final String failureCauseMsg, final BundleRestartStatus bundleRestartStatus) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                allBundleFailureReports.create(new BundleFailureReport(DateUtil.now(), getNodeName(), symbolicName, failureCauseMsg, bundleRestartStatus));
            }
        });
    }

    private void updateReportStatus(final String symbolicName, final BundleRestartStatus bundleRestartStatus) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // We retrieve the last fail report
                BundleFailureReport report = allBundleFailureReports.getLastInProgressReport(getNodeName(), symbolicName);

                if (report != null) {
                    report.setBundleRestartStatus(bundleRestartStatus);
                    allBundleFailureReports.update(report);
                }
            }
        });
    }

    private String getNodeName() {
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            return ip.getHostName();
        } catch (UnknownHostException e) {
            LOGGER.error("Cannot retrieve host name", e);
        }
        return null;
    }

    @Autowired
    public void setAllBundleFailureReports(AllBundleFailureReports allBundleFailureReports) {
        this.allBundleFailureReports = allBundleFailureReports;
    }

    @Autowired
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    public void setOsgiEventProxy(OsgiEventProxy osgiEventProxy) {
        this.osgiEventProxy = osgiEventProxy;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }


}
