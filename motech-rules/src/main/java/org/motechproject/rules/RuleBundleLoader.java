package org.motechproject.rules;

import org.apache.commons.io.FilenameUtils;
import org.motechproject.rules.service.KnowledgeBaseManager;
import org.motechproject.server.osgi.BundleLoader;
import org.osgi.framework.Bundle;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

/**
 * Custom log4j configuration loader
 * <p/>
 * Merge bundle specific configurations to the existing if a bundle contains
 * log4j.xml
 *
 * @author Ricky Wang
 */
public class RuleBundleLoader implements BundleLoader {

    // default rule folder
    private String ruleFolder = "/rules";

    @Autowired
    private KnowledgeBaseManager knowledgeBaseManager;

    @SuppressWarnings("unchecked")
    @Override
    public void loadBundle(Bundle bundle) throws Exception {
        Enumeration<URL> e = bundle.findEntries(ruleFolder, "*", false);
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
    }

    public void setRuleFolder(String ruleFolder) {
        this.ruleFolder = ruleFolder;
    }

    public void setKnowledgeBaseManager(KnowledgeBaseManager knowledgeBaseManager) {
        this.knowledgeBaseManager = knowledgeBaseManager;
    }

}
