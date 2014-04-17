package org.motechproject.mds.builder;

import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;

/**
 * An enum builder is responsible for building the enum class with the same values as those are
 * defined in the field.
 */
public interface EnumBuilder {

    ClassData build(ComboboxHolder holder);

}
