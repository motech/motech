package org.motechproject.mds.service.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.metadata.MemberMetadata;
import javax.jdo.metadata.TypeMetadata;

/**
 * Implementation of the {@link MetadataServiceImpl}. Will use the {@link PersistenceManagerFactory}
 * available for retrieving metadata. This allows retrieving the DataNucleus metadata without making any
 * assumptions.
 */
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    private PersistenceManagerFactory persistenceManagerFactory;

    @Override
    public String getComboboxTableName(String entityClassName, String cbFieldName) {
        TypeMetadata typeMetadata = persistenceManagerFactory.getMetadata(entityClassName);
        if (typeMetadata == null) {
            throw new IllegalArgumentException("No type metadata found for " + entityClassName);
        }

        MemberMetadata cbMetadata = findMemberMetadata(typeMetadata, cbFieldName);
        if (cbMetadata == null) {
            throw new IllegalArgumentException("No member metadata for field " + cbFieldName + " in "
                    + entityClassName);
        }

        final String tableName = cbMetadata.getTable();
        if (tableName == null) {
            throw new IllegalArgumentException("No table name specified for member " + cbFieldName + " in "
                    + entityClassName);
        }

        return tableName;
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
}
