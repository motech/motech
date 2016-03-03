package org.motechproject.mds.testutil;

import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.util.LookupName;

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
            lookupFieldDtos.add(new LookupFieldDto(id, null, LookupFieldType.VALUE));
        }
        return lookupFieldDtos;
    }

    public static List<LookupFieldDto> lookupFieldsFromNames(String... names) {
        return lookupFieldsFromNames(Arrays.asList(names));
    }

    public static List<LookupFieldDto> lookupFieldsFromNames(Collection<String> names) {
        List<LookupFieldDto> lookupFieldDtos = new ArrayList<>();
        for (String name : names) {
            LookupFieldDto lookupFieldDto = new LookupFieldDto(null, LookupName.getFieldName(name), LookupFieldType.VALUE);
            lookupFieldDto.setRelatedName(LookupName.getRelatedFieldName(name));
            lookupFieldDtos.add(lookupFieldDto);
        }
        return lookupFieldDtos;
    }

    public static LookupDto findByName(List<LookupDto> lookups, String name) {
        for (LookupDto lookup : lookups) {
            if (name.equals(lookup.getLookupName())) {
                return lookup;
            }
        }
        return null;
    }

    private LookupTestHelper() {
    }
}
