import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ApplyResultTest {

	@Test
	public void testApplyResult() {
		List<SpeedObject<Integer>> list = new ArrayList<>();
		SpeedObject<Integer> a = new SpeedObject<>(10,100,10,0,60);
		SpeedObject<Integer> b = new SpeedObject<>(10,100,10,0,60);
		SpeedObject<Integer> c = new SpeedObject<>(10,100,10,0,60);
		
		list.add(a);
		list.add(b);
		list.add(c);
		
		ApplyResult applyResult = new ApplyResult(list,null,null);
	}

	@Test
	public void testApplyResultEmpty(){
		ApplyResult applyResult = new ApplyResult(null,null,null);
	}
	@Test
	public void testApplyResultEmpty_2(){
		List<SpeedObject<Integer>> list = new ArrayList<>();
		ApplyResult applyResult = new ApplyResult(list, null,null);
	}
}
