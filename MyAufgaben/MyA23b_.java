/**
 * Created by celentano on 03.05.16.
 */

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class MyA23b_ implements PlugInFilter {
    public MyA23b_() {
    }

    public int setup(String var1, ImagePlus var2) {
        return DOES_ALL;
    }

    public void run(ImageProcessor var1) {
        int var2 = var1.getWidth();
        int var3 = var1.getHeight();

        for (int var4 = 0; var4 < var2; ++var4) {
            for (int var5 = 0; var5 < var3; ++var5) {
                int var6 = var1.getPixel(var4, var5);
                if (var6 >= 120 && var6 <= 130)
                    var1.putPixel(var4, var5, 0);
            }
        }
    }
}
