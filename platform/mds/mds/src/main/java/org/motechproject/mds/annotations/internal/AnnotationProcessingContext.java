package org.motechproject.mds.annotations.internal;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.type.NoSuchTypeException;
import org.motechproject.mds.util.TypeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationProcessingContext {

    private Map<String, Entity> entitiesByClassName;
    private Map<String, Type> typesByClassName;

    public AnnotationProcessingContext(Collection<Entity> entities, Collection<Type> types) {
        entitiesByClassName = new HashMap<>();
        typesByClassName = new HashMap<>();

        for (Entity entity : entities) {
            entitiesByClassName.put(entity.getClassName(), entity);
        }

        for (Type type : types) {
            typesByClassName.put(type.getTypeClassName(), type);
        }
    }

    public Entity getEntityByClassName(String entityClassName) {
        return entitiesByClassName.get(entityClassName);
    }

    public Type getType(Class clazz) {
        Type type = typesByClassName.get(TypeHelper.getClassNameForType(clazz));
        if (type == null) {
            throw new NoSuchTypeException(clazz.getCanonicalName());
        }
        return type;
    }

    public Type getType(TypeDto typeDto) {
        return typesByClassName.get(typeDto.getTypeClass());
    }

    public Collection<Entity> getAllEntities() {
        return entitiesByClassName.values();
    }

    public List<Entity> findEntitiesByPackage(final String packageName) {
        List<Entity> filtered = new ArrayList<>(entitiesByClassName.values());

        CollectionUtils.filter(filtered, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                Entity entity = (Entity) object;
                return StringUtils.startsWith(entity.getClassName(), packageName);
            }
        });

        return filtered;
    }
}
