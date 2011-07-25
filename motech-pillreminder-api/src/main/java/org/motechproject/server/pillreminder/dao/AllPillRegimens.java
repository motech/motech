package org.motechproject.server.pillreminder.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.Medicine;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Set;

public class AllPillRegimens extends MotechAuditableRepository<PillRegimen> {

    @Autowired
    public AllPillRegimens(@Qualifier("pillReminderDatabase") CouchDbConnector db) {
        super(PillRegimen.class, db);
        initStandardDesignDocument();
    }

    @View(name = "findByExternalId", map = "function(doc) {if (doc.type == 'PILLREGIMEN' && doc.externalId) {emit(doc.externalId, doc._id);}}")
    public PillRegimen findByExternalId(String externalID) {
        ViewQuery q = createQuery("findByExternalId").key(externalID).includeDocs(true);
        List<PillRegimen> regimens = db.queryView(q, PillRegimen.class);
        return regimens.isEmpty() ? null : regimens.get(0);
    }

    public List<String> getMedicinesFor(String pillRegimenId, String dosageId) {
        PillRegimen pillRegimen = get(pillRegimenId);
        for (Dosage dosage : pillRegimen.getDosages())
            if (dosage.getId().equals(dosageId))
                return dosage.getMedicineNames();
        return null;
    }
}
