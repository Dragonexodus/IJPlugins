import java.awt.*;
import java.awt.image.ImageFilter;
import java.io.File;

import ij.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.process.*;
import ij.plugin.Commands;
import ij.plugin.PlugIn;

import java.util.ArrayList;
import java.util.List;
import parseXML.XML;

public class Speed_Detection implements PlugIn {

	public void run(String arg) {

		final String fileName = "7";
		final String fileType = ".png";
		final String path = "/home/dragonexodus/Digitalebilderverarbeitung/Projekt/";
		final String filePath = path + fileName + fileType;

		if (!(new File(filePath)).exists()) {
			IJ.log("File not found: " + filePath);
			return;
		}

		ImagePlus original = new ImagePlus(filePath);
		ImagePlus duplicate = original.duplicate();
		ImageProcessor ipFilterAndRed = duplicate.getProcessor();

		/*
		 * check.erode(); // Closing //check.dilate(); //Funktioniert nicht bei
		 * Bild 5
		 */
		ipFilterAndRed.filter(ImageProcessor.MAX); // Sollte Rot etwas mehr
													// hervorheben?
		ipFilterAndRed.medianFilter();

		final double multiGreen = 2, multiYellow = 2;
		// TODO Vorher prüfen wie die Helligkeit des Bildes im Schnitt ist..
		// dann entsprechend multiplikatoren setzen..
		paintRed(duplicate, multiGreen, multiYellow);
		//duplicate.show();

		// TODO fill holes, ggf. stattdessen Linienprofil erkennen
		ImagePlus filledImage = new ImagePlus("Filled Holes", ipFilterAndRed.convertToByteProcessor());
		IJ.runPlugIn(filledImage, "ij.plugin.filter.Binary", "fill");

		ImageProcessor ipFilledDetection = filledImage.getProcessor().convertToByteProcessor();

		List<Coordinates<Integer>> posVerticalList = findLines(ipFilledDetection, true);
		List<Coordinates<Integer>> posHorizontalList = findLines(ipFilledDetection, false);
		// Visual Test -> Grey surrounding contur
		putConturPixel(ipFilledDetection, posHorizontalList, posVerticalList);

		// TODO findCircleContur();

		// TODO minRadius setzen
		int maxRadius = 0, centerX = 0, centerY = 0, previousY = 0;
		
		//TODO verschiedene konturen..
		for (Coordinates<Integer> coordinates : posVerticalList) {
			if (coordinates.getyStart() - previousY > 1) {
				if (maxRadius > 0) {
					findBoundingBoxCircle(ipFilledDetection, maxRadius, centerX, centerY);
				}
				maxRadius = 0;
				centerX = 0;
				centerY = 0;
			}

			// aktualisiere maximale Distanz
			int currentDistance = coordinates.getxStop() - coordinates.getxStart();
			if (currentDistance > maxRadius * 2) {
				maxRadius = currentDistance / 2;
				centerX = maxRadius + coordinates.getxStart();
				centerY = coordinates.getyStart();
			}
			previousY = coordinates.getyStart();

		}

		// Zeichne noch nicht gezeichnete komponenten
		if (maxRadius > 0) {
			findBoundingBoxCircle(ipFilledDetection, maxRadius, centerX, centerY);
		}
		original.show();
		new ImagePlus("Test", ipFilledDetection).show();
		// filledImage.show();
		XML resultXML = new XML(70, 10, 10, 200, 200);
		resultXML.addObject(1, 1, 1, 1, 1);
		resultXML.addObject(2, 2, 2, 2, 2);
		String fileOutputPath ="/home/dragonexodus/Digitalebilderverarbeitung/workspace/Projekt_Schilderkennung/test1.xml";
		resultXML.writeXMLFile("lol", "lol2", fileOutputPath);
	}

