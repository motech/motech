package org.motechproject.mds.repository.internal;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.domain.BundleFailureReport;
import org.motechproject.mds.domain.BundleRestartStatus;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.MotechDataRepository;
import org.motechproject.mds.util.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The <code>AllBundleFailureReports</code> class is a repository class that operates on instances of
 * {@link BundleFailureReport}.
 */
@Repository
public class AllBundleFailureReports extends MotechDataRepository<BundleFailureReport> {

    private static final String[] PROPERTIES = new String[] { "nodeName", "bundleSymbolicName", "bundleRestartStatus"};

    protected AllBundleFailureReports() {
        super(BundleFailureReport.class);
    }

    public BundleFailureReport getLastInProgressReport(String nodeName, String symbolicName) {
        List<BundleFailureReport> reportList = retrieveAll(PROPERTIES, new Object[] { nodeName, symbolicName, BundleRestartStatus.IN_PROGRESS },
                new QueryParams(1, 1, new Order("reportDate", Order.Direction.DESC)), null);

        if (CollectionUtils.isNotEmpty(reportList)) {
            return reportList.get(0);
        }

        return null;
    }
}
