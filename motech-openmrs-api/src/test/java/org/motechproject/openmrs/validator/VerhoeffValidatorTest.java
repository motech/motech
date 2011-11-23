package org.motechproject.openmrs.validator;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class VerhoeffValidatorTest {

	VerhoeffValidator validator;

	@Before
	public void setUp() throws Exception {
		validator = new VerhoeffValidator();
	}

	@Test
	public void testGetValidIsValidIdentifier() {
		String undecoratedIdentifier = "2";
		String identifier = validator.getValidIdentifier(undecoratedIdentifier);
		assertNotNull("Validator should not be null", identifier);
		assertEquals(2, identifier.length());
		assertEquals("27", identifier);
		assertTrue("Expected valid identifier with Verhoeff check digit",
				validator.isValid(identifier));
	}

	@Test
    public void testIsValidKnownVerhoeffIdentifier() {

		Integer[] staffIds = { 27, 36, 43, 58, 62, 70, 89, 91, 109, 113, 121,
				132, 145, 150, 166, 178, 184, 197, 204, 215, 227, 236, 243,
				258, 262, 270, 289, 291, 301, 317, 329, 338, 340, 355, 364,
				372, 386, 393, 408, 412, 420, 431, 449, 454 };

		Integer[] facilityIds = { 11117, 11210, 11223, 11313, 11418, 11425,
				11439, 11441, 11516, 11528, 11619, 11626, 11711, 11724, 11814,
				12113, 12121, 12132, 12215, 12227, 12236, 12243, 12317, 12329,
				12338, 12412, 12420, 12514, 12611, 12624, 99998 };

		for (Integer staffId : staffIds) {
			assertTrue("Expected valid identifier with Verhoeff check digit",
					validator.isValid(staffId.toString()));
		}
		for (Integer facilityId : facilityIds) {
			assertTrue("Expected valid identifier with Verhoeff check digit",
					validator.isValid(facilityId.toString()));
		}
	}

}

