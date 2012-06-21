package org.motechproject.rules.service;

import org.junit.Test;
import org.motechproject.rules.service.FilesystemRuleLoader;
import org.motechproject.rules.service.KnowledgeBaseManagerInterface;
import org.motechproject.server.osgi.OsgiFrameworkService;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FilesystemRuleLoaderTest {

    @Test
    public void loadTest() throws Exception {
		FilesystemRuleLoader loader = new FilesystemRuleLoader();
		OsgiFrameworkService ofs = mock(OsgiFrameworkService.class);
		KnowledgeBaseManagerInterface kbm = mock(KnowledgeBaseManagerInterface.class);
		loader.setInternalRuleFolder("/rules");
//		loader.setOsgiFrameworkService(ofs);
		loader.setKnowledgeBaseManager(kbm);
		loader.load();
		verify(kbm).addOrUpdateRule(any(File.class), any(ClassLoader.class));
	}
}
