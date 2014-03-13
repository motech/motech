package org.motechproject.mds.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.mds.dto.BrowsingSettingsDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The <code>BrowsingSettings</code> contains information about fields that will be visible on UI
 * and could be used as filter on UI.
 * <p/>
 * This class is read only (the data are not saved to database) and its main purpose is to
 * provide methods that help developer to get displayed and filterable fields.
 */
public class BrowsingSettings {
    private Entity entity;

    public BrowsingSettings(Entity entity) {
        this.entity = entity;
    }

    public BrowsingSettingsDto toDto() {
        BrowsingSettingsDto dto = new BrowsingSettingsDto();

        for (Field field : getFilterableFields()) {
            dto.addFilterableField(field.getId());
        }

        for (Field field : getDisplayedFields()) {
            dto.addDisplayedField(field.getId());
        }

        return dto;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<Field> getFilterableFields() {
        return getFields(new FilterableFieldPredicate(), null);
    }

    public List<Field> getDisplayedFields() {
        return getFields(new DisplayedFieldPredicate(), new DisplayedFieldComparator());
    }

    private List<Field> getFields(Predicate predicate, Comparator<Field> comparator) {
        List<Field> fields = new ArrayList<>(getEntity().getFields());

        if (null != predicate) {
            CollectionUtils.filter(fields, predicate);
        }

        if (null != comparator) {
            Collections.sort(fields, comparator);
        }

        return fields;
    }

    private static class FilterableFieldPredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            return object instanceof Field && ((Field) object).isUIFilterable();
        }

    }

    private static class DisplayedFieldPredicate implements Predicate {

        @Override
        public boolean evaluate(Object object) {
            return object instanceof Field && ((Field) object).isUIDisplayable();
        }

    }

    private static class DisplayedFieldComparator implements Comparator<Field> {

        @Override
        public int compare(Field field1, Field field2) {
            return Long.compare(field1.getUIDisplayPosition(), field2.getUIDisplayPosition());
        }

    }
}
