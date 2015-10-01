package org.motechproject.mds.query;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A utility for performing in memory filtering on collections. This is not performance
 * efficient, since its done on the application level. Filtering at the database level is advised.
 */
public final class InMemoryQueryFilter {

    /**
     * Filters the provided collection based on the provided query params.
     * @param objects the collection to filter
     * @param queryParams the query params with the ordering and paging information to apply
     * @param <T> the type of the filtered collection
     * @return the filtered, ordered list of objects from the original collection
     */
    public static <T> List<T> filter(Collection<T> objects, QueryParams queryParams) {
        // create the comparator on a bean property
        // if no ordering in the params the use ID, since we must always order here somehow
        // given that collection does not have any order, paging it would not make sense



        final String orderProp = queryParams.isOrderSet() ? queryParams.getOrder().getField() :
                Constants.Util.ID_FIELD_NAME;
        // ascending is the default direction
        final Order.Direction direction = queryParams.isOrderSet() ? queryParams.getOrder().getDirection() :
                Order.Direction.ASC;

        List<T> result = order(objects, orderProp, direction);

        // paginate if required
        if (queryParams.isPagingSet()) {
            result = paginate(result, queryParams.getPage(), queryParams.getPageSize());
        }

        return result;
    }

    /**
     * Orders the provided collection using the provided ordering information.
     * @param collection the collection to order
     * @param orderProp the name of the property to order on
     * @param direction the ordering direction
     * @param <T> the type of the collection to order
     * @return a new list with ordered objects from the provided collection
     */
    public static <T> List<T> order(Collection<T> collection, String orderProp, Order.Direction direction) {
        Comparator<T> comparator = new BeanComparator<>(orderProp);
        // reverse it if order is descending
        if (direction == Order.Direction.DESC) {
            comparator = new ReverseComparator(comparator);
        }

        // convert to a list and sort it
        List<T> result = new ArrayList<>(collection);
        Collections.sort(result, comparator);

        return result;
    }

    /**
     * Paginates the provided list, using {@link List#subList(int, int)}. If no objects fall into the provided
     * limits (the start index is larger then the list size) an empty list will be returned. If the end index goes
     * outside the list, the result will be capped at the end of the list.
     * @param list the list to paginate
     * @param page the page to return
     * @param pageSize the size of the page
     * @param <T> the type of the collection to paginate
     * @return the sub-list of the provided list, representing the desired page
     */
    public static <T> List<T> paginate(List<T> list, int page, int pageSize) {
        final int startIndex = (page - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, list.size());

        if (startIndex > list.size() - 1) {
            return Collections.emptyList();
        } else {
            return list.subList(startIndex, endIndex);
        }
    }

    private InMemoryQueryFilter() {
    }
}
