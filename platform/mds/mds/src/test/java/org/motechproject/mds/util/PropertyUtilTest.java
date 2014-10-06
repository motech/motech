package org.motechproject.mds.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyUtilTest {

    @Test
    public void shouldCopyObjectValues() {
        TestClass fromDb = new TestClass(7, "fromDb", "accessible", 5L);
        TestClass transientObj = new TestClass(8, "transient", "changed", 6L);

        PropertyUtil.copyPropertiesFromTransient(fromDb, transientObj);

        assertEquals(8, fromDb.publicInt);
        assertEquals("changed", fromDb.accessibleString);
        // no change
        assertEquals("fromDb", fromDb.privateStr);
        // no change since we ignore generated fields
        assertEquals(Long.valueOf(5), fromDb.id);
    }

    private class TestClass {

        private Long id;

        private TestClass(int publicInt, String privateStr, String accessibleString, Long id) {
            this.publicInt = publicInt;
            this.privateStr = privateStr;
            this.accessibleString = accessibleString;
            this.id = id;
        }

        public int publicInt;

        private String privateStr;

        private String accessibleString;

        public int getPublicInt() {
            return publicInt;
        }

        public void setPublicInt(int publicInt) {
            this.publicInt = publicInt;
        }

        public String getAccessibleString() {
            return accessibleString;
        }

        public void setAccessibleString(String accessibleString) {
            this.accessibleString = accessibleString;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    private class TestClass__History {

        private Long id;

        private TestClass__History(int publicInt, String accessibleString, Long id) {
            this.publicInt = publicInt;
            this.accessibleString = accessibleString;
            this.id = id;
        }

        public int publicInt;

        private String accessibleString;

        public int getPublicInt() {
            return publicInt;
        }

        public void setPublicInt(int publicInt) {
            this.publicInt = publicInt;
        }

        public String getAccessibleString() {
            return accessibleString;
        }

        public void setAccessibleString(String accessibleString) {
            this.accessibleString = accessibleString;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
