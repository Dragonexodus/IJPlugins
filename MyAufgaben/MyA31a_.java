import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31a_ implements PlugIn {

    public void run(String arg) {
        int w = 400, h = 400;
        ImageProcessor ip = new ColorProcessor(w, h);
        int[] pixels = (int[]) ip.getPixels();
        int i = 0;
        for (int y = 0; y < h; y++) {
//            int red = (y * 255) / (h - 1);
            for (int x = 0; x < w; x++) {
//                int blue = (x * 255) / (w - 1);
                //             Rot        +  Grün       = Gelb
                pixels[i++] = (255 << 16) | (255 << 8);
            }
        }
        new ImagePlus("Gelb", ip).show();
    }
}

