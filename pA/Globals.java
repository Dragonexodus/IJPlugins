package plugins.pA;

/**
 * Created by celentano on 20.06.16.
 */
public class Globals {
    private static boolean imgsShow = false;

    public static boolean isImgsShow() {
        return imgsShow;
    }

    public static void setImgsShow(String s) {
        if (Integer.parseInt(s) > 0)
            Globals.imgsShow = true;
        else
            Globals.imgsShow = false;
    }

    public static void setImgsShow(boolean imgsShow) {
        Globals.imgsShow = imgsShow;
    }
}
