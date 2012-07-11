package org.motechproject.cmslite.api.service;
/**
 * \defgroup cmslite CMS Lite
 * CMS Lite is lightweight content management supports multiple languages.
 */

import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.Content;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.stereotype.Component;

/**
 * \ingroup cmslite
 * CMS Lite is lightweight content management based on couchdb storage. It supports storing and retrieving of stream / text along with
 * custom meta data for each language. Implementer can also use {@link org.motechproject.cmslite.api.web.ResourceServlet} to export rest based
 * content retrieval.
 */
@Component
public interface CMSLiteService {
    /**
     * Get Stream content for given language.
     *
     * @param language
     * @param name
     * @return StreamContent with checksum and data type
     * @throws ContentNotFoundException
     */
    StreamContent getStreamContent(String language, String name) throws ContentNotFoundException;

    /**
     * Get Text Content for given tag and language.
     *
     * @param language
     * @param name
     * @return
     * @throws ContentNotFoundException
     */
    StringContent getStringContent(String language, String name) throws ContentNotFoundException;

    /**
     * Add content to CMS data-store
     *
     * @param content
     * @throws CMSLiteException
     * @see org.motechproject.cmslite.api.model.StreamContent
     * @see org.motechproject.cmslite.api.model.StringContent
     */
    void addContent(Content content) throws CMSLiteException;

    /**
     * Check if content available in stream format
     *
     * @param language
     * @param name
     * @return
     */
    boolean isStreamContentAvailable(String language, String name);

    /**
     * Check if content available in text format.
     *
     * @param language
     * @param name
     * @return
     */
    boolean isStringContentAvailable(String language, String name);
}
