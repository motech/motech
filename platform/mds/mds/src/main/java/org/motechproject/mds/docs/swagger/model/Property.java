package org.motechproject.mds.docs.swagger.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * A property that describes a type. Generally used for describing
 * types of collections, can be nested.
 * @see <a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#data-types for more info">Swagger data types</a>
 */
public class Property implements Serializable {

    private static final long serialVersionUID = -3578038867082692451L;

    private String type;
    private String format;
    private Property items;
    @SerializedName("enum")
    private List<String> enumValues;

    public Property() {
    }

    /**
     * Constructs a simple property that just has a type
     * @param type the type of the property
     */
    public Property(String type) {
        this.type = type;
    }

    /**
     * Constructs a simple property that has a type and format
     * @param type the type of the property
     * @param format the format of the property, depends on the type
     */
    public Property(String type, String format) {
        this.type = type;
        this.format = format;
    }

    /**
     * Builds a property with a nested property. Used for building collection properties.
     * @param type the type of this property (i.e. array in this case)
     * @param items the nested property representing the type of the items in the collection described by this property
     */
    public Property(String type, Property items) {
        this.type = type;
        this.items = items;
    }

    /**
     * Builds a new property. This constructor contains all property fields.
     * @param type the type of the property
     * @param format the format of the property, depends on the type
     * @param items the nested property representing the type of the items in the collection described by this property
     * @param enumValues list of allowed values for this property
     */
    public Property(String type, String format, Property items, List<String> enumValues) {
        this.type = type;
        this.format = format;
        this.items = items;
        this.enumValues = enumValues;
    }

    /**
     * @return the type of the property
     * @see <a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#data-types for more info">Swagger data types</a>
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type of the property
     * @see <a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#data-types for more info">Swagger data types</a>
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the format of the property, depends on the type
     * @see <a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#data-types for more info">Swagger data types</a>
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format of the property, depends on the type
     * @see <a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#data-types for more info">Swagger data types</a>
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the nested property representing the type of the items in the collection described by this property
     */
    public Property getItems() {
        return items;
    }

    /**
     * @param items the nested property representing the type of the items in the collection described by this property
     */
    public void setItems(Property items) {
        this.items = items;
    }

    /**
     * @return list of allowed values for this property
     */
    public List<String> getEnumValues() {
        return enumValues;
    }

    /**
     * @param enumValues list of allowed values for this property
     */
    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }
}
