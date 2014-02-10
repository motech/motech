package org.motechproject.mds.web.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.service.AvailableTypeService;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.comparator.AvailableTypeDisplayNameComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AvailableControllerIT extends BaseIT {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AvailableTypeService availableTypeService;

    private AvailableController controller;

    @Before
    public void setUp() throws Exception {
        clearDB();
        controller = new AvailableController(messageSource, availableTypeService);
    }

    @After
    public void tearDown() {
        clearDB();
    }

    @Test
    public void shouldReturnAllAvailableTypes() throws Exception {
        List<AvailableTypeDto> expected = availableTypeService.getAll();

        Collections.sort(expected, new AvailableTypeDisplayNameComparator(messageSource));

        SelectData data = new SelectData("", 1, expected.size());
        SelectResult<AvailableTypeDto> result = controller.getTypes(data);
        assertEquals(expected, result.getResults());
    }

}
