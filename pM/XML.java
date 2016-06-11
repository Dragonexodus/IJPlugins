


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class builds the xml as wished of sedenius.. further improvements can be done.. but aren't neccessary for us
 * @author MH
 *
 */
public class XML {
	
	private List<Integer> object;
	private int objectSize;
	
	/**
	 * constructer of an XML, also adds an speedsign
	 * @param speed 
	 * @param x -Pos of BoundingBox
	 * @param y -Pos of BoundingBox
	 * @param widht -of BoundingBox
	 * @param height -of BoundingBox
	 */
	public XML(Integer speed, Integer x, Integer y, Integer widht, Integer height) {
		super();
		object = new ArrayList<>();
		addObject(speed, x, y, widht,height);
		objectSize = object.size(); //Size of one object
	}

	/**
	 * Adds another detected speed sign, NO errorchecking.. so use "0" instead of "null"
	 * @param speed
	 * @param x
	 * @param y
	 * @param widht
	 * @param height
	 */
	public void addObject(Integer speed, Integer x, Integer y, Integer widht, Integer height){
		object.add(speed);
		object.add(x);
		object.add(y);
		object.add(widht);
		object.add(height);
	}
	
	/**
	 * This method writes the file.
	 * @param inputFilePath -e.g. "home/user/test/input.xml"
	 * @param inputFileName - e.g. "input.xml"
	 * @param outputFilePath -e.g. "full output path; "home/user/test/output.xml"
	 */
	public void writeXMLFile(String inputFilePath, String inputFileName, String outputFilePath) {
		try {
			//TODO: Make it better....
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("video");
			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:noNamespaceSchemaLocation", "objectDetection.xsd");
			doc.appendChild(rootElement);

			// info elements
			Element info = doc.createElement("info");
			rootElement.appendChild(info);

			// file element
			Element file = doc.createElement("file");
			file.setAttribute("type", "file");
			file.appendChild(doc.createTextNode(inputFilePath));
			info.appendChild(file);

			// file hash
			Element hash = doc.createElement("hash");
			hash.setAttribute("type", "md5");
			hash.appendChild(doc.createTextNode("ae61382ebb2a5b6bcc2e5aacc6402b04"));
			info.appendChild(hash);

			// file id
			Element id = doc.createElement("id");
			id.appendChild(doc.createTextNode("aHhq53FM4U51j"));
			info.appendChild(id);

			// filename
			Element name = doc.createElement("name");
			name.appendChild(doc.createTextNode(inputFileName));
			info.appendChild(name);

			// file description
			Element description = doc.createElement("description");
			description.appendChild(doc.createTextNode("Schilderkennung sedenius~~"));
			info.appendChild(description);

			// file id
			Element frameCount = doc.createElement("frameCount");
			frameCount.appendChild(doc.createTextNode("1"));
			info.appendChild(frameCount);

			// frames
			Element frames = doc.createElement("frames");
			rootElement.appendChild(frames);

			// frame
			Element frame = doc.createElement("frame");
			frame.setAttribute("index", "212");
			frames.appendChild(frame);
			
			//info frame
			Element infoFrame = doc.createElement("info");
			frame.appendChild(infoFrame);
			
			Element day = doc.createElement("day");
			day.setAttribute("confidence", "0.8");
			infoFrame.appendChild(day);
			
			Element night = doc.createElement("night");
			night.setAttribute("confidence", "0.2");
			infoFrame.appendChild(night);
			
			Element objects = doc.createElement("objects");
			frame.appendChild(objects);
					
			for(int i = 0; i < object.size();i = i + objectSize){
				buildObject(doc, objects, object.get(i),object.get(i+1),object.get(i+2),object.get(i+3),object.get(i+4));
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(
					outputFilePath));

			// Output to console for testing
			//result = new StreamResult(System.out);

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	/**
	 * builds an speed sign object ..
	 * @param doc
	 * @param objects
	 * @param speed
	 * @param x
	 * @param y
	 * @param widht
	 * @param height
	 */
	private void buildObject(Document doc, Element objects, Integer speed, Integer x, Integer y, Integer widht, Integer height) {
		Element object = doc.createElement("object");
		object.setAttribute("id", Integer.toString((int)(Math.random())));
		objects.appendChild(object);
		
		//info Object
		Element infoObject = doc.createElement("info");
		object.appendChild(infoObject);
		
		Element category1 = doc.createElement("category");
		category1.setAttribute("confidence", "0.9");
		category1.appendChild(doc.createTextNode("speed_limit_sign"));
		infoObject.appendChild(category1);
		
		Element category2 = doc.createElement("category");
		category2.setAttribute("confidence", "0.1");
		category2.appendChild(doc.createTextNode("tasty_venison"));
		infoObject.appendChild(category2);
		
		Element intAttribute= doc.createElement("intAttribute");
		intAttribute.setAttribute("key", "speedLimit");
		intAttribute.appendChild(doc.createTextNode(Integer.toString(speed)));
		infoObject.appendChild(intAttribute);
		
		Element shape = doc.createElement("shape");
		shape.setAttribute("type", "rectangle");
		object.appendChild(shape);
		
		Element widthElement = doc.createElement("width");
		widthElement.appendChild(doc.createTextNode(Integer.toString(widht)));
		shape.appendChild(widthElement);
		
		Element heightElement= doc.createElement("height");
		heightElement.appendChild(doc.createTextNode(Integer.toString(height)));
		shape.appendChild(heightElement);
		
		Element position= doc.createElement("position");
		shape.appendChild(position);
		
		Element xPos = doc.createElement("x");
		xPos.appendChild(doc.createTextNode(Integer.toString(x)));
		position.appendChild(xPos);
		
		Element yPos = doc.createElement("y");
		yPos.appendChild(doc.createTextNode(Integer.toString(y)));
		position.appendChild(yPos);
		
		Element refPoint = doc.createElement("referencePoint");
		refPoint.appendChild(doc.createTextNode("topleft"));
		shape.appendChild(refPoint);
	}

}