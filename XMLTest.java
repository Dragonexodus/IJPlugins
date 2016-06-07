package test;

import static org.junit.Assert.*;

import org.junit.Test;
import parseXML.*;

public class XMLTest {

	@Test
	public void testWriteXMLFile() {
		String fileOutputPath ="/home/dragonexodus/Digitalebilderverarbeitung/workspace/Projekt_Schilderkennung/test1.xml";
		
		XML t = new XML(60, 10, 20, 100, 200);
		t.writeXMLFile("filePath", "fileName", fileOutputPath);
		t.addObject(60, 0,0 , 10, 10);
		t.writeXMLFile("filePath", "fileName", fileOutputPath);
	}

}
