package org.motechproject.cmslite.api.web;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ResourceComparatorTest {

    @Test
    public void shouldCompareByNameWithAsc() {
        ResourceComparator comparator = new ResourceComparator(createGridSettings("name", "asc"));
        ResourceDto first = new ResourceDto("abc", "string", "english");
        ResourceDto second = new ResourceDto("def", "stream", "spanish");

        assertTrue(comparator.compare(first, second) < 0);
        assertTrue(comparator.compare(second, first) > 0);
        assertTrue(comparator.compare(first, first) == 0);
    }

    @Test
    public void shouldCompareByNameWithDesc() {
        ResourceComparator comparator = new ResourceComparator(createGridSettings("name", "desc"));
        ResourceDto first = new ResourceDto("abc", "string", "english");
        ResourceDto second = new ResourceDto("def", "stream", "spanish");

        assertTrue(comparator.compare(first, second) > 0);
        assertTrue(comparator.compare(second, first) < 0);
        assertTrue(comparator.compare(first, first) == 0);
    }

    @Test
    public void shouldCompareByTypeWithAsc() {
        ResourceComparator comparator = new ResourceComparator(createGridSettings("type", "asc"));
        ResourceDto first = new ResourceDto("abc", "string", "english");
        ResourceDto second = new ResourceDto("def", "stream", "spanish");

        assertTrue(comparator.compare(first, second) > 0);
        assertTrue(comparator.compare(second, first) < 0);
        assertTrue(comparator.compare(first, first) == 0);
    }

    @Test
    public void shouldCompareByTypeWithDesc() {
        ResourceComparator comparator = new ResourceComparator(createGridSettings("type", "desc"));
        ResourceDto first = new ResourceDto("abc", "string", "english");
        ResourceDto second = new ResourceDto("def", "stream", "spanish");

        assertTrue(comparator.compare(first, second) < 0);
        assertTrue(comparator.compare(second, first) > 0);
        assertTrue(comparator.compare(first, first) == 0);
    }

    private GridSettings createGridSettings(String sortColumn, String sortDirection) {
        GridSettings settings = new GridSettings();
        settings.setSortColumn(sortColumn);
        settings.setSortDirection(sortDirection);

        return settings;
    }
}
