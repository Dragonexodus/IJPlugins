import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31f_ implements PlugIn {

    public void run(String arg) {
        int w = 400, h = 400;
        ImageProcessor ip = new ColorProcessor(w, h);
        int[] pixels = (int[]) ip.getPixels();
        int i = 0;
        int multiplier = 20;
        for (int y = 0; y < h; y++) {
            int j = 0;
            int k = 0;
            int l = 0;
            for (int x = 0; x < w; x++) {
                if (l != 0 && l % (multiplier - k) == 0) {
                    j++;
                    l = 0;
                    if (j % 2 == 0) {
                        if (k >= multiplier - 1)
                            k = multiplier - 1;
                        else
                            k++;
                    }
                }
                if (j % 2 == 0)
                    pixels[i] = 255;
                else
                    pixels[i] = Integer.MAX_VALUE;
                i++;
                l++;
            }
        }
        new ImagePlus("Saege", ip).show();
    }
}
