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
package org.motechproject.core;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static junit.framework.Assert.assertEquals;


/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 2/28/11
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationPlatformCommon.xml",
                                 "/persistenceIntegrationContext.xml"})
public class MotechPersistenceIT {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CouchDbInstance couchDbInstance;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Actor male;
    private Actor female;
    private Visit first;

	@Before
	public void setup() throws Exception {
        // Create new patient
        male = new Actor();
        male.setName("Colin Firth");
        male.setDateCreated(new Date());
        male.setPhoneNumber("+1(123)456-7890");
        male.setTags(Arrays.asList("Male"));

        log.info("Creating patient " + male.getName());
        actorRepository.add(male);

        female = new Actor();
        female.setName("Natalie Portman");
        female.setDateCreated(new Date());
        female.setPhoneNumber("+1(123)456-7890");
        female.setTags(Arrays.asList("Female", "Pregnant"));

        log.info("Creating patient " + female.getName());
        actorRepository.add(female);

        first = new Visit();
        first.setActorId(female.getId());
        first.setDateCreated(new Date());
        first.setComment("First Visit");

        visitRepository.add(first);
        log.info("Creating visit " + first.getId());
    }

	@After
	public void tearDown() throws Exception {
        log.info("Removing visit");
        visitRepository.remove(first);

        log.info("Removing patient " + male.getName());
        actorRepository.remove(male);

        log.info("Removing patient " + female.getName());
        actorRepository.remove(female);

        log.info("Deleting patients database");
        couchDbInstance.deleteDatabase("actors");

        log.info("Deleting visits database");
        couchDbInstance.deleteDatabase("visits");
	}

    @Test
    public void testMotechPersistence() {
        assertEquals(2, actorRepository.getAll().size());

        List<Actor> pregnant = actorRepository.findByTag("Pregnant");
        assertEquals(1, pregnant.size());
        assertEquals("Natalie Portman", pregnant.get(0).getName());

        List<Visit> vists = visitRepository.findByActorId(female.getId());

        assertEquals(1, vists.size());
        assertEquals(female.getId(), vists.get(0).getActorId());
    }
}

class Actor extends CouchDbDocument {

    private static final long serialVersionUID = 1L;

    private String name;
    private String phoneNumber;
    private List<String> tags;
    private Date dateCreated;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

class Visit extends CouchDbDocument {

    private static final long serialVersionUID = 1L;

    private String actorId;
    private String comment;
    private Date dateCreated;

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}

@Component
class ActorRepository extends CouchDbRepositorySupport<Actor> {

    @Autowired
    public ActorRepository(@Qualifier("actorDatabase") CouchDbConnector db) {
        super(Actor.class, db);
    }

    @GenerateView
    public List<Actor> findByTag(String tag) {
        return queryView("by_tag", tag);
    }
}

@Component
@View( name="all", map = "function(doc) { if (doc.actorId) { emit(doc.dateCreated, doc._id) } }")
class VisitRepository extends CouchDbRepositorySupport<Visit>
{

    @Autowired
    public VisitRepository(@Qualifier("visitDatabase") CouchDbConnector db) {
        super(Visit.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<Visit> findByActorId(String actorId) {
        return queryView("by_actorId", actorId);
    }
}