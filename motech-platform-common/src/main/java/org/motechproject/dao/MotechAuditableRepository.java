/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.model.Audit;
import org.motechproject.model.MotechAuditableDataObject;


public abstract class MotechAuditableRepository <T extends MotechAuditableDataObject> extends MotechBaseRepository<T> implements BaseDao<T> {
    
    private static final String AUDIT_ID_SUFFIX = "_AUDIT";
    
    private final Class<T> type;

    protected MotechAuditableRepository(Class<T> type, CouchDbConnector db) {
        super(type, db);
        this.type = type;
    }

    @Override
    public void add(T entity) {
        super.add(entity);
        Audit audit = new Audit();
        Date now = new Date();
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
        audit.setLastUpdated(new Date());
        db.update(audit);
        entity.setAudits(new TreeSet<Audit>());
        entity.getAudits().add(audit);
    }
    
    @Override
    public void remove(T entity){

        Set<Audit> audits = entity.getAudits();
        if (audits != null) {
            for (Audit audit : audits) {
                db.delete(audit);
            }
        }
        super.remove(entity);
    }
    
    @GenerateView @Override
    public List<T> getAll() {
            ViewQuery q = createQuery("all").includeDocs(true);
            return db.queryView(q, this.type);
    }
    
}