	/**
	 * Prints contur pixels as visual feedback
	 * 
	 * @param check2
	 * @param posHorizontalList
	 * @param posVerticalList
	 */
	private void putConturPixel(ImageProcessor check2, List<Coordinates<Integer>> posHorizontalList,
			List<Coordinates<Integer>> posVerticalList) {

		for (Coordinates<Integer> coordinates : posVerticalList) {
			check2.putPixel(coordinates.getxStart(), coordinates.getyStart(), 128);
			check2.putPixel(coordinates.getxStop(), coordinates.getyStop(), 128);
		}

		for (Coordinates<Integer> coordinates : posHorizontalList) {
			check2.putPixel(coordinates.getxStart(), coordinates.getyStart(), 128);
			check2.putPixel(coordinates.getxStop(), coordinates.getyStop(), 128);
		}
	}

	/**
	 * Dominating red color is replaced through white, mismatch is replaced with
	 * black, at the end inverts
	 * 
	 * @param duplicate
	 * @param multiGreen
	 * @param multiYellow
	 */
	private void paintRed(ImagePlus duplicate, double multiGreen, double multiYellow) {
		final int h = duplicate.getHeight();
		final int w = duplicate.getWidth();
		ImageProcessor check = duplicate.getProcessor();
		// Trenne rot von restlicher farbe
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				final int[] color = duplicate.getPixel(x, y);
				if (color[0] > 0 && color[0] > multiGreen * color[1] && color[0] > multiYellow * color[2])
					check.putPixel(x, y, Integer.MAX_VALUE);
				else
					check.putPixel(x, y, 0);
			}
		}
		check.invert();
	}

	/**
	 * find start/end of connected black lines
	 * 
	 * @param ip
	 * @param isVertical
	 * @return List of Lists with contains
	 *         startX[0],startY[1],stopX[2],stopY[3], swapped X,Y if isVertical
	 *         false
	 */
	private List<Coordinates<Integer>> findLines(ImageProcessor ip, boolean isVertical) {
		// TODO Übergänge für vertikal und horizontal
		List<Coordinates<Integer>> posList = new ArrayList<>();
		int h = ip.getHeight();
		int w = ip.getWidth();

		// Tausche je nach vertikal oder horizontal
		if (!isVertical) {
			h = ip.getWidth();
			w = ip.getHeight();
		}

		for (int y = 0; y < h; y++) {
			boolean foundSth = false;
			Coordinates<Integer> tempCord = new Coordinates<Integer>();
			int x = 0;
			for (x = 0; x < w; x++) {
				byte color = 0;
				if (!isVertical) {
					color = (byte) ip.getPixel(y, x);
				} else {
					color = (byte) ip.getPixel(x, y);
				}

				if (color == 0 && !foundSth) {
					// Überprüfe ob mehrere black lines
					if (!tempCord.isStartEmpty()) {
						if (!isVertical)
							tempCord.swap();
						posList.add(tempCord);
						tempCord = new Coordinates<>();
					}
					tempCord.setStart(x, y);
					foundSth = true;
				} else if (color != 0 && foundSth) {
					tempCord.setStop(x, y);
					foundSth = false;
				}
			}

			if (isVertical) {
				if (!tempCord.isStartEmpty()) {
					if (tempCord.isStopEmpty()) {
						tempCord.setStop(tempCord.getxStart(), h - 1);
					}
					posList.add(tempCord);
				}
			} else {
				if (!tempCord.isStopEmpty()) {
					if (tempCord.isStartEmpty()) {
						tempCord.setStart(w - 1, tempCord.getyStop());
					}
					tempCord.swap();
					posList.add(tempCord);
				}
			}
		}

		return posList;
	}

	/**
	 * Prints bounding box for a circle
	 * 
	 * @param check2
	 * @param maxRadius
	 * @param centerX
	 * @param centerY
	 */
	private void findBoundingBoxCircle(ImageProcessor check2, int maxRadius, int centerX, int centerY) {
		check2.putPixel(centerX, centerY, 196);
		final int h = check2.getHeight();
		byte north = 0, south = 0;
		if (centerY - maxRadius > 0) {
			north = (byte) check2.getPixel(centerX, centerY - maxRadius);
			if (centerY + maxRadius < h) {
				south = (byte) check2.getPixel(centerX, centerY + maxRadius);
				// if (north == 0 && south == 0) {
				check2.drawRect(centerX - maxRadius, centerY - maxRadius, maxRadius * 2, maxRadius * 2);
				// }
			}
		}
	}

}
