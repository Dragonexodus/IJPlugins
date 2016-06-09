import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.ImageRoi;
import ij.gui.Overlay;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

import java.awt.*;

public class MyA23f2_ implements ExtendedPlugInFilter, DialogListener {

    ImagePlus img;
    private static Integer x = 0;
    private static Integer y = 0;
    private static int cR = 255;
    private static int cG = 255;
    private static int cB = 0;
    boolean previewing;

    public int setup(String s, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        img = imp;
        Overlay ovly = imp.getOverlay();
        GenericDialog gd = new GenericDialog("A23f", IJ.getInstance());
        gd.addNumericField("x:", x, 0);
        gd.addNumericField("y:", y, 0);
        gd.addNumericField("cR:", cR, 0);
        gd.addNumericField("cR:", cG, 0);
        gd.addNumericField("cR:", cB, 0);

        gd.addPreviewCheckbox(pfr);
        gd.addDialogListener(this);
        previewing = true;
        gd.showDialog();
        previewing = false;
        imp.setOverlay(ovly);

        if (gd.wasCanceled()) {
            return DONE;
        }
        return DOES_ALL;
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        x = (int) gd.getNextNumber();
        y = (int) gd.getNextNumber();
        cR = (int) gd.getNextNumber();
        cG = (int) gd.getNextNumber();
        cB = (int) gd.getNextNumber();

        return !gd.invalidNumber();
    }

    public void run(ImageProcessor ip) {
        if (ip == null) return; //preview interrupted?
        ip.setLineWidth(8);
        ip.setColor(new Color(cR, cG, cB));
        ip.drawDot(x, y);
        ip.drawString(x.toString() + ":" + y.toString(), x, y);
        if (previewing) {
            if (!Thread.currentThread().isInterrupted())
                img.setOverlay(new Overlay(new ImageRoi(0, 0, ip)));
        } else {
        }
    }

    @Override
    public void setNPasses(int nPasses) {

    }
}