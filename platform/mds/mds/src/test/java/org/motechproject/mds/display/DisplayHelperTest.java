package org.motechproject.mds.display;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.ManyToManyRelationship;
import org.motechproject.mds.domain.ManyToOneRelationship;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.domain.Relationship;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.testutil.records.display.DisplayTestEnum;
import org.motechproject.mds.testutil.records.display.ToStringTestClass;
import org.motechproject.mds.testutil.records.display.UIRepresentationTestClass;

import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DisplayHelperTest {

    @Test
    public void shouldReturnNullForRegularFields() {
        FieldDto field = FieldTestHelper.fieldDto("strField", String.class);
        assertNull(DisplayHelper.getDisplayValueForField(field, "value"));

        field = FieldTestHelper.fieldDto("intField", Integer.class);
        assertNull(DisplayHelper.getDisplayValueForField(field, 14));

        field = FieldTestHelper.fieldDto("dtField", DateTime.class);
        assertNull(DisplayHelper.getDisplayValueForField(field, DateUtil.now()));
    }

    @Test
    public void shouldDisplayOneToOneRelationship() {
        testSingleObjectRelationshipDisplay(OneToOneRelationship.class);
    }

    @Test
    public void shouldDisplayManyToOneRelationship() {
        testSingleObjectRelationshipDisplay(ManyToOneRelationship.class);
    }

    @Test
    public void shouldDisplayOneToManyRelationship() {
        testMultiObjectRelationshipDisplay(OneToManyRelationship.class);
    }

    @Test
    public void shouldDisplayManyToManyRelationship() {
        testMultiObjectRelationshipDisplay(ManyToManyRelationship.class);
    }

    @Test
    public void shouldDisplayCbSingleSelectNoUserSupplied() {
        FieldDto field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", false, false, DisplayTestEnum.valuesMap());
        assertEquals("Monday", DisplayHelper.getDisplayValueForField(field, DisplayTestEnum.MONDAY));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", false, false, DisplayTestEnum.valuesMap());
        assertNull(DisplayHelper.getDisplayValueForField(field, null));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", false, false, null);
        assertEquals(DisplayTestEnum.MONDAY, DisplayHelper.getDisplayValueForField(field, DisplayTestEnum.MONDAY));
    }

    @Test
    public void shouldDisplayCbSingleSelectUserSupplied() {
        FieldDto field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", false, true, null);
        assertEquals("Something", DisplayHelper.getDisplayValueForField(field, "Something"));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", false, true, null);
        assertEquals(null, DisplayHelper.getDisplayValueForField(field, null));
    }

    @Test
    public void shouldDisplayCbMultiSelectNoUserSupplied() {
        FieldDto field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", true, false, null);
        assertEquals(asList(DisplayTestEnum.MONDAY, DisplayTestEnum.TUESDAY),
                DisplayHelper.getDisplayValueForField(field, asList(DisplayTestEnum.MONDAY, DisplayTestEnum.TUESDAY)));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", true, false, DisplayTestEnum.valuesMap());
        assertEquals(asList("Monday", "Tuesday"),
                DisplayHelper.getDisplayValueForField(field, asList(DisplayTestEnum.MONDAY, DisplayTestEnum.TUESDAY)));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", true, false, DisplayTestEnum.valuesMap());
        assertNull(DisplayHelper.getDisplayValueForField(field, null));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", true, false, DisplayTestEnum.valuesMap());
        assertEquals(emptyList(), DisplayHelper.getDisplayValueForField(field, emptyList()));
    }

    @Test
    public void shouldDisplayCbMultiSelectUserSupplied() {
        FieldDto field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", true, true, null);
        assertEquals(asList("one", "two"), DisplayHelper.getDisplayValueForField(field, asList("one", "two")));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", true, true, null);
        assertNull(DisplayHelper.getDisplayValueForField(field, null));

        field = FieldTestHelper.comboboxFieldDto(1L, "cb", "cb", true, true, null);
        assertEquals(emptyList(), DisplayHelper.getDisplayValueForField(field, emptyList()));
    }

    private void testSingleObjectRelationshipDisplay(Class<? extends Relationship> relClass) {
        FieldDto field = FieldTestHelper.fieldDto("relField", relClass);

        assertEquals("Value", DisplayHelper.getDisplayValueForField(field, new ToStringTestClass(1, "Value")));
        assertEquals("Value", DisplayHelper.getDisplayValueForField(field, new UIRepresentationTestClass(1, "Value")));
    }

    private void testMultiObjectRelationshipDisplay(Class<? extends Relationship> relClass) {
        FieldDto field = FieldTestHelper.fieldDto("relField", relClass);

        Map<Long, String> displayMap = (Map<Long, String>) DisplayHelper.getDisplayValueForField(field, asList(
                new ToStringTestClass(1, "val1"), new ToStringTestClass(2, "val2"), new ToStringTestClass(3, "val3")
        ));
        assertDisplayMap(displayMap, "val1", "val2", "val3");

        displayMap = (Map<Long, String>) DisplayHelper.getDisplayValueForField(field, asList(
                new UIRepresentationTestClass(1, "val1"), new UIRepresentationTestClass(2, "val2"),
                new UIRepresentationTestClass(3, "val3")
        ));
        assertDisplayMap(displayMap, "val1", "val2", "val3");
    }

    private void assertDisplayMap(Map<Long, String> map, String... values) {
        for (int i = 0; i < values.length; i++) {
            assertEquals("Wrong entry for key: " + (i + 1), values[i], map.get(i + 1L));
        }
    }
}
