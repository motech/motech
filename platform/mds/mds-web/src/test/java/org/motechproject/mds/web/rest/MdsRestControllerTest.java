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
import org.motechproject.mds.ex.rest.RestNotSupportedException;
import org.motechproject.mds.ex.rest.RestOperationNotSupportedException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.rest.MdsRestFacade;
import org.motechproject.mds.util.Order;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.DefaultRequestBuilder;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.InputStream;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;
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

    // GET

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

    // POST

    @Test
    public void shouldDoCreateForEude() throws Exception {
        testCreateUpdate(ENTITY_NAME, null, null, false);
    }

    @Test
    public void shouldDoCreateForEntityWithModule() throws Exception {
        testCreateUpdate(ENTITY_NAME, MODULE_NAME, null, false);
    }

    @Test
    public void shouldDoCreateForEntityWithModuleAndNs() throws Exception {
        testCreateUpdate(ENTITY_NAME, MODULE_NAME, NAMESPACE, false);
    }

    // PUT

    @Test
    public void shouldDoUpdateForEude() throws Exception {
        testCreateUpdate(ENTITY_NAME, null, null, true);
    }

    @Test
    public void shouldDoUpdateForEntityWithModule() throws Exception {
        testCreateUpdate(ENTITY_NAME, MODULE_NAME, null, true);
    }

    @Test
    public void shouldDoUpdateForEntityWithModuleAndNs() throws Exception {
        testCreateUpdate(ENTITY_NAME, MODULE_NAME, NAMESPACE, true);
    }

    // DELETE

    @Test
    public void shouldDoDeleteForEude() throws Exception {
        testDelete(ENTITY_NAME, null, null);
    }

    @Test
    public void shouldDoDeleteForEntityWithModule() throws Exception {
        testDelete(ENTITY_NAME, MODULE_NAME, null);
    }

    @Test
    public void shouldDoDeleteForEntityWithModuleAndNs() throws Exception {
        testDelete(ENTITY_NAME, MODULE_NAME, NAMESPACE);
    }

    // ERRORS

    @Test
    public void shouldReturn404WhenEntityNotFound() throws Exception {
        when(restFacadeRetriever.getRestFacade(ENTITY_NAME, MODULE_NAME, NAMESPACE))
                .thenThrow(new RestNotSupportedException(ENTITY_NAME, MODULE_NAME, NAMESPACE));

        String url = buildUrl(ENTITY_NAME, MODULE_NAME, NAMESPACE);

        mockMvc.perform(
                get(url)
        ).andExpect(status().isNotFound());

        mockMvc.perform(
                put(url)
        ).andExpect(status().isNotFound());

        mockMvc.perform(
                post(url)
        ).andExpect(status().isNotFound());

        mockMvc.perform(
                delete(url + "/6")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn401WhenOperationsAreNotSupported() throws Exception {
        when(restFacadeRetriever.getRestFacade(ENTITY_NAME, MODULE_NAME, NAMESPACE))
                .thenReturn(restFacade);

        when(restFacade.get(any(QueryParams.class)))
                .thenThrow(new RestOperationNotSupportedException("not supported"));
        doThrow(new RestOperationNotSupportedException("not supported")).
                when(restFacade).create(any(InputStream.class));
        doThrow(new RestOperationNotSupportedException("not supported")).
                when(restFacade).update(any(InputStream.class));
        doThrow(new RestOperationNotSupportedException("not supported")).
                when(restFacade).delete(any(Long.class));

        String url = buildUrl(ENTITY_NAME, MODULE_NAME, NAMESPACE);

        mockMvc.perform(
                get(url)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                put(url)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                post(url)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                delete(url + "/6")
        ).andExpect(status().isForbidden());
    }

    private void testRead(String entityName, String moduleName, String namespace) throws Exception {
        final TestRecord record1 = new TestRecord("T1", 5);
        final TestRecord record2 = new TestRecord("T2", 5);
        final List<TestRecord> records = asList(record1, record2);

        when(restFacadeRetriever.getRestFacade(entityName, moduleName, namespace))
                .thenReturn(restFacade);
        when(restFacade.get(any(QueryParams.class))).thenReturn(records);

        when(restFacade.get(1l)).thenReturn(record1);

        mockMvc.perform(
                get(buildUrl(entityName, moduleName, namespace) +
                        "?page=5&pageSize=14&sort=name&order=desc")
        ).andExpect(status().isOk())
         .andExpect(content().string(objectMapper.writeValueAsString(records)));

        mockMvc.perform(
                get(buildUrl(entityName, moduleName, namespace) +
                        "?id=1")
        ).andExpect(status().isOk())
         .andExpect(content().string(objectMapper.writeValueAsString(record1)));

        ArgumentCaptor<QueryParams> captor = ArgumentCaptor.forClass(QueryParams.class);
        verify(restFacade).get(captor.capture());
        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(restFacade).get(longCaptor.capture());

        QueryParams queryParams = captor.getValue();
        assertNotNull(queryParams);
        assertEquals(Long.valueOf(1), longCaptor.getValue());
        assertEquals(Integer.valueOf(5), queryParams.getPage());
        assertEquals(Integer.valueOf(14), queryParams.getPageSize());
        Order order = queryParams.getOrder();
        assertNotNull(order);
        assertEquals("name", order.getField());
        assertEquals(Order.Direction.DESC, order.getDirection());
    }

    private void testCreateUpdate(String entityName, String moduleName, String namespace, boolean update) throws Exception {
        final TestRecord record = new TestRecord("A name", -98);
        final String recordJson = objectMapper.writeValueAsString(record);

        when(restFacadeRetriever.getRestFacade(entityName, moduleName, namespace))
                .thenReturn(restFacade);

        String url = buildUrl(entityName, moduleName, namespace);
        DefaultRequestBuilder requestBuilder = (update) ? put(url) : post(url);

        mockMvc.perform(
                requestBuilder.body(recordJson.getBytes())
        ).andExpect(status().isOk());

        ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);
        if (update) {
            verify(restFacade).update(captor.capture());
        } else {
            verify(restFacade).create(captor.capture());
        }

        try (InputStream in = captor.getValue()) {
            assertEquals(record, objectMapper.readValue(in, TestRecord.class));
        }
    }

    private void testDelete(String entityName, String moduleName, String namespace) throws Exception {
        when(restFacadeRetriever.getRestFacade(entityName, moduleName, namespace))
                .thenReturn(restFacade);

        mockMvc.perform(
                delete(buildUrl(entityName, moduleName, namespace) + "/7")
        ).andExpect(status().isOk());

        verify(restFacade).delete(7L);
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
