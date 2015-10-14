package org.motechproject.mds.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.ManyToManyRelationship;
import org.motechproject.mds.domain.ManyToOneRelationship;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.testutil.records.display.DisplayTestEnum;
import org.motechproject.mds.testutil.records.display.NoToString;
import org.motechproject.mds.testutil.records.display.ToStringTestClass;

import java.util.Collections;
import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCsvExportCustomizerTest {

    private DefaultCsvExportCustomizer exportCustomizer = new DefaultCsvExportCustomizer();

    @Test
    public void shouldFormatRegularFields() {
        FieldDto strField = FieldTestHelper.fieldDto("name", String.class);
        assertEquals("test string", exportCustomizer.formatField(strField, "test string"));

        FieldDto intField = FieldTestHelper.fieldDto("name", Integer.class);
        assertEquals("20" , exportCustomizer.formatField(intField, 20));

        FieldDto lcField = FieldTestHelper.fieldDto("name", Locale.class);
        assertEquals(Locale.ENGLISH.toString() , exportCustomizer.formatField(lcField, Locale.ENGLISH));

        final DateTime now = DateUtil.now();
        FieldDto dtField = FieldTestHelper.fieldDto("name", DateTime.class);
        assertEquals(now.toString() , exportCustomizer.formatField(dtField, now));

        final LocalDate today = DateUtil.today();
        FieldDto ldField = FieldTestHelper.fieldDto("name", LocalDate.class);
        assertEquals(today.toString() , exportCustomizer.formatField(ldField, today));
    }

    @Test
    public void shouldFormatComboboxFields() {
        FieldDto cbField = FieldTestHelper.comboboxFieldDto("cbSingleNonUS", false, false, DisplayTestEnum.valuesMap());
        assertEquals("Monday", exportCustomizer.formatField(cbField, DisplayTestEnum.MONDAY));
        assertEquals("", exportCustomizer.formatField(cbField, null));

        cbField = FieldTestHelper.comboboxFieldDto("cbSingleUS", false, true, null);
        assertEquals("something", exportCustomizer.formatField(cbField, "something"));
        assertEquals("", exportCustomizer.formatField(cbField, null));

        cbField = FieldTestHelper.comboboxFieldDto("cbMultiNonUS", true, false, DisplayTestEnum.valuesMap());
        assertEquals("Wednesday,Monday",
                exportCustomizer.formatField(cbField, asList(DisplayTestEnum.WEDNESDAY, DisplayTestEnum.MONDAY)));
        assertEquals("", exportCustomizer.formatField(cbField, null));
        assertEquals("", exportCustomizer.formatField(cbField, emptyList()));

        cbField = FieldTestHelper.comboboxFieldDto("cbMultiUS", true, true, "one,two,three");
        assertEquals("test1,test2", exportCustomizer.formatField(cbField, asList("test1", "test2")));
        assertEquals("", exportCustomizer.formatField(cbField, emptyList()));
        assertEquals("", exportCustomizer.formatField(cbField, null));
    }

    @Test
    public void shouldFormatRelationships() {
        // these two relationship types should be handled the same way
        for (Class clazz : asList(OneToOneRelationship.class, ManyToOneRelationship.class)) {
            FieldDto relField = FieldTestHelper.fieldDto("name", clazz);
            assertEquals("something", exportCustomizer.formatField(relField, new ToStringTestClass(1, "something")));
            assertEquals("4", exportCustomizer.formatField(relField, new NoToString(4)));
            assertEquals("", exportCustomizer.formatField(relField, null));
        }

        // these two as well
        for (Class clazz : asList(OneToManyRelationship.class, ManyToManyRelationship.class)) {
            FieldDto relField = FieldTestHelper.fieldDto("name", clazz);
            assertEquals("first,second", exportCustomizer.formatField(relField, asList(
                    new ToStringTestClass(1, "first"), new ToStringTestClass(2, "second"))));
            assertEquals("7,8", exportCustomizer.formatField(relField, asList(
                    new NoToString(7), new NoToString(8))));
            assertEquals("", exportCustomizer.formatField(relField, null));
            assertEquals("", exportCustomizer.formatField(relField, Collections.emptySet()));
        }
    }
}
