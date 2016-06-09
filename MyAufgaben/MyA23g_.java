import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.ImageRoi;
import ij.gui.Overlay;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.*;

public class MyA23g_ implements ExtendedPlugInFilter, DialogListener {

    ImagePlus img;
    private static int width = 20;
    boolean previewing;

    public int setup(String s, ImagePlus imp) {
        img = imp;
        ImageConverter ic = new ImageConverter(img);
        ic.convertToRGB();
        return DOES_ALL;
    }

    @Override
    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        Overlay ovly = imp.getOverlay();
        GenericDialog gd = new GenericDialog("A23f", IJ.getInstance());
        gd.addNumericField("width:", width, 0);

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
        width = (int) gd.getNextNumber();

        return !gd.invalidNumber();
    }

    public void run(ImageProcessor ip) {
        if (ip == null) return; //preview interrupted?
        ip.setLineWidth(1);
        ip.setColor(new Color(0, 255, 0));

        for (int i = 1; i < (img.getWidth() / width) + 1; i++) {
            ip.drawLine(i * width, 0, i * width, ip.getHeight());
        }
        for (int i = 0; i < (img.getHeight() / width) + 1; i++) {
            ip.drawLine(0, i * width, ip.getWidth(), i * width);
        }

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