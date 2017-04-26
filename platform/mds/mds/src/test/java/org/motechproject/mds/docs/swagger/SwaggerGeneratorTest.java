package org.motechproject.mds.docs.swagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.docs.swagger.gson.ParameterTypeAdapter;
import org.motechproject.mds.docs.swagger.model.Definition;
import org.motechproject.mds.docs.swagger.model.Info;
import org.motechproject.mds.docs.swagger.model.License;
import org.motechproject.mds.docs.swagger.model.Parameter;
import org.motechproject.mds.docs.swagger.model.ParameterType;
import org.motechproject.mds.docs.swagger.model.PathEntry;
import org.motechproject.mds.docs.swagger.model.Property;
import org.motechproject.mds.docs.swagger.model.Response;
import org.motechproject.mds.docs.swagger.model.ResponseWithSchema;
import org.motechproject.mds.docs.swagger.model.SwaggerModel;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.repository.internal.AllEntities;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.util.Constants;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.API_DESCRIPTION_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.ARRAY_TYPE;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.CREATE_BODY_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.CREATE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.CREATE_ID_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.DELETE_ID_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.DELETE_ID_PARAM_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.ID_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.INT64_FORMAT;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.INTEGER_TYPE;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.LICENSE_NAME_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.LICENSE_URL_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.ORDER_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.PAGESIZE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.PAGE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RANGE_PARAM_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.READ_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.READ_ID_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_BAD_REQUEST_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_FORBIDDEN_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_LIST_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_LOOKUP_NOT_FOUND_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_NEW_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_NOT_FOUND_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_SINGLE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.RESPONSE_UPDATED_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.SET_PARAM_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.SORT_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.STRING_TYPE;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.TITLE_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.UPDATE_BODY_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.UPDATE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.UPDATE_ID_KEY;
import static org.motechproject.mds.docs.swagger.model.SwaggerConstants.VERSION_KEY;
import static org.motechproject.mds.testutil.FieldTestHelper.field;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerGeneratorTest {

    private static final Locale LOCALE = new Locale("en", "US");

    @Mock
    private AllEntities allEntities;

    private Properties swaggerProperties;

    private MessageSource messageSource;

    private SwaggerGenerator swaggerGenerator = new SwaggerGenerator();

    @Before
    public void setUp() throws IOException {

        messageSource = new ReloadableResourceBundleMessageSource();
        ((ReloadableResourceBundleMessageSource) messageSource).setBasename("swagger-messages");

        swaggerGenerator.setSwaggerMessageSource(messageSource);

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("swagger.properties")) {
            swaggerProperties = new Properties();
            swaggerProperties.load(in);
            swaggerGenerator.setSwaggerProperties(swaggerProperties);
            swaggerGenerator.setAllEntities(allEntities);
        }

        when(allEntities.retrieveAll()).thenReturn(entities());
    }

    @Test
    public void shouldGenerateJson() {
        StringWriter stringWriter = new StringWriter();

        swaggerGenerator.generateDocumentation(stringWriter, "/motech-platform-server", LOCALE);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Response.class, new ResponseAdapter())
                .registerTypeAdapter(ParameterType.class, new ParameterTypeAdapter())
                .create();

        SwaggerModel swaggerModel = gson.fromJson(stringWriter.toString(), SwaggerModel.class);

        verifyCommonModel(swaggerModel);
        verifyDefinitions(swaggerModel);
        verifyPaths(swaggerModel);
    }

    private void verifyCommonModel(SwaggerModel swaggerModel) {
        assertNotNull(swaggerModel);
        assertEquals(json(), swaggerModel.getConsumes());
        assertEquals(json(), swaggerModel.getProduces());
        assertEquals("2.0", swaggerModel.getSwagger());
        assertEquals(asList("http"), swaggerModel.getSchemes());
        assertEquals("/motech-platform-server/mds/rest", swaggerModel.getBasePath());

        Info info = swaggerModel.getInfo();

        assertNotNull(info);
        assertEquals(msg(TITLE_KEY), info.getTitle());
        assertEquals(msg(API_DESCRIPTION_KEY), info.getDescription());
        assertEquals(msg(VERSION_KEY), info.getVersion());

        License license = info.getLicense();

        assertNotNull(license);
        assertEquals(msg(LICENSE_NAME_KEY), license.getName());
        assertEquals(msg(LICENSE_URL_KEY), license.getUrl());
    }

    private void verifyDefinitions(SwaggerModel swaggerModel) {
        Map<String, Definition> definitions = swaggerModel.getDefinitions();

        assertNotNull(definitions);
        assertEquals(7, definitions.size());
        verifyTestEntityDefinitions(definitions);
        verifyExampleEntDefinitions(definitions);
    }

    private void verifyPaths(SwaggerModel swaggerModel) {
        Map<String, Map<String, PathEntry>> paths = swaggerModel.getPaths();

        assertNotNull(paths);
        assertEquals(5, paths.size());
        verifyTestEntityPaths(paths);
        verifyExampleEntPaths(paths);
    }

    private List<Entity> entities() {
        List<Entity> entities = new ArrayList<>();

        // Entity 1

        Entity entity = new Entity();
        entity.setClassName("org.example.TestEntity");
        entity.setName("TestEntity");
        entity.setModule("example");
        entity.setNamespace("ns");

        RestOptions restOptions = new RestOptions(entity);
        restOptions.setAllowCreate(true);
        restOptions.setAllowRead(true);
        entity.setRestOptions(restOptions);

        List<Field> fields = new ArrayList<>();

        Field strField = field("str", String.class, true, true);
        strField.setTooltip("A string field used in tests");
        fields.add(strField);
        fields.add(field("integerField", Integer.class, true, true));
        Field longField = field("longField", Long.class, false, true);
        fields.add(longField);
        fields.add(field("timeField", Time.class, false, true));
        fields.add(field("ignoredField", String.class, false, false));
        fields.addAll(autoGeneratedFields());

        entity.setFields(fields);

        Lookup lookup = new Lookup("Find By Str & Long", true, true, asList(strField, longField), true, "findByStrLong");
        lookup.setFieldsOrder(asList("str", "longField"));
        entity.addLookup(lookup);

        entities.add(entity);

        // Entity 2

        entity = new Entity();
        entity.setClassName("org.motechproject.ExampleEnt");
        entity.setName("ExampleEnt");

        restOptions = new RestOptions(entity);
        restOptions.setAllowUpdate(true);
        restOptions.setAllowDelete(true);
        entity.setRestOptions(restOptions);

        fields = new ArrayList<>();
        fields.add(field("doubleField", Double.class, true, true));
        fields.add(field("dateField", Date.class, false, true));
        Field dtField = field("dtField", DateTime.class, false, true);
        fields.add(dtField);
        fields.add(field("ldField", LocalDate.class, false, true));
        Field localeField = field("localeField", Locale.class, false, true);
        fields.add(localeField);

        Field listField = FieldTestHelper.fieldWithComboboxSettings(entity, "listField", "list disp", List.class,
                true, false, asList("one", "two", "three"));
        listField.setExposedViaRest(true);
        listField.setRequired(true);
        fields.add(listField);

        fields.addAll(autoGeneratedFields());

        entity.setFields(fields);

        lookup = new Lookup("By Dt and Locale", false, true, asList(dtField, localeField), false, "byDtAndLocale");
        lookup.setRangeLookupFields(asList(dtField.getName()));
        lookup.setSetLookupFields(asList(localeField.getName()));
        lookup.setFieldsOrder(asList("dtField", "localeField"));
        entity.addLookup(lookup);

        entities.add(entity);

        return entities;
    }

    private List<Field> autoGeneratedFields() {
        List<Field> fields = new ArrayList<>();

        fields.add(field("owner", String.class, false, true, true));
        fields.add(field("creator", String.class, true, true, true));
        fields.add(field("modifiedBy", String.class, true, true, true));
        fields.add(field("modificationDate", DateTime.class, true, true, true));
        fields.add(field("creationDate", DateTime.class, true, true, true));

        return fields;
    }

    private void verifyTestEntityPaths(Map<String, Map<String, PathEntry>> paths) {
        Map<String, PathEntry> pathEntries = paths.get("/example/ns/testentity");
        assertNotNull(pathEntries);
        assertEquals(2, pathEntries.size());

        PathEntry pathEntry = pathEntries.get("get");
        verifyTestEntityGetAllPath(pathEntry);

        pathEntry = pathEntries.get("post");
        verifyTestEntityPostPath(pathEntry);

        pathEntries = paths.get("/lookup/example/ns/testentity/findByStrLong");
        assertNotNull(pathEntries);
        assertEquals(1, pathEntries.size());

        pathEntry = pathEntries.get("get");
        verifyTestEntityLookup(pathEntry);
    }

    private void verifyTestEntityGetAllPath(PathEntry pathEntry) {
        assertNotNull(pathEntry);
        verifyTestEntityPathEntryCommon(pathEntry, READ_DESC_KEY, READ_ID_KEY);

        List<Parameter> parameters = pathEntry.getParameters();

        assertNotNull(parameters);
        assertEquals(5, parameters.size());
        verifyQueryParameters(pathEntry.getParameters(),
                asList("str", "integerField", "longField", "timeField", "owner", "creator",
                        "modifiedBy", "modificationDate", "creationDate"), 0);
        verifyQueryParameter(parameters.get(4), "id", ID_DESC_KEY, "integer", "int64");

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(3, responses.size());

        verify400Response(responses);
        verify403Response(responses);

        Response response = responses.get(200);
        assertTrue(response instanceof ResponseWithSchema);
        ResponseWithSchema responseWithSchema = (ResponseWithSchema) response;
        assertEquals(msg(RESPONSE_LIST_DESC_KEY, "TestEntity"), responseWithSchema.getDescription());

        Map<String, String> schema = responseWithSchema.getSchema();

        assertNotNull(schema);
        assertEquals(1, schema.size());
        assertEquals("#/definitions/org.example.TestEntity-WithMetadata", schema.get("$ref"));
    }

    private void verifyTestEntityPostPath(PathEntry pathEntry) {
        assertNotNull(pathEntry);
        verifyTestEntityPathEntryCommon(pathEntry, CREATE_DESC_KEY, CREATE_ID_KEY);

        List<Parameter> parameters = pathEntry.getParameters();

        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        Parameter newParam = parameters.get(0);

        assertNotNull(newParam);
        assertEquals("TestEntity", newParam.getName());
        assertEquals(ParameterType.BODY, newParam.getIn());
        assertNull(newParam.getFormat());
        assertTrue(newParam.isRequired());
        assertEquals(msg(CREATE_BODY_DESC_KEY, "TestEntity"), newParam.getDescription());

        Map<String, String> newParamSchema = newParam.getSchema();

        assertNotNull(newParamSchema);
        assertEquals(1, newParamSchema.size());
        assertEquals("#/definitions/org.example.TestEntity-new", newParamSchema.get("$ref"));

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(3, responses.size());

        verify400Response(responses);
        verify403Response(responses);

        Response response = responses.get(200);

        assertTrue(response instanceof ResponseWithSchema);
        ResponseWithSchema singleItemResponse = (ResponseWithSchema) response;
        assertEquals(msg(RESPONSE_NEW_DESC_KEY, "TestEntity"), singleItemResponse.getDescription());

        Map<String, String> newResponseSchema = singleItemResponse.getSchema();

        assertNotNull(newResponseSchema);
        assertEquals(1, newResponseSchema.size());
        assertEquals("#/definitions/org.example.TestEntity", newResponseSchema.get("$ref"));
    }

    private void verifyTestEntityLookup(PathEntry pathEntry) {
        assertNotNull(pathEntry);
        assertEquals("Lookup - Find By Str & Long", pathEntry.getDescription());
        assertEquals(asList("org.example.TestEntity"), pathEntry.getTags());
        assertEquals("findByStrLong", pathEntry.getOperationId());
        assertEquals(json(), pathEntry.getProduces());

        List<Parameter> parameters = pathEntry.getParameters();

        Parameter strParam = parameters.get(0);

        assertNotNull(strParam);
        assertEquals("str", strParam.getName());
        assertEquals(ParameterType.QUERY, strParam.getIn());
        assertEquals("A string field used in tests", strParam.getDescription());
        assertEquals(STRING_TYPE, strParam.getType());
        assertNull(strParam.getFormat());
        assertNull(strParam.getItems());
        assertNull(strParam.getSchema());

        Parameter longParam = parameters.get(1);

        assertNotNull(longParam);
        assertEquals("longField", longParam.getName());
        assertEquals(ParameterType.QUERY, strParam.getIn());
        assertEquals("longField", longParam.getDescription());
        assertEquals(INTEGER_TYPE, longParam.getType());
        assertEquals(INT64_FORMAT, longParam.getFormat());
        assertNull(longParam.getItems());
        assertNull(longParam.getSchema());

        assertNotNull(parameters);
        assertEquals(6, parameters.size());
        verifyQueryParameters(pathEntry.getParameters(),
                asList("str", "integerField", "longField", "timeField", "owner", "creator",
                        "modifiedBy", "modificationDate", "creationDate"), 2);

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(4, responses.size());

        verify400Response(responses);
        verify403Response(responses);

        Response response = responses.get(404);

        assertNotNull(response);
        assertEquals(msg(RESPONSE_LOOKUP_NOT_FOUND_KEY, "TestEntity"), response.getDescription());

        response = responses.get(200);

        assertTrue(response instanceof ResponseWithSchema);
        ResponseWithSchema singleItemResponse = (ResponseWithSchema) response;
        assertEquals(msg(RESPONSE_SINGLE_DESC_KEY, "TestEntity"), singleItemResponse.getDescription());

        Map<String, String> newResponseSchema = singleItemResponse.getSchema();

        assertNotNull(newResponseSchema);
        assertEquals(1, newResponseSchema.size());
        assertEquals("#/definitions/org.example.TestEntity-WithMetadata", newResponseSchema.get("$ref"));
    }

    private void verifyTestEntityPathEntryCommon(PathEntry pathEntry, String descKey, String idKey) {
        assertEquals(msg(descKey, "TestEntity"), pathEntry.getDescription());
        assertEquals(asList("org.example.TestEntity"), pathEntry.getTags());
        assertEquals(msg(idKey, "TestEntity"), pathEntry.getOperationId());
        assertEquals(json(), pathEntry.getProduces());
    }

    private void verifyExampleEntPaths(Map<String, Map<String, PathEntry>> paths) {
        Map<String, PathEntry> pathEntries = paths.get("/exampleent");

        assertNotNull(pathEntries);
        assertEquals(1, pathEntries.size());

        verifyExampleEntUpdatePath(pathEntries.get("put"));

        pathEntries = paths.get("/exampleent/{id}");

        assertNotNull(pathEntries);
        assertEquals(1, pathEntries.size());

        verifyExampleEntDeletePath(pathEntries.get("delete"));

        pathEntries = paths.get("/lookup/exampleent/byDtAndLocale");
        verifyExampleEntLookup(pathEntries.get("get"));
    }

    private void verifyExampleEntUpdatePath(PathEntry pathEntry) {
        assertNotNull(pathEntry);

        verifyExampleEntCommonPath(pathEntry, UPDATE_DESC_KEY, UPDATE_ID_KEY);

        List<Parameter> parameters = pathEntry.getParameters();

        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        Parameter bodyParam = parameters.get(0);

        assertEquals("ExampleEnt", bodyParam.getName());
        assertEquals(msg(UPDATE_BODY_DESC_KEY, "ExampleEnt"), bodyParam.getDescription());
        assertEquals(ParameterType.BODY, bodyParam.getIn());
        assertNull(bodyParam.getItems());
        assertTrue(bodyParam.isRequired());
        assertNull(bodyParam.getType());
        assertNull(bodyParam.getFormat());

        Map<String, String> paramSchema = bodyParam.getSchema();

        assertNotNull(paramSchema);
        assertEquals(1, paramSchema.size());
        assertEquals("#/definitions/org.motechproject.ExampleEnt-new-withId", paramSchema.get("$ref"));

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(4, responses.size());

        verify404Response(responses, "ExampleEnt");
        verify400Response(responses);
        verify403Response(responses);

        Response response = responses.get(200);

        assertTrue(response instanceof ResponseWithSchema);
        ResponseWithSchema singleItemResponse = (ResponseWithSchema) response;
        assertEquals(msg(RESPONSE_UPDATED_DESC_KEY, "ExampleEnt"), singleItemResponse.getDescription());

        Map<String, String> newResponseSchema = singleItemResponse.getSchema();

        assertNotNull(newResponseSchema);
        assertEquals(1, newResponseSchema.size());
        assertEquals("#/definitions/org.motechproject.ExampleEnt", newResponseSchema.get("$ref"));
    }

    private void verifyExampleEntDeletePath(PathEntry pathEntry) {
        assertNotNull(pathEntry);

        verifyExampleEntCommonPath(pathEntry, DELETE_DESC_KEY, DELETE_ID_KEY);

        List<Parameter> parameters = pathEntry.getParameters();

        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        Parameter pathParam = parameters.get(0);

        assertEquals("id", pathParam.getName());
        assertEquals(msg(DELETE_ID_PARAM_KEY), pathParam.getDescription());
        assertEquals(ParameterType.PATH, pathParam.getIn());
        assertNull(pathParam.getItems());
        assertTrue(pathParam.isRequired());
        assertEquals("integer", pathParam.getType());
        assertEquals("int64", pathParam.getFormat());
        assertNull(pathParam.getSchema());

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(3, responses.size());

        verify400Response(responses);
        verify403Response(responses);

        Response response = responses.get(200);
        assertEquals(msg(RESPONSE_DELETE_DESC_KEY, "ExampleEnt"), response.getDescription());
    }

    private void verifyExampleEntLookup(PathEntry pathEntry) {
        assertNotNull(pathEntry);
        assertEquals("Lookup - By Dt and Locale", pathEntry.getDescription());
        assertEquals(asList("org.motechproject.ExampleEnt"), pathEntry.getTags());
        assertEquals("byDtAndLocale", pathEntry.getOperationId());
        assertEquals(json(), pathEntry.getProduces());

        List<Parameter> parameters = pathEntry.getParameters();

        Parameter dtParam = parameters.get(0);

        assertNotNull(dtParam);
        assertEquals("dtField", dtParam.getName());
        assertEquals(ParameterType.QUERY, dtParam.getIn());
        assertEquals("dtField - " + msg(RANGE_PARAM_DESC_KEY), dtParam.getDescription());
        assertEquals(STRING_TYPE, dtParam.getType());
        assertNull(dtParam.getFormat());
        assertNull(dtParam.getItems());
        assertNull(dtParam.getSchema());

        Parameter localeParam = parameters.get(1);

        assertNotNull(localeParam);
        assertEquals("localeField", localeParam.getName());
        assertEquals(ParameterType.QUERY, localeParam.getIn());
        assertEquals("localeField - " + msg(SET_PARAM_DESC_KEY), localeParam.getDescription());
        assertEquals(ARRAY_TYPE, localeParam.getType());
        assertNull(localeParam.getFormat());
        assertNull(localeParam.getSchema());

        Property localeParamItems = localeParam.getItems();

        assertNotNull(localeParamItems);
        assertEquals(STRING_TYPE, localeParamItems.getType());
        assertNull(localeParamItems.getFormat());
        assertNull(localeParamItems.getItems());

        assertNotNull(parameters);
        assertEquals(6, parameters.size());
        verifyQueryParameters(pathEntry.getParameters(),
                asList("doubleField", "dateField", "dtField", "ldField", "localeField", "listField",
                        "owner", "creator", "modifiedBy", "modificationDate", "creationDate"), 2);

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(3, responses.size());

        verify400Response(responses);
        verify403Response(responses);

        Response response = responses.get(200);

        assertTrue(response instanceof ResponseWithSchema);
        ResponseWithSchema responseWithSchema = (ResponseWithSchema) response;
        assertEquals(msg(RESPONSE_LIST_DESC_KEY, "ExampleEnt"), responseWithSchema.getDescription());

        Map<String, String> schema = responseWithSchema.getSchema();

        assertNotNull(schema);
        assertEquals("#/definitions/org.motechproject.ExampleEnt-WithMetadata", schema.get("$ref"));
    }

    private void verifyExampleEntCommonPath(PathEntry pathEntry, String descKey, String idKey) {
        assertEquals(msg(descKey, "ExampleEnt"), pathEntry.getDescription());
        assertEquals(asList("org.motechproject.ExampleEnt"), pathEntry.getTags());
        assertEquals(msg(idKey, "ExampleEnt"), pathEntry.getOperationId());
        assertEquals(json(), pathEntry.getProduces());
    }

    private void verifyTestEntityDefinitions(Map<String, Definition> definitions) {
        Definition definition = definitions.get("org.example.TestEntity");

        assertNotNull(definition);

        assertEquals(addRequiredAutoGenerated("str", "integerField"), definition.getRequired());
        verifyTestEntityDefinitionProps(definition.getProperties(), true, true);

        definition = definitions.get("org.example.TestEntity-new");

        assertNotNull(definition);
        assertEquals(asList("str", "integerField"), definition.getRequired());
        verifyTestEntityDefinitionProps(definition.getProperties(), false, false);
    }

    private void verifyExampleEntDefinitions(Map<String, Definition> definitions) {
        Definition definition = definitions.get("org.motechproject.ExampleEnt");

        assertNotNull(definition);
        assertEquals(addRequiredAutoGenerated("doubleField", "listField"), definition.getRequired());
        verifyExampleEntDefinitionProps(definition.getProperties(), true, true);

        definition = definitions.get("org.motechproject.ExampleEnt-new-withId");

        assertNotNull(definition);
        assertEquals(asList("doubleField", "listField"), definition.getRequired());
        verifyExampleEntDefinitionProps(definition.getProperties(), false, true);
    }

    private List<String> addRequiredAutoGenerated(String... fields) {
        List<String> expectedRequired = new ArrayList<>(asList(fields));
        expectedRequired.addAll(asList("creator", "modifiedBy", "modificationDate", "creationDate"));
        return expectedRequired;
    }

    private void verify404Response(Map<Integer, Response> responses, String entityName) {
        Response response = responses.get(404);

        assertNotNull(response);
        assertEquals(msg(RESPONSE_NOT_FOUND_KEY, entityName), response.getDescription());
    }

    private void verify400Response(Map<Integer, Response> responses) {
        Response response = responses.get(400);

        assertNotNull(response);
        assertEquals(msg(RESPONSE_BAD_REQUEST_KEY), response.getDescription());
    }


    private void verify403Response(Map<Integer, Response> responses) {
        Response response = responses.get(403);

        assertNotNull(response);
        assertEquals(msg(RESPONSE_FORBIDDEN_KEY), response.getDescription());
    }

    private void verifyTestEntityDefinitionProps(Map<String, Property> properties,
                                                 boolean shouldIncludeAutoGenerated,
                                                 boolean shouldIncludeId) {
        int expectedPropCount = 4;
        assertNotNull(properties);
        verifyProperty(properties.get("str"), "string");
        verifyProperty(properties.get("integerField"), "integer", "int32");
        verifyProperty(properties.get("longField"), "integer", "int64");
        verifyProperty(properties.get("timeField"), "string");

        if (shouldIncludeAutoGenerated) {
            // - 1 for the id field
            expectedPropCount += Constants.Util.GENERATED_FIELD_NAMES.length - 1;
            verifyAutoGeneratedProps(properties);
        }
        if (shouldIncludeId) {
            expectedPropCount++;
            verifyProperty(properties.get("id"), "integer", "int64");
        }

        assertEquals(expectedPropCount, properties.size());
    }

    private void verifyExampleEntDefinitionProps(Map<String, Property> properties,
                                                 boolean shouldIncludeAutoGenerated,
                                                 boolean shouldIncludeId) {
        int expectedPropCount = 6;
        assertNotNull(properties);
        verifyProperty(properties.get("doubleField"), "number", "double");
        verifyProperty(properties.get("dateField"), "string", "date-time");
        verifyProperty(properties.get("dtField"), "string", "date-time");
        verifyProperty(properties.get("ldField"), "string", "date");
        verifyProperty(properties.get("localeField"), "string");

        Property listProp = properties.get("listField");
        assertNotNull(listProp);
        assertEquals("array", listProp.getType());
        assertNull(listProp.getFormat());
        Property itemProp = listProp.getItems();
        assertEquals("string", itemProp.getType());
        assertEquals(asList("one", "two", "three"), itemProp.getEnumValues());
        assertNull(itemProp.getFormat());
        assertNull(itemProp.getItems());

        if (shouldIncludeAutoGenerated) {
            // - 1 for the id field
            expectedPropCount += Constants.Util.GENERATED_FIELD_NAMES.length - 1;
            verifyAutoGeneratedProps(properties);
        }
        if (shouldIncludeId) {
            expectedPropCount++;
            verifyProperty(properties.get("id"), "integer", "int64");
        }

        assertEquals(expectedPropCount, properties.size());
    }

    private void verifyAutoGeneratedProps(Map<String, Property> properties) {
        verifyProperty(properties.get("owner"), "string");
        verifyProperty(properties.get("creator"), "string");
        verifyProperty(properties.get("modifiedBy"), "string");
        verifyProperty(properties.get("modificationDate"), "string", "date-time");
        verifyProperty(properties.get("creationDate"), "string", "date-time");
    }

    private void verifyProperty(Property property, String expectedType) {
        assertEquals(expectedType, property.getType());
        assertNull(property.getItems());
        assertNull(property.getFormat());
    }

    private void verifyProperty(Property property, String expectedType, String expectedFormat) {
        assertEquals(expectedType, property.getType());
        assertEquals(expectedFormat, property.getFormat());
        assertNull(property.getItems());
    }

    private void verifyQueryParameters(List<Parameter> parameters, List<String> expectedSortFields, int startIndex) {
        assertNotNull(parameters);

        int i = startIndex;

        verifyQueryParameter(parameters.get(i++), "page", PAGE_DESC_KEY, "integer", "int32");
        verifyQueryParameter(parameters.get(i++), "pageSize", PAGESIZE_DESC_KEY, "integer", "int32");
        verifyQueryParameter(parameters.get(i++), "sort", SORT_DESC_KEY, "string", null);
        verifyQueryParameter(parameters.get(i), "order", ORDER_DESC_KEY, "string", null);

        // verify values in the sort parameter
        assertEquals(expectedSortFields, parameters.get(startIndex + 2).getEnumValues());
    }

    private void verifyQueryParameter(Parameter parameter, String expectedName, String expectedDescKey,
                                      String expectedType, String expectedFormat) {
        assertEquals(expectedName, parameter.getName());
        assertEquals(msg(expectedDescKey), parameter.getDescription());
        assertEquals(expectedType, parameter.getType());
        assertEquals(expectedFormat, parameter.getFormat());
        assertEquals(ParameterType.QUERY, parameter.getIn());
        assertFalse(parameter.isRequired());
    }

    private List<String> json() {
        return asList(MediaType.APPLICATION_JSON_VALUE);
    }

    private String msg(String key, Object... args) {
        String msg = swaggerProperties.getProperty(key);
        return msg != null ? MessageFormat.format(msg, args) : messageSource.getMessage(key, args, LOCALE);
    }

    /**
     * For deserialization of the response hierarchy
     */
    private class ResponseAdapter implements JsonDeserializer<Response> {
        @Override
        public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String desc = obj.get("description").getAsString();

            JsonObject schema = obj.getAsJsonObject("schema");
            if (schema == null) {
                return new Response(desc);
            } else {
                return new ResponseWithSchema(desc, schema.get("$ref").getAsString());
            }
        }
    }
}
