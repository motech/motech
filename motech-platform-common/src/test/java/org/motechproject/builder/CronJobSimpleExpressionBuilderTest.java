package org.motechproject.builder;

import static org.junit.Assert.*;

import org.junit.Test;
import org.motechproject.model.Time;

public class CronJobSimpleExpressionBuilderTest {

	@Test
	public void testBuild() {
		CronJobSimpleExpressionBuilder builder = new CronJobSimpleExpressionBuilder(new Time(10, 25));
		assertEquals("0 25 10 * * ?", builder.build());
	}

}
