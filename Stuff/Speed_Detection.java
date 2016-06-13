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

public class Speed_Detection implements PlugIn {
	// RGB * 255 / länge des Vektors = wurzel powR + powG + pow B

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

		ipFilterAndRed.dilate(); // Kanten hervorheben

		normalizeRGB(ipFilterAndRed);
		// new ImagePlus("Normalized", ipFilterAndRed.duplicate()).show();

		final double multiGreen = 2, multiYellow = 2;
		paintRed(duplicate, multiGreen, multiYellow);

		// TODO fill holes, ggf. stattdessen Linienprofil erkennen
		ImagePlus filledImage = new ImagePlus("Filled Holes", ipFilterAndRed.convertToByteProcessor());
		IJ.runPlugIn(filledImage, "ij.plugin.filter.Binary", "fill");

		ImageProcessor ipFilledDetection = filledImage.getProcessor().convertToByteProcessor();

		List<Coordinates<Integer>> posHorizontalList = findLines(ipFilledDetection, true);
		List<Coordinates<Integer>> posVerticalList = findLines(ipFilledDetection, false);
		// Visual Test -> Grey surrounding contur
		putConturPixel(ipFilledDetection, posHorizontalList, posVerticalList);

		// TODO findCircleContur();
		// gehe durch alle horizontalen position
		for (Coordinates<Integer> hortCord : posHorizontalList) {
			// suche nach korrespondierenden vertikalen positionen
			for (Coordinates<Integer> vertCord : posVerticalList) {
				// Gleicher Start
				if (vertCord.equalsStart(hortCord)) {
					// ignoriere einzelne pixel
					if (!vertCord.equalsStop(hortCord))
						detectContur(ipFilledDetection, posHorizontalList, posVerticalList, hortCord, vertCord);
				}
			}
		}

		// TODO minRadius setzen
		/*
		 * int maxRadius = 0, centerX = 0, centerY = 0, previousY = 0;
		 * 
		 * // TODO verschiedene konturen.. for (Coordinates<Integer> coordinates
		 * : posHorizontalList) { if (coordinates.getyStart() - previousY > 1) {
		 * if (maxRadius > 0) { // findBoundingBoxCircle(ipFilledDetection,
		 * maxRadius, // centerX, centerY); } maxRadius = 0; centerX = 0;
		 * centerY = 0; }
		 * 
		 * // aktualisiere maximale Distanz int currentDistance =
		 * coordinates.getxStop() - coordinates.getxStart(); if (currentDistance
		 * > maxRadius * 2) { maxRadius = currentDistance / 2; centerX =
		 * maxRadius + coordinates.getxStart(); centerY =
		 * coordinates.getyStart(); } previousY = coordinates.getyStart();
		 * 
		 * }
		 * 
		 * // Zeichne noch nicht gezeichnete komponenten if (maxRadius > 0) { //
		 * findBoundingBoxCircle(ipFilledDetection, maxRadius, centerX, //
		 * centerY); }
		 */
		original.show();
		new ImagePlus("Test", ipFilledDetection).show();
		// filledImage.show();

	}

	private void detectContur(ImageProcessor ipFilledDetection, List<Coordinates<Integer>> posHorizontalList,
			List<Coordinates<Integer>> posVerticalList, Coordinates<Integer> hortCord, Coordinates<Integer> vertCord) {
		
		int maxDX = 0;
		int maxDY = 0;
		int centX = 0;
		int centY = 0;
		
		// Suche ende von Vert und begin von anderem hort
		for (Coordinates<Integer> matchHortVert : posHorizontalList) {
			// übereinstimmung
			if (vertCord.getyStop().equals(matchHortVert.getyStart())) {
				// Suche ende von Hort und begin von anderem vert
				for (Coordinates<Integer> matchVertHort : posVerticalList) {
					// übereinstimmung
					if (hortCord.getxStop().equals(matchVertHort.getxStart())) {
						// hortCord, vertCord, match... spannen nun Quadrat auf
						// theoretischer mittelpunkt des Quadrats
						int y = (hortCord.getyStart() + matchHortVert.getyStart()) / 2;
						int x = (vertCord.getxStart() + matchVertHort.getxStart()) / 2;

						int diaX = 0;
						int diaY = 0;

						// existiert ein eintrag für den mittelpunkt?
						for (Coordinates<Integer> checkY : posHorizontalList) {
							if (checkY.getyStart() == y) {
								for (Coordinates<Integer> checkX : posVerticalList) {
									if (checkX.getxStart() == x) {
										diaX = checkY.getxStop() - checkY.getxStart();
										diaY = checkX.getyStop() - checkX.getyStart();
										
										if(maxDX < diaX || maxDY < diaY ){
											maxDX = diaX;
											maxDY = diaY;
											centX = x;
											centY = y;
										}else{
											break;
										}
										if(diaX < 30 || diaY < 30)
											break;
										// check if diameters are in range
										if (diaY < diaX * 1.05 && diaY > diaX * 0.95) {
											if (diaX < diaY * 1.05 && diaX > diaY * 0.95) {
												//ipFilledDetection.putPixel(x, y, 196);
												//findBoundingBoxCircle(ipFilledDetection, diaX / 2, x, y);
												
												//ipFilledDetection.setRoi(x-diaX/2, y-diaY/2, diaX, diaY);
												//ImageProcessor ip = ipFilledDetection.crop();
												//new ImagePlus("Test", ip).show();
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		ipFilledDetection.putPixel(centX, centY, 196);
	}

	/**
	 * normalize the rgb-vector to a vector with length 1
	 * 
	 * @param ipFilterAndRed
	 */
	private void normalizeRGB(ImageProcessor ipFilterAndRed) {
		for (int y = 0; y < ipFilterAndRed.getHeight(); y++) {
			for (int x = 0; x < ipFilterAndRed.getWidth(); x++) {
				int iArray[] = null;
				iArray = ipFilterAndRed.getPixel(x, y, null);
				double l = Math.sqrt(Math.pow(iArray[0], 2) + Math.pow(iArray[1], 2) + Math.pow(iArray[2], 2));

				if (l > 0) {
					iArray[0] = (int) (iArray[0] * 255 / l);
					iArray[1] = (int) (iArray[1] * 255 / l);
					iArray[2] = (int) (iArray[2] * 255 / l);
				} else {
					iArray[0] = 0;
					iArray[1] = 0;
					iArray[2] = 0;
				}
				ipFilterAndRed.putPixel(x, y, iArray);
			}
		}
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
	 * @param isHorizontal
	 * @return List of Lists with contains
	 *         startX[0],startY[1],stopX[2],stopY[3], swapped X,Y if isVertical
	 *         false
	 */
	private List<Coordinates<Integer>> findLines(ImageProcessor ip, boolean isHorizontal) {
		List<Coordinates<Integer>> posList = new ArrayList<>();
		int h = ip.getHeight();
		int w = ip.getWidth();

		// Tausche je nach vertikal oder horizontal
		if (!isHorizontal) {
			h = ip.getWidth();
			w = ip.getHeight();
		}

		for (int y = 0; y < h; y++) {
			boolean foundSth = false;
			Coordinates<Integer> tempCord = new Coordinates<Integer>();
			int x = 0;
			for (x = 0; x < w; x++) {
				byte color = 0;
				if (!isHorizontal) {
					color = (byte) ip.getPixel(y, x);
				} else {
					color = (byte) ip.getPixel(x, y);
				}

				if (color == 0 && !foundSth) {
					// Überprüfe ob mehrere black lines
					if (!tempCord.isStartEmpty()) {
						if (!isHorizontal)
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

			if (isHorizontal) {
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
