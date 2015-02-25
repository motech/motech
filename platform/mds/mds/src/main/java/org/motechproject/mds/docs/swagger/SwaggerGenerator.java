package org.motechproject.mds.docs.swagger;

import ch.lambdaj.Lambda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.motechproject.mds.docs.RestDocumentationGenerator;
import org.motechproject.mds.docs.swagger.gson.ParameterTypeAdapter;
import org.motechproject.mds.docs.swagger.model.Definition;
import org.motechproject.mds.docs.swagger.model.Info;
import org.motechproject.mds.docs.swagger.model.License;
import org.motechproject.mds.docs.swagger.model.MultiItemResponse;
import org.motechproject.mds.docs.swagger.model.Parameter;
import org.motechproject.mds.docs.swagger.model.ParameterType;
import org.motechproject.mds.docs.swagger.model.PathEntry;
import org.motechproject.mds.docs.swagger.model.Property;
import org.motechproject.mds.docs.swagger.model.Response;
import org.motechproject.mds.docs.swagger.model.SingleItemResponse;
import org.motechproject.mds.docs.swagger.model.SwaggerModel;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.motechproject.mds.docs.swagger.SwaggerConstants.API_DESCRIPTION_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ARRAY_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.BASE_PATH_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_BODY_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_ID_PARAM_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.HTTP;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ID_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ID_PATHVAR;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INT32_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INT64_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INTEGER_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.LICENSE_NAME_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.LICENSE_URL_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ORDER_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ORDER_DIR_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGESIZE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGE_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGE_SIZE_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.REF;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_BAD_REQUEST_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_LIST_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_NEW_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_NOT_FOUND_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_UPDATED_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.SORT_BY_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.SORT_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.STRING_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.TITLE_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.UPDATE_BODY_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.UPDATE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.UPDATE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.VERSION_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.V_2;

/**
 * A REST API documentation generator for Swagger - http://swagger.io/.
 * Generates a spec file in the Swagger JSON format. Gson is used for generating
 * the JSON file.
 */
