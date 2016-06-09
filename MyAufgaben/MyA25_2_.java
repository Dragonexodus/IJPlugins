import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class MyA25_2_ implements PlugInFilter {

    private static String title1 = "";
    private static String title2 = "";
    private static boolean createWindow = true;
    private static int operator;
    private static String[] operators = new String[]{"Add", "Subtract", "Multiply", "Divide", "AND", "OR", "XOR", "Min", "Max", "Average", "Difference", "Copy", "Transparent-zero"};
    private static boolean floatResult;

    private final int C_RED = 1;
    private final int C_GREEN = 2;
    private final int C_BLUE = 3;

    public int setup(String s, ImagePlus imp) {
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
            var11.showDialog();
            if (!var11.wasCanceled()) {
                int index1 = var11.getNextChoiceIndex();
                title1 = titles[index1];
                operator = var11.getNextChoiceIndex();
                int index2 = var11.getNextChoiceIndex();
                title2 = titles[index2];
                ImagePlus img1 = WindowManager.getImage(wList[index1]);
                ImagePlus img2 = WindowManager.getImage(wList[index2]);

                ImageCalculator icalc = new ImageCalculator();

                ImagePlus img3 = icalc.run(operators[operator] + " create", img1, img2);
                ImageConverter ic = new ImageConverter(img3);
                ic.convertToRGB();
                ic = new ImageConverter(img3);
                ic.convertToRGBStack();
                ImageStack is3 = img3.getStack();

                // gewünschten Farbkanal schwärzen (auf 0 setzen)
                is3.getProcessor(C_GREEN).setColor(0);
                is3.getProcessor(C_GREEN).fill();

                img3 = new ImagePlus("new32", is3);
                ic = new ImageConverter(img3);
                ic.convertRGBStackToRGB();

                ImagePlus img4 = icalc.run("add create", img1, img2);
                ic = new ImageConverter(img4);
                ic.convertToRGB();

                ImagePlus img5 = icalc.run("xor create", img4, img3);
                ic = new ImageConverter(img5);
                ic.convertToRGB();
                if (img5 != null)
                    img5.show();
            }
        }
    }
}