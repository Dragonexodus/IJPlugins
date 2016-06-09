import ij.*;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.macro.Interpreter;
import ij.measure.Calibration;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class MyA23f_ implements PlugInFilter {

    private static String title1 = "";
    private static boolean createWindow = true;
    private static int x = 0;
    private static int y = 0;
    private static int color = 0;
    private static String[] operators = new String[]{"Add", "Subtract", "Multiply", "Divide", "AND", "OR", "XOR", "Min", "Max", "Average", "Difference", "Copy", "Transparent-zero"};

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

            GenericDialog gd = new GenericDialog("A23f", IJ.getInstance());

            String var12;
            if (title1.equals(""))
                var12 = titles[0];
            else
                var12 = title1;
            gd.addChoice("Image:", titles, var12);

            gd.addNumericField("x:", x, 0);
            gd.addNumericField("y:", y, 0);
            gd.addNumericField("color:", color, 0);
            gd.showDialog();
            if (!gd.wasCanceled()) {
                int index1 = gd.getNextChoiceIndex();
                x = (int) gd.getNextNumber();
                y = (int) gd.getNextNumber();
                color = (int) gd.getNextNumber();
                title1 = titles[index1];
                ImagePlus img1 = WindowManager.getImage(wList[index1]);
                ImageProcessor ip1 = img1.getProcessor();

                ip1.setLineWidth(8);
                ip1.setColor(color);
                ip1.drawDot(x, y);

//                if (img1 != null)
//                    img1.show();
            }
        }
    }
}