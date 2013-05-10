package org.motechproject.demo;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoBundleIT extends BaseOsgiIT {

    private PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 60);

    public void testTreesController() throws IOException, InterruptedException {
        DecisionTreeService decisionTreeService =
                (DecisionTreeService) verifyServiceAvailable(DecisionTreeService.class.getName());

        Tree tree = new Tree();
        tree.setName("testTree");
        tree.setDescription("testDesc");

        decisionTreeService.saveDecisionTree(tree);

        login();

        String response = httpClient.get(String.format("http://localhost:%d/demo/api/trees", TestContext.getJettyPort()),
                new BasicResponseHandler());

        assertNotNull(response);

        MotechJsonReader motechJsonReader = new MotechJsonReader();
        List<Tree> trees  = (List<Tree>) motechJsonReader.readFromString(response,
                new TypeToken<List<Tree>>() { }.getType());

        assertNotNull(trees);

        Tree result = findTreeByName(trees, "testTree");

        assertNotNull(result);
        assertEquals("testDesc", tree.getDescription());
    }

    private void login() throws IOException, InterruptedException {
        final HttpPost loginPost = new HttpPost(
                String.format("http://localhost:%d/server/motech-platform-server/j_spring_security_check", TestContext.getJettyPort()));

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("j_username", "motech"));
        nvps.add(new BasicNameValuePair("j_password", "motech"));

        loginPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));

        final HttpResponse response = httpClient.execute(loginPost);
        EntityUtils.consume(response.getEntity());
    }

    private Tree findTreeByName(List<Tree> trees, String name) {
        for (Tree tree : trees) {
            if (StringUtils.equals(tree.getName(), name)) {
                return tree;
            }
        }
        return null;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testDemoBundleContext.xml"};
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("com.google.gson.reflect");
    }
}
