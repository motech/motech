package org.motechproject.mds.testutil;

import org.motechproject.mds.dto.LookupFieldDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class LookupTestHelper {

    public static List<LookupFieldDto> lookupFieldFromIds(Long... ids) {
        return lookupFieldsFromIds(Arrays.asList(ids));
    }

    public static List<LookupFieldDto> lookupFieldsFromIds(Collection<Long> ids) {
        List<LookupFieldDto> lookupFieldDtos = new ArrayList<>();
        for (Long id : ids) {
            lookupFieldDtos.add(new LookupFieldDto(id, null, LookupFieldDto.Type.VALUE));
        }
        return lookupFieldDtos;
    }

    public static List<LookupFieldDto> lookupFieldsFromNames(String... names) {
        return lookupFieldsFromNames(Arrays.asList(names));
    }

    public static List<LookupFieldDto> lookupFieldsFromNames(Collection<String> names) {
        List<LookupFieldDto> lookupFieldDtos = new ArrayList<>();
        for (String name : names) {
            lookupFieldDtos.add(new LookupFieldDto(null, name, LookupFieldDto.Type.VALUE));
        }
        return lookupFieldDtos;
    }

    private LookupTestHelper() {
    }
}
