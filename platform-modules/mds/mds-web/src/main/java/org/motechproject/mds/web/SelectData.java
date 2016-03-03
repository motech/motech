package org.motechproject.mds.web;

import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>SelectData</code> class contains information from select2.js ajax request.
 * <p/>
 * The class contains information such as:
 * <ul>
 * <li>
 * <strong>term</strong> - it can consist of several strings connected with a
 * <strong>,</strong> character. Only three first strings are taken into consideration,
 * others are ignored. By default it is equal to {@link #DEFAULT_TERM}.
 * </li>
 * <li>
 * <strong>pageLimit</strong> - number of records which should be on one page. By default
 * it is equal to {@link #DEFAULT_PAGE_LIMIT}.
 * </li>
 * <li>
 * <strong>page</strong> - number of the page which should be returned. By default it is
 * equal to {@link #DEFAULT_PAGE}.
 * </li>
 * </ul>
 *
 * @see org.motechproject.mds.web.controller.EntityController
 * @see org.motechproject.mds.web.controller.AvailableController
 * @see SelectResult
 * @see <a href="http://ivaynberg.github.io/select2/">select2.js library</a>
 */
public class SelectData {
    /**
     * The constant <code>DEFAULT_TERM</code> presents the default term used to find specific
     * entity.
     */
    public static final String DEFAULT_TERM = StringUtils.EMPTY;

    /**
     * The constant <code>DEFAULT_PAGE_LIMIT</code> presents the default number of records on one
     * page.
     */
    public static final Integer DEFAULT_PAGE_LIMIT = 5;

    /**
     * The constant <code>DEFAULT_PAGE</code> presents the default page number.
     */
    public static final Integer DEFAULT_PAGE = 1;

    private String term;
    private Integer pageLimit;
    private Integer page;

    public SelectData() {
        this(DEFAULT_TERM, DEFAULT_PAGE, DEFAULT_PAGE_LIMIT);
    }

    public SelectData(String term, Integer page, Integer pageLimit) {
        this.term = isNotBlank(term) ? term : DEFAULT_TERM;
        this.page = (null != page && page >= 0) ? page : DEFAULT_PAGE;
        this.pageLimit = (null != pageLimit && pageLimit >= 0) ? pageLimit : DEFAULT_PAGE_LIMIT;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = isNotBlank(term) ? term : DEFAULT_TERM;
    }

    public Integer getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(Integer pageLimit) {
        this.pageLimit = (null != pageLimit && pageLimit >= 0) ? pageLimit : DEFAULT_PAGE_LIMIT;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = (null != page && page >= 0) ? page : DEFAULT_PAGE;
    }

}
