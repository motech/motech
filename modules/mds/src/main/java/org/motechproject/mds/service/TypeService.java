package org.motechproject.mds.service;

import org.motechproject.mds.dto.TypeDto;

import java.util.List;

/**
 * The <code>TypeService</code> is an interface defining available methods to execute various
 * actions on Field Types.
 */
public interface TypeService {

    List<TypeDto> getAllTypes();

    TypeDto findType(Class<?> clazz);
}
