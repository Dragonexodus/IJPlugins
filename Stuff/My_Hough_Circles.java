/**
 * houghCircles_.java:
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @author Hemerson Pistori (pistori@ec.ucdb.br) and Eduardo Rocha Costa
 * @created 18 de Mar�o de 2004
 * <p>
 * The Hough Transform implementation was based on
 * Mark A. Schulze applet (http://www.markschulze.net/)
 */

//package sigus.templateMatching;
//import sigus.*;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.util.ArrayList;

/**
 * This ImageJ plugin shows the Hough Transform Space and search for
 * circles in a binary image. The image must have been passed through
 * an edge detection module and have edges marked in white (background
 * must be in black).
 */
public class My_Hough_Circles implements PlugInFilter {

    private static Point centerPoint[]; // Center Points of the Circles Found.
    private final int CR_RADIUS = 0;
    private final int CR_COUNT = 1;
    public int radiusMin;   // Find circles with radius grater or equal radiusMin
    public int radiusMax;   // Find circles with radius less or equal radiusMax
    public int radiusInc;   // Increment used to go from radiusMin to radiusMax
    public int maxCircles;  // Numbers of circles to be found
    public int threshold = -1; // An alternative to maxCircles. All circles with
    public int width;       // Hough Space width (depends on image width)
    public int height;      // Hough Space heigh (depends on image height)
    public int depth;       // Hough Space depth (depends on radius interval)
    public int offset;      // Image Width
    public int offx;        // ROI x offset
    public int offy;        // ROI y offset
    // a value in the hough space greater then threshold are marked. Higher thresholds
    // results in fewer circles.
    byte imageValues[];         // Raw image (returned by ip.getPixels())
    double houghValues[][][];   // Hough Space Values
    boolean useThreshold = false;
    int lut[][][];              // LookUp Table for rsin e rcos values
    private int vectorMaxSize = 500;
    //INFO my
    private ImagePlus img;
    private ArrayList<ArrayList<Integer>> circleRadius = new ArrayList<ArrayList<Integer>>();

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        //INFO my
        img = imp.duplicate();
        return DOES_RGB;
//        return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
    }

    public void run(ImageProcessor ip) {

        //INFO my
        Integer w = img.getWidth();
        Integer h = img.getHeight();
        ByteProcessor ipG8 = new ByteProcessor(w, h);

        Double mult = 2.0;

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int[] color = img.getPixel(x, y);
                if (color[0] > 0 && color[0] > mult * color[1] && color[0] > mult * color[2])
                    ipG8.putPixel(x, y, 255);
            }
        ip = ipG8;
        ImagePlus img1 = new ImagePlus("img1", ipG8);
        img1.show();
        //INFO

        imageValues = (byte[]) ip.getPixels();
        Rectangle r = ip.getRoi();

        offx = r.x;
        offy = r.y;
        width = r.width;
        height = r.height;
        offset = ip.getWidth();

        if (readParameters()) { // Show a Dialog Window for user input of
            // radius and maxCircles.

            houghTransform();

            // Create image View for Hough Transform.
            ImageProcessor newip = new ByteProcessor(width, height);
            byte[] newpixels = (byte[]) newip.getPixels();
            createHoughPixels(newpixels);

            // Create image View for Marked Circles.
            ImageProcessor circlesip = new ByteProcessor(width, height);
            byte[] circlespixels = (byte[]) circlesip.getPixels();

            // Mark the center of the found circles in a new image
            if (useThreshold)
                getCenterPointsByThreshold(threshold);
            else
                getCenterPoints(maxCircles);
            drawCircles(circlespixels);

            //info my
            // hier werden die richtigen Kreise ermittelt
//            ImageConverter ic = new ImageConverter(img1);
//            ic.convertToRGB();
//            ImageProcessor ipRgb = img1.getProcessor();

            for (int i = 0; i < circleRadius.size(); i++) {
                int radius = circleRadius.get(i).get(CR_RADIUS);
                ArrayList<Point> points = new ArrayList<Point>();
                double centerOffset = Math.sin(45.0) * circleRadius.get(i).get(CR_RADIUS) * 0.86;

                // oben
                Point p = new Point();
                p.setLocation(centerPoint[i].getX(), centerPoint[i].getY() + radius);
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

                // oben-rechts
                p = new Point();
                p.setLocation(centerPoint[i].getX() + centerOffset, centerPoint[i].getY() + centerOffset);
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

                // rechts
                p = new Point();
                p.setLocation(centerPoint[i].getX() + radius, centerPoint[i].getY());
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

                // rechts-unten
                p = new Point();
                p.setLocation(centerPoint[i].getX() + centerOffset, centerPoint[i].getY() - centerOffset);
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

                // unten
                p = new Point();
                p.setLocation(centerPoint[i].getX(), centerPoint[i].getY() - radius);
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

                // unten-links
                p = new Point();
                p.setLocation(centerPoint[i].getX() - centerOffset, centerPoint[i].getY() - centerOffset);
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

                // links
                p = new Point();
                p.setLocation(centerPoint[i].getX() - radius, centerPoint[i].getY());
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

                // lnks-oben
                p = new Point();
                p.setLocation(centerPoint[i].getX() - centerOffset, centerPoint[i].getY() + centerOffset);
                if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                    circleRadius.get(i).set(CR_COUNT, circleRadius.get(i).get(CR_COUNT) + 1);
                points.add(p);

//                ipRgb.setColor(255);
//                for (int j = 0; j < points.size(); j++)
//                    ipRgb.drawLine((int) centerPoint[i].getX(), (int) centerPoint[i].getY(), (int) points.get(j).getX(), (int) points.get(j).getY());

                img1.updateAndDraw();
//                IJ.log("r:" + circleRadius.get(i) + " offset: " + centerOffset);

                if (circleRadius.get(i).get(CR_COUNT) >= 7)
                    IJ.log("point:" + centerPoint[i] + " radius:" + circleRadius.get(i));
            }

            // SchieldMarkierung
            /*ByteProcessor ipG8_2 = new ByteProcessor(w, h);
            int whiteValue = 200;

            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++) {
                    int[] color = img.getPixel(x, y);
                    if (color[0] > whiteValue && color[1] > whiteValue && color[2] > whiteValue)
                        ipG8_2.putPixel(x, y, 255);
                }
            new ImagePlus("img2", ipG8_2).show();*/
            //info

            new ImagePlus("Hough Space [r=" + radiusMin + "]", newip).show(); // Shows only the hough space for the minimun radius
            new ImagePlus(maxCircles + " Circles Found", circlesip).show();
        }
    }

    void showAbout() {
        IJ.showMessage("About Circles_...", "This plugin finds n circles\n" +
                "using a basic HoughTransform operator\n." +
                "For better results apply an Edge Detector\n" +
                "filter and a binarizer before using this plugin\n" +
                "\nAuthor: Hemerson Pistori (pistori@ec.ucdb.br)"
        );
    }

    boolean readParameters() {

        GenericDialog gd = new GenericDialog("Hough Parameters", IJ.getInstance());
        gd.addNumericField("Minimum radius (in pixels) :", 10, 0);
        gd.addNumericField("Maximum radius (in pixels)", 20, 0);
        gd.addNumericField("Increment radius (in pixels) :", 2, 0);
        gd.addNumericField("Number of Circles (NC): (enter 0 if using threshold)", 10, 0);
        gd.addNumericField("Threshold: (not used if NC > 0)", 60, 0);

        gd.showDialog();

        if (gd.wasCanceled()) {
            return (false);
        }

        radiusMin = (int) gd.getNextNumber();
        radiusMax = (int) gd.getNextNumber();
        radiusInc = (int) gd.getNextNumber();
        depth = ((radiusMax - radiusMin) / radiusInc) + 1;
        maxCircles = (int) gd.getNextNumber();
        threshold = (int) gd.getNextNumber();
        if (maxCircles > 0) {
            useThreshold = false;
            threshold = -1;
        } else {
            useThreshold = true;
            if (threshold < 0) {
                IJ.showMessage("Threshold must be greater than 0");
                return (false);
            }
        }
        return (true);

    }

    /**
     * The parametric equation for a circle centered at (a,b) with
     * radius r is:
     * <p>
     * a = x - r*cos(theta)
     * b = y - r*sin(theta)
     * <p>
     * In order to speed calculations, we first construct a lookup
     * table (lut) containing the rcos(theta) and rsin(theta) values, for
     * theta varying from 0 to 2*PI with increments equal to
     * 1/8*r. As of now, a fixed increment is being used for all
     * different radius (1/8*radiusMin). This should be corrected in
     * the future.
     * <p>
     * Return value = Number of angles for each radius
     */
    private int buildLookUpTable() {

        int i = 0;
        int incDen = Math.round(8F * radiusMin);  // increment denominator

        lut = new int[2][incDen][depth];

        for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {
            i = 0;
            for (int incNun = 0; incNun < incDen; incNun++) {
                double angle = (2 * Math.PI * (double) incNun) / (double) incDen;
                int indexR = (radius - radiusMin) / radiusInc;
                int rcos = (int) Math.round((double) radius * Math.cos(angle));
                int rsin = (int) Math.round((double) radius * Math.sin(angle));
                if ((i == 0) | (rcos != lut[0][i][indexR]) & (rsin != lut[1][i][indexR])) {
                    lut[0][i][indexR] = rcos;
                    lut[1][i][indexR] = rsin;
                    i++;
                }
            }
        }

        return i;
    }

    private void houghTransform() {

        int lutSize = buildLookUpTable();

        houghValues = new double[width][height][depth];

        int k = width - 1;
        int l = height - 1;

        for (int y = 1; y < l; y++) {
            for (int x = 1; x < k; x++) {
                for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {
                    if (imageValues[(x + offx) + (y + offy) * offset] != 0) {// Edge pixel found
                        int indexR = (radius - radiusMin) / radiusInc;
                        for (int i = 0; i < lutSize; i++) {

                            int a = x + lut[1][i][indexR];
                            int b = y + lut[0][i][indexR];
                            if ((b >= 0) & (b < height) & (a >= 0) & (a < width)) {
                                houghValues[a][b][indexR] += 1;
                            }
                        }
                    }
                }
            }
        }
    }


    // Convert Values in Hough Space to an 8-Bit Image Space.
    private void createHoughPixels(byte houghPixels[]) {
        double d = -1D;
        for (int j = 0; j < height; j++) {
            for (int k = 0; k < width; k++)
                if (houghValues[k][j][0] > d) {
                    d = houghValues[k][j][0];
                }
        }

        for (int l = 0; l < height; l++) {
            for (int i = 0; i < width; i++) {
                houghPixels[i + l * width] = (byte) Math.round((houghValues[i][l][0] * 255D) / d);
            }
        }
    }

    // Draw the circles found in the original image.
    public void drawCircles(byte[] circlespixels) {

        // Copy original input pixels into output
        // circle location display image and
        // combine with saturation at 100
        int roiaddr = 0;
        for (int y = offy; y < offy + height; y++) {
            for (int x = offx; x < offx + width; x++) {
                // Copy;
                circlespixels[roiaddr] = imageValues[x + offset * y];
                // Saturate
                if (circlespixels[roiaddr] != 0)
                    circlespixels[roiaddr] = 100;
                else
                    circlespixels[roiaddr] = 0;
                roiaddr++;
            }
        }
        // Copy original image to the circlespixels image.
        // Changing pixels values to 100, so that the marked
        // circles appears more clear. Must be improved in
        // the future to show the resuls in a colored image.
        //for(int i = 0; i < width*height ;++i ) {
        //if(imageValues[i] != 0 )
        //if(circlespixels[i] != 0 )
        //circlespixels[i] = 100;
        //else
        //circlespixels[i] = 0;
        //}
        if (centerPoint == null) {
            if (useThreshold)
                getCenterPointsByThreshold(threshold);
            else
                getCenterPoints(maxCircles);
        }
        byte cor = -1;
        // Redefine these so refer to ROI coordinates exclusively
        int offset = width;
        int offx = 0;
        int offy = 0;

        for (int l = 0; l < maxCircles; l++) {
            int i = centerPoint[l].x;
            int j = centerPoint[l].y;
            // Draw a gray cross marking the center of each circle.
            for (int k = -10; k <= 10; ++k) {
                int p = (j + k + offy) * offset + (i + offx);
                if (!outOfBounds(j + k + offy, i + offx))
                    circlespixels[(j + k + offy) * offset + (i + offx)] = cor;
                if (!outOfBounds(j + offy, i + k + offx))
                    circlespixels[(j + offy) * offset + (i + k + offx)] = cor;
            }
            for (int k = -2; k <= 2; ++k) {
                if (!outOfBounds(j - 2 + offy, i + k + offx))
                    circlespixels[(j - 2 + offy) * offset + (i + k + offx)] = cor;
                if (!outOfBounds(j + 2 + offy, i + k + offx))
                    circlespixels[(j + 2 + offy) * offset + (i + k + offx)] = cor;
                if (!outOfBounds(j + k + offy, i - 2 + offx))
                    circlespixels[(j + k + offy) * offset + (i - 2 + offx)] = cor;
                if (!outOfBounds(j + k + offy, i + 2 + offx))
                    circlespixels[(j + k + offy) * offset + (i + 2 + offx)] = cor;
            }
        }
    }


    private boolean outOfBounds(int y, int x) {
        if (x >= width)
            return (true);
        if (x <= 0)
            return (true);
        if (y >= height)
            return (true);
        if (y <= 0)
            return (true);
        return (false);
    }

    public Point nthMaxCenter(int i) {
        return centerPoint[i];
    }


    /**
     * Search for a fixed number of circles.
     *
     * @param maxCircles The number of circles that should be found.
     */
    private void getCenterPoints(int maxCircles) {

        centerPoint = new Point[maxCircles];
        int xMax = 0;
        int yMax = 0;
        int rMax = 0;

        for (int c = 0; c < maxCircles; c++) {
            double counterMax = -1;
            for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {

                int indexR = (radius - radiusMin) / radiusInc;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (houghValues[x][y][indexR] > counterMax) {
                            counterMax = houghValues[x][y][indexR];
                            xMax = x;
                            yMax = y;
                            rMax = radius;
                        }
                    }
                }
            }

            centerPoint[c] = new Point(xMax, yMax);
            //info my
            ArrayList<Integer> aTemp = new ArrayList<Integer>();
            aTemp.add(rMax);
            aTemp.add(CR_RADIUS);               // erstmal alles auf 0 setzen
            circleRadius.add(aTemp);
