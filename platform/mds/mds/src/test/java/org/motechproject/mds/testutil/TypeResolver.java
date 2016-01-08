package org.motechproject.mds.testutil;

import org.motechproject.mds.domain.Type;
import org.motechproject.mds.dto.TypeDto;

import java.util.HashMap;
import java.util.Map;

public final class TypeResolver {

    private TypeResolver() {
    }

    private static Map<String, Type> TYPES;
    static {
        TYPES = new HashMap<>();
        mapType(TypeDto.PERIOD);
        mapType(TypeDto.MAP);
        mapType(TypeDto.BLOB);
        mapType(TypeDto.INTEGER);
        mapType(TypeDto.STRING);
        mapType(TypeDto.BOOLEAN);
        mapType(TypeDto.DATE);
        mapType(TypeDto.TIME);
        mapType(TypeDto.DATETIME);
        mapType(TypeDto.DOUBLE);
        mapType(TypeDto.COLLECTION);
        mapType(TypeDto.LOCAL_DATE);
        mapType(TypeDto.LONG);
        mapType(TypeDto.ONE_TO_ONE_RELATIONSHIP);
        mapType(TypeDto.ONE_TO_MANY_RELATIONSHIP);
        mapType(TypeDto.MANY_TO_ONE_RELATIONSHIP);
        mapType(TypeDto.MANY_TO_MANY_RELATIONSHIP);
    }

    private static Type mapType(TypeDto type) {
        return TYPES.put(type.getDisplayName(), toType(type));
    }

    private static Type toType(TypeDto typeDto) {
        try {
            return new Type(typeDto.getDisplayName(), typeDto.getDescription(), Class.forName(typeDto.getTypeClass()));
        } catch (ClassNotFoundException e) {
            return new Type(typeDto.getDisplayName(), typeDto.getDescription(), Object.class);
        }
    }

    public static Type resolve(String type) {
        return TYPES.get(type);
    }
}
