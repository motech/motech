package org.motechproject.mds.domain;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntityDraftTest {

    @Test
    public void shouldHandleColumnNameChanges() {
        EntityDraft draft = new EntityDraft();

        // origName1 -> newName1 -> newName2
        // origName2 -> newName2
        draft.addFieldNameChange("origName1", "newName1");
        draft.addFieldNameChange("newName1", "newName2");
        draft.addFieldNameChange("origName2", "newName1");

        Map<String, String> nameChanges = draft.getFieldNameChanges();
        assertEquals(2, nameChanges.size());
        assertEquals("newName2", nameChanges.get("origName1"));
        assertEquals("newName1", nameChanges.get("origName2"));
    }

    @Test
    public void shouldHandleUniqueConstraintRemoval() {
        EntityDraft draft = new EntityDraft();

        draft.addUniqueToRemove("name1");
        draft.addFieldNameChange("name1", "name2");
        draft.addUniqueToRemove("name3");
        draft.addFieldNameChange("unrelated", "unrelated2");

        assertEquals(2, draft.getUniqueIndexesToDrop().size());
        assertTrue(draft.getUniqueIndexesToDrop().contains("name2"));
        assertTrue(draft.getUniqueIndexesToDrop().contains("name3"));
    }
}
