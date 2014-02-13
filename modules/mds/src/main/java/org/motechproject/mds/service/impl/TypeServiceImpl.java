package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.Type;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.TypeNotFoundException;
import org.motechproject.mds.repository.AllTypes;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link org.motechproject.mds.service.TypeService} interface
 */
@Service
public class TypeServiceImpl extends BaseMdsService implements TypeService {

    private AllTypes allTypes;

    @Override
    @Transactional
    public List<TypeDto> getAllTypes() {
        List<TypeDto> list = new ArrayList<>();

        for (Type type : allTypes.retrieveAll()) {
            list.add(type.toDto());
        }

        return list;
    }

    @Override
    @Transactional
    public TypeDto findType(Class<?> clazz) {
        Type type = allTypes.retrieveByClassName(clazz.getName());

        if (null != type) {
            return type.toDto();
        } else {
            throw new TypeNotFoundException("Type unavailable: " + clazz.getName());
        }
    }

    @Autowired
    public void setAllTypes(AllTypes allTypes) {
        this.allTypes = allTypes;
    }

}
