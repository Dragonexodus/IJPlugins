import ij.*;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.macro.Interpreter;
import ij.measure.Calibration;
import ij.plugin.ImageCalculator;
import ij.plugin.PlugIn;
import ij.plugin.filter.ExtendedPlugInFilter;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.StackProcessor;

import java.awt.*;

import static ij.plugin.filter.PlugInFilter.DOES_ALL;
import static java.text.BreakIterator.DONE;

public class MyA24a_ implements PlugInFilter {

    private static String title1 = "";
    private static String title2 = "";
    private static boolean createWindow = true;

    ImagePlus calculate(ImagePlus img1, ImagePlus img2, boolean apiCall) {
        ImagePlus img3 = null;

        int size1 = img1.getStackSize();
        int size2 = img2.getStackSize();

        if (size1 <= 1 && size2 <= 1) {
            img3 = this.doOperation(img1, img2);        // ein layer (8G)
        } else {
            img3 = this.doStackOperation(img1, img2);   // mehrere layers
        }

        return img3;
    }

    ImagePlus doStackOperation(ImagePlus img1, ImagePlus img2) {
        ImagePlus img3 = null;
        int size1 = img1.getStackSize();
        int size2 = img2.getStackSize();
        if(size1 > 1 && size2 > 1 && size1 != size2) {
            IJ.error("Image Calculator", "\'Image1\' and \'image2\' must be stacks with the same\nnumber of slices, or \'image2\' must be a single image.");
            return null;
        } else {
            if(createWindow) {
                img1 = this.duplicateStack(img1);
                if(img1 == null) {
                    IJ.error("Calculator", "Out of memory");
                    return null;
                }

                img3 = img1;
            }

            int mode = 4;//this.getBlitterMode();
            ImageWindow win = img1.getWindow();
            if(win != null) {
                WindowManager.setCurrentWindow(win);
            } else if(Interpreter.isBatchMode() && !createWindow && WindowManager.getImage(img1.getID()) != null) {
                IJ.selectWindow(img1.getID());
            }

            Undo.reset();
            ImageStack stack1 = img1.getStack();
            StackProcessor sp = new StackProcessor(stack1, img1.getProcessor());

            try {
                if(size2 == 1) {
                    sp.copyBits(img2.getProcessor(), 0, 0, mode);
                } else {
                    sp.copyBits(img2.getStack(), 0, 0, mode);
                }
            } catch (IllegalArgumentException var11) {
                IJ.error("\"" + img1.getTitle() + "\": " + var11.getMessage());
                return null;
            }

            img1.setStack((String)null, stack1);
            if(img1.getType() != 0) {
                img1.getProcessor().resetMinAndMax();
            }

            if(img3 == null) {
                img1.updateAndDraw();
            }

            return img3;
        }
    }

    ImagePlus doOperation(ImagePlus img1, ImagePlus img2) {
        ImagePlus img3 = null;
        int mode = 4;//this.getBlitterMode();
        ImageProcessor ip1 = img1.getProcessor();
        ImageProcessor ip2 = img2.getProcessor();
        Calibration cal1 = img1.getCalibration();
        Calibration cal2 = img2.getCalibration();
        if(createWindow) {
            ip1 = this.createNewImage(ip1, ip2);
        } else {
            ImageWindow e = img1.getWindow();
            if(e != null) {
                WindowManager.setCurrentWindow(e);
            } else if(Interpreter.isBatchMode() && !createWindow && WindowManager.getImage(img1.getID()) != null) {
                IJ.selectWindow(img1.getID());
            }

            ip1.snapshot();
            Undo.setup(1, img1);
        }

        try {
            ip1.copyBits(ip2, 0, 0, mode);
        } catch (IllegalArgumentException var10) {
            IJ.error("\"" + img1.getTitle() + "\": " + var10.getMessage());
            return null;
        }

        if(!(ip1 instanceof ByteProcessor)) {
            ip1.resetMinAndMax();
        }

        if(createWindow) {
            img3 = new ImagePlus("Result of " + img1.getTitle(), ip1);
            img3.setCalibration(cal1);
        } else {
            img1.updateAndDraw();
        }

        return img3;
    }

    ImageProcessor createNewImage(ImageProcessor ip1, ImageProcessor ip2) {
        int width = Math.min(ip1.getWidth(), ip2.getWidth());
        int height = Math.min(ip1.getHeight(), ip2.getHeight());
        ImageProcessor ip3 = ip1.createProcessor(width, height);

        ip3.insert(ip1, 0, 0);
        return ip3;
    }

    ImagePlus duplicateStack(ImagePlus img1) {
        Calibration cal = img1.getCalibration();
        ImageStack stack1 = img1.getStack();
        int width = stack1.getWidth();
        int height = stack1.getHeight();
        int n = stack1.getSize();
        ImageStack stack2 = img1.createEmptyStack();

        try {
            for(int img3 = 1; img3 <= n; ++img3) {
                ImageProcessor dim = stack1.getProcessor(img3);
                dim.resetRoi();
                ImageProcessor ip2 = dim.crop();

                stack2.addSlice(stack1.getSliceLabel(img3), ip2);
            }
        } catch (OutOfMemoryError var11) {
            stack2.trim();
            stack2 = null;
            return null;
        }

        Object var12 = new ImagePlus("Result of " + img1.getTitle(), stack2);
        ((ImagePlus)var12).setCalibration(cal);
        if(((ImagePlus)var12).getStackSize() == n) {
            int[] var13 = img1.getDimensions();
            ((ImagePlus)var12).setDimensions(var13[2], var13[3], var13[4]);
            if(img1.isComposite()) {
                var12 = new CompositeImage((ImagePlus)var12, 0);
                ((CompositeImage)var12).copyLuts(img1);
            }

            if(img1.isHyperStack()) {
                ((ImagePlus)var12).setOpenAsHyperStack(true);
            }
        }

        return (ImagePlus)var12;
    }

    public int setup(String s, ImagePlus imp) {
//        IJ.log("SETUP");
        return DOES_ALL;
    }

    public void run(ImageProcessor imageProcessor) {
        int[] wList = WindowManager.getIDList();
        if (wList == null) {
            IJ.noImage();
        } else {
            IJ.register(ImageCalculator.class);
            String[] titles = new String[wList.length];

            for (int gd = 0; gd < wList.length; ++gd) {
                ImagePlus defaultItem = WindowManager.getImage(wList[gd]);
                if (defaultItem != null) {
                    titles[gd] = defaultItem.getTitle();
                } else {
                    titles[gd] = "";
                }
            }

            GenericDialog var11 = new GenericDialog("Image Calculator", IJ.getInstance());

            String var12;
            if (title1.equals("")) {
                var12 = titles[0];
            } else {
                var12 = title1;
            }
            var11.addChoice("Image1:", titles, var12);
            if (title2.equals("")) {
                var12 = titles[0];
            } else {
                var12 = title2;
            }
            var11.addChoice("Image2:", titles, var12);
            var11.addCheckbox("Create new window", createWindow);
            var11.showDialog();
            if (!var11.wasCanceled()) {
                int index1 = var11.getNextChoiceIndex();
                title1 = titles[index1];
                int index2 = var11.getNextChoiceIndex();
                createWindow = var11.getNextBoolean();
                title2 = titles[index2];
                ImagePlus img1 = WindowManager.getImage(wList[index1]);
                ImagePlus img2 = WindowManager.getImage(wList[index2]);
                ImagePlus img3 = this.calculate(img1, img2, false);
                if (img3 != null) {
                    img3.show();
                } else
                    IJ.log("ERR: img3 = null");
            }
        }
    }
}
