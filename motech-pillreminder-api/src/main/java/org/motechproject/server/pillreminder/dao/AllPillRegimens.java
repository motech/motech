package org.motechproject.server.pillreminder.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;

public class AllPillRegimens extends MotechAuditableRepository<PillRegimen> {

    @Autowired
    public AllPillRegimens(@Qualifier("pillReminderDbConnector") CouchDbConnector db) {
        super(PillRegimen.class, db);
    }

    @View(name = "findByExternalId", map = "function(doc) {if (doc.type == 'PILLREGIMEN' && doc.externalId) {emit(doc.externalId, doc._id);}}")
    public PillRegimen findByExternalId(String externalID) {
        ViewQuery q = createQuery("findByExternalId").key(externalID).includeDocs(true);
        List<PillRegimen> regimens = db.queryView(q, PillRegimen.class);
        return regimens.isEmpty() ? null : regimens.get(0);
    }

    public List<String> medicinesFor(String regimenId, String dosageId) {
        PillRegimen pillRegimen = get(regimenId);
        Dosage dosage = pillRegimen.getDosage(dosageId);
        return dosage != null ? dosage.getMedicineNames() : Collections.EMPTY_LIST;
    }

    public void stopTodaysReminders(String regimenId, String dosageId) {
        PillRegimen pillRegimen = get(regimenId);
        Dosage dosage = pillRegimen.getDosage(dosageId);
        dosage.updateCurrentDate();
        update(pillRegimen);
    }

}
