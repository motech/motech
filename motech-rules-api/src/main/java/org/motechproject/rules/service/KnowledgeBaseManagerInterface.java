package org.motechproject.rules.service;

import org.drools.KnowledgeBase;
import org.motechproject.rules.repository.AllRules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface KnowledgeBaseManagerInterface {
    void addOrUpdateRule(File ruleFile, ClassLoader... cl) throws IOException;

    void addOrUpdateRule(File ruleFile, String bundleSymbolicName, ClassLoader... cl) throws IOException;

    void addOrUpdateRule(String ruleId, String bundleSymbolicName, InputStream inputStream, ClassLoader... cl) throws IOException;

    KnowledgeBase getKnowledgeBase(String ruleId);

    void setAllRules(AllRules allRules);
}
