package org.motechproject.mds.web.rest.docs.swagger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.web.rest.docs.RestDocumentationGenerator;
import org.motechproject.mds.web.rest.docs.RestEntry;
import org.motechproject.mds.web.rest.docs.swagger.model.Definition;
import org.motechproject.mds.web.rest.docs.swagger.model.Info;
import org.motechproject.mds.web.rest.docs.swagger.model.License;
import org.motechproject.mds.web.rest.docs.swagger.model.Parameter;
import org.motechproject.mds.web.rest.docs.swagger.model.PathEntry;
import org.motechproject.mds.web.rest.docs.swagger.model.Property;
import org.motechproject.mds.web.rest.docs.swagger.model.Response;
import org.motechproject.mds.web.rest.docs.swagger.model.Schema;
import org.motechproject.mds.web.rest.docs.swagger.model.SwaggerModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.lowerCase;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.API_DESCRIPTION_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.ARRAY_TYPE;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.BASE_PATH_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.BODY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.CREATE_BODY_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.CREATE_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.CREATE_ID_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.HTTP;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.ID_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.ID_PATHVAR;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.INT32_FORMAT;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.INTEGER_TYPE;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.LICENSE_NAME_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.LICENSE_URL_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.ORDER_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.PAGESIZE_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.PAGE_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.PATH;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.QUERY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.READ_ALL_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.READ_ALL_ID_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.READ_ID_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.READ_ID_ID_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.REF;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.RESPONSE_DELETE_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.RESPONSE_LIST_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.RESPONSE_NEW_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.RESPONSE_SINGLE_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.SORT_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.STRING_TYPE;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.TITLE_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.UPDATE_DESC_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.UPDATE_ID_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.VERSION_KEY;
import static org.motechproject.mds.web.rest.docs.swagger.SwaggerConstants.V_2;


/**
 * A REST API documentation generator for Swagger - http://swagger.io/.
 * Generates a resources file in the Swagger JSON format.
 */
