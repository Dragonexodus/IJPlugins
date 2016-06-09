import ij.*;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.macro.Interpreter;
import ij.measure.Calibration;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class MyA25_ implements PlugInFilter {

    private static String title1 = "";
    private static String title2 = "";
    private static boolean createWindow = true;
    private static int operator;
    private static String[] operators = new String[]{"Add", "Subtract", "Multiply", "Divide", "AND", "OR", "XOR", "Min", "Max", "Average", "Difference", "Copy", "Transparent-zero"};
    private static boolean floatResult;

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
            var11.addChoice("Operation:", operators, operators[operator]);
            if (title2.equals("")) {
                var12 = titles[0];
            } else {
                var12 = title2;
            }
            var11.addChoice("Image2:", titles, var12);
//            var11.addCheckbox("Create new window", createWindow);
            var11.showDialog();
            if (!var11.wasCanceled()) {
                int index1 = var11.getNextChoiceIndex();
                title1 = titles[index1];
                operator = var11.getNextChoiceIndex();
                int index2 = var11.getNextChoiceIndex();
//                createWindow = var11.getNextBoolean();
                title2 = titles[index2];
                ImagePlus img1 = WindowManager.getImage(wList[index1]);
                ImagePlus img2 = WindowManager.getImage(wList[index2]);
                ImagePlus img3 = this.calculate(img1, img2, -1);
                ImageConverter ic = new ImageConverter(img3);
                ic.convertToRGB();
                ic = new ImageConverter(img3);
                ic.convertToRGBStack();
                ImageStack is3 = img3.getStack();
//                IJ.log("img3 stackSize: " + is3.size());
                for (int is = 0; is < 1/*is3.size() - 1*/; is++) {
                    for (int x = 0; x < img3.getWidth(); x++) {
                        for (int y = 0; y < img3.getHeight(); y++) {
                            // nur Grün auswählen
                            is3.getProcessor(is + 2).putPixel(x, y, 0);
                        }
                    }
                }
                img3 = new ImagePlus("new32", is3);
                ic = new ImageConverter(img3);
                ic.convertRGBStackToRGB();

                ImagePlus img4 = this.calculate(img1, img2, 0); // add
                ic = new ImageConverter(img4);
                ic.convertToRGB();
//                IJ.log("img4 stackSize: " + img4.getStackSize());

                ImagePlus img5 = this.calculate(img4, img3, 6); // xor
                ic = new ImageConverter(img5);
                ic.convertToRGB();
                if (img5 != null)
                    img5.show();
            }
        }
    }

    ImagePlus calculate(ImagePlus img1, ImagePlus img2, int operator) {
        if (operator != -1)
            this.operator = operator;   // Operator manuell festlegen

        ImagePlus img3 = null;
        if (img1.getCalibration().isSigned16Bit() || img2.getCalibration().isSigned16Bit())
            floatResult = true;
        if (floatResult && (img1.getBitDepth() != 32 || img2.getBitDepth() != 32))
            createWindow = true;

        int size1 = img1.getStackSize();
        int size2 = img2.getStackSize();

        if (size1 <= 1 && size2 <= 1)
            img3 = this.doOperation(img1, img2);        // ein layer (8G)
        else
            img3 = this.doStackOperation(img1, img2);   // mehrere layers
        return img3;
    }

    ImagePlus doOperation(ImagePlus img1, ImagePlus img2) {
        ImagePlus img3 = null;
        int mode = getBlitterMode();
        ImageProcessor ip1 = img1.getProcessor();
        ImageProcessor ip2 = img2.getProcessor();
        Calibration cal1 = img1.getCalibration();
        Calibration cal2 = img2.getCalibration();
        if (createWindow)
            ip1 = createNewImage(ip1, ip2);
        else {
            ImageWindow win = img1.getWindow();
            if (win != null)
                WindowManager.setCurrentWindow(win);
            else if (Interpreter.isBatchMode() && !createWindow && WindowManager.getImage(img1.getID()) != null)
                IJ.selectWindow(img1.getID());
            ip1.snapshot();
            Undo.setup(Undo.FILTER, img1);
        }
        if (floatResult) ip2 = ip2.convertToFloat();
        try {
            ip1.copyBits(ip2, 0, 0, mode);
        } catch (IllegalArgumentException e) {
            IJ.error("\"" + img1.getTitle() + "\": " + e.getMessage());
            return null;
        }
        if (!(ip1 instanceof ByteProcessor))
            ip1.resetMinAndMax();
        if (createWindow) {
            img3 = new ImagePlus("Result of " + img1.getTitle(), ip1);
            img3.setCalibration(cal1);
        } else
            img1.updateAndDraw();
        return img3;
    }

    /**
     * img1 = img2 op img2 (e.g. img1 = img2/img1)
     */
    ImagePlus doStackOperation(ImagePlus img1, ImagePlus img2) {
        ImagePlus img3 = null;
        int size1 = img1.getStackSize();
        int size2 = img2.getStackSize();
        if (size1 > 1 && size2 > 1 && size1 != size2) {
            IJ.error("Image Calculator", "'Image1' and 'image2' must be stacks with the same\nnumber of slices, or 'image2' must be a single image.");
            return null;
        }
        if (createWindow) {
            img1 = duplicateStack(img1);
            if (img1 == null) {
                IJ.error("Calculator", "Out of memory");
                return null;
            }
            img3 = img1;
        }
        int mode = getBlitterMode();
        ImageWindow win = img1.getWindow();
        if (win != null)
            WindowManager.setCurrentWindow(win);
        else if (Interpreter.isBatchMode() && !createWindow && WindowManager.getImage(img1.getID()) != null)
            IJ.selectWindow(img1.getID());
        Undo.reset();
        ImageStack stack1 = img1.getStack();
        StackProcessor sp = new StackProcessor(stack1, img1.getProcessor());
        try {
            if (size2 == 1)
                sp.copyBits(img2.getProcessor(), 0, 0, mode);
            else
                sp.copyBits(img2.getStack(), 0, 0, mode);
        } catch (IllegalArgumentException e) {
            IJ.error("\"" + img1.getTitle() + "\": " + e.getMessage());
            return null;
        }
        img1.setStack(null, stack1);
        if (img1.getType() != ImagePlus.GRAY8) {
            img1.getProcessor().resetMinAndMax();
        }
        if (img3 == null)
            img1.updateAndDraw();
        return img3;
    }

    ImageProcessor createNewImage(ImageProcessor ip1, ImageProcessor ip2) {
        int width = Math.min(ip1.getWidth(), ip2.getWidth());
        int height = Math.min(ip1.getHeight(), ip2.getHeight());
        ImageProcessor ip3 = ip1.createProcessor(width, height);
        if (floatResult) {
            ip1 = ip1.convertToFloat();
            ip3 = ip3.convertToFloat();
        }
        ip3.insert(ip1, 0, 0);
        return ip3;
    }

    ImagePlus duplicateStack(ImagePlus img1) {
        Calibration cal = img1.getCalibration();
        ImageStack stack1 = img1.getStack();
        int n = stack1.getSize();
        ImageStack stack2 = img1.createEmptyStack();
        try {
            for (int i = 1; i <= n; i++) {
                ImageProcessor ip1 = stack1.getProcessor(i);
                ip1.resetRoi();
                ImageProcessor ip2 = ip1.crop();
                if (floatResult) {
                    ip2.setCalibrationTable(cal.getCTable());
                    ip2 = ip2.convertToFloat();
                }
                stack2.addSlice(stack1.getSliceLabel(i), ip2);
            }
        } catch (OutOfMemoryError e) {
            stack2.trim();
            stack2 = null;
            return null;
        }
        ImagePlus img3 = new ImagePlus("Result of " + img1.getTitle(), stack2);
        img3.setCalibration(cal);
        if (img3.getStackSize() == n) {
            int[] dim = img1.getDimensions();
            img3.setDimensions(dim[2], dim[3], dim[4]);
            if (img1.isComposite()) {
                img3 = new CompositeImage(img3, 0);
                ((CompositeImage) img3).copyLuts(img1);
            }
            if (img1.isHyperStack())
                img3.setOpenAsHyperStack(true);
        }
        return img3;
    }

    private int getBlitterMode() {
        int mode = 0;
        switch (operator) {
            case 0:
                mode = Blitter.ADD;
                break;
            case 1:
                mode = Blitter.SUBTRACT;
                break;
            case 2:
                mode = Blitter.MULTIPLY;
                break;
            case 3:
                mode = Blitter.DIVIDE;
                break;
            case 4:
                mode = Blitter.AND;
                break;
            case 5:
                mode = Blitter.OR;
                break;
            case 6:
                mode = Blitter.XOR;
                break;
            case 7:
                mode = Blitter.MIN;
                break;
            case 8:
                mode = Blitter.MAX;
                break;
            case 9:
                mode = Blitter.AVERAGE;
                break;
            case 10:
                mode = Blitter.DIFFERENCE;
                break;
            case 11:
                mode = Blitter.COPY;
                break;
            case 12:
                mode = Blitter.COPY_ZERO_TRANSPARENT;
                break;
        }
        return mode;
    }
}