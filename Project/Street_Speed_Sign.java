import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.io.FileSaver;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import plugins.pA.HoughCircles;
import plugins.pA.OcrString;
import plugins.pM.ApplyResult;
import plugins.pM.SpeedObject;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by celentano on 13.06.16.
 */
public class Street_Speed_Sign implements PlugIn {

    private static Integer imgCounter = 0;
    private ArrayList<SpeedObject<Integer>> speedList;
    private int hit;

    @Override
    public void run(String arg) {
//TODO ACHTUNG: GOCR Pfad ist seltsam genauso wie plugins/img.png -PFAD
        // GenericDialog -------------------------------------------------------
        GenericDialog gd = new GenericDialog("Street Speed Sign", IJ.getInstance());
        //gd.addStringField("Filepath", "/home/dragonexodus/Digitalebilderverarbeitung/Projekt/7.png",50);
        gd.addStringField("Filepath", "plugins/Project/result/vlcsnap-2016-05-04-14h27m18s219.png", 50);
        gd.addNumericField("Minimum radius: ", 10, 0);
        gd.addNumericField("Maximum radius: ", 50, 0);
        gd.addNumericField("Increment radius: ", 2, 0);
        gd.addNumericField("Number of circles", 6, 0);
        gd.addNumericField("Hits to detect circle", 6, 0);

        gd.showDialog();
        if (gd.wasCanceled())
            return;

        String imgFile = gd.getNextString();
        int radiusMin = (int) gd.getNextNumber();
        int radiusMax = (int) gd.getNextNumber();
        int radiusInc = (int) gd.getNextNumber();
        int maxCircles = (int) gd.getNextNumber();
        hit = (int) gd.getNextNumber();
        // ---------------------------------------------------------------------

        // imgFile =
        // "plugins/Project/result/vlcsnap-2016-05-04-14h27m18s219.png";
        // imgFile =
        // "plugins/Project/result/vlcsnap-2016-05-04-14h26m18s343.png";

        if (!(new File(imgFile)).exists()) {
            IJ.log("File not found: " + imgFile);
            return;
        }

        ImagePlus imgOrigin = new ImagePlus(imgFile);
        ImagePlus imgDup = imgOrigin.duplicate();
        ImagePlus imgG8 = getRedRegionsImg(imgDup);

        HoughCircles hc = new HoughCircles();
        hc.setParameters(radiusMin, radiusMax, radiusInc, maxCircles, 50);
        hc.run(imgG8.getProcessor());
        speedList = hc.getSpeedList();

        // imgG8.show();
        checkCirkles(imgG8, speedList);

        checkSpeedSign(imgDup, speedList);

        ApplyResult ar = new ApplyResult(speedList, imgFile);

        // info Testausgabe
        // for (int i = 0; i < speedList.size(); i++)
        // IJ.log("point: " + speedList.get(i).getxCenter() + "," +
        // speedList.get(i).getyCenter() + " radius: " +
        // speedList.get(i).getRadius());
    }

