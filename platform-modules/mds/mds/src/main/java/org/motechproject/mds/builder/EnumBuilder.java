package org.motechproject.mds.builder;

import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;

/**
 * An enum builder is responsible for building the enum class with the same values as those are
 * defined in the field.
 */
public interface EnumBuilder {

    /**
     * Builds an Enum, based on the passed {@link org.motechproject.mds.domain.ComboboxHolder} object. If not
     * specified otherwise by the field metadata, the enum will be named after the entity and field name.
     *
     * @param holder A helper object, representing MDS Combobox type.
     * @return Bytecode representation of the enum.
     */
    ClassData build(ComboboxHolder holder);

}
