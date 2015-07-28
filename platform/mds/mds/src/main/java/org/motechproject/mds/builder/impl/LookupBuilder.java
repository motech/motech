package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.util.JavassistUtil;
import org.motechproject.mds.query.CollectionProperty;
import org.motechproject.mds.query.PropertyBuilder;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.TypeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

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
    private String lookupName;
    private CtClass definition;
    private LookupType lookupType;
    private AllEntities allEntities;

    LookupBuilder(Entity entity, Lookup lookup, CtClass definition,
                  LookupType lookupType, AllEntities allEntities) {
        this.definition = definition;
        this.allEntities = allEntities;
        this.className = entity.getClassName();

        this.lookup = lookup;
        this.lookupType = lookupType;
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
        List<String> fieldOrder = lookup.getFieldsOrder();

        for (int i = 0; i < fieldOrder.size(); i++) {
            Field field = lookup.getLookupFieldByName(LookupName.getFieldName(fieldOrder.get(i)));
            Field relationField = null;
            if (fieldOrder.get(i).contains(".")) {
                Entity relatedEntity = allEntities.retrieveByClassName(new RelationshipHolder(field).getRelatedClass());
                relationField = relatedEntity.getField(LookupName.getRelatedFieldName(fieldOrder.get(i)));
            }
            String type = getTypeForParam(i, relationField == null ? field : relationField, fieldOrder.get(i));

            String param = String.format("%s %s", type, fieldOrder.get(i).replace(".", ""));
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

        Map<String, String> jdoQueryVariables = new HashMap<>();
        for (String lookupFieldName : lookup.getFieldsOrder()) {
            boolean appendJdoVariableName = false;
            Field field = lookup.getLookupFieldByName(LookupName.getFieldName(lookupFieldName));
            Field relationField = null;

            if (lookupFieldName.contains(".")) {
                Entity relatedEntity = allEntities.retrieveByClassName(new RelationshipHolder(field).getRelatedClass());
                relationField = relatedEntity.getField(LookupName.getRelatedFieldName(lookupFieldName));
            }

            Type type = field.getType();
            String typeClassName = resolveClassName(type, relationField);

            body.append("properties.add(");

            if (type.isCombobox()) {
                ComboboxHolder holder = new ComboboxHolder(field);
                typeClassName = holder.getUnderlyingType();
                body.append(resolvePropertyForCombobox(holder, type));
            } else if (relationField != null && relationField.getType().isCombobox()) {
                ComboboxHolder holder = new ComboboxHolder(relationField);
                typeClassName = holder.getUnderlyingType();
                body.append(resolvePropertyForCombobox(holder, type));
                addJdoVariableName(jdoQueryVariables, lookupFieldName);
                appendJdoVariableName = needsJdoVariable(type);
            } else if (relationField != null && (type.getTypeClassName().equals(TypeDto.MANY_TO_MANY_RELATIONSHIP.getTypeClass())
                    || type.getTypeClassName().equals(TypeDto.ONE_TO_MANY_RELATIONSHIP.getTypeClass()))) {
                //related class collection
                body.append(PropertyBuilder.class.getName());
                body.append(".createRelationProperty");
                addJdoVariableName(jdoQueryVariables, lookupFieldName);
                appendJdoVariableName = true;
            } else {
                body.append(PropertyBuilder.class.getName());
                body.append(".create");
            }

            body.append(buildPropertyParameters(appendJdoVariableName, lookupFieldName, typeClassName, jdoQueryVariables));
        }
        body.append(buildReturn());
        return body.toString();
    }

    private void addJdoVariableName(Map<String, String> jdoQueryVariables, String lookupFieldName) {
        if (!jdoQueryVariables.containsKey(LookupName.getFieldName(lookupFieldName))) {
            jdoQueryVariables.put(LookupName.getFieldName(lookupFieldName), buildJdoVariableName(lookupFieldName));
        }
    }

    private String resolveClassName(Type type, Field relationField) {
        return relationField == null ? type.getTypeClassName() : relationField.getType().getTypeClassName();
    }

    private String buildPropertyParameters(boolean appendJdoVariableName, String lookupFieldName, String typeClassName, Map<String, String> jdoQueryVariables) {
        StringBuilder sb = new StringBuilder();
        sb.append("(\""); // open constructor or create method
        if (appendJdoVariableName) {
            sb.append(jdoQueryVariables.get(LookupName.getFieldName(lookupFieldName)));
            sb.append("\", \"");
        }
        sb.append(lookupFieldName);
        sb.append("\", ($w)"); //in case the type is primitive, we wrap it to its object representation
        sb.append(lookupFieldName.replace(".", ""));
        // append the param type
        sb.append(", \"").append(typeClassName).append('\"');

        // append a custom operator for the lookup field, if defined
        String customOperator = lookup.getCustomOperators().get(lookupFieldName);
        if (StringUtils.isNotBlank(customOperator)) {
            sb.append(",\"").append(customOperator).append('"');
        }

        sb.append(")"); // close constructor or create method
        sb.append(");"); // close add method

        return sb.toString();
    }

    private String resolvePropertyForCombobox(ComboboxHolder holder, Type type) {
        StringBuilder sb = new StringBuilder();
        if (holder.isCollection()) {
            if (needsJdoVariable(type)) {
                sb.append(PropertyBuilder.class.getName());
                sb.append(".createRelationPropertyForComboboxCollection");
            } else {
                sb.append("new ");
                sb.append(CollectionProperty.class.getName());
            }
        } else {
            sb.append(PropertyBuilder.class.getName());
            if (needsJdoVariable(type)) {
                sb.append(".createRelationProperty");
            } else {
                sb.append(".create");
            }
        }

        return sb.toString();
    }

    private boolean needsJdoVariable(Type type) {
        return (type.getTypeClassName().equals(TypeDto.MANY_TO_MANY_RELATIONSHIP.getTypeClass())
                || type.getTypeClassName().equals(TypeDto.ONE_TO_MANY_RELATIONSHIP.getTypeClass()));
    }

    private String buildReturn() {
        StringBuilder sb = new StringBuilder();
        if (COUNT == lookupType) {
            sb.append("return count(properties);");
        } else {
            if (lookup.isSingleObjectReturn()) {
                sb.append("Object result = retrieveUnique(properties");
            } else {
                sb.append("java.util.List list = retrieveAll(properties");
            }

            if (WITH_QUERY_PARAMS == lookupType) {
                sb.append(", queryParams");
            } else if (SIMPLE == lookupType) {
                // by default, order by id
                sb.append(", ").append(QueryParams.class.getName()).append(".ORDER_ID_ASC");
            }

            sb.append(");");

            if (lookup.isSingleObjectReturn()) {
                sb.append("return (" + className + ") result;");
            } else {
                sb.append("return list;");
            }
        }

        return sb.toString();
    }

    private String buildJdoVariableName(String lookupFieldName) {
        return String.format("%s%s%s", "element", LookupName.getFieldName(lookupFieldName), LookupName.getRelatedFieldName(lookupFieldName));
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

    private String getTypeForParam(int idx, Field field, String lookupFieldName) throws NotFoundException {
        // firstly we check if field is Range or Set
        if (lookup.isRangeParam(lookupFieldName)) {
            return Range.class.getName();
        } else if (lookup.isSetParam(lookupFieldName)) {
            return Set.class.getName();
        }
        // we try to copy type param from existing method signature...
        String type = copyParamTypeFromMethod(idx, field);
        if (type != null) {
            return type;
        }
        // .. if method with type param on idx position does not exist then we will return
        // type based on field type
        if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(field);

            return holder.getTypeClassName();
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

        List<String> fieldsOrder = lookup.getFieldsOrder();
        for (int i = 0; i < fieldsOrder.size(); ++i) {
            Field field = lookup.getLookupFieldByName(LookupName.getFieldName(fieldsOrder.get(i)));

            Field relationField = null;
            if (fieldsOrder.get(i).contains(".")) {
                Entity relatedEntity = allEntities.retrieveByClassName(new RelationshipHolder(field).getRelatedClass());
                relationField = relatedEntity.getField(LookupName.getRelatedFieldName(fieldsOrder.get(i)));
            }

            String paramType = getTypeForParam(i, resolveField(field, relationField), fieldsOrder.get(i));
            String genericType;

            Type type = resolveField(field, relationField).getType();

            if (type.isCombobox()) {
                ComboboxHolder holder = new ComboboxHolder(resolveField(field, relationField));
                genericType = holder.getUnderlyingType();
            } else {
                genericType = type.getTypeClassName();
            }

            if (StringUtils.equals(paramType, genericType) || TypeHelper.isPrimitive(paramType)) {
                // simple parameter
                sb.append(JavassistUtil.toGenericParam(paramType));
            } else {
                // we wrap in a range/set or a different wrapper
                sb.append(JavassistUtil.genericSignature(paramType, genericType));
            }
        }
        sb.append(')');

        if (lookup.isSingleObjectReturn()) {
            sb.append(JavassistUtil.toGenericParam(className));
        } else {
            sb.append(JavassistUtil.genericSignature(List.class.getName(), className));
        }

        return sb.toString();
    }

    private Field resolveField(Field field, Field relationField) {
        return relationField == null ? field : relationField;
    }

}
