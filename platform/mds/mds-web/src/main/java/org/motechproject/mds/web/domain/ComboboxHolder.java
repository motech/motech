package org.motechproject.mds.web.domain;

import static org.apache.commons.lang.StringUtils.capitalize;

/**
 * Extension of the {@link org.motechproject.mds.domain.ComboboxHolder} class, from the MDS module. Allows
 * instantiation from web-specific classes.
 */
public class ComboboxHolder extends org.motechproject.mds.domain.ComboboxHolder {

    /**
     * Constructor.
     *
     * @param instance an actual instance of an entity
     * @param field representation of a field with the combobox type
     */
    public ComboboxHolder(Object instance, FieldRecord field) {
        super(
                field.getMetadata(), field.getSettings(),
                instance.getClass().getName() + capitalize(field.getName())
        );
    }

}
