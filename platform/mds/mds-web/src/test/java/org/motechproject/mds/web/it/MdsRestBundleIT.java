package org.motechproject.mds.web.it;

import ch.lambdaj.Lambda;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.JarGeneratorService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsRestBundleIT extends BasePaxIT {

    private static final String ENTITY_NAME = "RestTestEnt";
    private static final String FILTERED_ENTITY_NAME = "FilteredRestTestEnt";
    private static final String ENTITY_URL = String.format("http://localhost:%d/mds/rest/%s",
            TestContext.getJettyPort(), ENTITY_NAME);
    private static final String FILTERED_ENTITY_URL = String.format("http://localhost:%d/mds/rest/%s",
            TestContext.getJettyPort(), FILTERED_ENTITY_NAME);

    private static final Set<String> FILTERED_REST_FIELDS = new HashSet<>(asList("intField", "owner", "id"));

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    private EntityService entityService;

    @Inject
    private JarGeneratorService jarGeneratorService;

    @Inject
    private BundleContext bundleContext;

    @Override
    protected Collection<String> getAdditionalTestDependencies() {
        return asList("org.motechproject:motech-scheduler");
    }

    @BeforeClass
    public static void setUpClass() throws IOException, InterruptedException {
        createAdminUser();

        getHttpClient().getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthPolicy.BASIC),
                new UsernamePasswordCredentials("motech", "motech")
        );
    }

    @Before
    public void setUp() throws IOException {
        if (getDataService() == null || getDataServiceForFilteredEntity() == null) {
            clearEntities();
            prepareEntity();
            prepareFilteredEntity();
            jarGeneratorService.regenerateMdsDataBundle(true);
        }

        getDataService().deleteAll();
        getDataServiceForFilteredEntity().deleteAll();
    }

    @Test
    public void testBasicCrud() throws Exception {
        final MotechDataService dataService = getDataService();
        final Class<?> entityClass = dataService.getClassType();

        // CREATE
        // create 11 records using REST

        getLogger().info("Creating instance via REST");

        for (int i = 0; i < 11; i++) {
            HttpPost post = new HttpPost(ENTITY_URL);
            post.setEntity(new StringEntity(recordJsonString("string" + i, i),
                    ContentType.APPLICATION_JSON));

            HttpResponse response = getHttpClient().execute(post);

            assertNotNull(response);
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }

        // UPDATE
        // update records 4 & 5
        // we add "updated" at the end of the string

        // GET object Ids
        // TODO: should we return ID in REST responses?
        // TODO: invoke this lookup through REST
        QueryParams queryParams = new QueryParams(new Order("intField", Order.Direction.ASC));
        List objList = (List) MethodUtils.invokeMethod(dataService, "byIntSet", new Object[] {
                new HashSet<>(asList(4, 5)), queryParams
        });
        // verify that the lookup works correctly just in case
        assertNotNull(objList);
        assertEquals(2, objList.size());

        for (Object record : objList) {
            HttpPut put = new HttpPut(ENTITY_URL);
            put.setEntity(new StringEntity(recordJsonString(
                    PropertyUtil.getProperty(record, "strField") + "Updated",
                    (int) PropertyUtil.getProperty(record, "intField"),
                    (long) PropertyUtil.getProperty(record, "id")
            )));

            HttpResponse response = getHttpClient().execute(put);

            assertNotNull(response);
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }

        // READ
        // read records from 5 to 1

        getLogger().info("Reading instances via REST");

        String body = getHttpClient().get(ENTITY_URL + "?page=2&pageSize=5&sort=intField&order=desc",
                new BasicResponseHandler());

        assertNotNull(body);

        List list = OBJECT_MAPPER.readValue(body, OBJECT_MAPPER.getTypeFactory()
                .constructCollectionType(List.class, entityClass));

        assertEquals(5, list.size());
        for (int i = 0; i < 5; i++) {
            int expectedIntField = 5 - i;
            // we updated the int fields X 10 for these two records
            boolean fieldWasUpdated = expectedIntField == 5 || expectedIntField == 4;
            // and added "Updated" to the string field
            String expectedStrField = "string" + expectedIntField;
            if (fieldWasUpdated) {
                expectedStrField += "Updated";
            }

            Object record = list.get(i);

            assertEquals(expectedStrField, PropertyUtils.getProperty(record, "strField"));
            assertEquals(expectedIntField, PropertyUtils.getProperty(record, "intField"));
        }

        for (Object rec1 : list) {
            String responseBody = getHttpClient().get(ENTITY_URL + "?id=" + PropertyUtils.getProperty(rec1, "id"),
                    new BasicResponseHandler());

            assertNotNull(responseBody);

            Object rec2 = OBJECT_MAPPER.readValue(responseBody, OBJECT_MAPPER.getTypeFactory().constructType(entityClass));

            assertEquals(PropertyUtils.getProperty(rec2, "strField"), PropertyUtils.getProperty(rec1, "strField"));
        }

        // DELETE
        // delete 5 records using REST

        getLogger().info("Delete instances via REST");

        List list2 = dataService.retrieveAll();
        for (int i = 0; i < 5; i++) {
            Object record2 = list2.get(i);
            long id = (long) PropertyUtils.getProperty(record2,"id");

            HttpDelete delete = new HttpDelete(ENTITY_URL + "/" + id);
            HttpResponse response2 = getHttpClient().execute(delete);

            assertNotNull(response2);
            assertEquals(HttpStatus.SC_OK, response2.getStatusLine().getStatusCode());

            assertNull(dataService.findById(id));
        }
        assertEquals(dataService.retrieveAll().size(), 6);
    }

    @Test
    public void testLookups() throws Exception {
        final MotechDataService dataService = getDataService();
        final Class<?> entityClass = dataService.getClassType();

        // create some records
        // make sure to use spaces
        List<String> recordJsons = new ArrayList<>();
        recordJsons.add(recordJsonString("myStr 1", 5));
        recordJsons.add(recordJsonString("myStr 2", 10));
        recordJsons.add(recordJsonString("myStr 3", 10));
        recordJsons.add(recordJsonString("myStr 4", 15));
        recordJsons.add(recordJsonString("myStr 5", 15));

        for (String json : recordJsons) {
            HttpPost post = new HttpPost(ENTITY_URL);
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            HttpResponse response = getHttpClient().execute(post);

            assertNotNull(response);
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        }

        // test single return lookup
        verifySingleLookup(entityClass, "myStr 1", 5);
        verifySingleLookup(entityClass, "myStr 2", 10);
        verifySingleLookup(entityClass, "myStr 3", 10);
        verifySingleLookup(entityClass, "myStr 4", 15);
        verifySingleLookup(entityClass, "myStr 5", 15);

        // test multiple return lookup
        verifyMultiLookup(entityClass, 5, "myStr 1");
        verifyMultiLookup(entityClass, 10, "myStr 2", "myStr 3");
        verifyMultiLookup(entityClass, 15, "myStr 4", "myStr 5");
    }

    @Test
    public void testRestExposedFields() throws Exception {
        final JavaType mapType = OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        final JavaType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, mapType);

        // CREATE
        HttpPost post = new HttpPost(FILTERED_ENTITY_URL);
        post.setEntity(new StringEntity(recordJsonString("some-string", 42),
                ContentType.APPLICATION_JSON));
        String body = getHttpClient().execute(post, new BasicResponseHandler());
        Map record = OBJECT_MAPPER.readValue(body, mapType);

        assertEquals(FILTERED_REST_FIELDS.size(), record.size());
        assertEquals(FILTERED_REST_FIELDS, record.keySet());
        assertEquals(42, record.get("intField"));

        // UPDATE
        HttpPut put = new HttpPut(FILTERED_ENTITY_URL);
        put.setEntity(new StringEntity(recordJsonString(
            record.get("strField") + "Updated",
                (int) record.get("intField") + 13,
                (long) (int) record.get("id")
        )));

        body = getHttpClient().execute(put, new BasicResponseHandler());
        record = OBJECT_MAPPER.readValue(body, mapType);

        assertEquals(FILTERED_REST_FIELDS.size(), record.size());
        assertEquals(FILTERED_REST_FIELDS, record.keySet());
        assertEquals(42 + 13, record.get("intField"));

        // READ
        body = getHttpClient().get(FILTERED_ENTITY_URL, new BasicResponseHandler());
        List<Map> records = OBJECT_MAPPER.readValue(body, listType);
        assertEquals(1, records.size());
        record = records.get(0);

        assertEquals(FILTERED_REST_FIELDS.size(), record.size());
        assertEquals(FILTERED_REST_FIELDS, record.keySet());
        assertEquals(42 + 13, record.get("intField"));

        // Make sure the string field is ignored
        List result = getDataServiceForFilteredEntity().retrieveAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        for (Object obj : result) {
            assertNotNull(obj);
            assertNull(PropertyUtil.getProperty(obj, "strField"));
        }
    }

    @Test
    public void shouldReturnBadBodyResponseForIncompleteData() throws IOException, InterruptedException {
        HttpPost post = new HttpPost(ENTITY_URL);
        post.setEntity(new StringEntity("{}"));
        HttpResponse response = getHttpClient().execute(post);

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldReturn200ForDeletingNonExistingItem() throws IOException, InterruptedException {
        HttpDelete delete = new HttpDelete(ENTITY_URL + "/1988");
        HttpResponse response = getHttpClient().execute(delete);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    private void verifySingleLookup(Class entityClass, String lookupParam, int expectedInt) throws Exception {
        HttpGet get = new HttpGet(ENTITY_URL + "?lookup=byStr&strField=" + URLEncoder.encode(lookupParam, "UTF-8"));

        String responseBody = getHttpClient().execute(get, new BasicResponseHandler());
        assertNotNull(responseBody);

        Object record = OBJECT_MAPPER.readValue(responseBody, entityClass);
        assertEquals(lookupParam, PropertyUtils.getProperty(record, "strField"));
        assertEquals(expectedInt, PropertyUtils.getProperty(record, "intField"));
        assertNotNull(PropertyUtils.getProperty(record, "id"));
    }

    private void verifyMultiLookup(Class entityClass, int lookupParam, String... expectedStrings)
            throws Exception {
        HttpGet get = new HttpGet(ENTITY_URL + "?lookup=byInt&intField=" + lookupParam +
            "&sort=strField&order=ASC");

        String responseBody = getHttpClient().execute(get, new BasicResponseHandler());
        List list = OBJECT_MAPPER.readValue(responseBody, OBJECT_MAPPER.getTypeFactory()
                .constructCollectionType(List.class, entityClass));

        List strings = Lambda.extract(list, PropertyUtils.getProperty(on(entityClass), "strField"));

        assertEquals(asList(expectedStrings), strings);
    }

    private void clearEntities() {
        getLogger().info("Cleaning up entities");

        for (EntityDto entity : entityService.listEntities()) {
            if (!entity.isDDE()) {
                entityService.deleteEntity(entity.getId());
            }
        }
    }

    private void prepareEntity() throws IOException {
        EntityDto entityDto = new EntityDto(ENTITY_NAME);
        entityDto = entityService.createEntity(entityDto);
        FieldDto strField = new FieldDto(null, entityDto.getId(), TypeDto.STRING,
                new FieldBasicDto("strFieldDisp", "strField", true), false, null);
        FieldDto intField = new FieldDto(null, entityDto.getId(), TypeDto.INTEGER,
                new FieldBasicDto("intFieldDisp", "intField"), false, null);

        entityService.addFields(entityDto, asList(strField, intField));

        RestOptionsDto restOptions = new RestOptionsDto(true, true, true, true, false);
        restOptions.setFieldNames(prepareAllRestFieldNames(entityService.getEntityFields(entityDto.getId())));
        entityService.updateRestOptions(entityDto.getId(), restOptions);

        // a set based lookup for our convenience
        LookupFieldDto intSetLookupField = new LookupFieldDto(null, "intField", LookupFieldType.SET);
        LookupDto setLookup = new LookupDto("byIntSet", false, true, asList(intSetLookupField),false);
        // list return REST lookup
        LookupFieldDto intLookupField = new LookupFieldDto(null, "intField", LookupFieldType.VALUE);
        LookupDto listLookup = new LookupDto("byInt", false, true, asList(intLookupField), false);
        // single return REST lookup
        LookupFieldDto strLookupField = new LookupFieldDto(null, "strField", LookupFieldType.VALUE);
        LookupDto singleLookup = new LookupDto("byStr", true, true, asList(strLookupField), false);

        entityService.addLookups(entityDto.getId(), asList(setLookup, listLookup, singleLookup));
    }

    private void prepareFilteredEntity() throws IOException {
        EntityDto entityDto = new EntityDto(FILTERED_ENTITY_NAME);
        entityDto = entityService.createEntity(entityDto);
        FieldDto strField = new FieldDto(null, entityDto.getId(), TypeDto.STRING,
                new FieldBasicDto("strFieldDisp", "strField"), false, null);
        FieldDto intField = new FieldDto(null, entityDto.getId(), TypeDto.INTEGER,
                new FieldBasicDto("intFieldDisp", "intField"), false, null);

        entityService.addFields(entityDto, asList(strField, intField));

        RestOptionsDto restOptions = new RestOptionsDto(true, true, true, true, false);
        restOptions.setFieldNames(prepareFilteredRestFieldNames(entityService.getEntityFields(entityDto.getId())));
        entityService.updateRestOptions(entityDto.getId(), restOptions);
    }

    private List<String> prepareAllRestFieldNames(List<FieldDto> fieldDtos) {
        List<String> restFieldsIds = new ArrayList<>();
        for (FieldDto fieldDto : fieldDtos) {
            restFieldsIds.add(fieldDto.getBasic().getName());
        }
        return restFieldsIds;
    }

    private List<String> prepareFilteredRestFieldNames(List<FieldDto> fieldDtos) {
        List<String> restFieldNames = new ArrayList<>();
        for (FieldDto fieldDto : fieldDtos) {
            if (FILTERED_REST_FIELDS.contains(fieldDto.getBasic().getName())) {
                restFieldNames.add(fieldDto.getBasic().getName());
            }
        }
        return restFieldNames;
    }

    private String recordJsonString(String strField, int intField) {
        return String.format("{\"strField\": \"%s\", \"intField\": \"%d\"}",
                strField, intField);
    }

    private String recordJsonString(String strField, int intField, Long id) {
        return String.format("{\"strField\": \"%s\", \"intField\": \"%d\", \"id\": \"%d\"}",
                strField, intField, id);

    }

    private MotechDataService getDataService() {
        return OSGiServiceUtils.findService(bundleContext, ClassName.getInterfaceName(ENTITY_NAME));
    }

    private MotechDataService getDataServiceForFilteredEntity() {
        return OSGiServiceUtils.findService(bundleContext, ClassName.getInterfaceName(FILTERED_ENTITY_NAME));
    }
}
