import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31b_ implements PlugIn {

    public void run(String arg) {
        int w = 400, h = 400;
        ImageProcessor ip = new ColorProcessor(w, h);
        int[] pixels = (int[]) ip.getPixels();
        int i = 0;
        int j = 0;  // counter
        int multiplier = 20;
        for (int y = 0; y < h; y++) {
            if (y != 0 && y % multiplier == 0)
                j++;
            for (int x = 0; x < w; x++) {
                if (j % 2 == 0 && y >= j * multiplier && y < (j * multiplier) + multiplier)
                    pixels[i] = 0;                          // schwarz
                else if (j % 2 != 0 && y >= j * multiplier && y < (j * multiplier) + multiplier)
                    pixels[i] = (255 << 16) | (255 << 8);   // gelb
                i++;
            }
        }
        new ImagePlus("Gelb", ip).show();
    }
}