@Service
public class SwaggerGenerator implements RestDocumentationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerGenerator.class);

    private Properties swaggerProperties;

    @Override
    public void generateDocumentation(Writer writer, List<Entity> entities, String serverPrefix) {
        LOGGER.info("Generating REST documentation");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ParameterType.class, new ParameterTypeAdapter())
                .setPrettyPrinting()
                .create();

        SwaggerModel swaggerModel = initialSwaggerModel(serverPrefix);

        for (Entity entity : entities) {
            final String entityPath = ClassName.restUrl(entity.getName(), entity.getModule(), entity.getNamespace());

            // add CRUD operations to the model

            RestOptions restOptions = entity.getRestOptions();
            if (restOptions == null) {
                // everything off
                restOptions = new RestOptions();
            }

            if (restOptions.isAllowRead()) {
                // retrieveAll and retrieveById
                swaggerModel.addPathEntry(entityPath, HttpMethod.GET, readPathEntry(entity));
            }
            if (restOptions.isAllowCreate()) {
                // post new item
                swaggerModel.addPathEntry(entityPath, HttpMethod.POST, postPathEntry(entity));
            }
            if (restOptions.isAllowUpdate()) {
                // update an existing item
                swaggerModel.addPathEntry(entityPath, HttpMethod.PUT, putPathEntry(entity));
            }
            if (restOptions.isAllowDelete()) {
                // delete an item
                swaggerModel.addPathEntry(entityPath + ID_PATHVAR, HttpMethod.DELETE, deletePathEntry(entity));
            }

            // lookups
            for (Lookup lookup : entity.getLookups()) {
                if (lookup.isExposedViaRest()) {

                }
            }

            // add definitions
            if (restOptions.supportsAnyOperation()) {
                // no auto-generated fields
                swaggerModel.addDefinition(entity.getClassName(), definition(entity, true, true));
            }
            if (restOptions.isAllowCreate()) {
                // all fields, including generated ones
                swaggerModel.addDefinition(definitionNewName(entity.getClassName()), definition(entity, false, false));
            }
            if (restOptions.isAllowUpdate()) {
                // no auto-generated fields, except ID
                swaggerModel.addDefinition(definitionUpdateName(entity.getClassName()), definition(entity, false, true));
            }
        }

        gson.toJson(swaggerModel, writer);
    }

    private SwaggerModel initialSwaggerModel(String serverPrefix) {
        SwaggerModel swaggerModel = new SwaggerModel();

        swaggerModel.setSwagger(V_2);
        swaggerModel.setBasePath(serverPrefix + msg(BASE_PATH_KEY));

        swaggerModel.setInfo(mdsApiInfo());

        swaggerModel.setSchemes(Arrays.asList(HTTP));
        swaggerModel.setProduces(json());
        swaggerModel.setConsumes(json());

        return swaggerModel;
    }

    private Info mdsApiInfo() {
        Info info = new Info();

        info.setVersion(msg(VERSION_KEY));
        info.setDescription(msg(API_DESCRIPTION_KEY));
        info.setTitle(msg(TITLE_KEY));

        info.setLicense(motechLicense());

        return info;
    }

    private License motechLicense() {
        return new License(msg(LICENSE_NAME_KEY), msg(LICENSE_URL_KEY));
    }

    private PathEntry readPathEntry(Entity entity) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(READ_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(READ_ID_KEY, entityName));
        pathEntry.addResponse(HttpStatus.OK, listResponse(entity));
        pathEntry.addResponse(HttpStatus.BAD_REQUEST, badRequestResponse());
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());
        pathEntry.setParameters(queryParamsParameters(entity.getFieldsExposedByRest()));
        pathEntry.addParameter(idQueryParameter());

        return pathEntry;
    }

    private PathEntry postPathEntry(Entity entity) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(CREATE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(CREATE_ID_KEY, entityName));
        pathEntry.addParameter(newEntityParameter(entity));
        pathEntry.addResponse(HttpStatus.OK, newItemResponse(entity));
        pathEntry.addResponse(HttpStatus.BAD_REQUEST, badRequestResponse());
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        return pathEntry;
    }

    private PathEntry putPathEntry(Entity entity) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(UPDATE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(UPDATE_ID_KEY, entityName));
        pathEntry.addResponse(HttpStatus.OK, updatedItemResponse(entity));
        pathEntry.addResponse(HttpStatus.NOT_FOUND, notFoundResponse(entity));
        pathEntry.addResponse(HttpStatus.BAD_REQUEST, badRequestResponse());
        pathEntry.addParameter(updateEntityParameter(entity));
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        return pathEntry;
    }

    private PathEntry deletePathEntry(Entity entity) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(DELETE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(DELETE_ID_KEY, entityName));
        pathEntry.addParameter(deleteIdPathParameter());
        pathEntry.addResponse(HttpStatus.OK, deleteResponse(entity));
        pathEntry.addResponse(HttpStatus.BAD_REQUEST, badRequestResponse());
        pathEntry.addResponse(HttpStatus.NOT_FOUND, notFoundResponse(entity));
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        return pathEntry;
    }

    private PathEntry lookupPathEntry(Entity entity, Lookup lookup) {
        final PathEntry pathEntry = new PathEntry();

        pathEntry.setDescription("");
        pathEntry.setOperationId(lookup.getMethodName());

        List<Parameter> parameters = new ArrayList<>();

        for (Field lookupField : lookup.getFields()) {
            Parameter parameter = Swag
        }

        parameters.addAll(queryParamsParameters(entity.getFieldsExposedByRest()));
        pathEntry.setParameters(parameters);

        return pathEntry;
    }

    private List<Parameter> queryParamsParameters(List<Field> restExposedFields) {
        final List<Parameter> parameters = new ArrayList<>();

        parameters.add(pageParameter());
        parameters.add(pageSizeParameter());
        parameters.add(sortParameter(restExposedFields));
        parameters.add(orderParameter());

        return parameters;
    }

    private Parameter lookupParameter(Field lookupField) {
        Parameter parameter = SwaggerFieldConverter
    }

    private Parameter idQueryParameter() {
        return queryParameter(Constants.Util.ID_FIELD_NAME, msg(ID_DESC_KEY), INTEGER_TYPE, INT64_FORMAT);
    }

    private Parameter deleteIdPathParameter() {
        return pathParameter(Constants.Util.ID_FIELD_NAME, msg(DELETE_ID_PARAM_KEY), INTEGER_TYPE, INT64_FORMAT);
    }

    private Parameter newEntityParameter(Entity entity) {
        return bodyParameter(entity.getName(), msg(CREATE_BODY_DESC_KEY, entity.getName()),
                definitionNewPath(entity.getClassName()));
    }

    private Parameter updateEntityParameter(Entity entity) {
        return bodyParameter(entity.getName(), msg(UPDATE_BODY_DESC_KEY, entity.getName()),
                definitionUpdatePath(entity.getClassName()));
    }

    private Parameter pageParameter() {
        return queryParameter(PAGE_PARAM, msg(PAGE_DESC_KEY), INTEGER_TYPE, INT32_FORMAT);
    }

    private Parameter pageSizeParameter() {
        return queryParameter(PAGE_SIZE_PARAM, msg(PAGESIZE_DESC_KEY), INTEGER_TYPE, INT32_FORMAT);
    }

    private Parameter sortParameter(List<Field> restExposedFields) {
        Parameter sortParameter = queryParameter(SORT_BY_PARAM, msg(SORT_DESC_KEY), STRING_TYPE);

        List<String> restExposedFieldNames = Lambda.extract(restExposedFields, Lambda.on(Field.class).getName());
        sortParameter.setEnumValues(restExposedFieldNames);

        return sortParameter;
    }

    private Parameter orderParameter() {
        Parameter orderParameter = queryParameter(ORDER_DIR_PARAM, msg(ORDER_DESC_KEY), STRING_TYPE);
        orderParameter.setEnumValues(Arrays.asList("Ascending", "Descending"));
        return orderParameter;
    }

    private Parameter queryParameter(String name, String description, String type) {
        return queryParameter(name, description, type, null);
    }

    private Parameter queryParameter(String name, String description, String type, String format) {
        return parameter(name, description, type, format, ParameterType.QUERY, false);
    }

    private Parameter pathParameter(String name, String description, String type, String format) {
        return parameter(name, description, type, format, ParameterType.PATH, true);
    }

    private Parameter parameter(String name, String description, String type, String format,
                                ParameterType in, boolean required) {
        final Parameter param = new Parameter();

        param.setName(name);
        param.setDescription(description);
        param.setIn(in);
        param.setRequired(required);
        param.setType(type);
        param.setFormat(format);

        return param;
    }

    private Parameter bodyParameter(String name, String description, String ref) {
        final Parameter bodyParameter = new Parameter();

        bodyParameter.setName(name);
        bodyParameter.setDescription(description);
        bodyParameter.setIn(ParameterType.BODY);
        bodyParameter.setRequired(true);
        bodyParameter.addSchema(REF, ref);

        return bodyParameter;
    }

    private Response listResponse(Entity entity) {
        return new MultiItemResponse(msg(RESPONSE_LIST_DESC_KEY, entity.getName()),
                definitionPath(entity.getClassName()),
                ARRAY_TYPE);
    }

    private Response newItemResponse(Entity entity) {
        return new SingleItemResponse(msg(RESPONSE_NEW_DESC_KEY, entity.getName()),
                definitionPath(entity.getClassName()));
    }

    private Response updatedItemResponse(Entity entity) {
        return new SingleItemResponse(msg(RESPONSE_UPDATED_DESC_KEY, entity.getName()),
                definitionPath(entity.getClassName()));
    }

    private Response deleteResponse(Entity entity) {
        return new Response(msg(RESPONSE_DELETE_DESC_KEY, entity.getName()));
    }

    private Response notFoundResponse(Entity entity) {
        return new Response(msg(RESPONSE_NOT_FOUND_KEY, entity.getName()));
    }

    private Response badRequestResponse() {
        return new Response(msg(RESPONSE_BAD_REQUEST_KEY));
    }

    private Definition definition(Entity entity, boolean includeAuto, boolean includeId) {
        final Definition definition = new Definition();

        final List<String> required = new ArrayList<>();
        final Map<String, Property> properties = new LinkedHashMap<>();

        if (includeId) {
            properties.put(Constants.Util.ID_FIELD_NAME, new Property(INTEGER_TYPE, INT64_FORMAT));
        }

        for (Field field : entity.getFields()) {
            final String fieldName = field.getName();
            if (field.isExposedViaRest()) {
                // auto generated fields included only in responses
                if (!field.isAutoGenerated() || includeAuto) {
                    Property property = SwaggerFieldConverter.fieldToProperty(field);
                    properties.put(fieldName, property);
                    if (field.isRequired()) {
                        required.add(fieldName);
                    }
                }
            }
        }

        definition.setRequired(required);
        definition.setProperties(properties);

        return definition;
    }

    private List<String> json() {
        return Arrays.asList(MediaType.APPLICATION_JSON_VALUE);
    }

    private String definitionPath(String entityClassName) {
        return String.format("#/definitions/%s", entityClassName);
    }

    private String definitionNewPath(String entityClassName) {
        return "#/definitions/" + definitionNewName(entityClassName);
    }

    private String definitionUpdatePath(String entityClassName) {
        return "#/definitions/" + definitionUpdateName(entityClassName);
    }

    private String definitionNewName(String entityClassName) {
        return entityClassName + "-new";
    }

    private String definitionUpdateName(String entityClassName) {
        return definitionNewName(entityClassName) + "-withId";
    }

    private String msg(String key) {
        return swaggerProperties.getProperty(key);
    }

    private String msg(String key, Object... args) {
        return MessageFormat.format(swaggerProperties.getProperty(key), args);
    }

    @Autowired
    @Qualifier("swaggerProperties")
    public void setSwaggerProperties(Properties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }
}
