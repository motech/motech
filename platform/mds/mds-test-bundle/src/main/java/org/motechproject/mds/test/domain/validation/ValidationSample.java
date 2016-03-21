package org.motechproject.mds.test.domain.validation;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@BothNamesOrNoName
@Entity
public class ValidationSample {

    @Field
    @Pattern(regexp = "[a-z]+")
    private String stringPattern;

    @Field
    @Min(10)
    private Integer minInt;

    @Field
    @Max(10)
    private Long maxLong;

    @Field
    private String firstName;

    @Field
    private String lastName;

    public String getStringPattern() {
        return stringPattern;
    }

    public void setStringPattern(String stringPattern) {
        this.stringPattern = stringPattern;
    }

    public Integer getMinInt() {
        return minInt;
    }

    public void setMinInt(Integer minInt) {
        this.minInt = minInt;
    }

    public Long getMaxLong() {
        return maxLong;
    }

    public void setMaxLong(Long maxLong) {
        this.maxLong = maxLong;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
