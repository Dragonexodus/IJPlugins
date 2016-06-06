import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA32a_ implements PlugIn {

    private static Double d = 4.0;
    private static Double num = 50.0;

    boolean runDialog() {
        // create the dialog and show:
        GenericDialog gd = new GenericDialog("Schachbrett");
        gd.addNumericField("d:", d, 0);
        gd.addNumericField("num:", num, 0);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        else {
            d = gd.getNextNumber();
            num = gd.getNextNumber();
            return true;
        }
    }

    public void run(String arg) {
        if (runDialog()) {
            int w = 400;
            int h = w;
            ImageProcessor ip = new ColorProcessor(w, h);

            ip.setColor(Integer.MAX_VALUE);
            ip.setLineWidth(d.intValue());

            for (int i = 0; i < num; i++) {
                Double d = i * (this.d * 4);        // *4, da offset
                Double offset = w / 2 - (d / 2);
                ip.drawOval(offset.intValue(), offset.intValue(), d.intValue(), d.intValue());
            }

            new ImagePlus("Kreise", ip).show();
        }
    }
}
