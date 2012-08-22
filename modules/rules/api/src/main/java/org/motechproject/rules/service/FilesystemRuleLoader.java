package org.motechproject.rules.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Load rule files from the file system
 *
 * @author Ricky Wang
 */
public class FilesystemRuleLoader {

    private static Logger logger = LoggerFactory.getLogger(FilesystemRuleLoader.class);

    private String internalRuleFolder;

    private String externalRuleFolder;

    @Autowired
    private KnowledgeBaseManagerInterface knowledgeBaseManager;

    /**
     * Load rule files from the internal and external rule folders
     *
     * @throws Exception
     */
    public void load() throws Exception {
        List<File> ruleFiles = new ArrayList<File>();
        if (internalRuleFolder != null) {
            File[] internalRuleFiles = new File(URLDecoder.decode(getClass().getResource(internalRuleFolder).getFile(), "UTF-8")).listFiles();
            ruleFiles.addAll(Arrays.asList(internalRuleFiles));
        }

        if (externalRuleFolder != null) {
            File folder = new File(externalRuleFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            } else {
                File[] externalRuleFiles = folder.listFiles();
                ruleFiles.addAll(Arrays.asList(externalRuleFiles));
            }
        }

        // Map<String, ClassLoader> bundleClassLoaderLookup = osgiFrameworkService.getBundleClassLoaderLookup();
        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
        classLoaders.add(Thread.currentThread().getContextClassLoader());
        // classLoaders.addAll(bundleClassLoaderLookup.values());

        for (File file : ruleFiles) {
            if (file.getName().toLowerCase().endsWith(".drl")) {
                try {
                    knowledgeBaseManager.addOrUpdateRule(file, classLoaders.toArray(new ClassLoader[classLoaders.size()]));
                } catch (IOException e) {
                    logger.error("Failed to load the rule file [" + file.getName() + "]", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setInternalRuleFolder(String ruleFolder) {
        this.internalRuleFolder = ruleFolder;
    }

    public void setExternalRuleFolder(String externalRuleFolder) {
        this.externalRuleFolder = externalRuleFolder;
    }

    public void setKnowledgeBaseManager(KnowledgeBaseManagerInterface knowledgeBaseManager) {
        this.knowledgeBaseManager = knowledgeBaseManager;
    }

    /* public void setOsgiFrameworkService(OsgiFrameworkService osgiFrameworkService) {
        this.osgiFrameworkService = osgiFrameworkService;
    }*/
}
