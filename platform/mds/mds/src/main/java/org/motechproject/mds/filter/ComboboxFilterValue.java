package org.motechproject.mds.filter;

import java.util.Arrays;
import java.util.List;

/**
 * Represents Combobox values used by filtering in MDS Data Browser. Those values are defined by user when new entity is created.
 * Provides proper value, param and operator for value.
 */
public class ComboboxFilterValue extends FilterValue {

    private boolean multiSelect;

    public ComboboxFilterValue(String value) {
        super.setValue(value);
        this.multiSelect = false;
    }

    public void setMultiSelect() {
        this.multiSelect = true;
    }

    @Override
    public Object valueForQuery() {
        return super.getValue();
    }

    @Override
    public String paramTypeForQuery() {
        return String.class.getName();
    }

    @Override
    public List<String> operatorForQueryFilter() {
        return multiSelect ? Arrays.asList(".contains(", ")") : Arrays.asList("==");
    }
}
