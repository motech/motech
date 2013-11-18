package org.motechproject.mds.web.controller;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.ex.FieldNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * The <code>FieldController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on entity fields.
 *
 * @see FieldDto
 * @see EntityDto
 */
@Controller
public class FieldController extends MdsController {

    @RequestMapping(value = "/entities/{entityId}/fields", method = RequestMethod.GET)
    @ResponseBody
    public List<FieldDto> getFields(@PathVariable String entityId) {
        if (null == getExampleData().getEntity(entityId)) {
            throw new EntityNotFoundException();
        }

        return getExampleData().getFields(entityId);
    }

    @RequestMapping(
            value = {
                    "/entities/{entityId}/fields",
                    "/entities/{entityId}/fields/{fieldId}"
            },
            method = RequestMethod.POST
    )
    @ResponseStatus(HttpStatus.OK)
    public void saveField(@PathVariable String entityId, @RequestBody FieldDto field) {
        EntityDto entity = getExampleData().getEntity(entityId);

        if (null == entity) {
            throw new EntityNotFoundException();
        } else if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        getExampleData().addField(field);
    }

    @RequestMapping(value = "/entities/{entityId}/fields/{fieldId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeField(@PathVariable String entityId, @PathVariable String fieldId) {
        EntityDto entity = getExampleData().getEntity(entityId);

        if (null == entity) {
            throw new EntityNotFoundException();
        } else if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        if (!getExampleData().removeField(fieldId)) {
            throw new FieldNotFoundException();
        }
    }

    @RequestMapping(value = "/fields/validation/get/{type}", method = RequestMethod.GET)
    @ResponseBody
    public FieldValidationDto getValidationByType(@PathVariable String type) {
        String fieldType = "mds.field.".concat(type);

        if (fieldType.equals(TypeDto.INTEGER.getDisplayName())) {
            return FieldValidationDto.INTEGER;
        } else if (fieldType.equals(TypeDto.DOUBLE.getDisplayName())) {
            return FieldValidationDto.DECIMAL;
        } else if (fieldType.equals(TypeDto.STRING.getDisplayName())) {
            return FieldValidationDto.STRING;
        }

        return new FieldValidationDto();
    }
}
