package org.motechproject.mds.it.reposistory;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mds.domain.BundleFailsReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.it.BaseIT;
import org.motechproject.mds.repository.AllBundleFailsReports;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllBundleFailsReportsContextIT extends BaseIT {

    private static final String SAMPLE_SYMBOLIC_NAME = "sample-symbolic-name";
    private static final String FAILURE_MESSAGE = "failure-message";
    private static final String NODE_NAME = "MOTECH-node-1";

    @Autowired
    private AllBundleFailsReports allBundleFailsReports;

    @Test
    public void shouldRetrieveOnlyLastReport() {
        DateTime dateTime1 = DateTime.now();
        DateTime dateTime2 = DateTime.now().minusMinutes(1);
        DateTime dateTime3 = DateTime.now().minusHours(1);
        DateTime dateTime4 = DateTime.now().minusSeconds(20);

        allBundleFailsReports.create(new BundleFailsReport(dateTime4, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_1", BundleRestartStatus.IN_PROGRESS));
        allBundleFailsReports.create(new BundleFailsReport(dateTime3, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_2", BundleRestartStatus.IN_PROGRESS));
        allBundleFailsReports.create(new BundleFailsReport(dateTime1, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_3", BundleRestartStatus.IN_PROGRESS));
        allBundleFailsReports.create(new BundleFailsReport(dateTime2, NODE_NAME, SAMPLE_SYMBOLIC_NAME, FAILURE_MESSAGE + "_4", BundleRestartStatus.IN_PROGRESS));

        BundleFailsReport bundleFailsReport = allBundleFailsReports.getLastInProgressReport(NODE_NAME, SAMPLE_SYMBOLIC_NAME);

        assertNotNull(bundleFailsReport);
        assertEquals(FAILURE_MESSAGE + "_3", bundleFailsReport.getErrorMessage());
        assertEquals(dateTime1, bundleFailsReport.getReportDate());
        assertEquals(BundleRestartStatus.IN_PROGRESS, bundleFailsReport.getBundleRestartStatus());
        assertEquals(SAMPLE_SYMBOLIC_NAME, bundleFailsReport.getBundleSymbolicName());
        assertEquals(NODE_NAME, bundleFailsReport.getNodeName());
    }
}
