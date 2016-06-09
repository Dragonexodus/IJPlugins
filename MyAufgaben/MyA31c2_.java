import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31c2_ implements PlugIn {

    public void run(String arg) {
        int w = 400, h = 400;
        ImageProcessor ip = new ColorProcessor(w, h);
        int[] pixels = (int[]) ip.getPixels();
        int j = 0;  // counter
        int[] line = new int[w];
        int multiplier = 20;

        for (int x = 0; x < w; x++) {
            if (x != 0 && x % multiplier == 0)
                j++;
            if (j % 2 == 0 && x >= j * multiplier && x < (j * multiplier) + multiplier)
                line[x] = 255;                          // schwarz
            else if (j % 2 != 0 && x >= j * multiplier && x < (j * multiplier) + multiplier)
                line[x] = (255 << 16) | (255 << 8);     // gelb
        }

        for (int y = 0; y < h; y++)
            for (int x = 0; x < w - y; x++)
                pixels[y * h + x] = line[x + y];
        int i = 0;
        for (int y = h - 1; y > 0; y--) {
            for (int x = w - y; x < w; x++)
                pixels[y * h + x] = line[x - i];
            i++;
        }
        new ImagePlus("Gelb", ip).show();
    }
}
