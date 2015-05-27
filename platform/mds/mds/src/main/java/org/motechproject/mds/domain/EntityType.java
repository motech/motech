package org.motechproject.mds.domain;

import org.motechproject.mds.util.ClassName;

public enum EntityType {
    STANDARD {
        @Override
        public String getName(String className) {
            return className;
        }
    }, HISTORY {
        @Override
        public String getName(String className) {
            return ClassName.getHistoryClassName(className);
        }
    }, TRASH {
        @Override
        public String getName(String className) {
            return ClassName.getTrashClassName(className);
        }
    };

    public abstract String getName(String className);

    public static EntityType forClassName(String className) {
        if (ClassName.isHistoryClassName(className)) {
            return HISTORY;
        } else if (ClassName.isTrashClassName(className)) {
            return TRASH;
        } else {
            return STANDARD;
        }
    }

    public String getTableName(String originalName) {
        String tableName = originalName;
        if (!EntityType.STANDARD.equals(this)) {
            tableName = tableName.concat("__").concat(this.toString());
        }

        return tableName;
    }
}
