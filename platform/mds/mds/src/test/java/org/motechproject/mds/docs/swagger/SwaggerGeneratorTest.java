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
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.docs.swagger.model.Definition;
import org.motechproject.mds.docs.swagger.model.Info;
import org.motechproject.mds.docs.swagger.model.License;
import org.motechproject.mds.docs.swagger.model.MultiItemResponse;
import org.motechproject.mds.docs.swagger.model.Parameter;
import org.motechproject.mds.docs.swagger.model.PathEntry;
import org.motechproject.mds.docs.swagger.model.Property;
import org.motechproject.mds.docs.swagger.model.Response;
import org.motechproject.mds.docs.swagger.model.Schema;
import org.motechproject.mds.docs.swagger.model.SingleItemResponse;
import org.motechproject.mds.docs.swagger.model.SwaggerModel;
import org.motechproject.mds.domain.EntityInfo;
import org.motechproject.mds.domain.FieldInfo;
import org.motechproject.mds.util.Constants;
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
import static org.motechproject.mds.docs.swagger.SwaggerConstants.API_DESCRIPTION_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_BODY_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_ID_PARAM_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ID_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.LICENSE_NAME_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.LICENSE_URL_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ORDER_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGESIZE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_ALL_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_ALL_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_ID_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_ID_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_LIST_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_NEW_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_SINGLE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_UPDATED_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.SORT_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.TITLE_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.UPDATE_BODY_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.UPDATE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.UPDATE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.VERSION_KEY;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldInfo;

public class SwaggerGeneratorTest {

    private SwaggerGenerator swaggerGenerator = new SwaggerGenerator();

    private Properties swaggerProperties;

