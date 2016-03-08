package org.motechproject.mds.query;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;
import org.springframework.util.comparator.CompoundComparator;

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
        // if no ordering in the params the use ID, since we must always order here somehow
        // given that collection does not have any order, paging it would not make sense
        List<Order> orderList = new ArrayList<>(queryParams.getOrderList());
        // we always add the order on the ID field as the final one, to keep the results consistent
        if (!queryParams.containsOrderOnField(Constants.Util.ID_FIELD_NAME)) {
            orderList.add(new Order(Constants.Util.ID_FIELD_NAME, Order.Direction.ASC));
        }

        List<T> result = order(objects, orderList);

        // paginate if required
        if (queryParams.isPagingSet()) {
            result = paginate(result, queryParams.getPage(), queryParams.getPageSize());
        }

        return result;
    }

    /**
     * Orders the provided collection using the provided ordering information.
     * @param collection the collection to order
     * @param orderList list of orders that should be applied to the collection
     * @param <T> the type of the collection to order
     * @return a new list with ordered objects from the provided collection
     */
    private static <T> List<T> order(Collection<T> collection, List<Order> orderList) {
        List<Comparator<T>> comparatorList = new ArrayList<>();

        for (Order order : orderList) {
            Comparator<T> comparator = new BeanComparator<>(order.getField(), new NullComparator());

            // reverse it if order is descending
            if (order.getDirection() == Order.Direction.DESC) {
                comparator = new ReverseComparator(comparator);
            }

            comparatorList.add(comparator);
        }

        // we use a compound comparator to chain comparators for each provided order
        CompoundComparator<T> compoundComparator = new CompoundComparator<>(comparatorList.toArray(
                new Comparator[comparatorList.size()]));

        // convert to a list and sort it
        List<T> result = new ArrayList<>(collection);
        Collections.sort(result, compoundComparator);

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
    private static <T> List<T> paginate(List<T> list, int page, int pageSize) {
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
