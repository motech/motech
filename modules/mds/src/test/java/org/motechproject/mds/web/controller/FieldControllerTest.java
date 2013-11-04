package org.motechproject.mds.web.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.ex.FieldNotFoundException;
import org.motechproject.mds.web.ExampleData;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.dto.TypeDto.INTEGER;
import static org.motechproject.mds.dto.TypeDto.STRING;

public class FieldControllerTest {
    private FieldController controller = new FieldController();

    @Before
    public void setUp() throws Exception {
        FieldController.setExampleData(new ExampleData());
    }

    @Test
    public void shouldGetAllEntityFields() throws Exception {
        List<FieldDto> expected = new ArrayList<>();
        expected.add(
                new FieldDto(
                        "2", "7", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null)
                )
        );
        expected.add(
                new FieldDto(
                        "3", "7", STRING,
                        new FieldBasicDto("Drug Regimen", "regimen")
                )
        );
        expected.add(
                new FieldDto(
                        "4", "7", INTEGER,
                        new FieldBasicDto("Voucher Number", "voucherNumber")
                )
        );
        expected.add(
                new FieldDto(
                        "5", "7", STRING,
                        new FieldBasicDto("Redeemed By", "redeemedBy")
                )
        );

        assertEquals(expected, controller.getFields("7"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfEntityNotExistWhenGetEntityFields() throws Exception {
        controller.getFields("14");
    }

    @Test
    public void shouldSaveField() throws Exception {
        FieldDto dto = new FieldDto("10", "7", STRING, null);
        controller.saveField("7", dto);

        List<FieldDto> expected = new ArrayList<>();
        expected.add(
                new FieldDto(
                        "2", "7", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null)
                )
        );
        expected.add(
                new FieldDto(
                        "3", "7", STRING,
                        new FieldBasicDto("Drug Regimen", "regimen")
                )
        );
        expected.add(
                new FieldDto(
                        "4", "7", INTEGER,
                        new FieldBasicDto("Voucher Number", "voucherNumber")
                )
        );
        expected.add(
                new FieldDto(
                        "5", "7", STRING,
                        new FieldBasicDto("Redeemed By", "redeemedBy")
                )
        );
        expected.add(dto);

        assertEquals(expected, controller.getFields("7"));
    }

    @Test
    public void shouldUpdateField() throws Exception {
        FieldDto dto = new FieldDto("3", "7", STRING, new FieldBasicDto("drugRegimen", "regimen"));
        controller.saveField("7", dto);

        List<FieldDto> expected = new ArrayList<>();
        expected.add(
                new FieldDto(
                        "2", "7", STRING,
                        new FieldBasicDto("ID", "ID", false, "pass", null)
                )
        );
        expected.add(dto);
        expected.add(
                new FieldDto(
                        "4", "7", INTEGER,
                        new FieldBasicDto("Voucher Number", "voucherNumber")
                )
        );
        expected.add(
                new FieldDto(
                        "5", "7", STRING,
                        new FieldBasicDto("Redeemed By", "redeemedBy")
                )
        );
        assertEquals(expected, controller.getFields("7"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfEntityNotExistWhenSaveOrUpdateFields() throws Exception {
        controller.saveField("15", null);
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldThrowExceptionIfEntityIsReadOnlyWhenSaveOrUpdateFields() throws Exception {
        controller.saveField("3", null);
    }

    @Test
    public void shouldRemoveField() throws Exception {
        List<FieldDto> expected = new ArrayList<>();
        expected.add(
                new FieldDto(
                        "3", "7", STRING,
                        new FieldBasicDto("Drug Regimen", "regimen")
                )
        );
        expected.add(
                new FieldDto(
                        "4", "7", INTEGER,
                        new FieldBasicDto("Voucher Number", "voucherNumber")
                )
        );
        expected.add(
                new FieldDto(
                        "5", "7", STRING,
                        new FieldBasicDto("Redeemed By", "redeemedBy")
                )
        );

        controller.removeField("7", "2");
        assertEquals(expected, controller.getFields("7"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionIfEntityNotExistWhenRemoveField() throws Exception {
        controller.removeField("15", null);
    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldThrowExceptionIfEntityIsReadOnlyWhenRemoveField() throws Exception {
        controller.removeField("3", null);
    }

    @Test(expected = FieldNotFoundException.class)
    public void shouldThrowExceptionIfFieldNotFoundWhenRemoveField() throws Exception {
        controller.removeField("7", "16");
    }

}
