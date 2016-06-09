import ij.*;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.macro.Interpreter;
import ij.measure.Calibration;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.StackProcessor;

public class MyA24b_ implements PlugInFilter {

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

    ImagePlus doOperation(ImagePlus img1, ImagePlus img2) {
        ImagePlus img3 = null;
        ImageProcessor ip1 = img1.getProcessor();
        ImageProcessor ip2 = img2.getProcessor();
        int xMin = Math.min(img1.getWidth(), img2.getWidth());
        int yMin = Math.min(img1.getHeight(), img2.getHeight());
        Calibration cal1 = img1.getCalibration();

        if (createWindow) {
            ip1 = this.createNewImage(ip1, ip2);
        } else {
            ImageWindow e = img1.getWindow();
            if (e != null) {
                WindowManager.setCurrentWindow(e);
            } else if (Interpreter.isBatchMode() && !createWindow && WindowManager.getImage(img1.getID()) != null) {
                IJ.selectWindow(img1.getID());
            }
            ip1.snapshot();
            Undo.setup(1, img1);
        }
        for (int x = 0; x < xMin; x++) {
            for (int y = 0; y < yMin; y++) {
                int pixel = ip1.getPixel(x, y) + ip2.getPixel(x, y);
                /*if (pixel > 255)
                    pixel = 255;*/
                ip1.putPixel(x, y, pixel);
            }
        }
        if (!(ip1 instanceof ByteProcessor)) {
            ip1.resetMinAndMax();
        }
        if (createWindow) {
            img3 = new ImagePlus("Result of " + img1.getTitle(), ip1);
            img3.setCalibration(cal1);
        } else {
            img1.updateAndDraw();
        }
        return img3;
    }

    ImagePlus doStackOperation(ImagePlus img1, ImagePlus img2) {
        ImagePlus img3 = null;
        int size1 = img1.getStackSize();
        int size2 = img2.getStackSize();
        int xMin = Math.min(img1.getWidth(), img2.getWidth());
        int yMin = Math.min(img1.getHeight(), img2.getHeight());

        if (size1 > 1 && size2 > 1 && size1 != size2) {
            IJ.error("Image Calculator", "\'Image1\' and \'image2\' must be stacks with the same\nnumber of slices, or \'image2\' must be a single image.");
            return null;
        } else {
            if (createWindow) {
                img3 = this.duplicateStack(img1);
                if (img3 == null) {
                    IJ.error("Calculator", "Out of memory");
                    return null;
                }
            } else
                img3 = img1;

            ImageWindow win = img3.getWindow();
            if (win != null) {
                WindowManager.setCurrentWindow(win);
            } else if (Interpreter.isBatchMode() && !createWindow && WindowManager.getImage(img3.getID()) != null) {
                IJ.selectWindow(img3.getID());
            }

            Undo.reset();
            ImageStack stack1 = img3.getStack();

            if (size2 == 1) {
                ImageProcessor ip2 = img2.getProcessor();
                for (int i = 0; i < size1; i++) {
                    ImageProcessor ip1 = stack1.getProcessor(i);
                    for (int x = 0; x < xMin; x++) {
                        for (int y = 0; y < yMin; y++) {
                            int pixel = ip1.getPixel(x, y) + ip2.getPixel(x, y);
                            ip1.putPixel(x, y, pixel);
                        }
                    }
                }
            } else {
                for (int i = 0; i < size1; i++) {
                    ImageProcessor ip1 = stack1.getProcessor(i);
                    ImageProcessor ip2 = img3.getImageStack().getProcessor(i);
                    for (int x = 0; x < xMin; x++) {
                        for (int y = 0; y < yMin; y++) {
                            int pixel = ip1.getPixel(x, y) + ip2.getPixel(x, y);
                            ip1.putPixel(x, y, pixel);
                        }
                    }
                }
            }

            img3.setStack((String) null, stack1);
            if (img3.getType() != 0) {
                img3.getProcessor().resetMinAndMax();
            }

            /*if (img3 == null) {
                img1.updateAndDraw();
            }*/

            return img3;
        }
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
        ImageStack stack1 = img1.getStack();            // altes stack
        int n = stack1.getSize();
        ImageStack stack2 = img1.createEmptyStack();    // neues stack

        try {
            for (int i = 1; i <= n; ++i) {
                ImageProcessor ip = stack1.getProcessor(i);
                ip.resetRoi();
                ImageProcessor ip2 = ip.crop();
                stack2.addSlice(stack1.getSliceLabel(i), ip2);
            }
        } catch (OutOfMemoryError e) {
            stack2.trim();
            stack2 = null;
            IJ.log("ERR: stack2 = null");
            return null;
        }

        ImagePlus img = new ImagePlus("Result of " + img1.getTitle(), stack2);
        img.setCalibration(cal);
        if (img.getStackSize() == n) {
            int[] var13 = img1.getDimensions();
            img.setDimensions(var13[2], var13[3], var13[4]);
            if (img1.isComposite()) {
                img = new CompositeImage(img, 0);
                ((CompositeImage) img).copyLuts(img1);
            }
            if (img1.isHyperStack()) {
                img.setOpenAsHyperStack(true);
            }
        }
        return img;
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

            GenericDialog gd = new GenericDialog("Image Calculator", IJ.getInstance());

            String var12;
            if (title1.equals("")) {
                var12 = titles[0];
            } else {
                var12 = title1;
            }
            gd.addChoice("Image1:", titles, var12);
            if (title2.equals("")) {
                var12 = titles[0];
            } else {
                var12 = title2;
            }
            gd.addChoice("Image2:", titles, var12);
            gd.addCheckbox("Create new window", createWindow);
            gd.showDialog();
            if (!gd.wasCanceled()) {
                int index1 = gd.getNextChoiceIndex();
                title1 = titles[index1];
                int index2 = gd.getNextChoiceIndex();
                createWindow = gd.getNextBoolean();
                title2 = titles[index2];
                ImagePlus img1 = WindowManager.getImage(wList[index1]);
                ImagePlus img2 = WindowManager.getImage(wList[index2]);
                ImagePlus img3 = this.calculate(img1, img2, false);
                if (img3 != null) {
                    img3.show();
                }
            }
        }
    }
}
