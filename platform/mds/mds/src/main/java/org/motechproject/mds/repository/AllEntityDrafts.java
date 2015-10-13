package org.motechproject.mds.repository;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.domain.Tracking;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

/**
 * This a repository for persisting entity drafts. It provides methods which create drafts, by
 * cloning the given entity.
 */
@Repository
public class AllEntityDrafts extends MotechDataRepository<EntityDraft> {

    public AllEntityDrafts() {
        super(EntityDraft.class);
    }

    public EntityDraft create(Entity entity, String username) {
        EntityDraft draft = new EntityDraft();
        setProperties(draft, entity, username);

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
        delete("parentEntity", entity);
    }

    @Override
    public EntityDraft update(EntityDraft draft) {
        draft.setLastModificationDate(DateUtil.nowUTC());
        draft.setChangesMade(true);

        return draft;
    }

    public void setProperties(EntityDraft draft, Entity entity) {
        setProperties(draft, entity, draft.getDraftOwnerUsername());
    }

    public void setProperties(EntityDraft draft, Entity entity, String username) {
        draft.setParentEntity(entity);
        draft.setParentVersion(entity.getEntityVersion());

        draft.setDraftOwnerUsername(username);
        draft.setLastModificationDate(DateUtil.nowUTC());

        draft.setName(entity.getName());
        draft.setClassName(entity.getClassName());
        draft.setNamespace(entity.getNamespace());
        draft.setModule(entity.getModule());
        draft.setIsDiscriminated(entity.isDiscriminated());
        draft.setSecurityMode(entity.getSecurityMode());
        draft.setSecurityMembers(new HashSet<>(entity.getSecurityMembers()));
        draft.setReadOnlySecurityMode(entity.getReadOnlySecurityMode());
        draft.setReadOnlySecurityMembers(new HashSet<>(entity.getReadOnlySecurityMembers()));

        draft.getFields().clear();
        for (Field field : entity.getFields()) {
            draft.addField(field.copy());
        }

        draft.getLookups().clear();
        for (Lookup lookup : entity.getLookups()) {
            Lookup newLookup = lookup.copy(draft.getFields());
            draft.addLookup(newLookup);
        }

        if (entity.getRestOptions() != null) {
            RestOptions restOptions =  entity.getRestOptions().copy();
            restOptions.setEntity(draft);
            draft.setRestOptions(restOptions);
        }

        if (entity.getTracking() != null) {
            Tracking tracking = entity.getTracking().copy();
            tracking.setEntity(draft);
            draft.setTracking(tracking);
        }
    }
}
