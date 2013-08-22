package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.ParameterType;
import org.motechproject.tasks.events.constants.TaskFailureCause;
import org.motechproject.tasks.ex.TaskHandlerException;

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

    public boolean checkFilters(List<Filter> filters, TaskContext taskContext) throws TaskHandlerException {
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
                value = keyEvaluator.getValue(key);
            } catch (TaskHandlerException e) {
                if (TaskFailureCause.DATA_SOURCE.equals(e.getFailureCause())) {
                    throw e;    // data source lookups disable the task
                }
                value = null;   // trigger parameter lookups don't disable the task
            } catch (Exception e) {
                value = null;
            }

            filterCheck = value != null && checkValue(filter, value);

            if (!filter.isNegationOperator()) {
                filterCheck = !filterCheck;
            }

            if (!filterCheck) {
                break;
            }
        }

        return filterCheck;
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
                    result = true;
                    break;
                case STARTSWITH:
                    result = param.startsWith(expression);
                    break;
                case ENDSWITH:
                    result = param.endsWith(expression);
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
                case EQUALS:
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
