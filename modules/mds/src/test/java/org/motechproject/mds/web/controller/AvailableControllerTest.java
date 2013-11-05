package org.motechproject.mds.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.comparator.AvailableTypeDisplayNameComparator;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mds.dto.SettingOptions.POSITIVE;
import static org.motechproject.mds.dto.SettingOptions.REQUIRE;
import static org.motechproject.mds.dto.TypeDto.BOOLEAN;
import static org.motechproject.mds.dto.TypeDto.DATE;
import static org.motechproject.mds.dto.TypeDto.DATETIME;
import static org.motechproject.mds.dto.TypeDto.DOUBLE;
import static org.motechproject.mds.dto.TypeDto.INTEGER;
import static org.motechproject.mds.dto.TypeDto.LIST;
import static org.motechproject.mds.dto.TypeDto.STRING;
import static org.motechproject.mds.dto.TypeDto.TIME;

public class AvailableControllerTest {
    @Mock
    private MessageSource messageSource;

    private AvailableController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        controller = new AvailableController(messageSource);
    }

    @Test
    public void shouldReturnAllAvailabletypes() throws Exception {
        List<AvailableTypeDto> expected = new ArrayList<>();
        expected.add(new AvailableTypeDto("1", "int", INTEGER));
        expected.add(new AvailableTypeDto("2", "str", STRING));
        expected.add(new AvailableTypeDto("3", "bool", BOOLEAN));
        expected.add(new AvailableTypeDto("4", "date", DATE));
        expected.add(new AvailableTypeDto("5", "time", TIME));
        expected.add(new AvailableTypeDto("6", "datetime", DATETIME));
        expected.add(
                new AvailableTypeDto(
                        "7", "decimal", DOUBLE,
                        new SettingDto("mds.form.label.precision", 9, INTEGER, REQUIRE, POSITIVE),
                        new SettingDto("mds.form.label.scale", 2, INTEGER, REQUIRE, POSITIVE)
                )
        );
        expected.add(
                new AvailableTypeDto(
                        "8", "list", LIST,
                        new SettingDto("mds.form.label.values", new LinkedList<>(), LIST, REQUIRE),
                        new SettingDto("mds.form.label.allowUserSupplied", false, BOOLEAN),
                        new SettingDto("mds.form.label.allowMultipleSelections", false, BOOLEAN)
                )
        );

        for (AvailableTypeDto dto : expected) {
            String displayName = dto.getType().getDisplayName();

            when(messageSource.getMessage(displayName, null, null)).thenReturn(displayName);
        }

        Collections.sort(expected, new AvailableTypeDisplayNameComparator(messageSource));

        SelectData data = new SelectData("", 1, expected.size());
        SelectResult<AvailableTypeDto> result = controller.getTypes(data);
        assertEquals(expected, result.getResults());
    }

}
