package org.motechproject.mds.repository;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.domain.BundleFailsReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The <code>AllBundleFailsReports</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.BundleFailsReport}.
 */
@Repository
public class AllBundleFailsReports extends MotechDataRepository<BundleFailsReport> {

    private static final String[] PROPERTIES = new String[] { "nodeName", "bundleSymbolicName", "bundleRestartStatus"};

    protected AllBundleFailsReports() {
        super(BundleFailsReport.class);
    }

    public BundleFailsReport getLastInProgressReport(String nodeName, String symbolicName) {
        List<BundleFailsReport> reportList = retrieveAll(PROPERTIES, new Object[] { nodeName, symbolicName, BundleRestartStatus.IN_PROGRESS },
                new QueryParams(1, 1, new Order("reportDate", Order.Direction.DESC)), null);

        if (CollectionUtils.isNotEmpty(reportList)) {
            return reportList.get(0);
        }

        return null;
    }
}
