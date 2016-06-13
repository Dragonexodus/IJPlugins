import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import plugins.pA.HoughCircles;
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

    @Override
    public void run(String arg) {
        final String imgFile = "plugins/Project/imgs/vlcsnap-2016-05-04-14h27m18s219.png";

        if (!(new File(imgFile)).exists()) {
            IJ.log("File not found: " + imgFile);
            return;
        }

        ImagePlus imgOrigin = new ImagePlus(imgFile);
        ImagePlus imgDup = imgOrigin.duplicate();
        ImagePlus imgG8 = getRedRegionsImg(imgDup);

        HoughCircles hc = new HoughCircles();
        hc.setParameters(10, 50, 2, 4, 50);
        hc.run(imgG8.getProcessor());
        speedList = hc.getSpeedList();

        imgG8.show();
        checkCirkles(imgG8, speedList);

        //info Testausgabe
        for (int i = 0; i < speedList.size(); i++)
            IJ.log("point: " + speedList.get(i).getxCenter() + "," + speedList.get(i).getyCenter() + " radius: " + speedList.get(i).getRadius());
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

        ImageProcessor ipG8 = img.getProcessor();

        for (int i = 0; i < speedList.size(); i++) {
            Integer treffer = 0;
            Integer x = speedList.get(i).getxCenter();
            Integer y = speedList.get(i).getyCenter();
            Integer radius = speedList.get(i).getRadius();
            ArrayList<Point> points = new ArrayList<Point>();
            Double diagonalPixelMultiplier = 1.0 / Math.sqrt(2.0);          // 0.7071
            Integer centerOffset = (int) (Math.sin(45.0) * radius * 0.86);  // 0.86

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

            //info Testausgabe
            /*ImagePlus imgRgb = img.duplicate();
            ImageConverter ic = new ImageConverter(imgRgb);
            ic.convertToRGB();
            ImageProcessor ipRgb = imgRgb.getProcessor();
            ipRgb.setColor(255);
            for (int j = 0; j < points.size(); j++)
                ipRgb.drawLine(x, y, (int) points.get(j).getX(), (int) points.get(j).getY());
            imgRgb.show();*/

            if (treffer >= 6)
                IJ.log("point: " + x + "," + y + " radius:" + radius);
            else {
                speedList.remove(i);
                i--;
            }
        }
    }
}
