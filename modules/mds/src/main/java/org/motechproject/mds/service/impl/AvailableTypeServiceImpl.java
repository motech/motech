package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.AvailableType;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.repository.AllAvailableTypes;
import org.motechproject.mds.service.AvailableTypeService;
import org.motechproject.mds.service.BaseMdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link org.motechproject.mds.service.AvailableTypeService} interface
 */
@Service
public class AvailableTypeServiceImpl extends BaseMdsService implements AvailableTypeService {
    private AllAvailableTypes allAvailableTypes;

    @Override
    public List<AvailableTypeDto> getAll() {
        List<AvailableType> availableTypes = allAvailableTypes.retrieveAll();
        List<AvailableTypeDto> list = new ArrayList<>();

        if (null != availableTypes) {
            for (AvailableType type : availableTypes) {
                list.add(type.toDto());
            }
        }

        return list;
    }

    @Autowired
    public void setAllAvailableTypes(AllAvailableTypes allAvailableTypes) {
        this.allAvailableTypes = allAvailableTypes;
    }
}
