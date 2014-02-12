package org.motechproject.mds.repository;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This a repository for persisting entity drafts. It provides methods which created drafts, by
 * cloning the giving entity.
 */
@Repository
public class AllEntityDrafts extends MotechDataRepository<EntityDraft> {

    public AllEntityDrafts() {
        super(EntityDraft.class);
    }

    public EntityDraft create(Entity entity, String username) {
        EntityDraft draft = new EntityDraft();

        draft.setParentEntity(entity);
        draft.setParentVersion(entity.getEntityVersion());

        draft.setDraftOwnerUsername(username);
        draft.setLastModificationDate(DateUtil.nowUTC());

        draft.setClassName(entity.getClassName());
        draft.setNamespace(entity.getNamespace());
        draft.setModule(entity.getModule());

        for (Field field : entity.getFields()) {
            draft.addField(field.copy());
        }

        for (Lookup lookup : entity.getLookups()) {
            draft.addLookup(lookup.copy(draft.getFields()));
        }

        if (entity.getRestOptions() != null) {
            draft.setRestOptions(entity.getRestOptions().copy());
        }

        if (entity.getTracking() != null) {
            draft.setTracking(entity.getTracking().copy());
        }

        return create(draft);
    }

    public EntityDraft retrieve(Entity entity, String username) {
        return retrieve(
                new String[]{"parentEntity", "draftOwnerUsername"},
                new Object[]{entity, username}
        );
    }

    public List<EntityDraft> retrieveAll(String username) {
        return retrieveAll("draftOwnerUsername", username);
    }

    public List<EntityDraft> retrieveAll(Entity entity) {
        return retrieveAll("parentEntity", entity);
    }

    public void deleteAll(Entity entity) {
        deleteAll("parentEntity", entity);
    }

    @Override
    public EntityDraft update(EntityDraft draft) {
        draft.setLastModificationDate(DateUtil.nowUTC());
        draft.setChangesMade(true);

        return draft;
    }

}
