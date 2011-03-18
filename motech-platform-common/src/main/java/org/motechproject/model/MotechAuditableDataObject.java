/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.model;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.docref.DocumentReferences;
import org.ektorp.docref.FetchType;

/**
 * 
 * 
 * @author Ricky Wang
 */
public abstract class MotechAuditableDataObject extends MotechBaseDataObject{

    private static final long serialVersionUID = 1L;
    
    //the current version of ektorp (1.1) only supports @DocumentReferences on a Set
    //Otherwise, a 1-to-1 relationship is enough 
    @DocumentReferences(backReference = "dataObjectId", fetch = FetchType.LAZY, descendingSortOrder = true, orderBy = "lastUpdated")
    private Set<Audit> audits;
    
    public Set<Audit> getAudits() {
        return audits;
    }
    
    public void setAudits(Set<Audit> audits) {
        this.audits = audits;
    }
    
    /**
     * Convenience method to get the audit data
     * 
     * @return audit data of the latest version of the document (does NOT support auditing for older versions)
     */
    @JsonIgnore
    public Audit getAudit(){
        Audit audit = null;
        if (audits != null && !audits.isEmpty()) {
            audit = audits.iterator().next();
        }
        return audit;
    }
    
}
