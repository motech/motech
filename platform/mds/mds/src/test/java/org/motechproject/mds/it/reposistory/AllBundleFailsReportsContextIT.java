package org.motechproject.mds.it.reposistory;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mds.domain.BundleFailureReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.it.BaseIT;
import org.motechproject.mds.repository.internal.AllBundleFailureReports;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllBundleFailsReportsContextIT extends BaseIT {

    private static final String SAMPLE_SYMBOLIC_NAME = "sample-symbolic-name";
    private static final String FAILURE_MESSAGE = "failure-message";
    private static final String NODE_NAME = "MOTECH-node-1";

    @Autowired
    private AllBundleFailureReports allBundleFailureReports;

    @Test
    public void shouldRetrieveOnlyLastReport() {
        DateTime dateTime1 = DateTime.now();
        DateTime dateTime2 = DateTime.now().minusMinutes(1);
        DateTime dateTime3 = DateTime.now().minusHours(1);
        DateTime dateTime4 = DateTime.now().minusSeconds(20);

        allBundleFailureReports.create(new BundleFailureReport(dateTime4, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_1", BundleRestartStatus.IN_PROGRESS));
        allBundleFailureReports.create(new BundleFailureReport(dateTime3, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_2", BundleRestartStatus.IN_PROGRESS));
        allBundleFailureReports.create(new BundleFailureReport(dateTime1, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_3", BundleRestartStatus.IN_PROGRESS));
        allBundleFailureReports.create(new BundleFailureReport(dateTime2, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_4", BundleRestartStatus.IN_PROGRESS));

        BundleFailureReport bundleFailureReport = allBundleFailureReports.getLastInProgressReport(NODE_NAME, SAMPLE_SYMBOLIC_NAME);

        assertNotNull(bundleFailureReport);
        assertEquals(FAILURE_MESSAGE + "_3", bundleFailureReport.getErrorMessage());
        assertEquals(dateTime1, bundleFailureReport.getReportDate());
        assertEquals(BundleRestartStatus.IN_PROGRESS, bundleFailureReport.getBundleRestartStatus());
        assertEquals(SAMPLE_SYMBOLIC_NAME, bundleFailureReport.getBundleSymbolicName());
        assertEquals(NODE_NAME, bundleFailureReport.getNodeName());
    }
}
