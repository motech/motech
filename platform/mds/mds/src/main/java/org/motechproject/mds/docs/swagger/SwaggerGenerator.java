package org.motechproject.mds.docs.swagger;

import ch.lambdaj.Lambda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
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
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.motechproject.mds.docs.swagger.SwaggerConstants.API_DESCRIPTION_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ARRAY_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.BASE_PATH_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.BLOB_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_BODY_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.CREATE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.DELETE_ID_PARAM_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.HTTP;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ID_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ID_PATHVAR;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INCLUDE_BLOB_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INT32_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INT64_FORMAT;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.INTEGER_TYPE;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.LICENSE_NAME_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.LICENSE_URL_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.LOOKUP_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.NO_ENTITY_IS_EXPOSED_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ORDER_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.ORDER_DIR_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGESIZE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGE_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.PAGE_SIZE_PARAM;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RANGE_PARAM_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.READ_ID_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.REF;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_BAD_REQUEST_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_DELETE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_FORBIDDEN_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_LIST_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_LOOKUP_NOT_FOUND_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_NEW_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_NOT_FOUND_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_SINGLE_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.RESPONSE_UPDATED_DESC_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.REST_API_DOCS_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.REST_API_DOCS_URL_KEY;
import static org.motechproject.mds.docs.swagger.SwaggerConstants.SET_PARAM_DESC_KEY;
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

    private MessageSource messageSource;
    private Properties swaggerProperties;

    @Override
    public void generateDocumentation(Writer writer, List<Entity> entities, String serverPrefix, Locale locale) {
        LOGGER.info("Generating REST documentation");

        SwaggerModel swaggerModel = initialSwaggerModel(serverPrefix, locale);

        for (Entity entity : entities) {
            addCrudEndpoints(swaggerModel, entity, locale);
            addLookupEndpoints(swaggerModel, entity, locale);
            addDefinitions(swaggerModel, entity);
        }

        if (MapUtils.isEmpty(swaggerModel.getDefinitions())) {
            swaggerModel.getInfo().setDescription(String.format("%s \n\n**%s [%s](%s)**",
                    msg(locale, API_DESCRIPTION_KEY), msg(locale, NO_ENTITY_IS_EXPOSED_KEY),
                    msg(locale, REST_API_DOCS_KEY), property(REST_API_DOCS_URL_KEY)));
        }

        Gson gson = buildGson();

        gson.toJson(swaggerModel, writer);
    }

    private void addCrudEndpoints(SwaggerModel swaggerModel, Entity entity, Locale locale) {
        final String entityPath = ClassName.restUrl(entity.getName(), entity.getModule(), entity.getNamespace());

        RestOptions restOptions = restOptionsOrDefault(entity);

        if (restOptions.isAllowRead()) {
            // retrieveAll and retrieveById
            swaggerModel.addPathEntry(entityPath, HttpMethod.GET, readPathEntry(entity, locale));
        }
        if (restOptions.isAllowCreate()) {
            // post new item
            swaggerModel.addPathEntry(entityPath, HttpMethod.POST, postPathEntry(entity, locale));
        }
        if (restOptions.isAllowUpdate()) {
            // update an existing item
            swaggerModel.addPathEntry(entityPath, HttpMethod.PUT, putPathEntry(entity, locale));
        }
        if (restOptions.isAllowDelete()) {
            // delete an item
            swaggerModel.addPathEntry(entityPath + ID_PATHVAR, HttpMethod.DELETE, deletePathEntry(entity, locale));
        }
    }

    private void addLookupEndpoints(SwaggerModel swaggerModel, Entity entity, Locale locale) {
        for (Lookup lookup : entity.getLookupsExposedByRest()) {
            String lookupUrl = ClassName.restLookupUrl(entity.getName(), entity.getModule(),
                    entity.getNamespace(), lookup.getMethodName());
            swaggerModel.addPathEntry(lookupUrl, HttpMethod.GET, lookupPathEntry(entity, lookup, locale));
        }
    }

    private void addDefinitions(SwaggerModel swaggerModel, Entity entity) {
        RestOptions restOptions = restOptionsOrDefault(entity);

        if (restOptions.supportsAnyOperation() || !entity.getLookupsExposedByRest().isEmpty()) {
            // all fields, including generated ones
            swaggerModel.addDefinition(entity.getClassName(), definition(entity, true, true));
        }
        if (restOptions.isAllowCreate()) {
            // no auto-generated fields
            swaggerModel.addDefinition(definitionNewName(entity.getClassName()), definition(entity, false, false));
        }
        if (restOptions.isAllowUpdate()) {
            // no auto-generated fields, except ID
            swaggerModel.addDefinition(definitionUpdateName(entity.getClassName()), definition(entity, false, true));
        }
    }

    private SwaggerModel initialSwaggerModel(String serverPrefix, Locale locale) {
        SwaggerModel swaggerModel = new SwaggerModel();

        swaggerModel.setSwagger(V_2);
        swaggerModel.setBasePath(serverPrefix + property(BASE_PATH_KEY));

        swaggerModel.setInfo(mdsApiInfo(locale));

        swaggerModel.setSchemes(Arrays.asList(HTTP));
        swaggerModel.setProduces(json());
        swaggerModel.setConsumes(json());

        return swaggerModel;
    }

    private Info mdsApiInfo(Locale locale) {
        Info info = new Info();

        info.setVersion(property(VERSION_KEY));
        info.setDescription(msg(locale, API_DESCRIPTION_KEY));
        info.setTitle(property(TITLE_KEY));

        info.setLicense(motechLicense());

        return info;
    }

    private License motechLicense() {
        return new License(property(LICENSE_NAME_KEY), property(LICENSE_URL_KEY));
    }

    private PathEntry readPathEntry(Entity entity, Locale locale) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(locale, READ_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(locale, READ_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        pathEntry.setParameters(queryParamsParameters(entity.getFieldsExposedByRest(), locale));
        pathEntry.addParameter(idQueryParameter(locale));

        pathEntry.addResponse(HttpStatus.OK, listResponse(entity, locale));
        addCommonResponses(pathEntry, locale);

        return pathEntry;
    }

    private PathEntry postPathEntry(Entity entity, Locale locale) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(locale, CREATE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(locale, CREATE_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        pathEntry.addParameter(newEntityParameter(entity, locale));

        pathEntry.addResponse(HttpStatus.OK, newItemResponse(entity, locale));
        addCommonResponses(pathEntry, locale);

        return pathEntry;
    }

    private PathEntry putPathEntry(Entity entity, Locale locale) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(locale, UPDATE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(locale, UPDATE_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        pathEntry.addParameter(updateEntityParameter(entity, locale));

        addCommonResponses(pathEntry, locale);
        pathEntry.addResponse(HttpStatus.OK, updatedItemResponse(entity, locale));
        pathEntry.addResponse(HttpStatus.NOT_FOUND, notFoundResponse(entity, locale));

        return pathEntry;
    }

    private PathEntry deletePathEntry(Entity entity, Locale locale) {
        final PathEntry pathEntry = new PathEntry();

        final String entityName = entity.getName();

        pathEntry.setDescription(msg(locale, DELETE_DESC_KEY, entityName));
        pathEntry.setOperationId(msg(locale, DELETE_ID_KEY, entityName));
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        pathEntry.addParameter(deleteIdPathParameter(locale));

        addCommonResponses(pathEntry, locale);
        pathEntry.addResponse(HttpStatus.OK, deleteResponse(entity, locale));

        return pathEntry;
    }

    private PathEntry lookupPathEntry(Entity entity, Lookup lookup, Locale locale) {
        final PathEntry pathEntry = new PathEntry();

        pathEntry.setDescription(msg(locale, LOOKUP_DESC_KEY, lookup.getLookupName()));
        pathEntry.setOperationId(lookup.getMethodName());
        pathEntry.setProduces(json());
        pathEntry.addTag(entity.getClassName());

        pathEntry.setParameters(lookupParameters(entity, lookup, locale));

        pathEntry.addResponse(HttpStatus.OK, lookupResponse(entity, lookup, locale));
        addCommonResponses(pathEntry, locale);

        if (lookup.isSingleObjectReturn()) {
            pathEntry.addResponse(HttpStatus.NOT_FOUND, lookup404Response(entity, locale));
        }

        return pathEntry;
    }

    private void addCommonResponses(PathEntry pathEntry, Locale locale) {
        pathEntry.addResponse(HttpStatus.BAD_REQUEST, badRequestResponse(locale));
        pathEntry.addResponse(HttpStatus.FORBIDDEN, forbiddenResponse(locale));
    }

    private List<Parameter> lookupParameters(Entity entity, Lookup lookup, Locale locale) {
        List<Parameter> parameters = new ArrayList<>();

        for (Field lookupField : lookup.getFields()) {
            LookupFieldType lookupFieldType = lookup.getLookupFieldType(lookupField.getName());
            String paramDesc = lookupParamDescription(lookupField, lookupFieldType, locale);

            Parameter parameter = SwaggerFieldConverter.lookupParameter(lookupField, lookupFieldType, paramDesc);
            parameters.add(parameter);
        }

        parameters.addAll(queryParamsParameters(entity.getFieldsExposedByRest(), locale));

        return parameters;
    }

    private List<Parameter> queryParamsParameters(List<Field> restExposedFields, Locale locale) {
        final List<Parameter> parameters = new ArrayList<>();

        parameters.add(pageParameter(locale));
        parameters.add(pageSizeParameter(locale));
        parameters.add(sortParameter(restExposedFields, locale));
        parameters.add(orderParameter(locale));
        if (hasBlobField(restExposedFields)) {
            parameters.add(includeBlobParameter(locale));
        }

        return parameters;
    }

    private boolean hasBlobField(List<Field> fields) {
        for (Field field : fields) {
            if (field.getType().isBlob()) {
                return true;
            }
        }
        return false;
    }

    private Parameter idQueryParameter(Locale locale) {
        return queryParameter(Constants.Util.ID_FIELD_NAME, msg(locale, ID_DESC_KEY), INTEGER_TYPE, INT64_FORMAT);
    }

    private Parameter deleteIdPathParameter(Locale locale) {
        return pathParameter(Constants.Util.ID_FIELD_NAME, msg(locale, DELETE_ID_PARAM_KEY), INTEGER_TYPE, INT64_FORMAT);
    }

    private Parameter newEntityParameter(Entity entity, Locale locale) {
        return bodyParameter(entity.getName(), msg(locale, CREATE_BODY_DESC_KEY, entity.getName()),
                definitionNewPath(entity.getClassName()));
    }

    private Parameter updateEntityParameter(Entity entity, Locale locale) {
        return bodyParameter(entity.getName(), msg(locale, UPDATE_BODY_DESC_KEY, entity.getName()),
                definitionUpdatePath(entity.getClassName()));
    }

    private Parameter pageParameter(Locale locale) {
        return queryParameter(PAGE_PARAM, msg(locale, PAGE_DESC_KEY), INTEGER_TYPE, INT32_FORMAT);
    }

    private Parameter pageSizeParameter(Locale locale) {
        return queryParameter(PAGE_SIZE_PARAM, msg(locale, PAGESIZE_DESC_KEY), INTEGER_TYPE, INT32_FORMAT);
    }

    private Parameter sortParameter(List<Field> restExposedFields, Locale locale) {
        Parameter sortParameter = queryParameter(SORT_BY_PARAM, msg(locale, SORT_DESC_KEY), STRING_TYPE);

        List<String> restExposedFieldNames = Lambda.extract(restExposedFields, Lambda.on(Field.class).getName());
        sortParameter.setEnumValues(restExposedFieldNames);

        return sortParameter;
    }

    private Parameter orderParameter(Locale locale) {
        Parameter orderParameter = queryParameter(ORDER_DIR_PARAM, msg(locale, ORDER_DESC_KEY), STRING_TYPE);
        orderParameter.setEnumValues(Arrays.asList("Ascending", "Descending"));
        return orderParameter;
    }

    private Parameter includeBlobParameter(Locale locale) {
        Parameter includeBlobParameter = queryParameter(INCLUDE_BLOB_PARAM, msg(locale, BLOB_DESC_KEY), STRING_TYPE);
        includeBlobParameter.setEnumValues(Arrays.asList("true", "false"));
        return includeBlobParameter;
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

    private Response lookupResponse(Entity entity, Lookup lookup, Locale locale) {
        if (lookup.isSingleObjectReturn()) {
            return new SingleItemResponse(msg(locale, RESPONSE_SINGLE_DESC_KEY, entity.getName()),
                    definitionPath(entity.getClassName()));
        } else {
            return new MultiItemResponse(msg(locale, RESPONSE_LIST_DESC_KEY, entity.getName()),
                    definitionPath(entity.getClassName()),
                    ARRAY_TYPE);
        }
    }

    private Response lookup404Response(Entity entity, Locale locale) {
        return new Response(msg(locale, RESPONSE_LOOKUP_NOT_FOUND_KEY, entity.getName()));
    }

    private Response listResponse(Entity entity, Locale locale) {
        return new MultiItemResponse(msg(locale, RESPONSE_LIST_DESC_KEY, entity.getName()),
                definitionPath(entity.getClassName()),
                ARRAY_TYPE);
    }

    private Response newItemResponse(Entity entity, Locale locale) {
        return new SingleItemResponse(msg(locale, RESPONSE_NEW_DESC_KEY, entity.getName()),
                definitionPath(entity.getClassName()));
    }

    private Response updatedItemResponse(Entity entity, Locale locale) {
        return new SingleItemResponse(msg(locale, RESPONSE_UPDATED_DESC_KEY, entity.getName()),
                definitionPath(entity.getClassName()));
    }

    private Response deleteResponse(Entity entity, Locale locale) {
        return new Response(msg(locale, RESPONSE_DELETE_DESC_KEY, entity.getName()));
    }

    private Response notFoundResponse(Entity entity, Locale locale) {
        return new Response(msg(locale, RESPONSE_NOT_FOUND_KEY, entity.getName()));
    }

    private Response badRequestResponse(Locale locale) {
        return new Response(msg(locale, RESPONSE_BAD_REQUEST_KEY));
    }


    private Response forbiddenResponse(Locale locale) {
        return new Response(msg(locale, RESPONSE_FORBIDDEN_KEY));
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

    private String lookupParamDescription(Field field, LookupFieldType lookupFieldType, Locale locale) {
        // start with tooltip or name if tooltip is not defined
        // for sets and ranges append appropriate info

        StringBuilder desc = new StringBuilder(StringUtils.isNotBlank(field.getTooltip())
                ? field.getTooltip() : field.getName());

        switch (lookupFieldType) {
            case SET:
                desc.append(" - ").append(msg(locale, SET_PARAM_DESC_KEY));
                break;
            case RANGE:
                desc.append(" - ").append(msg(locale, RANGE_PARAM_DESC_KEY));
                break;
            default:
                break;
        }

        return desc.toString();
    }

    private String property(String key) {
        return swaggerProperties.getProperty(key);
    }

    private String msg(Locale locale, String key, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }

    private RestOptions restOptionsOrDefault(Entity entity) {
        RestOptions restOptions = entity.getRestOptions();
        if (restOptions == null) {
            // everything off
            restOptions = new RestOptions();
        }
        return restOptions;
    }

    private Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ParameterType.class, new ParameterTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Autowired
    @Qualifier("swaggerProperties")
    public void setSwaggerProperties(Properties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Resource(name="swaggerMessageSource")
    public void setSwaggerMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
