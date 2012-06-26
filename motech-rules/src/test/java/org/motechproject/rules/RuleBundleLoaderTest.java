package org.motechproject.rules;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.motechproject.rules.service.KnowledgeBaseManager;
import org.osgi.framework.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RuleBundleLoaderTest {

    @Test
    public void loadBundleTest() throws Exception {
        String ruleFolder = "/rules";
        String ruleFile = "test.drl";
        String bundleSymbolicName = "test.bundle";
        RuleBundleLoader loader = new RuleBundleLoader();

        ArrayList<URL> urls = new ArrayList<URL>();
        urls.add(this.getClass().getResource(ruleFolder + "/" + ruleFile));
        Bundle bundle = mock(Bundle.class);
        when(bundle.findEntries(ruleFolder, "*", false)).thenReturn(Collections.enumeration(urls));
        when(bundle.getSymbolicName()).thenReturn(bundleSymbolicName);

        KnowledgeBaseManager kbm = mock(KnowledgeBaseManager.class);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws IOException {
                Object[] args = invocation.getArguments();
                System.out.println("test");
                BufferedReader br = new BufferedReader(new InputStreamReader((InputStream) args[2]));
                assertEquals(br.readLine(), "//test");
                return null;
            }
        })
                .when(kbm).addOrUpdateRule(eq(ruleFile), eq(bundleSymbolicName), any(InputStream.class));

        loader.setKnowledgeBaseManager(kbm);
        loader.loadBundle(bundle);
    }
}
