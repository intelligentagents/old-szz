/**
 * 
 */
package control;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author joao.neves
 *
 */
public class UtilsTest {

	@Test
	public void retrievePreviousCommit() {
		Assert.assertEquals("bd24edc018f1c6fbbf1888dd13715c18b5d30d7e", 
				Utils.retrievePreviousCommit("92ce68ae96dfac3ea3b3cc87f5618207e13a4005", 
						"/Users/bonoddr/Development/Workspace/doutorado/tomcat/"));
	}
}
