package org.motechproject.rules.osgi;

import org.apache.commons.io.FilenameUtils;
import org.motechproject.rules.service.KnowledgeBaseManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 *  TODO: Tracker is not used properly in activator. @see org.motechproject.rules.osgi.Activator
 */
public class RuleBundleLoader extends BundleTracker {

    // default rule folder
    final static private String RULE_FOLDER = "/rules";

    @Autowired
    private KnowledgeBaseManager knowledgeBaseManager;
    private Logger logger = Logger.getLogger(RuleBundleLoader.class.getClass().getName());

    public RuleBundleLoader(BundleContext context, int stateMask, BundleTrackerCustomizer customizer) {
        super(context, stateMask, customizer);
    }


    @Override
    public Object addingBundle(Bundle bundle, BundleEvent event) {
        try {
            Enumeration<URL> e = bundle.findEntries(RULE_FOLDER, "*", false);
            String symbolicName = bundle.getSymbolicName();
            if (e != null) {
                while (e.hasMoreElements()) {
                    URL url = e.nextElement();
                    URLConnection conn = url.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    knowledgeBaseManager.addOrUpdateRule(FilenameUtils.getName(url.getFile()), symbolicName, inputStream);
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return super.addingBundle(bundle, event);
    }

    public void setKnowledgeBaseManager(KnowledgeBaseManager knowledgeBaseManager) {
        this.knowledgeBaseManager = knowledgeBaseManager;
    }

}
