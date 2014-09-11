package org.motechproject.mds.lookup;

import org.apache.commons.lang.reflect.MethodUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.ex.FieldNotFoundException;
import org.motechproject.mds.ex.LookupExecutorException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.TypeHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class allowing executing lookups by providing the lookup name
 * as a string and the lookup params in name-value map. Used both
 * by the REST api and the Databrowser UI for executing lookups based
 * on only metadata. The dataservice and metadata must be provided during
 * construction.
 */
public class LookupExecutor {

    private final MotechDataService dataService;
    private final LookupDto lookup;
    private final Map<Long, FieldDto> fieldsById;
    private final Class entityClass;
    private final ClassLoader classLoader;

    public LookupExecutor(MotechDataService dataService, LookupDto lookup, Map<Long, FieldDto> fieldsById) {
        this.dataService = dataService;
        this.lookup = lookup;
        this.fieldsById = fieldsById;
        this.entityClass = dataService.getClassType();
        this.classLoader = dataService.getClass().getClassLoader();
    }

    public Object execute(Map<String, ?> lookupMap) {
        return execute(lookupMap, null);
    }

    public Object execute(Map<String, ?> lookupMap, QueryParams queryParams) {
        List<Object> args = getLookupArgs(lookupMap);
        List<Class> argTypes = buildArgTypes();

        if (queryParams != null) {
            args.add(queryParams);
            argTypes.add(QueryParams.class);
        }

        try {
            return MethodUtils.invokeMethod(dataService, lookup.getMethodName(),
                    args.toArray(new Object[args.size()]),
                    argTypes.toArray(new Class[argTypes.size()]));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new LookupExecutorException("Unable to execute lookup " + lookup.getLookupName(), e);
        }
    }

    public long executeCount(Map<String, Object> lookupMap) {
        List<Object> args = getLookupArgs(lookupMap);
        List<Class> argTypes = buildArgTypes();

        String countMethodName = LookupName.lookupCountMethod(lookup.getMethodName());

        try {
            return (long) MethodUtils.invokeMethod(dataService, countMethodName,
                    args.toArray(new Object[args.size()]),
                    argTypes.toArray(new Class[argTypes.size()]));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new LookupExecutorException("Unable to execute count lookup " + lookup.getLookupName(), e);
        }
    }


    private List<Object> getLookupArgs(Map<String, ?> paramMap) {
        List<Object> args = new ArrayList<>();
        for (LookupFieldDto lookupField : lookup.getLookupFields()) {
            FieldDto field = fieldsById.get(lookupField.getId());
            if (field == null) {
                throw new FieldNotFoundException();
            }

            Object val = paramMap.get(field.getBasic().getName());

            String typeClass = getTypeClass(field);
            String genericType = getGenericTypeClass(field);

            Object arg;
            if (lookupField.getType() == LookupFieldDto.Type.RANGE) {
                arg = TypeHelper.toRange(val, typeClass);
            } else if (lookupField.getType() == LookupFieldDto.Type.SET) {
                arg = TypeHelper.toSet(val, typeClass);
            } else {
                arg = TypeHelper.parse(val, lookupField.isUseGenericParam() ? genericType : typeClass, classLoader);
            }

            args.add(arg);
        }
        return args;
    }

    private String getTypeClass(FieldDto field) {
        String typeClass = null;

        if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entityClass, field);

            if (holder.isEnum()) {
                typeClass = holder.getEnumName();
            } else if (holder.isEnumList()) {
                typeClass = List.class.getName();
            } else if (holder.isStringList()) {
                typeClass = List.class.getName();
            } else if (holder.isString()) {
                typeClass = String.class.getName();
            }
        } else {
            typeClass = field.getType().getTypeClass();
        }

        return typeClass;
    }

    private String getGenericTypeClass(FieldDto field) {
        String genericType = null;

        if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entityClass, field);

            if (holder.isEnumList()) {
                genericType = holder.getEnumName();
            } else if (holder.isStringList()) {
                genericType = String.class.getName();
            }
        }

        return genericType;
    }

    private List<Class> buildArgTypes() {
        List<Class> argTypes = new ArrayList<>();

        for (LookupFieldDto lookupField : lookup.getLookupFields()) {

            switch (lookupField.getType()) {
                case RANGE:
                    argTypes.add(Range.class);
                    break;
                case SET:
                    argTypes.add(Set.class);
                    break;
                default:
                    FieldDto field = fieldsById.get(lookupField.getId());
                    if (field == null) {
                        throw new FieldNotFoundException();
                    }

                    String typeClassName = getTypeClassName(lookupField, field);

                    try {
                        ClassLoader safeClassLoader = null == classLoader
                                ? MDSClassLoader.getInstance()
                                : classLoader;

                        argTypes.add(safeClassLoader.loadClass(typeClassName));
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("Type not found " + typeClassName, e);
                    }
            }
        }

        return argTypes;
    }

    private String getTypeClassName(LookupFieldDto lookupField, FieldDto field) {
        String typeClassName = field.getType().getTypeClass();

        if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entityClass, field);

            if (holder.isEnum()) {
                typeClassName = holder.getEnumName();
            } else if (holder.isEnumList()) {
                typeClassName = List.class.getName();

                if (lookupField.isUseGenericParam()) {
                    typeClassName = holder.getEnumName();
                }
            } else if (holder.isStringList()) {
                typeClassName = List.class.getName();

                if (lookupField.isUseGenericParam()) {
                    typeClassName = String.class.getName();
                }
            } else if (holder.isString()) {
                typeClassName = String.class.getName();
            }
        }

        return typeClassName;
    }
}
