package org.motechproject.mds.builder.impl;

import org.junit.Test;
import org.motechproject.mds.domain.EntityType;

import static org.junit.Assert.assertEquals;

public class KeyNamesTest {

    @Test
    public void shouldBuildLookupIndexNames() {
        assertEquals("lkp_idx_TestEntity_testField_3",
                KeyNames.lookupIndexKeyName("TestEntity", 3L, "testField", EntityType.STANDARD));
        assertEquals("lkp_idx_TestEntity__History_testField_3",
                KeyNames.lookupIndexKeyName("TestEntity", 3L, "testField", EntityType.HISTORY));
        assertEquals("lkp_idx_TestEntity__Trash_testField_3",
                KeyNames.lookupIndexKeyName("TestEntity", 3L, "testField", EntityType.TRASH));
    }

    @Test
    public void shouldBuildForeignKeyNames() {
        assertEquals("fk_TestEntity_books_3",
                KeyNames.foreignKeyName("TestEntity", 3L, "books", EntityType.STANDARD));
        assertEquals("fk_TestEntity__History_books_3",
                KeyNames.foreignKeyName("TestEntity", 3L, "books", EntityType.HISTORY));
        assertEquals("fk_TestEntity__Trash_books_3",
                KeyNames.foreignKeyName("TestEntity", 3L, "books", EntityType.TRASH));
    }

    @Test
    public void shouldBuildMapForeignKeyNames() {
        assertEquals("map_fk_TestEntity_mapField_3",
                KeyNames.mapForeignKeyName("TestEntity", 3L, "mapField", EntityType.STANDARD));
        assertEquals("map_fk_TestEntity__History_mapField_3",
                KeyNames.mapForeignKeyName("TestEntity", 3L, "mapField", EntityType.HISTORY));
        assertEquals("map_fk_TestEntity__Trash_mapField_3",
                KeyNames.mapForeignKeyName("TestEntity", 3L, "mapField", EntityType.TRASH));
    }

    @Test
    public void shouldBuildComboboxForeignKeyNames() {
        assertEquals("cb_fk_TestEntity_comboBox_3",
                KeyNames.cbForeignKeyName("TestEntity", 3L, "comboBox", EntityType.STANDARD));
        assertEquals("cb_fk_TestEntity__History_comboBox_3",
                KeyNames.cbForeignKeyName("TestEntity", 3L, "comboBox", EntityType.HISTORY));
        assertEquals("cb_fk_TestEntity__Trash_comboBox_3",
                KeyNames.cbForeignKeyName("TestEntity", 3L, "comboBox", EntityType.TRASH));
    }
}
