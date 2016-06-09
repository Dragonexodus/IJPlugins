import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31c_ implements PlugIn {

    public void run(String arg) {
        int w = 400, h = 400;
        ImageProcessor ip = new ColorProcessor(w, h);
        int[] pixels = (int[]) ip.getPixels();
        int i = 0;
        int j = 0;  // counter
        int k = 0;
        int multiplier = 20;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                if (x != 0 && x % multiplier == 0)
                    j++;

                if (j % 2 == 0 && x >= (j * multiplier) + y && x < (j * multiplier) + multiplier + y)
                    pixels[i] = 255;                          // schwarz
                else if (j % 2 != 0 && x >= (j * multiplier) + y && x < (j * multiplier) + multiplier + y)
                    pixels[i] = (255 << 16) | (255 << 8);   // gelb
                i++;
            }
            j = 0;
        }
        new ImagePlus("diagonal", ip).show();
    }
}
