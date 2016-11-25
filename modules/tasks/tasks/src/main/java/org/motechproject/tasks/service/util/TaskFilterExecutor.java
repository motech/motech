package org.motechproject.tasks.service.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.enums.LogicalOperator;
import org.motechproject.tasks.domain.mds.task.OperatorType;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.constants.TaskFailureCause;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.motechproject.tasks.domain.KeyInformation.parse;

/**
 * The <code>TaskFilterExecutor</code> applies a list of filters in a #{@link TaskContext}.
 * <p/>
 * <ul>
 * <li><b>convertTo</b> - convert a given value to a correct type,</li>
 * <li><b>getFieldValue</b> - get value of a field defined in the key from the given object,</li>
 * <li><b>getTriggerKey</b> - get value of a trigger event parameter,</li>
 * <li><b>checkFilters</b> - executed defined filters for a task,</li>
 * <li><b>manipulate</b> - executed the given manipulation on the given string value.</li>
 * </ul>
 * <p/>
 */
public class TaskFilterExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskFilterExecutor.class);

    /**
     * Default constructor.
     */
    public TaskFilterExecutor() {
    }

    /**
     * Checks whether task with the given context matches the given filters.
     *
     * @param filters  the filters, null returns true
     * @param logicalOperator  the logical operator
     * @param taskContext  the task context, not null
     * @return true if the task matches the filters
     * @throws TaskHandlerException if there were problems while handling task
     */
    public boolean checkFilters(List<Filter> filters, LogicalOperator logicalOperator, TaskContext taskContext)
            throws TaskHandlerException {
        LOGGER.debug("Checking if task: {} matches the filters", taskContext.getTask().getName());
        Map<String, Object> parameters = taskContext.getTriggerParameters();
        if (isEmpty(filters) || parameters == null) {
            return true;
        }

        boolean filterCheck = false;
        for (Filter filter : filters) {
            KeyInformation key = parse(filter.getKey());
            Object value;
            try {
                KeyEvaluator keyEvaluator = new KeyEvaluator(taskContext);
                value = keyEvaluator.getManipulatedValue(key);
            } catch (TaskHandlerException e) {
                if (TaskFailureCause.DATA_SOURCE.equals(e.getFailureCause())) {
                    throw e;    // data source lookups disable the task
                }

                value = null;   // trigger parameter lookups don't disable the task
                LOGGER.error("Unable to retrieve value for filter", e);
            } catch (RuntimeException e) {
                value = null;
                LOGGER.error("Unable to retrieve value for filter", e);
            }

            filterCheck = value != null && checkValue(filter, value);

            if (!filter.isNegationOperator()) {
                filterCheck = !filterCheck;
            }

            LOGGER.debug("Result of checking filter: {} for task: {} is: {}", filter.getDisplayName(), taskContext.getTask().getName(), filterCheck);

            if (isFilterConditionFulfilled(filterCheck, logicalOperator)) {
                LOGGER.debug("Filters condition is fulfilled, because logicalOperator is: {} and filters checking has already: {} value", logicalOperator, filterCheck);
                break;
            }
        }

        LOGGER.info("Result of checking filters for task: {} is: {}", taskContext.getTask().getName(), filterCheck);
        return filterCheck;
    }

    private boolean isFilterConditionFulfilled(boolean filterCheck, LogicalOperator logicalOperator) {
        return (logicalOperator == LogicalOperator.AND && !filterCheck) ||
                (logicalOperator == LogicalOperator.OR && filterCheck);
    }

    private boolean checkValue(Filter filter, Object value) {
        ParameterType type = filter.getType();
        boolean filterCheck;

        if (type.isString()) {
            filterCheck = checkFilterForString(filter, value.toString());
        } else if (type.isNumber()) {
            filterCheck = checkFilterForNumber(filter, new BigDecimal(value.toString()));
        } else if (type == ParameterType.DATE) {
            filterCheck = checkFilterForDate(filter, DateTime.parse(value.toString()));
        } else if (type == ParameterType.BOOLEAN) {
            filterCheck = checkFilterForBoolean(filter, Boolean.parseBoolean(value.toString()));
        } else {
            filterCheck = false;
        }

        return filterCheck;
    }

    private boolean checkFilterForString(Filter filter, String param) {
        OperatorType operatorType = OperatorType.fromString(filter.getOperator());
        String expression = filter.getExpression();
        boolean result = false;

        if (operatorType != null) {
            switch (operatorType) {
                case EQUALS:
                    result = param.equals(expression);
                    break;
                case CONTAINS:
                    result = param.contains(expression);
                    break;
                case EXIST:
                    result = StringUtils.isNotEmpty(param);
                    break;
                case STARTSWITH:
                    result = param.startsWith(expression);
                    break;
                case ENDSWITH:
                    result = param.endsWith(expression);
                    break;
                case EQUALS_IGNORE_CASE:
                    result = param.equalsIgnoreCase(expression);
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private boolean checkFilterForNumber(Filter filter, BigDecimal param) {
        OperatorType operatorType = OperatorType.fromString(filter.getOperator());
        boolean result = false;
        int compare;

        if (operatorType == null || operatorType == OperatorType.EXIST) {
            compare = 0;
        } else {
            compare = param.compareTo(new BigDecimal(filter.getExpression()));
        }

        if (operatorType != null) {
            switch (operatorType) {
                case EQ_NUMBER:
                    result = compare == 0;
                    break;
                case GT:
                    result = compare == 1;
                    break;
                case LT:
                    result = compare == -1;
                    break;
                case EXIST:
                    result = true;
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private boolean checkFilterForBoolean(Filter filter, Boolean param) {
        OperatorType operatorType = OperatorType.fromString(filter.getOperator());
        boolean result = false;
        boolean expressionValue = Boolean.parseBoolean(filter.getExpression());

        if (operatorType != null) {
            switch (operatorType) {
                case IS_TRUE:
                    result = param;
                    break;
                case AND:
                    result = param && expressionValue;
                    break;
                case OR:
                    result = param || expressionValue;
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private boolean checkFilterForDate(Filter filter, DateTime param) {
        return OperatorType.needExpression(filter.getOperator())
                ? checkFilterForDate(param, filter.getOperator(), filter.getExpression())
                : checkFilterForDate(param, filter.getOperator());
    }

    private boolean checkFilterForDate(DateTime param, String operator) {
        OperatorType operatorType = OperatorType.fromString(operator);
        boolean result = false;

        if (operatorType != null) {
            switch (operatorType) {
                case EXIST:
                    result = true;
                    break;
                case AFTER_NOW:
                    result = param.isAfterNow();
                    break;
                case BEFORE_NOW:
                    result = param.isBeforeNow();
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private boolean checkFilterForDate(DateTime param, String operator, String expression) {
        OperatorType operatorType = OperatorType.fromString(operator);
        boolean result = false;

        if (operatorType != null) {
            switch (operatorType) {
                case EQUALS:
                    result = param.isEqual(DateTime.parse(expression));
                    break;
                case AFTER:
                    result = param.isAfter(DateTime.parse(expression));
                    break;
                case BEFORE:
                    result = param.isBefore(DateTime.parse(expression));
                    break;
                case LESS_DAYS_FROM_NOW:
                    result = countNumberOfDays(param) < Integer.valueOf(expression);
                    break;
                case LESS_MONTHS_FROM_NOW:
                    result = countNumberOfMonths(param) < Integer.valueOf(expression);
                    break;
                case MORE_DAYS_FROM_NOW:
                    result = countNumberOfDays(param) > Integer.valueOf(expression);
                    break;
                case MORE_MONTHS_FROM_NOW:
                    result = countNumberOfMonths(param) > Integer.valueOf(expression);
                    break;
                default:
                    result = false;
            }
        }

        return result;
    }

    private int countNumberOfDays(DateTime param) {
        return param.isBeforeNow()
                ? Days.daysBetween(param, DateUtil.now()).getDays()
                : Days.daysBetween(DateUtil.now(), param).getDays();
    }

    private int countNumberOfMonths(DateTime param) {
        return param.isBeforeNow()
                ? Months.monthsBetween(param, DateUtil.now()).getMonths()
                : Months.monthsBetween(DateUtil.now(), param).getMonths();
    }
}
