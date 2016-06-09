import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA32b2_ implements PlugIn {

    private static int w = 40;
    private static int h = 40;

    boolean runDialog() {
        // create the dialog and show:
        GenericDialog gd = new GenericDialog("Schachbrett");
        gd.addNumericField("w:", w, 0);
        gd.addNumericField("h:", h, 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        else {
            w = (int) gd.getNextNumber();
            h = (int) gd.getNextNumber();
            return true;
        }
    }

    @Override
    public void run(String arg) {
        if (runDialog()) {
            int wWindow = 400, hWindow = 400;
            ImageProcessor ip = new ColorProcessor(wWindow, hWindow);
            int[] pixels = (int[]) ip.getPixels();
            int i = 0;
            Boolean k = false;
            for (int y = 0; y < hWindow; y++) {
                int j = 0;
                if (y != 0 && y % h == 0)
                    k = !k;
                for (int x = 0; x < wWindow; x++) {
                    if (x != 0 && x % w == 0)
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
}