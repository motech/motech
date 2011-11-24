package org.motechproject.openmrs.omod.validator;

import org.openmrs.patient.UnallowedIdentifierException;

public class MotechIdVerhoeffValidator extends VerhoeffValidator {

	public static final String VERHOEFF_NAME = "MoTeCH ID Verhoeff Check Digit Validator";

	public static final int VERHOEFF_ID_LENGTH = 7;

	public static final int VERHOEFF_UNDECORATED_ID_LENGTH = VERHOEFF_ID_LENGTH - 1;

	@Override
	public String getName() {
		return VERHOEFF_NAME;
	}

	@Override
	public boolean isValid(String identifier)
			throws UnallowedIdentifierException {

		checkAllowedIdentifier(identifier);

		if (identifier.length() != VERHOEFF_ID_LENGTH)
			throw new UnallowedIdentifierException("Identifier must be "
					+ VERHOEFF_ID_LENGTH + " digits long.");

		return isValidCheckDigit(identifier);
	}

	@Override
	public String getValidIdentifier(String undecoratedIdentifier)
			throws UnallowedIdentifierException {

		checkAllowedIdentifier(undecoratedIdentifier);

		if (undecoratedIdentifier.length() != VERHOEFF_UNDECORATED_ID_LENGTH)
			throw new UnallowedIdentifierException(
					"Undecorated identifier must be "
							+ VERHOEFF_UNDECORATED_ID_LENGTH + " digits long.");

		int checkDigit = getCheckDigit(undecoratedIdentifier);
        return undecoratedIdentifier + checkDigit;
	}

}
