package org.motechproject.mds.service.impl;

import org.motechproject.mds.osgi.MdsBundleWatcher;
import org.motechproject.mds.service.BundleWatcherSuspensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BundleWatcherSuspensionServiceImpl implements BundleWatcherSuspensionService {

    private MdsBundleWatcher mdsBundleWatcher;

    @Override
    public void suspendBundleProcessing() {
        mdsBundleWatcher.suspendProcessing();
    }

    @Override
    public void restoreBundleProcessing() {
        mdsBundleWatcher.restoreProcessing();
    }

    @Autowired
    public void setMdsBundleWatcher(MdsBundleWatcher mdsBundleWatcher) {
        this.mdsBundleWatcher = mdsBundleWatcher;
    }

}
