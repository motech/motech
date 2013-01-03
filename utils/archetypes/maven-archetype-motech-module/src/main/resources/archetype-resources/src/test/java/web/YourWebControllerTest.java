package ${groupId}.web;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;


/**
 * Unit test for Controller.
 */
public class YourWebControllerTest {
    @Test
    public void testController() {
        assertNotNull(new YourWebController().getAllObjects());
    }
}
