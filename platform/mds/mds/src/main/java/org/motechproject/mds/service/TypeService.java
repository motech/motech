package org.motechproject.mds.service;

import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.dto.TypeDto;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * The <code>TypeService</code> is an interface defining available methods to execute various
 * actions on Field Types.
 */
public interface TypeService {

    /**
     * Retrieves all available MDS types.
     *
     * @return a list of types
     */
    List<TypeDto> getAllTypes();

    /**
     * Retrieves MDS type, based on the class that handles that type in the backend.
     * Throws {@link org.motechproject.mds.exception.type.TypeNotFoundException} when the given class
     * does not handle any MDS type.
     *
     * @param clazz handler class
     * @return MDS type that is handled by the given class
     */
    TypeDto findType(Class<?> clazz);

    /**
     * Retrieves all MDS validations for the given type, that are triggered by the given annotation.
     *
     * @param type MDS type representation
     * @param aClass Annotation class type
     * @return A list of validations that match the criteria or empty list, if none were found
     * @see org.motechproject.mds.domain.TypeValidation
     */
    List<TypeValidation> findValidations(TypeDto type, Class<? extends Annotation> aClass);

    /**
     * Retrieves MDS Type, connected to the given validation.
     *
     * @param validation Validation representation
     * @return MDS Type that is connected to this validation
     * @see org.motechproject.mds.domain.TypeValidation
     */
    Type getType(TypeValidation validation);
}
