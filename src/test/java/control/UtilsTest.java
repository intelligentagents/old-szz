/**
 * 
 */
package control;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author joao.neves
 *
 */
public class UtilsTest {

	@Test
	public void retrievePreviousCommit() {
		Assert.assertEquals("bd24edc018f1c6fbbf1888dd13715c18b5d30d7e", Utils.retrievePreviousCommit(
				"92ce68ae96dfac3ea3b3cc87f5618207e13a4005", "/Users/bonoddr/Development/Workspace/doutorado/tomcat/"));
	}

	@Test
	public void retrieveTempFilesToDiffAbsentPreviousFile() {
		Assert.assertEquals(Collections.EMPTY_MAP,
				Utils.retrieveTempFilesToDiff("92ce68ae96dfac3ea3b3cc87f5618207e13a4005",
						"java/org/apache/catalina/valves/AbstractAccessLogValve.java",
						"/Users/bonoddr/Development/Workspace/doutorado/tomcat/"));
	}

	@Test
	public void retrieveTempFilesToDiff() {
		Utils.retrieveTempFilesToDiff("4df1957a58207893494c101f82260648508b2bfa", "java/javax/el/BeanELResolver.java",
				"/Users/bonoddr/Development/Workspace/doutorado/tomcat/");
	}

	@Test
	public void emptyFileBuider() {
		Assert.assertEquals("", new StringBuilder().toString());
	}
}
