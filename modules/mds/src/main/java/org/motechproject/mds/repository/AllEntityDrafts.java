package org.motechproject.mds.repository;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.FieldMapping;
import org.motechproject.mds.domain.LookupMapping;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

/**
 * This a repository for persisting entity drafts. It provides methods which created drafts, by cloning the giving
 * entity.
 */
@Repository
public class AllEntityDrafts extends BaseMdsRepository {

    public EntityDraft createDraft(EntityMapping entity, String username) {
        EntityDraft draft = new EntityDraft();

        draft.setParentEntity(entity);
        draft.setParentVersion(entity.getEntityVersion());

        draft.setDraftOwnerUsername(username);
        draft.setLastModificationDate(DateUtil.nowUTC());

        draft.setClassName(entity.getClassName());
        draft.setNamespace(entity.getNamespace());
        draft.setModule(entity.getModule());

        for (FieldMapping field : entity.getFields()) {
            draft.addField(field.copy());
        }

        for (LookupMapping lookup : entity.getLookups()) {
            draft.addLookup(lookup.copy());
        }

        return getPersistenceManager().makePersistent(draft);
    }

    public EntityDraft getDraft(EntityMapping entity, String username) {
        Query query = getPersistenceManager().newQuery(EntityDraft.class);

        query.setFilter("paramUsername == draftOwnerUsername && paramEntity == parentEntity");
        query.declareParameters("java.lang.String paramUsername, org.motechproject.mds.domain.EntityMapping paramEntity");

        query.setUnique(true);

        return (EntityDraft) query.execute(username, entity);
    }

    public List<EntityDraft> getAllUserDrafts(String username) {
        Query query = getPersistenceManager().newQuery(EntityDraft.class);
        query.setFilter("paramUsername == draftOwnerUsername");
        query.declareParameters("java.lang.String paramUsername");

        Collection collection = (Collection) query.execute(username);
        return cast(EntityDraft.class, collection);
    }

    public void save(EntityDraft draft) {
        draft.setLastModificationDate(DateUtil.nowUTC());
        draft.setChangesMade(true);
    }

    public void delete(EntityDraft draft) {
        getPersistenceManager().deletePersistent(draft);
    }
}
