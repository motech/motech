package org.motechproject.mds.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.comparator.AvailableTypeDisplayNameComparator;
import org.motechproject.mds.web.matcher.AvailableTypeMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;


/**
 * The <code>AvailableController</code> is the Spring Framework Controller used by view layer for
 * get list of available objects (like a field types).
 *
 * @see AvailableTypeDto
 */
@Controller
public class AvailableController extends MdsController {
    private MessageSource messageSource;

    @Autowired
    public AvailableController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/available/types", method = RequestMethod.GET)
    @ResponseBody
    public SelectResult<AvailableTypeDto> getTypes(SelectData data) {
        List<AvailableTypeDto> list = getExampleData().getTypes();

        CollectionUtils.filter(list, new AvailableTypeMatcher(data.getTerm(), messageSource));
        Collections.sort(list, new AvailableTypeDisplayNameComparator(messageSource));

        return new SelectResult<>(data, list);
    }

}