//            IJ.log("point:" + centerPoint[c] + "radius:" + circleRadius.get(c).toString());

            clearNeighbours(xMax, yMax, rMax);
        }
    }


    /**
     * Search circles having values in the hough space higher than a threshold
     *
     * @param threshold The threshold used to select the higher point of Hough Space
     */
    private void getCenterPointsByThreshold(int threshold) {

        centerPoint = new Point[vectorMaxSize];
        int xMax = 0;
        int yMax = 0;
        int countCircles = 0;

        for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {
            int indexR = (radius - radiusMin) / radiusInc;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    if (houghValues[x][y][indexR] > threshold) {

                        if (countCircles < vectorMaxSize) {

                            centerPoint[countCircles] = new Point(x, y);

                            clearNeighbours(xMax, yMax, radius);

                            ++countCircles;
                        } else
                            break;
                    }
                }
            }
        }

        maxCircles = countCircles;
    }

    /**
     * Clear, from the Hough Space, all the counter that are near (radius/2) a previously found circle C.
     *
     * @param x The x coordinate of the circle C found.
     * @param x The y coordinate of the circle C found.
     * @param x The radius of the circle C found.
     */
    private void clearNeighbours(int x, int y, int radius) {

        // The following code just clean the points around the center of the circle found.

        double halfRadius = radius / 2.0F;
        double halfSquared = halfRadius * halfRadius;

        int y1 = (int) Math.floor((double) y - halfRadius);
        int y2 = (int) Math.ceil((double) y + halfRadius) + 1;
        int x1 = (int) Math.floor((double) x - halfRadius);
        int x2 = (int) Math.ceil((double) x + halfRadius) + 1;

        if (y1 < 0)
            y1 = 0;
        if (y2 > height)
            y2 = height;
        if (x1 < 0)
            x1 = 0;
        if (x2 > width)
            x2 = width;

        for (int r = radiusMin; r <= radiusMax; r = r + radiusInc) {
            int indexR = (r - radiusMin) / radiusInc;
            for (int i = y1; i < y2; i++) {
                for (int j = x1; j < x2; j++) {
                    if (Math.pow(j - x, 2D) + Math.pow(i - y, 2D) < halfSquared) {
                        houghValues[j][i][indexR] = 0.0D;
                    }
                }
            }
        }
    }
}


