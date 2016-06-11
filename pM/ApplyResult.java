import java.awt.*;
import java.awt.image.ImageFilter;
import java.io.File;
import java.lang.reflect.GenericArrayType;

import ij.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.process.*;
import ij.plugin.Commands;
import ij.plugin.PlugIn;

import java.util.ArrayList;
import java.util.List;

public class ApplyResult {
	private List<SpeedObject<Integer>> speedList;
	private String inputFilePath;
	private String outputFilePath;

	
	public ApplyResult(List<SpeedObject<Integer>> list, String inputFilePath, String OutputFilePath) {
		
		final String fileName = "7";
		final String fileType = ".png";
		final String path = "/home/dragonexodus/Digitalebilderverarbeitung/Projekt/";
		final String filePath = path + fileName + fileType;
		
		if (!(new File(filePath)).exists()) {
			IJ.log("File not found: " + filePath);
			return;
		}
		
		this.speedList = list;
		if (this.speedList == null) {
			return;
		}
		if (this.speedList.isEmpty() || this.speedList.get(0) == null) {
			return;
		}
		SpeedObject<Integer> first = this.speedList.get(0);
		int speed = first.getSpeed();
		int xBB = first.getxCenter() - first.getRadius();
		int yBB = first.getyCenter() - first.getRadius();
		int w = first.getRadius() * 2;

		this.speedList.remove(0);
		XML xml = new XML(speed, xBB, yBB, w, w);

		for (SpeedObject<Integer> speedObject : this.speedList) {

			speed = speedObject.getSpeed();
			xBB = speedObject.getxCenter() - speedObject.getRadius();
			yBB = speedObject.getyCenter() - speedObject.getRadius();
			w = speedObject.getRadius() * 2;

			xml.addObject(speed, xBB, yBB, w, w);
		}

		// xml.writeXMLFile(inputFilePath, inputFileName, outputFilePath);

	}
}
