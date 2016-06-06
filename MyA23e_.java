import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class MyA23e_ implements PlugInFilter {
    ImagePlus imp;

    static double winkel = 0;

    boolean runDialog() {
        // create the dialog and show:
        GenericDialog gd = new GenericDialog("Linear Blending");
        gd.addNumericField("Winkel [0..360]:", winkel, 3);
        gd.showDialog();
        if (gd.wasCanceled())
            return false;
        else {
            winkel = gd.getNextNumber();
            return true;
        }
    }

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
        if (runDialog()) {
            ip.rotate(winkel);
        }
    }
}