    @Before
    public void setUp() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("swagger.properties")) {
            swaggerProperties = new Properties();
            swaggerProperties.load(in);
            swaggerGenerator.setSwaggerProperties(swaggerProperties);
        }
    }

    @Test
    public void shouldGenerateJson() {
        StringWriter stringWriter = new StringWriter();

        swaggerGenerator.generateDocumentation(stringWriter, entities());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Response.class, new ResponseAdapter())
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
        assertEquals(4, definitions.size());
        verifyTestEntityDefinitions(definitions);
        verifyExampleEntDefinitions(definitions);
    }

    private void verifyPaths(SwaggerModel swaggerModel) {
        Map<String, Map<String, PathEntry>> paths = swaggerModel.getPaths();

        assertNotNull(paths);
        assertEquals(4, paths.size());
        verifyTestEntityPaths(paths);
        verifyExampleEntPaths(paths);
    }

    private List<EntityInfo> entities() {
        List<EntityInfo> entities = new ArrayList<>();

        // Entity 1

        EntityInfo entity = new EntityInfo();
        entity.setClassName("org.example.TestEntity");
        entity.setEntityName("TestEntity");
        entity.setModule("example");
        entity.setNamespace("ns");

        entity.setRestCreateEnabled(true);
        entity.setRestReadEnabled(true);

        List<FieldInfo> fields = new ArrayList<>();
        fields.add(fieldInfo("str", String.class, true, true));
        fields.add(fieldInfo("integerField", Integer.class, true, true));
        fields.add(fieldInfo("longField", Long.class, false, true));
        fields.add(fieldInfo("timeField", Time.class, false, true));
        fields.add(fieldInfo("ignoredField", String.class, false, false));

        entity.setFieldsInfo(fields);

        entities.add(entity);

        // Entity 2

        entity = new EntityInfo();
        entity.setClassName("org.motechproject.ExampleEnt");
        entity.setEntityName("ExampleEnt");

        entity.setRestUpdateEnabled(true);
        entity.setRestDeleteEnabled(true);

        fields = new ArrayList<>();
        fields.add(fieldInfo("doubleField", Double.class, true, true));
        fields.add(fieldInfo("dateField", Date.class, false, true));
        fields.add(fieldInfo("dtField", DateTime.class, false, true));
        fields.add(fieldInfo("ldField", LocalDate.class, false, true));
        fields.add(fieldInfo("localeField", Locale.class, false, true));

        FieldInfo listField = fieldInfo("listField", List.class, true, true);
        listField.setAdditionalTypeInfo(FieldInfo.TypeInfo.ALLOWS_MULTIPLE_SELECTIONS);
        fields.add(listField);

        entity.setFieldsInfo(fields);

        entities.add(entity);

        return entities;
    }

    private void verifyTestEntityPaths(Map<String, Map<String, PathEntry>> paths) {
        verifyTestEntityGetAllPath(paths);
        verifyTestEntityPostPath(paths);
        verifyTestEntityGetOnePath(paths);
    }

    private void verifyTestEntityGetAllPath(Map<String, Map<String, PathEntry>> paths) {
        Map<String, PathEntry> pathEntries = paths.get("/example/ns/testentity");
        assertNotNull(pathEntries);
        assertEquals(2, pathEntries.size());

        PathEntry pathEntry = pathEntries.get("get");

        assertNotNull(pathEntry);
        verifyTestEntityPathEntryCommon(pathEntry, READ_ALL_DESC_KEY, READ_ALL_ID_KEY);
        verifyQueryParameters(pathEntry.getParameters());

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response response = responses.get(200);
        assertTrue(response instanceof MultiItemResponse);
        MultiItemResponse multiItemResponse = (MultiItemResponse) response;
        assertEquals(msg(RESPONSE_LIST_DESC_KEY, "TestEntity"), multiItemResponse.getDescription());

        Schema schema = multiItemResponse.getSchema();

        assertNotNull(schema);
        assertEquals("array", schema.getType());

        Map<String, String> items = schema.getItems();

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("#/definitions/org.example.TestEntity", items.get("$ref"));
    }

    private void verifyTestEntityPostPath(Map<String, Map<String, PathEntry>> paths) {
        Map<String, PathEntry> pathEntries = paths.get("/example/ns/testentity");
        assertNotNull(pathEntries);
        assertEquals(2, pathEntries.size());

        PathEntry pathEntry = pathEntries.get("post");

        assertNotNull(pathEntry);
        verifyTestEntityPathEntryCommon(pathEntry, CREATE_DESC_KEY, CREATE_ID_KEY);

        List<Parameter> parameters = pathEntry.getParameters();

        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        Parameter newParam = parameters.get(0);

        assertNotNull(newParam);
        assertEquals("TestEntity", newParam.getName());
        assertEquals("body", newParam.getIn());
        assertNull(newParam.getFormat());
        assertTrue(newParam.isRequired());
        assertEquals(msg(CREATE_BODY_DESC_KEY, "TestEntity"), newParam.getDescription());

        Map<String, String> newParamSchema = newParam.getSchema();

        assertNotNull(newParamSchema);
        assertEquals(1, newParamSchema.size());
        assertEquals("#/definitions/org.example.TestEntity-new", newParamSchema.get("$ref"));

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response response = responses.get(200);

        assertTrue(response instanceof SingleItemResponse);
        SingleItemResponse singleItemResponse = (SingleItemResponse) response;
        assertEquals(msg(RESPONSE_NEW_DESC_KEY, "TestEntity"), singleItemResponse.getDescription());

        Map<String, String> newResponseSchema = singleItemResponse.getSchema();

        assertNotNull(newResponseSchema);
        assertEquals(1, newResponseSchema.size());
        assertEquals("#/definitions/org.example.TestEntity", newResponseSchema.get("$ref"));
    }

    private void verifyTestEntityGetOnePath(Map<String, Map<String, PathEntry>> paths) {
        Map<String, PathEntry> pathEntries = paths.get("/example/ns/testentity/{id}");

        assertNotNull(pathEntries);
        assertEquals(1, pathEntries.size());

        PathEntry pathEntry = pathEntries.get("get");

        assertNotNull(pathEntry);
        verifyTestEntityPathEntryCommon(pathEntry, READ_ID_DESC_KEY, READ_ID_ID_KEY);

        List<Parameter> parameters = pathEntry.getParameters();

        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        Parameter pathParam = parameters.get(0);

        assertEquals("id", pathParam.getName());
        assertEquals(msg(ID_DESC_KEY), pathParam.getDescription());
        assertEquals("path", pathParam.getIn());
        assertNull(pathParam.getItems());
        assertEquals("integer", pathParam.getType());
        assertEquals("int64", pathParam.getFormat());
        assertTrue(pathParam.isRequired());

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response response = responses.get(200);

        assertTrue(response instanceof SingleItemResponse);
        SingleItemResponse singleItemResponse = (SingleItemResponse) response;
        assertEquals(msg(RESPONSE_SINGLE_DESC_KEY, "TestEntity"), singleItemResponse.getDescription());

        Map<String, String> newResponseSchema = singleItemResponse.getSchema();

        assertNotNull(newResponseSchema);
        assertEquals(1, newResponseSchema.size());
        assertEquals("#/definitions/org.example.TestEntity", newResponseSchema.get("$ref"));
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
        assertEquals("body", bodyParam.getIn());
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
        assertEquals(1, responses.size());

        Response response = responses.get(200);

        assertTrue(response instanceof SingleItemResponse);
        SingleItemResponse singleItemResponse = (SingleItemResponse) response;
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
        assertEquals("path", pathParam.getIn());
        assertNull(pathParam.getItems());
        assertTrue(pathParam.isRequired());
        assertEquals("integer", pathParam.getType());
        assertEquals("int64", pathParam.getFormat());
        assertNull(pathParam.getSchema());

        Map<Integer, Response> responses = pathEntry.getResponses();

        assertNotNull(responses);
        assertEquals(1, responses.size());

        Response response = responses.get(200);
        assertEquals(msg(RESPONSE_DELETE_DESC_KEY, "ExampleEnt"), response.getDescription());
    }

    private void verifyExampleEntCommonPath(PathEntry pathEntry, String descKey, String idKey) {
        assertEquals(msg(descKey, "ExampleEnt"), pathEntry.getDescription());
        assertEquals(asList("org.motechproject.ExampleEnt"), pathEntry.getTags());
        assertEquals(msg(idKey, "ExampleEnt"), pathEntry.getOperationId());
        assertEquals(json(), pathEntry.getProduces());
    }

    private void verifyTestEntityDefinitions(Map<String, Definition> definitions) {
        Definition definition = definitions.get("TestEntity");

        assertNotNull(definition);
        assertEquals(asList("str", "integerField"), definition.getRequired());
        verifyTestEntityDefinitionProps(definition.getProperties(), true, true);

        definition = definitions.get("org.example.TestEntity-new");

        assertNotNull(definition);
        assertEquals(asList("str", "integerField"), definition.getRequired());
        verifyTestEntityDefinitionProps(definition.getProperties(), false, false);
    }

    private void verifyExampleEntDefinitions(Map<String, Definition> definitions) {
        Definition definition = definitions.get("ExampleEnt");

        assertNotNull(definition);
        assertEquals(asList("doubleField", "listField"), definition.getRequired());
        verifyExampleEntDefinitionProps(definition.getProperties(), true, true);

        definition = definitions.get("org.motechproject.ExampleEnt-new-withId");

        assertNotNull(definition);
        assertEquals(asList("doubleField", "listField"), definition.getRequired());
        verifyExampleEntDefinitionProps(definition.getProperties(), false, true);
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

    private void verifyQueryParameters(List<Parameter> parameters) {
        assertNotNull(parameters);
        assertEquals(4, parameters.size());
        verifyQueryParameter(parameters.get(0), "page", PAGE_DESC_KEY, "integer", "int32");
        verifyQueryParameter(parameters.get(1), "pageSize", PAGESIZE_DESC_KEY, "integer", "int32");
        verifyQueryParameter(parameters.get(2), "sort", SORT_DESC_KEY, "string", null);
        verifyQueryParameter(parameters.get(3), "order", ORDER_DESC_KEY, "string", null);
    }

    private void verifyQueryParameter(Parameter parameter, String expectedName, String expectedDescKey,
                                      String expectedType, String expectedFormat) {
        assertEquals(expectedName, parameter.getName());
        assertEquals(msg(expectedDescKey), parameter.getDescription());
        assertEquals(expectedType, parameter.getType());
        assertEquals(expectedFormat, parameter.getFormat());
        assertEquals("query", parameter.getIn());
        assertFalse(parameter.isRequired());
    }

    private List<String> json() {
        return asList(MediaType.APPLICATION_JSON_VALUE);
    }

    private String msg(String key, Object... args) {
        return MessageFormat.format(swaggerProperties.getProperty(key), args);
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
                JsonObject items = schema.getAsJsonObject("items");
                if (items == null) {
                    return new SingleItemResponse(desc, schema.get("$ref").getAsString());
                } else {
                    String type = schema.get("type").getAsString();
                    String ref = schema.getAsJsonObject("items").get("$ref").getAsString();
                    return new MultiItemResponse(desc, ref, type);
                }
            }
        }
    }
}
