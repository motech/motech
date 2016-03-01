package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.type.NoSuchTypeException;
import org.motechproject.mds.repository.AllTypeValidations;
import org.motechproject.mds.repository.AllTypes;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.util.TypeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link org.motechproject.mds.service.TypeService} interface
 */
@Service
public class TypeServiceImpl implements TypeService {
    private AllTypeValidations allTypeValidations;
    private AllTypes allTypes;

    @Override
    @Transactional
    public List<TypeDto> getAllTypes() {
        List<TypeDto> list = new ArrayList<>();

        for (Type type : allTypes.retrieveAll()) {
            list.add(type.toDto());
        }

        return list;
    }

    @Override
    @Transactional
    public TypeDto findType(Class<?> clazz) {
        String className = getClassNameForType(clazz);

        Type type = allTypes.retrieveByClassName(className);

        if (null != type) {
            return type.toDto();
        } else {
            throw new NoSuchTypeException(clazz.getCanonicalName());
        }
    }

    @Override
    @Transactional
    public List<TypeValidation> findValidations(TypeDto type, Class<? extends Annotation> aClass) {
        Type typeSource = allTypes.retrieveByClassName(type.getTypeClass());
        List<TypeValidation> list = null == typeSource ? new ArrayList<TypeValidation>() : typeSource.getValidations();
        List<TypeValidation> validations = new ArrayList<>();

        for (TypeValidation validation : list) {
            if (validation.getAnnotations().contains(aClass)) {
                validations.add(validation);
            }
        }

        return validations;
    }

    @Override
    @Transactional
    public Type getType(TypeValidation validation) {
        TypeValidation retrieve = allTypeValidations.retrieve(validation.getId());
        return null == retrieve ? null : retrieve.getValueType();
    }

    private String getClassNameForType(Class<?> clazz) {
        Class<?> chosenClass = clazz;

        // box primitives
        if (clazz.isPrimitive() || byte[].class.equals(clazz)) {
            chosenClass = TypeHelper.getWrapperForPrimitive(clazz);
        }

        return chosenClass.getName();
    }

    @Autowired
    public void setAllTypes(AllTypes allTypes) {
        this.allTypes = allTypes;
    }

    @Autowired
    public void setAllTypeValidations(AllTypeValidations allTypeValidations) {
        this.allTypeValidations = allTypeValidations;
    }
}
