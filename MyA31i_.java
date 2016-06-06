import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31i_ implements PlugIn {

    public void run(String arg) {
        int w = 800, h = 800;
        ImageProcessor ip = new ColorProcessor(w, h);

        int dicke = 21; // 22
        int durchlaeufe = 90;
        ip.setColor(Integer.MAX_VALUE);

        Double d = dicke * 1.0;
        Double temp = 0.0;
        for (int i = 0; i < durchlaeufe; i++) {
            if (i != 0)
                d = dicke * 4 + temp + 4;
            temp = d;
            Double offset = d / 2;
            ip.setLineWidth(dicke);
            ip.drawOval(-offset.intValue(), -offset.intValue(), d.intValue(), d.intValue());
            if (dicke >= 1)
                dicke--;
        }
        ip.setRoi(0, 0, 400, 400);
        ip = ip.crop();
        new ImagePlus("Kreise", ip).show();
    }
}
