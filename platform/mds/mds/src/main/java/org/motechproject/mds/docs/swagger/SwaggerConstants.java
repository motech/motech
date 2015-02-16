package org.motechproject.mds.docs.swagger;

/**
 * String constants related to generating Swagger documentation.
 */
public final class SwaggerConstants {

    public static final String V_2 = "2.0";
    public static final String HTTP = "http";
    public static final String REF = "$ref";
    public static final String TYPE = "type";
    public static final String ID_PATHVAR = "/{id}";

    public static final String QUERY = "query";
    public static final String BODY = "body";
    public static final String PATH = "path";

    public static final String INTEGER_TYPE = "integer";
    public static final String STRING_TYPE = "string";
    public static final String ARRAY_TYPE = "array";
    public static final String NUMBER_TYPE = "number";
    public static final String BOOLEAN_TYPE = "boolean";

    public static final String DOUBLE_FORMAT = "double";
    public static final String INT32_FORMAT = "int32";
    public static final String INT64_FORMAT = "int64";
    public static final String BYTE_FORMAT = "byte";
    public static final String DATE_FORMAT = "date";
    public static final String DATETIME_FORMAT = "date-time";

    public static final String VERSION_KEY = "info.version";
    public static final String TITLE_KEY = "title";
    public static final String API_DESCRIPTION_KEY = "info.description";
    public static final String LICENSE_NAME_KEY = "info.license.name";
    public static final String LICENSE_URL_KEY = "info.license.url";
    public static final String BASE_PATH_KEY = "basePath";

    public static final String READ_ALL_DESC_KEY = "mds.readAll.description";
    public static final String READ_ALL_ID_KEY = "mds.readAll.operationId";
    public static final String READ_ID_DESC_KEY = "mds.readById.description";
    public static final String READ_ID_ID_KEY = "mds.readById.operationId";
    public static final String CREATE_DESC_KEY = "mds.create.description";
    public static final String CREATE_ID_KEY = "mds.create.operationId";
    public static final String UPDATE_DESC_KEY = "mds.update.description";
    public static final String UPDATE_ID_KEY = "mds.update.operationId";
    public static final String DELETE_DESC_KEY = "mds.delete.description";
    public static final String DELETE_ID_KEY = "mds.delete.operationId";

    public static final String PAGE_DESC_KEY = "mds.queryparams.page.description";
    public static final String PAGESIZE_DESC_KEY = "mds.queryparams.pagesize.description";
    public static final String SORT_DESC_KEY = "mds.queryparams.sort.description";
    public static final String ORDER_DESC_KEY = "mds.queryparams.order.description";
    public static final String ID_DESC_KEY = "mds.idparam.description";
    public static final String CREATE_BODY_DESC_KEY = "mds.create.body.description";
    public static final String UPDATE_BODY_DESC_KEY = "mds.update.body.description";
    public static final String DELETE_ID_PARAM_KEY = "mds.delete.idparam.description";

    public static final String RESPONSE_LIST_DESC_KEY = "mds.response.list.description";
    public static final String RESPONSE_NEW_DESC_KEY = "mds.response.newItem.description";
    public static final String RESPONSE_SINGLE_DESC_KEY = "mds.response.singleItem.description";
    public static final String RESPONSE_DELETE_DESC_KEY = "mds.response.delete.description";

    private SwaggerConstants() {
    }
}
