package org.motechproject.mds.service;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.BundleFailsReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.repository.AllBundleFailsReports;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * The <code>MdsOsgiBundleApplicationContextListener</code> acts as an listener for Blueprint events to get notified about
 * modules being started or failing. When the module failing then it will be automatically restarted.
 */
@Component
public class MdsOsgiBundleApplicationContextListener implements OsgiBundleApplicationContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsOsgiBundleApplicationContextListener.class);

    private Set<String> restartedBundles = new HashSet<>();
    private AllBundleFailsReports allBundleFailsReports;
    private JdoTransactionManager transactionManager;

    @Override
    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
        String symbolicName = event.getBundle().getSymbolicName();

        // We want refresh bundles only one time
        if (event instanceof OsgiBundleContextFailedEvent && !restartedBundles.contains(symbolicName)) {
            OsgiBundleContextFailedEvent failedEvent = (OsgiBundleContextFailedEvent) event;
            final String failureCauseMsg = failedEvent.getFailureCause().getMessage();
            Bundle bundle = failedEvent.getBundle();

            LOGGER.error("Received context failed event {} from {}", event, symbolicName);
            LOGGER.error("{} failed to start due to {}", symbolicName, failureCauseMsg);

            logFailReport(symbolicName, failureCauseMsg, BundleRestartStatus.IN_PROGRESS);
            restartedBundles.add(symbolicName);

            LOGGER.info("Trying to restart {} bundle", symbolicName);
            try {
                bundle.stop();
                bundle.start();
                updateReportStatus(symbolicName, BundleRestartStatus.SUCCESS);
            } catch (BundleException e) {
                LOGGER.error("Cannot restart {} bundle due to {}", symbolicName, e);
                updateReportStatus(symbolicName, BundleRestartStatus.ERROR);
            }
        }
    }

    public void clearBundlesSet() {
        restartedBundles.clear();
    }

    private void logFailReport(final String symbolicName, final String failureCauseMsg, final BundleRestartStatus bundleRestartStatus) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                allBundleFailsReports.create(new BundleFailsReport(DateUtil.now(), getNodeName(), symbolicName, failureCauseMsg, bundleRestartStatus));
            }
        });
    }

    private void updateReportStatus(final String symbolicName, final BundleRestartStatus bundleRestartStatus) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // We retrieve the last fail report
                BundleFailsReport report = allBundleFailsReports.getLastInProgressReport(getNodeName(), symbolicName);

                if (report != null) {
                    report.setBundleRestartStatus(bundleRestartStatus);
                    allBundleFailsReports.update(report);
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
    public void setAllBundleFailsReports(AllBundleFailsReports allBundleFailsReports) {
        this.allBundleFailsReports = allBundleFailsReports;
    }

    @Autowired
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
