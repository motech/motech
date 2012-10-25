package org.motechproject.event.aggregation.web;

import org.motechproject.event.aggregation.model.Aggregation;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static java.lang.String.format;

@Controller
@RequestMapping("/aggregations")
public class AggregationController {

    @Autowired
    private AllAggregatedEvents allAggregatedEvents;

    @RequestMapping(value = "{ruleName}/{eventStatus}", method = RequestMethod.GET)
    @ResponseBody
    public List<? extends Aggregation> getAggregations(@PathVariable String ruleName, @PathVariable String eventStatus) throws ServletRequestBindingException {
        if (eventStatus.equals("valid")) {
            return allAggregatedEvents.findAllAggregations(ruleName);
        } else if (eventStatus.equals("invalid")) {
            return allAggregatedEvents.findAllErrorEventsForAggregations(ruleName);
        } else {
            throw new ServletRequestBindingException(format("[%s] is not a valid event status", eventStatus));
        }
    }
}
