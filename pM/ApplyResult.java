package plugins.pM;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.*;
import java.io.File;
import java.util.List;

public class ApplyResult {
	private List<SpeedObject<Integer>> speedList;
	private File inFile;
	private String outXMLPath;
	private String outGraphicPath;

	/**
	 * This class handles boundingBox and speed painting, file writing ...
	 * 
	 * @param list
	 * @param inputFilePath
	 *            Complete Path to File
	 * @param outputPath
	 *            Path to a directory ( home/test/) without a file!
	 */
	public ApplyResult(List<SpeedObject<Integer>> list, String inputFilePath, String outputPath) {

		if(inputFilePath == null){
			IJ.log("input path empty");
			return;
		}
		if(outputPath == null){
			outputPath = inputFilePath;
		}
		this.inFile = new File(inputFilePath);
		if (!inFile.exists()) {
			IJ.log("File not found: " + inputFilePath);
			return;
		}
		if (!inFile.isFile()) {
			IJ.log("Its not a File");
			return;
		}

		this.speedList = list;
		if (this.speedList == null) {
			IJ.log("SpeedList is null");
			return;
		}
		if (this.speedList.isEmpty() || this.speedList.get(0) == null) {
			IJ.log("SpeedObject is null");
			return;
		}

		exctractOutPaths(outputPath);

		paintAndExport();

	}

	private void paintAndExport() {
		ImagePlus original = new ImagePlus(inFile.getAbsolutePath());
		ImagePlus duplicate = original.duplicate();
		ImageProcessor ip = duplicate.getProcessor();

		XML xml = new XML();

		for (SpeedObject<Integer> sO : this.speedList) {

			int xBB = sO.getxCenter() - sO.getRadius();
			int yBB = sO.getyCenter() - sO.getRadius();
			int w = sO.getRadius() * 2;

			ip.setColor(new Color(200, 0, 0));
			Color background = new Color(0, 0, 0);
			ip.drawRect(xBB, yBB, w, w);
			ip.setColor(new Color(255, 255, 255));
			ip.drawString(sO.getSpeed().toString(), sO.getxCenter(), sO.getyCenter() + sO.getRadius() + sO.getOffset(),
					background);

			xml.addObject(sO.getSpeed(), xBB, yBB, w, w);
		}
		IJ.save(duplicate, outGraphicPath);
		xml.writeXMLFile(inFile.getAbsolutePath(), inFile.getName(), outXMLPath);
	}

	private void exctractOutPaths(String outputPath) {
		File outFile = new File(outputPath);
		if (!outFile.isDirectory()) {
			String path = outFile.getParent();
			outFile = new File(path);
		}

		int splitPos = inFile.getName().lastIndexOf(".");
		String outName = File.separator + inFile.getName().substring(0, splitPos);
		String outXML = outName + "_RSD.XML";
		String outGraphic = outName + "_RSD" + inFile.getName().substring(splitPos);

		outGraphicPath = outFile.getAbsolutePath() + outGraphic;
		outXMLPath = outFile.getAbsolutePath() + outXML;
	}

}
