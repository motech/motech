package org.motechproject.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.model.Audit;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public abstract class MotechAuditableRepository<T extends MotechAuditableDataObject> extends MotechBaseRepository<T> implements BaseDao<T> {

    private static final String AUDIT_ID_SUFFIX = "_AUDIT";

    private final Class<T> type;

    protected MotechAuditableRepository(Class<T> type, CouchDbConnector db) {
        super(type, db);
        this.type = type;
        initStandardDesignDocument();
    }

    @Override
    public void add(T entity) {
        super.add(entity);
        Audit audit = new Audit();
        Date now = DateUtil.now().toDate();
        audit.setId(entity.getId() + AUDIT_ID_SUFFIX);
        audit.setDateCreated(now);
        audit.setLastUpdated(now);
        audit.setDataObjectId(entity.getId());
        db.create(audit);
        entity.setAudits(new TreeSet<Audit>());
        entity.getAudits().add(audit);
    }

    @Override
    public void update(T entity) {
        super.update(entity);
        Audit audit = db.get(Audit.class, entity.getId() + AUDIT_ID_SUFFIX);
        audit.setLastUpdated(DateUtil.now().toDate());
        db.update(audit);
        entity.setAudits(new TreeSet<Audit>());
        entity.getAudits().add(audit);
    }

    @Override
    public void remove(T entity) {
        Set<Audit> audits = entity.getAudits();
        if (audits != null) {
            for (Audit audit : audits) {
                db.delete(audit);
            }
        }
        super.remove(entity);
    }

    @GenerateView
    @Override
    public List<T> getAll() {
        ViewQuery q = createQuery("all").includeDocs(true);
        return db.queryView(q, this.type);
    }
}
