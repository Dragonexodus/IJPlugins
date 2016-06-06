import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This a prototype ImageJ plug-in.
 */
public class MyA31h_ implements PlugIn {

    public void run(String arg) {
        int w = 400, h = 400;
        ImageProcessor ip = new ColorProcessor(w, h);

        int dicke = 8;
        ip.setColor(Integer.MAX_VALUE);
        ip.setLineWidth(dicke);

        for (int i = 0; i < 50; i++) {
            Double d = i * (dicke * 4.0);
            Double offset = d / 2;
            ip.drawOval(200 - offset.intValue(), 200 - offset.intValue(), d.intValue(), d.intValue());
        }

        new ImagePlus("Kreise", ip).show();
    }
}
