package org.motechproject.email.search;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.mds.query.MatchesProperty;
import org.motechproject.mds.query.Property;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.query.QueryUtil;
import org.motechproject.mds.query.RangeProperty;
import org.motechproject.mds.query.RestrictionProperty;
import org.motechproject.mds.query.SetProperty;
import org.motechproject.mds.util.InstanceSecurityRestriction;
import org.motechproject.mds.util.SecurityUtil;

import javax.jdo.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is an abstract base for searches done in the email module.
 * This abstract base prepares the query for its implementations by setting
 * three conditions
 * @param <T>
 */
public abstract class AbstractSearchExecution<T> implements QueryExecution<T> {

    private final EmailRecordSearchCriteria criteria;

    public AbstractSearchExecution(EmailRecordSearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public T execute(Query query, InstanceSecurityRestriction restriction) {
        List<Property> properties = new ArrayList<>();

        Range<LocalDateTime> deliveryTimeRange = criteria.getDeliveryTimeRange();
        boolean dateRangeUsed = isUsableRange(deliveryTimeRange);
        if (dateRangeUsed) {
            properties.add(new RangeProperty<>("deliveryTime", deliveryTimeRange, LocalDateTime.class.getName()));
        }

        Set<DeliveryStatus> deliveryStatuses = criteria.getDeliveryStatuses();
        if (deliveryStatuses.isEmpty()) {
            deliveryStatuses = new HashSet<>(Arrays.asList(DeliveryStatus.values()));
        }
        properties.add(new SetProperty<>("deliveryStatus", deliveryStatuses, DeliveryStatus.class.getName()));

        StringBuilder queryBuilder = new StringBuilder(initialQuery(dateRangeUsed));
        int initialLength = queryBuilder.length();

        if (StringUtils.isNotEmpty(criteria.getToAddress())) {
            properties.add(new MatchesProperty("toAddress", criteria.getToAddress()));
            extendQueryWithOrClause(queryBuilder, initialLength);
        }
        if (StringUtils.isNotEmpty(criteria.getFromAddress())) {
            properties.add(new MatchesProperty("fromAddress", criteria.getFromAddress()));
            extendQueryWithOrClause(queryBuilder, initialLength);
        }
        if (StringUtils.isNotEmpty(criteria.getMessage())) {
            properties.add(new MatchesProperty("message", criteria.getMessage()));
            extendQueryWithOrClause(queryBuilder, initialLength);
        }
        if (StringUtils.isNotEmpty(criteria.getSubject())) {
            properties.add(new MatchesProperty("subject", criteria.getSubject()));
            extendQueryWithOrClause(queryBuilder, initialLength);
        }

        closeQuery(queryBuilder, initialLength);

        if (restriction != null && !restriction.isEmpty()) {
            properties.add(new RestrictionProperty(restriction, SecurityUtil.getUsername()));
            queryBuilder.append(" && %s");
        }

        QueryUtil.useFilterFromPattern(query, queryBuilder.toString(), properties);

        return execute(query, properties);
    }

    protected abstract T execute(Query query, List<Property> properties);

    protected String initialQuery(boolean rangeUsed) {
        return rangeUsed ? "%s && %s" : "%s";
    }

    protected void extendQueryWithOrClause(StringBuilder queryBuilder, int initialLength) {
        if (queryBuilder.length() == initialLength) {
            queryBuilder.append(" && (%s");
        } else {
            queryBuilder.append(" || %s");
        }
    }

    protected void closeQuery(StringBuilder queryBuilder, int initialLength) {
        if (queryBuilder.length() != initialLength) {
            queryBuilder.append(')');
        }
    }

    protected EmailRecordSearchCriteria getCriteria() {
        return criteria;
    }

    protected boolean isUsableRange(Range range) {
        return range != null && (range.getMin() != null || range.getMax() != null);
    }
}
