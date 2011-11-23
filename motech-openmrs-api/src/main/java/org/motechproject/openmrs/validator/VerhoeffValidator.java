package org.motechproject.openmrs.validator;

import org.openmrs.patient.UnallowedIdentifierException;
import org.openmrs.patient.impl.BaseHyphenatedIdentifierValidator;

//@Component

public class VerhoeffValidator extends BaseHyphenatedIdentifierValidator {
    public static final String ALLOWED_CHARS = "0123456789";
    public static final String VERHOEFF_NAME = "Verhoeff Check Digit Validator";

    @Override
    public String getAllowedCharacters() {
        return ALLOWED_CHARS;
    }

    @Override
    public String getName() {
        return VERHOEFF_NAME;
    }

    @Override
    protected int getCheckDigit(String undecoratedIdentifier) {
        int checkDigit = calculateCheckDigit(undecoratedIdentifier, false);
        return inv_table[checkDigit];
    }

    @Override
    public boolean isValid(String identifier) throws UnallowedIdentifierException {
        checkAllowedIdentifier(identifier);
        return isValidCheckDigit(identifier);
    }

    @Override
    public String getValidIdentifier(String undecoratedIdentifier) throws UnallowedIdentifierException {
        checkAllowedIdentifier(undecoratedIdentifier);
        int checkDigit = getCheckDigit(undecoratedIdentifier);
        return undecoratedIdentifier + checkDigit;
    }

    protected boolean isValidCheckDigit(String identifier) {
        int checkDigit = calculateCheckDigit(identifier, true);
        return checkDigit == 0;
    }

    protected int calculateCheckDigit(String identifier, boolean includesCheckDigit) {
        int checkDigit = 0;
        int i = includesCheckDigit ? 0 : 1;
        for (int j = identifier.length() - 1; j >= 0; j--) {
            int number = Character.getNumericValue(identifier.charAt(j));
            if (number < 0 || number > 9) {
                throw new UnallowedIdentifierException("\"" + identifier.charAt(j) + "\" is an invalid character.");
            }
            checkDigit = d_table[checkDigit][p_table[i % 8][number]];
            i++;
        }
        return checkDigit;
    }

    /**
     * The multiplication table
     */
    private final static int[][] d_table = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
            {2, 3, 4, 0, 1, 7, 8, 9, 5, 6}, {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
            {4, 0, 1, 2, 3, 9, 5, 6, 7, 8}, {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
            {6, 5, 9, 8, 7, 1, 0, 4, 3, 2}, {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
            {8, 7, 6, 5, 9, 3, 2, 1, 0, 4}, {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}};

    /**
     * The permutation table
     */
    private final static int[][] p_table = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
            {5, 8, 0, 3, 7, 9, 6, 1, 4, 2}, {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
            {9, 4, 5, 3, 1, 2, 6, 8, 7, 0}, {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
            {2, 7, 9, 3, 8, 0, 6, 4, 1, 5}, {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}};

    /**
     * The inverse table
     */
    private final static int[] inv_table = {0, 4, 3, 2, 1, 5, 6, 7, 8, 9};
}
