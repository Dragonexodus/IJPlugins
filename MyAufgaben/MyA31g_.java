import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31g_ implements PlugIn {

    public void run(String arg) {
        int w = 400, h = 400;
        ImageProcessor ip = new ColorProcessor(w, h);
        int[] pixels = (int[]) ip.getPixels();
        int i = 0;
        Boolean k = false;
        int multiplier = 25;
        for (int y = 0; y < h; y++) {
            int j = 0;
            if (y != 0 && y % multiplier == 0)
                k = !k;
            for (int x = 0; x < w; x++) {
                if (x != 0 && x % multiplier == 0)
                    j++;
                if (k) {
                    if (j % 2 == 0)
                        pixels[i] = Integer.MAX_VALUE;
                    else if (j % 2 != 0)
                        pixels[i] = 0;
                } else {
                    if (j % 2 == 0)
                        pixels[i] = 0;
                    else if (j % 2 != 0)
                        pixels[i] = Integer.MAX_VALUE;
                }
                i++;
            }
        }
        new ImagePlus("Schachbrett", ip).show();
    }
}
