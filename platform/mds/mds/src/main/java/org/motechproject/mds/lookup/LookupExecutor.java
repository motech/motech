package org.motechproject.mds.lookup;

import org.apache.commons.lang.reflect.MethodUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.exception.field.FieldNotFoundException;
import org.motechproject.mds.exception.lookup.LookupExecutorException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.TypeHelper;
import org.datanucleus.store.query.QueryNotUniqueException;

import javax.jdo.JDOUserException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class allows executing lookups by providing the lookup name
 * as a string and the lookup params in name-value map. Used both
 * by the REST api and the Databrowser UI for executing lookups based
 * on only metadata. The dataservice and metadata must be provided during
 * construction.
 */
public class LookupExecutor {

    private final MotechDataService dataService;
    private final LookupDto lookup;
    private final Map<String, FieldDto> fieldsByName;
    private final Class entityClass;
    private final ClassLoader classLoader;

    public LookupExecutor(MotechDataService dataService, LookupDto lookup, Map<String, FieldDto> fieldsByName) {
        this.dataService = dataService;
        this.lookup = lookup;
        this.fieldsByName = fieldsByName;
        this.entityClass = dataService.getClassType();
        this.classLoader = dataService.getClass().getClassLoader();
    }

    public Object execute(Map<String, ?> lookupMap) {
        return execute(lookupMap, null);
    }

    public Object execute(Map<String, ?> lookupMap, QueryParams queryParams) {
        List<Object> args = getLookupArgs(lookupMap);
        List<Class> argTypes = buildArgTypes();
        String lookupExceptionMessage = "Unable to execute lookup ";
        String lookupExceptionMessageKey = "mds.error.lookupExecError";

        if (queryParams != null) {
            args.add(queryParams);
            argTypes.add(QueryParams.class);
        }

        try {
            return MethodUtils.invokeMethod(dataService, lookup.getMethodName(),
                    args.toArray(new Object[args.size()]),
                    argTypes.toArray(new Class[argTypes.size()]));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new LookupExecutorException(lookupExceptionMessage + lookup.getLookupName() + ".", e, null);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof JDOUserException) {
                JDOUserException userException = (JDOUserException) e.getTargetException();
                lookupExceptionMessageKey = "mds.error.lookupExecUserError";
                for (Throwable exception : userException.getNestedExceptions()) {
                    if (exception instanceof QueryNotUniqueException) {
                        lookupExceptionMessageKey = "mds.error.lookupExecNotUniqueError";
                    }
                }
            }
            throw new LookupExecutorException(lookupExceptionMessage + lookup.getLookupName() + ".", e, lookupExceptionMessageKey);
        }
    }

    public long executeCount(Map<String, ?> lookupMap) {
        List<Object> args = getLookupArgs(lookupMap);
        List<Class> argTypes = buildArgTypes();

        String countMethodName = LookupName.lookupCountMethod(lookup.getMethodName());

        try {
            return (long) MethodUtils.invokeMethod(dataService, countMethodName,
                    args.toArray(new Object[args.size()]),
                    argTypes.toArray(new Class[argTypes.size()]));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new LookupExecutorException("Unable to execute count lookup " + lookup.getLookupName() + ".", e, null);
        }
    }


    private List<Object> getLookupArgs(Map<String, ?> paramMap) {
        List<Object> args = new ArrayList<>();
        for (LookupFieldDto lookupField : lookup.getLookupFields()) {
            FieldDto field = fieldsByName.get(lookupField.getLookupFieldName());
            if (field == null) {
                throw new FieldNotFoundException(entityClass.getName(), lookupField.getName());
            }

            Object val = paramMap.get(lookupField.getLookupFieldName());

            String typeClass = getTypeClass(field);
            String genericType = getGenericTypeClass(field);

            Object arg;
            if (lookupField.getType() == LookupFieldType.RANGE) {
                arg = TypeHelper.toRange(val, typeClass);
            } else if (lookupField.getType() == LookupFieldType.SET) {
                arg = TypeHelper.toSet(val, typeClass, classLoader);
            } else {
                arg = TypeHelper.parse(val, lookupField.isUseGenericParam() ? genericType : typeClass, classLoader);
            }

            args.add(arg);
        }
        return args;
    }

    private String getTypeClass(FieldDto field) {
        String typeClass;

        if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entityClass, field);
            typeClass = holder.getTypeClassName();
        } else {
            typeClass = (field.getType().isTextArea()) ? "java.lang.String" : field.getType().getTypeClass();
        }

        return typeClass;
    }

    private String getGenericTypeClass(FieldDto field) {
        String genericType = null;

        if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entityClass, field);

            if (holder.isCollection()) {
                genericType = holder.getUnderlyingType();
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
                    FieldDto field = fieldsByName.get(lookupField.getLookupFieldName());
                    if (field == null) {
                        throw new FieldNotFoundException(entityClass.getName(), lookupField.getName());
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
        String typeClassName = (field.getType().isTextArea()) ? "java.lang.String" : field.getType().getTypeClass();

        if (field.getType().isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entityClass, field);
            
            if (holder.isCollection() && lookupField.isUseGenericParam()) {
                typeClassName = holder.getUnderlyingType();
            } else {
                typeClassName = holder.getTypeClassName();
            }
        }

        return typeClassName;
    }
}
