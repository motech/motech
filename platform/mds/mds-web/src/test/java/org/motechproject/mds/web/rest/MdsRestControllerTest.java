package org.motechproject.mds.web.rest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.rest.MdsRestFacade;
import org.motechproject.mds.util.Order;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class MdsRestControllerTest {

    private static final String ENTITY_NAME = "testrecord";
    private static final String MODULE_NAME = "somemodule";
    private static final String NAMESPACE = "somens";

    @Mock
    private MdsRestFacadeRetriever restFacadeRetriever;

    @Mock
    private MdsRestFacade restFacade;

    @InjectMocks
    private MdsRestController mdsRestController = new MdsRestController();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mdsRestController).build();
    }

    @Test
    public void shouldDoReadForEude() throws Exception {
        testRead(ENTITY_NAME, null, null);
    }

    @Test
    public void shouldDoReadForEntityWithModule() throws Exception {
        testRead(ENTITY_NAME, MODULE_NAME, null);
    }

    @Test
    public void shouldDoReadForEntityWithModuleAndNs() throws Exception {
        testRead(ENTITY_NAME, MODULE_NAME, NAMESPACE);
    }

    private void testRead(String entityName, String moduleName, String namespace) throws Exception {
        final TestRecord record1 = new TestRecord("T1", 5);
        final TestRecord record2 = new TestRecord("T2", 5);
        final List<TestRecord> records = asList(record1, record2);

        when(restFacadeRetriever.getRestFacade(entityName, moduleName, namespace))
                .thenReturn(restFacade);
        when(restFacade.get(any(QueryParams.class))).thenReturn(records);

        mockMvc.perform(
                get(buildUrl(entityName, moduleName, namespace) +
                        "?page=5&pageSize=14&sort=name&order=desc")
        ).andExpect(status().isOk())
         .andExpect(content().string(objectMapper.writeValueAsString(records)));

        ArgumentCaptor<QueryParams> captor = ArgumentCaptor.forClass(QueryParams.class);
        verify(restFacade).get(captor.capture());

        QueryParams queryParams = captor.getValue();
        assertNotNull(queryParams);
        assertEquals(Integer.valueOf(5), queryParams.getPage());
        assertEquals(Integer.valueOf(14), queryParams.getPageSize());
        Order order = queryParams.getOrder();
        assertNotNull(order);
        assertEquals("name", order.getField());
        assertEquals(Order.Direction.DESC, order.getDirection());
    }

    private String buildUrl(String entityName, String moduleName, String namespace) {
        StringBuilder sb = new StringBuilder("/rest");

        if (StringUtils.isNotBlank(moduleName)) {
            sb.append('/').append(moduleName);
        }

        if (StringUtils.isNotBlank(namespace)) {
            sb.append('/').append(namespace);
        }

        sb.append('/').append(entityName);

        return sb.toString();
    }
}
