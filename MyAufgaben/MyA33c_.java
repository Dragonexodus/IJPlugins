import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA33c_ implements PlugIn {

    private static int w = 20;

    boolean runDialog() {
        // create the dialog and show:
        GenericDialog gd = new GenericDialog("Schachbrett");
        gd.addNumericField("w:", w, 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        else {
            w = (int) gd.getNextNumber();
            return true;
        }
    }

    public void run(String arg) {
        if (runDialog()) {
            int w = 400, h = 400;
            ImageProcessor ip = new ColorProcessor(w, h);
            int[] pixels = (int[]) ip.getPixels();
            int i = 0;
            int j = 0;  // counter
            for (int y = 0; y < h; y++) {
                if (y != 0 && y % this.w == 0)
                    j++;
                for (int x = 0; x < w; x++) {
                    if (j % 2 == 0)
                        pixels[i] = 0;                          // schwarz
                    else if (j % 2 != 0)
                        pixels[i] = (255 << 16) | (255 << 8);   // gelb
                    i++;
                }
            }
            new ImagePlus("Gelb", ip).show();
        }
    }
}