package org.motechproject.event.aggregation.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.event.aggregation.model.AggregatedEventRecord;
import org.motechproject.event.aggregation.model.Aggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ektorp.ComplexKey.emptyObject;

@Repository
public class AllAggregatedEvents extends MotechBaseRepository<AggregatedEventRecord> {

    @Autowired
    public AllAggregatedEvents(@Qualifier("eventAggregationDbConnector") CouchDbConnector db) {
        super(AggregatedEventRecord.class, db);
    }

    private static final String FIND =
        "function(doc) {                                                                                      \n" +
        "   if (doc.type === 'AggregatedEvent') {                                                             \n" +
        "       emit([doc.aggregationRuleName, doc.aggregationParams, doc.nonAggregationParams], doc._id);    \n" +
        "   }                                                                                                 \n" +
        "}";
    @View(name = "by_rule_and_event_params", map = FIND)
    public AggregatedEventRecord find(String aggregationRuleName, Map<String, Object> aggregationParams, Map<String, Object> nonAggregationParams) {
        return singleResult(queryView("by_rule_and_event_params", ComplexKey.of(aggregationRuleName, aggregationParams, nonAggregationParams)));
    }

    public void addIfAbsent(AggregatedEventRecord aggregatedEvent) {
        AggregatedEventRecord dbRecord = find(aggregatedEvent.getAggregationRuleName(), aggregatedEvent.getAggregationParams(), aggregatedEvent.getNonAggregationParams());
        if (dbRecord == null) {
            add(aggregatedEvent);
        }
    }

    private static final String BY_ERROR_STATE =
        "function(doc) {                                                           \n" +
            "   if (doc.type === 'AggregatedEvent') {                              \n" +
            "       emit([doc.aggregationRuleName, doc.hasError], doc._id);        \n" +
            "   }                                                                  \n" +
            "}";
    @View(name = "by_error_state", map = BY_ERROR_STATE)
    public List<AggregatedEventRecord> findAllByErrorState(String aggregationRuleName, boolean hasError) {
        return queryView("by_error_state", ComplexKey.of(aggregationRuleName, hasError));
    }

    public List<AggregatedEventRecord> findAllAggregated(String aggregationRuleName) {
        return findAllByErrorState(aggregationRuleName, false);
    }

    public List<AggregatedEventRecord> findAllErrored(String aggregationRuleName) {
        return findAllByErrorState(aggregationRuleName, true);
    }

    private static final String VALID_EVENTS_BY_AGGREGATION_FIELDS =
        "function(doc) {                                                                                                 \n" +
    "   if (doc.type === 'AggregatedEvent' && doc.hasError === false) {                                                  \n" +
        "       emit([doc.aggregationRuleName, [doc.aggregationParams]], [doc.nonAggregationParams, doc.timeStamp]);     \n" +
        "   }                                                                                                            \n" +
        "}";
    private static final String ERROR_EVENTS_BY_AGGREGATION_FIELDS =
        "function(doc) {                                                                                                 \n" +
    "   if (doc.type === 'AggregatedEvent' && doc.hasError === true) {                                                   \n" +
        "       emit([doc.aggregationRuleName, [doc.aggregationParams]], [doc.nonAggregationParams, doc.timeStamp]);     \n" +
        "   }                                                                                                            \n" +
        "}";
    private static final String GROUP =
        "function(keys, values, rereduce) {                                 \n" +
        "   if (rereduce) {                                                 \n" +
        "      var events = [];                                             \n" +
        "      for (var i = 0; i < values.length; i++) {                    \n" +
        "         events.push.apply(events, values[i].events);              \n" +
        "      }                                                            \n" +
        "      return {                                                     \n" +
        "         \"aggregationRuleName\": values[0].aggregationRuleName,   \n" +
        "         \"events\": events                                        \n" +
        "      };                                                           \n" +
        "   }                                                               \n" +
        "   var events = [];                                                \n" +
        "   for (var i = 0; i < values.length; i++) {                       \n" +
        "       events[i] = {                                               \n" +
        "           \"aggregationParams\"    : keys[0][0][1][0],            \n" +
        "           \"nonAggregationParams\" : values[i][0],                \n" +
        "           \"timeStamp\"            : values[i][1]                 \n" +
        "       };                                                          \n" +
        "   }                                                               \n" +
        "   return {                                                        \n" +
        "       \"aggregationRuleName\" : keys[0][0][0],                    \n" +
        "       \"events\"              : events                            \n" +
        "   }                                                               \n" +
        "}";
    @View(name = "valid_events_by_aggregation_fields", map = VALID_EVENTS_BY_AGGREGATION_FIELDS, reduce = GROUP)
    public List<Aggregation> findAllAggregations(String aggregationRuleName) {
        return db.queryView(createQuery("valid_events_by_aggregation_fields")
            .startKey(ComplexKey.of(aggregationRuleName, null))
            .endKey(ComplexKey.of(aggregationRuleName, emptyObject()))
            .group(true), Aggregation.class);
    }

    @View(name = "error_events_by_aggregation_fields", map = ERROR_EVENTS_BY_AGGREGATION_FIELDS, reduce = GROUP)
    public List<Aggregation> findAllErrorEventsForAggregations(String aggregationRuleName) {
        return db.queryView(createQuery("error_events_by_aggregation_fields")
            .startKey(ComplexKey.of(aggregationRuleName, null))
            .endKey(ComplexKey.of(aggregationRuleName, emptyObject()))
            .group(true), Aggregation.class);
    }

    private static final String FIND_ALL_BY_AGGREGATION_RULE =
        "function(doc) {                                       \n" +
            "   if (doc.type === 'AggregatedEvent') {          \n" +
            "       emit(doc.aggregationRuleName, doc._id);    \n" +
            "   }                                              \n" +
            "}";
    @View(name = "by_aggregation_rule", map = FIND_ALL_BY_AGGREGATION_RULE)
    public List<AggregatedEventRecord> findByAggregationRule(String aggregationRuleName) {
        return queryView("by_aggregation_rule", aggregationRuleName);
    }

    public void removeByAggregationRule(String aggregationRule) {
        List<BulkDeleteDocument> docs = new ArrayList<>();
        for (AggregatedEventRecord aggregatedEvent : findByAggregationRule(aggregationRule)) {
            docs.add(BulkDeleteDocument.of(aggregatedEvent));
        }
        db.executeBulk(docs);
    }
}
