package org.motechproject.mds.dto;

import java.util.List;

/**
 * The <code>LookupDto</code> class contains information about single lookup defined by user
 */
public class LookupDto {
    private String lookupName;
    private boolean singleObjectReturn;
    private List<FieldBasicDto> fieldList;

    public String getLookupName() {
        return lookupName;
    }

    public void setLookupName(String lookupName) {
        this.lookupName = lookupName;
    }

    public boolean isSingleObjectReturn() {
        return singleObjectReturn;
    }

    public void setSingleObjectReturn(boolean singleObjectReturn) {
        this.singleObjectReturn = singleObjectReturn;
    }

    public List<FieldBasicDto> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldBasicDto> fieldList) {
        this.fieldList = fieldList;
    }

    @Override
    public String toString() {
        return "LookupDto{" +
                "lookupName='" + lookupName + '\'' +
                ", singleObjectReturn=" + singleObjectReturn +
                ", fieldList=" + fieldList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LookupDto lookupDto = (LookupDto) o;

        if (singleObjectReturn != lookupDto.singleObjectReturn) {
            return false;
        }
        if (fieldList != null ? !fieldList.equals(lookupDto.fieldList) : lookupDto.fieldList != null) {
            return false;
        }
        if (lookupName != null ? !lookupName.equals(lookupDto.lookupName) : lookupDto.lookupName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = lookupName != null ? lookupName.hashCode() : 0;
        result = 31 * result + (singleObjectReturn ? 1 : 0);
        result = 31 * result + (fieldList != null ? fieldList.hashCode() : 0);
        return result;
    }
}
