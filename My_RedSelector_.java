/**
 * Created by celentano on 03.05.16.
 */

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class My_RedSelector_ implements PlugInFilter {
    private static Integer counter = 0;
    private ImagePlus img;

    public int setup(String arg, ImagePlus imp) {
        img = imp.duplicate();
        return DOES_RGB;
    }

    public void run(ImageProcessor ip) {

        Integer w = img.getWidth();
        Integer h = img.getHeight();
        ByteProcessor ipNew = new ByteProcessor(w, h);

        Double mult = 2.0;

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int[] color = img.getPixel(x, y);
                if (color[0] > 0 && color[0] > mult * color[1] && color[0] > mult * color[2])
                    ipNew.putPixel(x, y, 255);
            }

        counter++;
        new ImagePlus("RedSelector " + counter.toString(), ipNew).show();
    }
}
