package org.motechproject.mds.helper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.metadata.MemberMetadata;
import javax.jdo.metadata.TypeMetadata;

@Component
public class MetadataHelper {

    @Autowired
    private PersistenceManagerFactory persistenceManagerFactory;

    public String getComboboxTableName(Entity entity, Field cbField) {
        final String entityClassName = entity.getClassName();
        final String cbFieldName = cbField.getName();

        TypeMetadata typeMetadata = persistenceManagerFactory.getMetadata(entityClassName);
        if (typeMetadata == null) {
            throw new IllegalArgumentException("No type metadata found for " + entityClassName);
        }

        MemberMetadata cbMetadata = findMemberMetadata(typeMetadata, cbFieldName);
        if (cbMetadata == null) {
            throw new IllegalArgumentException("No member metadata for field " + cbFieldName + " in "
                    + entityClassName);
        }

        String cbTableName = cbMetadata.getTable();

        if (cbTableName == null) {
            String entityTable = getEntityTableName(typeMetadata, entity);
            cbTableName = ClassTableName.getTableName(entityTable, cbFieldName);
        }

        return cbTableName;
    }

    private MemberMetadata findMemberMetadata(TypeMetadata typeMetadata, String memberName) {
        MemberMetadata[] members = typeMetadata.getMembers();
        if (members != null) {
            for (MemberMetadata member : members) {
                if (StringUtils.equals(member.getName(), memberName)) {
                    return member;
                }
            }
        }
        return null;
    }

    private String getEntityTableName(TypeMetadata typeMetadata, Entity entity) {
        return typeMetadata.getTable() == null ? ClassTableName.getTableName(entity) : typeMetadata.getTable();
    }
}
