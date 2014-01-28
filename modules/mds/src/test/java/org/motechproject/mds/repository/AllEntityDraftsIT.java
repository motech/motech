package org.motechproject.mds.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.JDOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AllEntityDraftsIT extends BaseIT {

    private static final String USERNAME = "username";
    private static final String USERNAME_2 = USERNAME + "2";

    @Autowired
    private AllEntityMappings allEntityMappings;

    @Autowired
    private AllEntityDrafts allEntityDrafts;

    @Before
    public void setUp() {
        clearDB();
    }

    @Test
    public void shouldCreateAndDeleteDrafts() {
        EntityDto dto = new EntityDto();
        dto.setClassName("DraftCls");

        EntityMapping entity = allEntityMappings.save(dto);

        allEntityDrafts.createDraft(entity, USERNAME);
        allEntityDrafts.createDraft(entity, USERNAME_2);

        EntityDraft draft = allEntityDrafts.getDraft(entity, USERNAME);

        assertNotNull(draft);
        assertEquals("DraftCls", draft.getClassName());
        assertEquals(USERNAME, draft.getDraftOwnerUsername());

        draft = allEntityDrafts.getDraft(entity, USERNAME_2);

        assertNotNull(draft);
        assertEquals("DraftCls", draft.getClassName());
        assertEquals(USERNAME_2, draft.getDraftOwnerUsername());

        assertNull(allEntityDrafts.getDraft(entity, "otherUser"));

        allEntityDrafts.deleteAllDraftsForEntity(entity);

        assertTrue(allEntityDrafts.getAllEntityDrafts(entity).isEmpty());
        assertNull(allEntityDrafts.getDraft(entity, USERNAME));
        assertNull(allEntityDrafts.getDraft(entity, USERNAME_2));
    }

    @Test(expected = JDOException.class)
    public void shouldNotAllowTwoDraftsOfTheSameEntityForOneUser() {
        EntityDto dto = new EntityDto();
        dto.setClassName("DraftCls2");

        EntityMapping entity = allEntityMappings.save(dto);

        allEntityDrafts.createDraft(entity, USERNAME);
        allEntityDrafts.createDraft(entity, USERNAME);
    }
}
