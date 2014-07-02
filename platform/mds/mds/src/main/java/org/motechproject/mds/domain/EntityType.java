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
    }, UNKNOWN {
        @Override
        public String getName(String className) {
            return className;
        }
    };

    public abstract String getName(String className);
}
