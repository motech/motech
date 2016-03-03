package org.motechproject.security.validator.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.ex.PasswordValidatorException;
import org.motechproject.security.validator.PasswordValidator;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * A base for validators, it counts the types of charactesr in the password and
 * validates against the set minimal values.
 */
public class MotechPasswordValidator implements PasswordValidator {

    private final int minLowerCase;
    private final int minUpperCase;
    private final int minDigit;
    private final int minSpecialChar;
    private final String name;
    private final String errorKey;
    private final MessageSource messageSource;

    public MotechPasswordValidator(int minLowerCase, int minUpperCase, int minDigit, int minSpecialChar,
                                   String name, MessageSource messageSource) {
        this.minLowerCase = minLowerCase;
        this.minUpperCase = minUpperCase;
        this.minDigit = minDigit;
        this.minSpecialChar = minSpecialChar;
        this.name = name;
        this.messageSource = messageSource;
        this.errorKey = String.format("security.validator.error.%s", name);
    }

    @Override
    public void validate(String password) {
        CharacterCount count = new CharacterCount(password);

        if (count.getLowerCase() < minLowerCase || count.getUpperCase() < minUpperCase ||
                count.getDigit() < minDigit || count.getSpecial() < minSpecialChar) {
            throw new PasswordValidatorException("Invalid password, validator name - " + name);
        }
    }

    @Override
    public String getValidationError(Locale locale) {
        return messageSource.getMessage(errorKey, new Object[0], locale);
    }

    @Override
    public String getName() {
        return name;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    protected static class CharacterCount {

        private int lowerCase;
        private int upperCase;
        private int digit;
        private int special;

        public CharacterCount(String str) {
            if (StringUtils.isNotBlank(str)) {
                for (char c : str.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        upperCase++;
                    } else if (Character.isLowerCase(c)) {
                        lowerCase++;
                    } else if (Character.isDigit(c)) {
                        digit++;
                    } else {
                        special++;
                    }
                }
            }
        }

        public int getLowerCase() {
            return lowerCase;
        }

        public int getUpperCase() {
            return upperCase;
        }

        public int getDigit() {
            return digit;
        }

        public int getSpecial() {
            return special;
        }
    }
}
