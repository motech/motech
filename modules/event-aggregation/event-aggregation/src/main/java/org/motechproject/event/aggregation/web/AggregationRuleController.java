package org.motechproject.event.aggregation.web;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.aggregation.service.AggregationRule;
import org.motechproject.event.aggregation.service.EventAggregationService;
import org.motechproject.event.aggregation.service.impl.AggregationRuleRequest;
import org.motechproject.org.hibernate.validator.ValidatorFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/rules")
public class AggregationRuleController {

    private AllAggregationRules allAggregationRules;
    private Validator validator;

    @Autowired
    private EventAggregationService aggregationService;

    @Autowired
    public AggregationRuleController(AllAggregationRules allAggregationRules) {
        this.allAggregationRules = allAggregationRules;
        validator = ValidatorFactoryBean.getInstance().getValidator();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<? extends AggregationRule> getAllAggregationRules() {
        return allAggregationRules.getAll();
    }

    @RequestMapping(value = "/{ruleName}", method = RequestMethod.GET)
    @ResponseBody
    public AggregationRuleRequest get(@PathVariable String ruleName) {
        return new AggregationRuleMapper().toRequest(allAggregationRules.findByName(ruleName));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleMethodArgumentNotValidException(BadRequestException exception) throws IOException {
        ArrayList<FieldError> errors = new ArrayList<>();
        for (ConstraintViolation violation : exception.getViolations()) {
            errors.add(new FieldError(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return new ObjectMapper().writeValueAsString(errors);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addOrReplace(@RequestBody AggregationRuleRequest aggregationRule) {
        Set<ConstraintViolation<AggregationRuleRequest>> violations = validator.validate(aggregationRule);
        if (violations != null && violations.size() > 0) {
            throw new BadRequestException(violations);
        } else {
            aggregationService.createRule(aggregationRule);
        }
    }

    @RequestMapping(value = "{ruleName}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.CREATED)
    public void delete(@PathVariable String ruleName) {
        allAggregationRules.remove(ruleName);
    }
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class BadRequestException extends RuntimeException {

    private Set<ConstraintViolation<AggregationRuleRequest>> violations;

    public BadRequestException(Set<ConstraintViolation<AggregationRuleRequest>> violations) {
        this.violations = violations;
    }

    public Set<ConstraintViolation<AggregationRuleRequest>> getViolations() {
        return violations;
    }
}

class FieldError {

    @JsonProperty
    private String field;
    @JsonProperty
    private String errorMessage;

    FieldError(String field, String errorMessage) {
        this.field = field;
        this.errorMessage = errorMessage;
    }

    public String getField() {
        return field;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

