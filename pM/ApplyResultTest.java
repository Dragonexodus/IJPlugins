package plugins.pM;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ApplyResultTest {

	@Test
	public void testApplyResult() {
		List<SpeedObject<Integer>> list = new ArrayList<SpeedObject<Integer>>();
		SpeedObject<Integer> a = new SpeedObject<Integer>(100,100,10,10,60);
		SpeedObject<Integer> b = new SpeedObject<Integer>(500,500,200,10,50);
		

		list.add(b);
		list.add(a);
		//ACHTUNG: Als PNG speichern nur möglich, wenn Klasse über Plugin aufgerufen wurde
		final String in = "/home/dragonexodus/Digitalebilderverarbeitung/Projekt/7.png";
		final String out = "/home/dragonexodus/Digitalebilderverarbeitung/Projekt/";
		ApplyResult applyResult = new ApplyResult(list,in,out);
	}

	@Test
	public void testApplyResultEmpty(){
		ApplyResult applyResult = new ApplyResult(null,null,null);
	}
	@Test
	public void testApplyResultEmpty_2(){
		List<SpeedObject<Integer>> list = new ArrayList<SpeedObject<Integer>>();
		ApplyResult applyResult = new ApplyResult(list, null,null);
	}
}