@Service
public class SwaggerGenerator implements RestDocumentationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerGenerator.class);

    private Properties swaggerProperties;

    @Override
    public void generateDocumentation(Writer writer, List<RestEntry> restEntries) {
        LOGGER.info("Generating REST documentation");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        SwaggerModel swaggerModel = initialSwaggerModel();

        for (RestEntry restEntry : restEntries) {
            final String entityPath = buildPath(restEntry.getEntity());

            // add CRUD operations to the model
            if (restEntry.getRestOptions().isRead()) {
                // retrieveAll and retrieveById
                swaggerModel.addPathEntry(entityPath, HttpMethod.GET, readAllPathEntry(restEntry));
                swaggerModel.addPathEntry(entityPath + ID_PATHVAR, HttpMethod.GET, readByIdPathEntry(restEntry));
            }
            if (restEntry.getRestOptions().isCreate()) {
                // post new item
                swaggerModel.addPathEntry(entityPath, HttpMethod.POST, postPathEntry(restEntry));
            }
            if (restEntry.getRestOptions().isUpdate()) {
                // update an existing item
                swaggerModel.addPathEntry(entityPath, HttpMethod.PUT, putPathEntry(restEntry));
            }
            if (restEntry.getRestOptions().isDelete()) {
                // delete an item
                swaggerModel.addPathEntry(entityPath + ID_PATHVAR, HttpMethod.DELETE, deletePathEntry(restEntry));
            }

            // add definitions
            if (restEntry.getRestOptions().supportAnyRestAccess()) {
                // type returned
                swaggerModel.addDefinition(definitionPath(restEntry.getEntityName()),
                        definition(restEntry, true));
            }
            if (restEntry.getRestOptions().isCreate() || restEntry.getRestOptions().isUpdate()) {
                // type for create/update
                swaggerModel.addDefinition(definitionNewPath(restEntry.getEntityName()),
                        definition(restEntry, false));
            }
        }

        gson.toJson(swaggerModel, writer);
    }

    private SwaggerModel initialSwaggerModel() {
        SwaggerModel swaggerModel = new SwaggerModel();

        swaggerModel.setSwagger(V_2);
        swaggerModel.setBasePath(msg(BASE_PATH_KEY));

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

    private PathEntry readAllPathEntry(RestEntry restEntry) {
        PathEntry pathEntry = new PathEntry();

        final String entityName = restEntry.getEntityName();

        pathEntry.setDescription(msg(READ_ALL_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(READ_ALL_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.setParameters(queryParamsParameters());
        pathEntry.addResponse(HttpStatus.OK, listResponse(entityName));

        return pathEntry;
    }

    private PathEntry readByIdPathEntry(RestEntry restEntry) {
        PathEntry pathEntry = new PathEntry();

        final String entityName = restEntry.getEntityName();

        pathEntry.setDescription(msg(READ_ID_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(READ_ID_ID_KEY, entityName));
        pathEntry.addParameter(idPathParameter());
        pathEntry.addResponse(HttpStatus.OK, singleItemResponse(entityName));
        pathEntry.setProduces(json());

        return pathEntry;
    }

    private PathEntry postPathEntry(RestEntry restEntry) {
        PathEntry pathEntry = new PathEntry();

        final String entityName = restEntry.getEntityName();

        pathEntry.setDescription(msg(CREATE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(CREATE_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.addParameter(newEntityParameter(entityName));
        pathEntry.addResponse(HttpStatus.OK, newItemResponse(entityName));

        return pathEntry;
    }

    private PathEntry putPathEntry(RestEntry restEntry) {
        PathEntry pathEntry = new PathEntry();

        final String entityName = restEntry.getEntityName();

        pathEntry.setDescription(msg(UPDATE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(UPDATE_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.addParameter(idPathParameter());
        pathEntry.addResponse(HttpStatus.OK, newItemResponse(entityName));

        return pathEntry;
    }

    private PathEntry deletePathEntry(RestEntry restEntry) {
        PathEntry pathEntry = new PathEntry();

        final String entityName = restEntry.getEntityName();

        pathEntry.setDescription(msg(UPDATE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(UPDATE_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.addParameter(newEntityParameter(entityName));
        pathEntry.addResponse(HttpStatus.OK, deleteResponse(entityName));

        return pathEntry;
    }

    private List<Parameter> queryParamsParameters() {
        List<Parameter> parameters = new ArrayList<>();

        parameters.add(pageParameter());
        parameters.add(pageSizeParameter());
        parameters.add(sortParameter());
        parameters.add(orderParameter());

        return parameters;
    }

    private Parameter idPathParameter() {
        return pathParameter(msg(ID_DESC_KEY), INTEGER_TYPE, INT32_FORMAT);
    }

    private Parameter newEntityParameter(String entityName) {
        return bodyParameter(msg(CREATE_BODY_DESC_KEY, entityName), entityName,
                definitionNewPath(entityName));
    }

    private Parameter pageParameter() {
        return queryParameter(msg(PAGE_DESC_KEY), INTEGER_TYPE, INT32_FORMAT);
    }

    private Parameter pageSizeParameter() {
        return queryParameter(msg(PAGESIZE_DESC_KEY), INTEGER_TYPE, INT32_FORMAT);
    }

    private Parameter sortParameter() {
        return queryParameter(msg(SORT_DESC_KEY), STRING_TYPE);
    }

    private Parameter orderParameter() {
        return queryParameter(msg(ORDER_DESC_KEY), STRING_TYPE);
    }

    private Parameter queryParameter(String description, String type) {
        return queryParameter(description, type, null);
    }

    private Parameter queryParameter(String description, String type, String format) {
        return parameter(description, type, format, QUERY, false);
    }

    private Parameter pathParameter(String description, String type, String format) {
        return parameter(description, type, format, PATH, true);
    }

    private Parameter parameter(String description, String type, String format,
                                String in, boolean required) {
        Parameter pageParam = new Parameter();

        pageParam.setDescription(description);
        pageParam.setIn(in);
        pageParam.setRequired(required);
        pageParam.setType(type);
        pageParam.setFormat(format);

        return pageParam;
    }

    private Parameter bodyParameter(String description, String name, String ref) {
        Parameter bodyParameter = new Parameter();

        bodyParameter.setDescription(description);
        bodyParameter.setName(name);
        bodyParameter.setIn(BODY);
        bodyParameter.setRequired(true);
        bodyParameter.addSchema(REF, ref);

        return bodyParameter;
    }

    private Response listResponse(String entityName) {
        return response(msg(RESPONSE_LIST_DESC_KEY, entityName),
                definitionPath(entityName),
                ARRAY_TYPE);
    }

    private Response newItemResponse(String entityName) {
        return response(msg(RESPONSE_NEW_DESC_KEY, entityName),
                definitionPath(entityName));
    }

    private Response singleItemResponse(String entityName) {
        return response(msg(RESPONSE_SINGLE_DESC_KEY, entityName),
                definitionPath(entityName));
    }

    private Response deleteResponse(String entityName) {
        return response(msg(RESPONSE_DELETE_DESC_KEY, entityName));
    }

    private Response response(String description) {
        return response(description, null, null);
    }

    private Response response(String description, String ref) {
        return response(description, ref, null);
    }

    private Response response(String description, String ref, String type) {
        Response response = new Response();

        response.setDescription(description);

        Map<String, String> items = null;
        if (ref != null) {
            items = new HashMap<>();
            items.put(REF, ref);
        }

        if (type != null && items != null) {
            response.setSchema(new Schema(type, items));
        }

        return response;
    }

    private Definition definition(RestEntry restEntry, boolean includeAuto) {
        Definition definition = new Definition();

        List<String> required = new ArrayList<>();
        Map<String, Property> properties = new HashMap<>();

        for (FieldDto field : restEntry.getFields()) {
            if (restEntry.getRestOptions().isFieldExposed(field)) {
                // auto generated fields included only in responses
                String fieldName = field.getBasic().getName();
                if (includeAuto || !ArrayUtils.contains(Constants.Util.GENERATED_FIELD_NAMES, fieldName))  {
                    Property property = SwaggerFieldConverter.fieldToProperty(field);
                    properties.put(fieldName, property);
                    if (field.getBasic().isRequired()) {
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

    private String buildPath(EntityDto entity) {
        if (StringUtils.isNotBlank(entity.getNamespace())) {
            return String.format("/%s/%s/%s", lowerCase(entity.getModule()), lowerCase(entity.getNamespace()),
                    lowerCase(entity.getName()));
        } else if (StringUtils.isNotBlank(entity.getModule())) {
            return String.format("/%s/%s", lowerCase(entity.getModule()), lowerCase(entity.getName()));
        } else {
            return String.format("/%s", lowerCase(entity.getName()));
        }
    }

    private String definitionPath(String entityName) {
        return String.format("#/definitions/%s", entityName);
    }

    private String definitionNewPath(String entityName) {
        return String.format("#/definitions/new%s", StringUtils.capitalize(entityName));
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
