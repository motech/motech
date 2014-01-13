package org.motechproject.mds.service.impl;

import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.ex.TypeAlreadyExistsException;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of TypeService interface
 */
@Service
public class TypeServiceImpl extends BaseMdsService implements TypeService {

    private AllFieldTypes allFieldTypes;

    @Override
    @Transactional
    public void createFieldType(AvailableTypeDto type) {
        if (allFieldTypes.typeExists(type)) {
            throw new TypeAlreadyExistsException();
        } else {
            allFieldTypes.save(type);
        }
    }

    @Override
    @Transactional
    public List<AvailableTypeDto> getAllFieldTypes() {
        return allFieldTypes.getAll();
    }

    @Autowired
    public void setAllFieldTypes(AllFieldTypes allFieldTypes) {
        this.allFieldTypes = allFieldTypes;
    }

}
