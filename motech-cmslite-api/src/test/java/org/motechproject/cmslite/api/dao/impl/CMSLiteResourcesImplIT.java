package org.motechproject.cmslite.api.dao.impl;

import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.cmslite.api.ResourceNotFoundException;
import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.cmslite.api.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testCmsLiteConfiguration.xml")
public class CMSLiteResourcesImplIT {

    @Autowired
    CMSLiteResourcesImpl cmsLiteDAO;
    @Autowired
    protected CouchDbConnector couchDbConnector;

    @Autowired
    private String pathToFile;

    private Resource resourceEnglish;
    private Resource resourceFrench;

    @Before
    public void setUpData() throws FileNotFoundException {
        resourceEnglish = new Resource();
        resourceEnglish.setLanguage("en");
        resourceEnglish.setName("test");

        InputStream inputStreamToResource = this.getClass().getResourceAsStream(pathToFile);

        couchDbConnector.create(resourceEnglish);
        couchDbConnector.createAttachment(resourceEnglish.getId(), resourceEnglish.getRevision(), new AttachmentInputStream(resourceEnglish.getId(), inputStreamToResource, "image/png"));
        resourceEnglish = couchDbConnector.get(Resource.class,resourceEnglish.getId());

        resourceFrench = new Resource();
        resourceFrench.setLanguage("fr");
        resourceFrench.setName("new");

        couchDbConnector.create(resourceFrench);
        couchDbConnector.createAttachment(resourceFrench.getId(), resourceFrench.getRevision(), new AttachmentInputStream(resourceFrench.getId(), inputStreamToResource, "image/png"));
        resourceFrench = couchDbConnector.get(Resource.class,resourceFrench.getId());

    }

    @Test
    public void shouldGetResourceByNameAndLanguageIfTheyArePresent() throws IOException {
        ResourceQuery query = new ResourceQuery("test", "en");
        Resource resource = cmsLiteDAO.getResource(query);

        assertNotNull(resource);
        assertEquals("test", resource.getName());
        assertEquals("en", resource.getLanguage());
        assertNotNull(resource.getResourceAsInputStream());
    }

    @Test
    public void shouldReturnNullWhenNameOrLanguageIsNotPresentInTheDB() {
        ResourceQuery query = new ResourceQuery("test", "ger");
        Resource resource = cmsLiteDAO.getResource(query);
        assertNull(resource);
        query = new ResourceQuery("testNone", "en");
        resource = cmsLiteDAO.getResource(query);
        assertNull(resource);
    }

    @Test
    public void shouldNotRetrieveAResourceIfCaseDoesNotMatch() {
        ResourceQuery query = new ResourceQuery("test", "En");
        Resource resource = cmsLiteDAO.getResource(query);
        assertNull(resource);
        query = new ResourceQuery("Test", "en");
        resource = cmsLiteDAO.getResource(query);
        assertNull(resource);
    }

    @Test
    public void shouldReturnNoResourceWhenNameIsNull() throws ResourceNotFoundException {
        ResourceQuery query = new ResourceQuery(null, "en");
        Resource resource = cmsLiteDAO.getResource(query);
        assertNull(resource);
    }

    @Test
    public void shouldReturnNoResourceWhenLanguageIsNull() throws ResourceNotFoundException {
        ResourceQuery query = new ResourceQuery("test", null);
        Resource resource = cmsLiteDAO.getResource(query);
        assertNull(resource);
    }

    @After
    public void tearData() {
        if (this.resourceEnglish != null && this.couchDbConnector.contains(resourceEnglish.getId())) {
            this.couchDbConnector.delete(resourceEnglish);
        }
        if (this.resourceFrench != null && this.couchDbConnector.contains(resourceFrench.getId())) {
            this.couchDbConnector.delete(resourceFrench);
        }
    }
}
