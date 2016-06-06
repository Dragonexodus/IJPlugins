/**
 * Created by celentano on 03.05.16.
 */

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

// 0----51----102----153----204----255

public class MyA23c_ implements PlugInFilter {

    public int setup(String s, ImagePlus img) {
        return DOES_8G;
    }

    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int pixel = ip.getPixel(x, y);
                int val = pixel / 51;
                if (val == 5)
                    ip.putPixel(x, y, 4 * 51);
                else
                    ip.putPixel(x, y, val * 51);
            }
        }
    }
}