    private ImagePlus getRedRegionsImg(ImagePlus img) {
        Integer w = img.getWidth();
        Integer h = img.getHeight();
        ByteProcessor ip = new ByteProcessor(w, h);

        Double mult = 2.0;

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int[] color = img.getPixel(x, y);
                if (color[0] > 0 && color[0] > mult * color[1] && color[0] > mult * color[2])
                    ip.putPixel(x, y, 255);
            }
        imgCounter++;
        return new ImagePlus("img_" + imgCounter.toString(), ip);
    }

    private void checkCirkles(ImagePlus img, ArrayList<SpeedObject<Integer>> speedList) {

        // info Testausgabe
        boolean printTest = false;
        ImagePlus imgRgb = img.duplicate();
        ImageConverter ic = new ImageConverter(imgRgb);
        ic.convertToRGB();
        ImageProcessor ipRgb = imgRgb.getProcessor();
        ipRgb.setColor(255); // blue
        if (printTest)
            imgRgb.show();

        ImageProcessor ipG8 = img.getProcessor();

        for (int i = 0; i < speedList.size(); i++) {
            Integer treffer = 0;
            Integer x = speedList.get(i).getxCenter();
            Integer y = speedList.get(i).getyCenter();
            Integer radius = speedList.get(i).getRadius();
            ArrayList<Point> points = new ArrayList<Point>();
            Double diagonalPixelMultiplier = 1.0 / Math.sqrt(2.0); // 0.7071
            Integer centerOffset = (int) (Math.sin(45.0) * radius * 0.86); // 0.86

            // unten
            Point p = new Point(x, y + radius);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // unten-rechts
            p = new Point(x + centerOffset, y + centerOffset);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // rechts
            p = new Point(x + radius, y);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // rechts-oben
            p = new Point(x + centerOffset, y - centerOffset);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // oben
            p = new Point(x, y - radius);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // oben-links
            p = new Point(x - centerOffset, y - centerOffset);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // links
            p = new Point(x - radius, y);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // lnks-unten
            p = new Point(x - centerOffset, y + centerOffset);
            if (ipG8.getPixel((int) p.getX(), (int) p.getY()) == 255)
                treffer++;
            points.add(p);

            // info Testausgabe
            if (printTest) {
                for (int j = 0; j < points.size(); j++)
                    ipRgb.drawLine(x, y, (int) points.get(j).getX(), (int) points.get(j).getY());
                imgRgb.updateAndDraw();
            }

            if (treffer >= hit)
                IJ.log("point: " + x + "," + y + " radius:" + radius);
            else {
                speedList.remove(i);
                i--;
            }
        }
    }

    private void checkSpeedSign(ImagePlus img, ArrayList<SpeedObject<Integer>> speedList) {

        ArrayList<Integer> speedSigns = new ArrayList<Integer>();
        speedSigns.add(5);
        speedSigns.add(10);
        speedSigns.add(20);
        speedSigns.add(30);
        speedSigns.add(40);
        speedSigns.add(50);
        speedSigns.add(60);
        speedSigns.add(80);
        speedSigns.add(90);
        speedSigns.add(100);
        speedSigns.add(110);
        speedSigns.add(120);
        speedSigns.add(130);

        for (int i = 0; i < speedList.size(); i++) {
            Integer x = speedList.get(i).getxCenter();
            Integer y = speedList.get(i).getyCenter();
            Integer radius = speedList.get(i).getRadius();
            Double diagonalPixelMultiplier = 1.0 / Math.sqrt(2.0); // 0.7071
            Integer centerOffset = (int) (Math.sin(45.0) * radius * 0.86); // 0.86

            ImageProcessor ip = img.duplicate().getProcessor();
            ip.setRoi(x - centerOffset, y - centerOffset, centerOffset * 2, centerOffset * 2);
            ImageProcessor ipNew = ip.crop();
            ImagePlus imgNew = new ImagePlus("img", deleteRedColor(ipNew).getProcessor());

            //TODO einige Bilder werden nicht mehr erkannt
            imgNew.getProcessor().dilate();

            //TODO KOMISCHEN Pfad ÄNDERN
            new FileSaver(imgNew).saveAsPgm("plugins/img.pgm");

            String speed = OcrString.getString("plugins/img.png");

            if (speed != null) {
                String pattern = "\\D+";
                speed = speed.replaceAll(pattern, "");
                if (speed.length() > 0) {
                    if (speedSigns.contains(Integer.parseInt(speed))) {
                        speedList.get(i).setSpeed(Integer.parseInt(speed));
                        IJ.log("speed: " + speed);
                    } else {
                        speedList.remove(i);
                        i--;
                    }
                } else {
                    speedList.remove(i);
                    i--;
                }
            } else {
                speedList.remove(i);
                i--;
            }
        }
    }

    private ImagePlus deleteRedColor(ImageProcessor ip_) {
        ImagePlus img = new ImagePlus("img", ip_);
        ImageProcessor ip = img.getProcessor();
        Double mult = 1.33;

        for (int y = 0; y < img.getHeight(); y++)
            for (int x = 0; x < img.getWidth(); x++) {
                int[] color = img.getPixel(x, y);
                if (color[0] > 0 && color[0] > mult * color[1] && color[0] > mult * color[2])
                    ip.putPixel(x, y, Integer.MAX_VALUE);
            }
        /*
		 * ImageConverter ic = new ImageConverter(img); ic.convertToGray8();
		 */

        // Convert to Binary
        ImagePlus imgBin = new ImagePlus("Binary", img.getProcessor());
        IJ.runPlugIn(imgBin, "ij.plugin.Thresholder", "");

        return imgBin;
    }
}
