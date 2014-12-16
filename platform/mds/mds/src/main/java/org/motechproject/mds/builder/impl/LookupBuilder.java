package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.query.CollectionProperty;
import org.motechproject.mds.query.PropertyBuilder;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.TypeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.motechproject.mds.builder.impl.LookupType.COUNT;
import static org.motechproject.mds.builder.impl.LookupType.SIMPLE;
import static org.motechproject.mds.builder.impl.LookupType.WITH_QUERY_PARAMS;

/**
 * The <code>LookupBuilder</code> class allows to create a method signature with(out) body. A method
 * signature without body should be added into javassist class that represent an interface. A method
 * signature with body should be added into javassist class that represent a normal java class.
 */
class LookupBuilder {
    private String className;
    private Lookup lookup;
    private List<Field> fields;
    private String lookupName;
    private CtClass definition;
    private LookupType lookupType;

    LookupBuilder(Entity entity, Lookup lookup, CtClass definition,
                  LookupType lookupType) {
        this.definition = definition;

        this.className = entity.getClassName();

        this.lookup = lookup;
        this.lookupType = lookupType;
        this.fields = CollectionUtils.isEmpty(lookup.getFields())
                ? new ArrayList<Field>()
                : lookup.getFields();
        this.lookupName = (lookupType == COUNT)
                ? LookupName.lookupCountMethod(lookup.getMethodName())
                : lookup.getMethodName();
    }

    CtMethod buildSignature() throws CannotCompileException, NotFoundException {
        return build(false);
    }

    CtMethod buildMethod() throws CannotCompileException, NotFoundException {
        return build(true);
    }

    private CtMethod build(boolean body) throws CannotCompileException, NotFoundException {
        Collection<String> paramCollection = new ArrayList<>();

        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);

            String type = getTypeForParam(i, field);
            String param = String.format("%s %s", type, field.getName());

            paramCollection.add(param);
        }

        // query params at the end for ordering/paging
        if (WITH_QUERY_PARAMS == lookupType) {
            String queryParam = String.format("%s queryParams", QueryParams.class.getName());

            paramCollection.add(queryParam);
        }

        String params = StringUtils.join(paramCollection, ", ");
        String signature = String.format("public %s %s(%s)", returnType(), lookupName, params);
        String methodAsString = body
                ? String.format("%s{%s}", signature, body())
                : String.format("%s;", signature);

        String generic = buildGenericSignature();

        CtMethod method = CtNewMethod.make(methodAsString, definition);
        method.setGenericSignature(generic);

        return method;
    }

    private String body() {
        StringBuilder body = new StringBuilder();

        body.append("java.util.List properties = new java.util.ArrayList();");

        for (Field field : fields) {
            String name = field.getName();
            Type type = field.getType();
            String typeClassName = type.getTypeClassName();

            body.append("properties.add(");

            if (type.isCombobox()) {
                ComboboxHolder holder = new ComboboxHolder(field);

                typeClassName = holder.getUnderlyingType();

                if (holder.isStringList() || holder.isEnumList()) {
                    body.append("new ");
                    body.append(CollectionProperty.class.getName());
                } else {
                    body.append(PropertyBuilder.class.getName());
                    body.append(".create");
                }
            } else {
                body.append(PropertyBuilder.class.getName());
                body.append(".create");
            }

            body.append("(\""); // open constructor or create method
            body.append(name);
            body.append("\", ($w)"); //in case the type is primitive, we wrap it to its object representation
            body.append(name);
            // append the param type
            body.append(", \"").append(typeClassName).append('\"');

            // append a custom operator for the lookup field, if defined
            String customOperator = lookup.getCustomOperators().get(field.getName());
            if (StringUtils.isNotBlank(customOperator)) {
                body.append(",\"").append(customOperator).append('"');
            }

            body.append(")"); // close contructor or create method
            body.append(");"); // close add method
        }

        if (COUNT == lookupType) {
            body.append("return count(properties);");
        } else {
            body.append("java.util.List list = retrieveAll(properties");

            if (WITH_QUERY_PARAMS == lookupType) {
                body.append(", queryParams");
            } else if (SIMPLE == lookupType) {
                // by default, order by id
                body.append(", ").append(QueryParams.class.getName()).append(".ORDER_ID_ASC");
            }

            body.append(");");

            if (lookup.isSingleObjectReturn()) {
                body.append("return list.isEmpty() ? null : (");
                body.append(className);
                body.append(") list.get(0);");
            } else {
                body.append("return list;");
            }
        }

        return body.toString();
    }

    private String returnType() {
        if (lookupType == LookupType.COUNT) {
            return "long";
        } else if (lookup.isSingleObjectReturn()) {
            return className;
        } else {
            return List.class.getName();
        }
    }

    private String getTypeForParam(int idx, Field field) throws NotFoundException {
        // firstly we try to copy type param from existing method signature...
        String type = copyParamTypeFromMethod(idx, field);
        if (type != null) {
            return type;
        }
        // .. if method with type param on idx position does not exist then we will return
        // type based on field type
        if (lookup.isRangeParam(field)) {
            return Range.class.getName();
        } else if (lookup.isSetParam(field)) {
            return Set.class.getName();
        } else if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(field);

            if (holder.isEnum()) {
                return holder.getEnumName();
            } else if (holder.isString()) {
                return String.class.getName();
            } else {
                return List.class.getName();
            }
        } else {
            return field.getType().getTypeClassName();
        }
    }

    private String copyParamTypeFromMethod(int idx, Field field) throws NotFoundException {
        for (CtMethod method : definition.getMethods()) {
            if (method.getName().equalsIgnoreCase(lookupName) ||
                    LookupName.lookupCountMethod(method.getName()).equalsIgnoreCase(lookupName)) {
                CtClass[] types = method.getParameterTypes();

                if (types.length > idx) {
                    if (lookup.isReadOnly() || types[idx].getName() == field.getType().getTypeClassName()) {
                        return types[idx].getName();
                    }
                }
            }
        }
        return null;
    }

    private String buildGenericSignature() throws NotFoundException {
        // we must build generic signatures for lookup methods
        // an example signature for the method signature
        // List<org.motechproject.mds.Test> method(String p1, Integer p2)
        // is
        // cmt -- (Ljava/lang/String;Ljava/lang/Integer;)Ljava/util/List<Lorg/motechproject/mds/Test;>;
        StringBuilder sb = new StringBuilder();

        sb.append('(');
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);

            String paramType = getTypeForParam(i, field);
            String genericType;

            Type type = field.getType();

            if (type.isCombobox()) {
                ComboboxHolder holder = new ComboboxHolder(field);

                if (holder.isEnum() || holder.isEnumList()) {
                    genericType = holder.getEnumName();
                } else if (holder.isString() || holder.isStringList()) {
                    genericType = String.class.getName();
                } else {
                    genericType = type.getTypeClassName();
                }
            } else {
                genericType = type.getTypeClassName();
            }

            if (StringUtils.equals(paramType, genericType) || TypeHelper.isPrimitive(paramType)) {
                // simple parameter
                sb.append(JavassistHelper.toGenericParam(paramType));
            } else {
                // we wrap in a range/set or a different wrapper
                sb.append(JavassistHelper.genericSignature(paramType, genericType));
            }
        }
        sb.append(')');

        if (lookup.isSingleObjectReturn()) {
            sb.append(JavassistHelper.toGenericParam(className));
        } else {
            sb.append(JavassistHelper.genericSignature(List.class.getName(), className));
        }

        return sb.toString();
    }

}
