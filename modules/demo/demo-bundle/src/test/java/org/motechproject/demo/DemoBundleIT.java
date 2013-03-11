package org.motechproject.demo;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DemoBundleIT extends BaseOsgiIT {

    public void testTreesController() throws IOException, InterruptedException {
        DecisionTreeService decisionTreeService =
                (DecisionTreeService) verifyServiceAvailable(DecisionTreeService.class.getName());

        Tree tree = new Tree();
        tree.setName("testTree");
        tree.setDescription("testDesc");

        decisionTreeService.saveDecisionTree(tree);

        PollingHttpClient httpClient = new PollingHttpClient();

        String response = httpClient.get("http://localhost:8080/demo/api/trees", new BasicResponseHandler());

        assertNotNull(response);

        MotechJsonReader motechJsonReader = new MotechJsonReader();
        List<Tree> trees  = (List<Tree>) motechJsonReader.readFromString(response,
                new TypeToken<List<Tree>>() { }.getType());

        assertNotNull(trees);

        Tree result = findTreeByName(trees, "testTree");

        assertNotNull(result);
        assertEquals("testDesc", tree.getDescription());
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
