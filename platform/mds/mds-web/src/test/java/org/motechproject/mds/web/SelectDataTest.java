package org.motechproject.mds.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SelectDataTest {
    private static final String TEST_TERM = "Person";
    private static final Integer TEST_PAGE = 5;
    private static final Integer TEST_PAGE_LIMIT = 2;

    @Test
    public void shouldContainsDefaultValues() throws Exception {
        SelectData data = new SelectData();

        assertEquals(SelectData.DEFAULT_PAGE, data.getPage());
        assertEquals(SelectData.DEFAULT_PAGE_LIMIT, data.getPageLimit());
        assertEquals(SelectData.DEFAULT_TERM, data.getTerm());
    }

    @Test
    public void shouldNotContainsNullOrNegativeValues() throws Exception {
        SelectData data = new SelectData(null, null, null);

        assertEquals(SelectData.DEFAULT_PAGE, data.getPage());
        assertEquals(SelectData.DEFAULT_PAGE_LIMIT, data.getPageLimit());
        assertEquals(SelectData.DEFAULT_TERM, data.getTerm());

        data.setTerm("     ");
        assertEquals(SelectData.DEFAULT_TERM, data.getTerm());

        data.setPage(null);
        assertEquals(SelectData.DEFAULT_PAGE, data.getPage());

        data.setPage(-1 * TEST_PAGE);
        assertEquals(SelectData.DEFAULT_PAGE, data.getPage());

        data.setPageLimit(null);
        assertEquals(SelectData.DEFAULT_PAGE_LIMIT, data.getPageLimit());

        data.setPageLimit(-1 * TEST_PAGE_LIMIT);
        assertEquals(SelectData.DEFAULT_PAGE_LIMIT, data.getPageLimit());
    }

    @Test
    public void shouldContainsGivenValues() throws Exception {
        SelectData data = new SelectData(TEST_TERM, TEST_PAGE, TEST_PAGE_LIMIT);

        assertEquals(TEST_PAGE, data.getPage());
        assertEquals(TEST_PAGE_LIMIT, data.getPageLimit());
        assertEquals(TEST_TERM, data.getTerm());

        data = new SelectData();
        data.setPageLimit(TEST_PAGE_LIMIT);
        data.setPage(TEST_PAGE);
        data.setTerm(TEST_TERM);

        assertEquals(TEST_PAGE, data.getPage());
        assertEquals(TEST_PAGE_LIMIT, data.getPageLimit());
        assertEquals(TEST_TERM, data.getTerm());
    }
}
