package org.motechproject.mds.web.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.comparator.AvailableTypeDisplayNameComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.DATE;
import static org.motechproject.mds.dto.TypeDto.DATETIME;
import static org.motechproject.mds.dto.TypeDto.DOUBLE;
import static org.motechproject.mds.dto.TypeDto.INTEGER;
import static org.motechproject.mds.dto.TypeDto.LIST;
import static org.motechproject.mds.dto.TypeDto.STRING;
import static org.motechproject.mds.dto.TypeDto.TIME;

public class AvailableControllerIT extends BaseIT {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TypeService typeService;

    @Autowired
    private AllFieldTypes allFieldTypes;

    private AvailableController controller;

    @Before
    public void setUp() throws Exception {
        clearDB();
        controller = new AvailableController(messageSource, typeService);
        for (AvailableTypeDto type : getAllAvailableTypes()) {
            allFieldTypes.save(type);
        }
    }

    @After
    public void tearDown() {
        clearDB();
    }

    @Test
    public void shouldReturnAllAvailableTypes() throws Exception {
        List<AvailableTypeDto> expected = getAllAvailableTypes();

        Collections.sort(expected, new AvailableTypeDisplayNameComparator(messageSource));

        SelectData data = new SelectData("", 1, expected.size());
        SelectResult<AvailableTypeDto> result = controller.getTypes(data);
        assertEquals(expected, result.getResults());
    }

    private List<AvailableTypeDto> getAllAvailableTypes() {
        List<AvailableTypeDto> allTypes = new ArrayList<>();
        allTypes.add(new AvailableTypeDto(1L, "int", INTEGER));
        allTypes.add(new AvailableTypeDto(2L, "string", STRING));
        allTypes.add(new AvailableTypeDto(3L, "bool", BOOLEAN));
        allTypes.add(new AvailableTypeDto(4L, "date", DATE));
        allTypes.add(new AvailableTypeDto(5L, "time", TIME));
        allTypes.add(new AvailableTypeDto(6L, "dateTime", DATETIME));
        allTypes.add(new AvailableTypeDto(7L, "double", DOUBLE));
        allTypes.add(new AvailableTypeDto(8L, "list", LIST));

        return allTypes;
    }
}
