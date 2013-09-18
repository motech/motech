package org.motechproject.seuss.ui.web;

import org.motechproject.seuss.ui.domain.ObjectDto;

import java.util.List;

/**
 * The <code>SelectResult</code> class contains information which will be returned to select2.js.
 * <p/>
 * The class contains information such as:
 * <ul>
 * <li><strong>results</strong> - this is a list consisting of up to
 * {@link org.motechproject.seuss.ui.web.SelectData#getPageLimit()} records, which are of the
 * {@link ObjectDto} type,</li>
 * <li><strong>more</strong> - it equals to <i>true</i> if select2.js should load more data if user
 * reaches the end of the list; otherwise it equals to <i>false</i>.</li>
 * </ul>
 *
 * @see ObjectController
 * @see SelectData
 * @see <a href="http://ivaynberg.github.io/select2/">select2.js library</a>
 */
public class SelectResult {
    private List<ObjectDto> results;
    private boolean more;

    public SelectResult(SelectData data, List<ObjectDto> list) {
        Integer page = data.getPage();
        Integer records = list.size();
        Integer pages = records <= data.getPageLimit() ? 1 : (records / data.getPageLimit()) + 1;
        Integer start = data.getPageLimit() * (page > pages ? pages : page) - data.getPageLimit();
        Integer count = start + data.getPageLimit();
        Integer end = count > records ? records : count;

        this.results = list.subList(start, end);
        this.more = (page * data.getPageLimit()) < records;
    }

    public List<ObjectDto> getResults() {
        return results;
    }

    public boolean isMore() {
        return more;
    }
}
