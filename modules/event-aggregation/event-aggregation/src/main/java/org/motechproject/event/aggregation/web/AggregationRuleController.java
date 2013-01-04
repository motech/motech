package org.motechproject.event.aggregation.web;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.validator.ValidatorFactoryBean;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.aggregation.service.AggregationRule;
import org.motechproject.event.aggregation.service.EventAggregationService;
import org.motechproject.event.aggregation.service.AggregationRuleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleMethodArgumentNotValidException(Exception exception) throws IOException {
        ArrayList<RequestError> errors = new ArrayList<>();
        if (exception instanceof BadRequestException) {
            for (ConstraintViolation violation : ((BadRequestException) exception).getViolations()) {
                errors.add(new FieldError(violation.getPropertyPath().toString(), violation.getMessage()));
            }
        } else {
            errors.add(new RequestError(exception.getMessage()));
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

class RequestError {

    @JsonProperty
    private String errorMessage;

    RequestError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

class FieldError extends RequestError {

    @JsonProperty
    private String field;

    FieldError(String field, String errorMessage) {
        super(errorMessage);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
