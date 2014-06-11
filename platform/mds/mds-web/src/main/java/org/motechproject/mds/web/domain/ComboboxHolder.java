package org.motechproject.mds.web.domain;

import static org.apache.commons.lang.StringUtils.capitalize;

public class ComboboxHolder extends org.motechproject.mds.domain.ComboboxHolder {

    public ComboboxHolder(Object instance, FieldRecord field) {
        super(
                field.getMetadata(), field.getSettings(),
                instance.getClass().getName() + capitalize(field.getName())
        );
    }

}
