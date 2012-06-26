package org.motechproject.rules.service;

import org.apache.commons.io.IOUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.motechproject.rules.domain.Rule;
import org.motechproject.rules.repository.AllRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KnowledgeBaseManager implements KnowledgeBaseManagerInterface {

    private static Logger logger = LoggerFactory.getLogger(KnowledgeBaseManager.class);

    private Map<String, KnowledgeBase> knowledgeBaseLookup = new ConcurrentHashMap<String, KnowledgeBase>();

    @Autowired
    private AllRules allRules;

    /**
     * @param ruleFile
     * @throws java.io.IOException
     */
    public void addOrUpdateRule(File ruleFile, ClassLoader... cl) throws IOException {
        addOrUpdateRule(ruleFile, null, cl);
    }

    /**
     * @param ruleFile
     * @param bundleSymbolicName
     * @throws java.io.IOException
     */
    public void addOrUpdateRule(File ruleFile, String bundleSymbolicName, ClassLoader... cl) throws IOException {
        InputStream inputStream = new FileInputStream(ruleFile);
        addOrUpdateRule(ruleFile.getName(), bundleSymbolicName, inputStream, cl);
        inputStream.close();
    }

    /**
     * Add or update a rule in the repository and update the in-memory knowledgeBaseLookup
     * <p/>
     * TODO: this might need re-work if we want to support changing rules on the fly.
     *
     * @param ruleId
     * @param bundleSymbolicName
     * @param inputStream
     * @throws java.io.IOException
     */
    public void addOrUpdateRule(String ruleId, String bundleSymbolicName, InputStream inputStream, ClassLoader... cl) throws IOException {
        logger.debug("Adding rule [" + ruleId + "," + bundleSymbolicName + "]");

        Rule rule = null;
        if (allRules.contains(ruleId)) {
            rule = allRules.get(ruleId);
        } else {
            rule = new Rule();
            rule.setId(ruleId);
        }
        rule.setContent(IOUtils.toString(inputStream));
        rule.setBundleSymbolicName(bundleSymbolicName);

        if (rule.isNew()) {
            allRules.add(rule);
        } else {
            allRules.update(rule);
        }

        // update the in-memory knowledgeBaseLookup
        KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, cl);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(rule.getContent())), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            logger.error(kbuilder.getErrors().toString());
        } else {
            KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, cl);
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            knowledgeBaseLookup.put(rule.getId(), kbase);
        }
    }

    /**
     * Get a KnowledgeBase instance by a rule id
     *
     * @param ruleId
     * @return
     */
    public KnowledgeBase getKnowledgeBase(String ruleId) {
        if (ruleId != null) {
            return knowledgeBaseLookup.get(ruleId);
        } else {
            return null;
        }
    }

    public void setAllRules(AllRules allRules) {
        this.allRules = allRules;
    }

}
