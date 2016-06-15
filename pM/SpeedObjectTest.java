package plugins.pM;

import junit.framework.Assert;
import org.junit.Test;

public class SpeedObjectTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testSpeedObjectAAAAA() {
		SpeedObject<Integer> test= new SpeedObject<Integer>(10,100,10,0,60);
		Assert.assertEquals(60,(int)test.getSpeed());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSpeedObject() {
		SpeedObject<Integer> test= new SpeedObject<Integer>();
		
		Assert.assertTrue(test.isCenterEmpty());
		
	}
}
