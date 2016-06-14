package plugins.pA;

/**
 * Created by celentano on 13.06.16.
 */
public class RegExpTest {
    public static void main(String args[]) {
        String str = "\"50";
        String pattern = "\\D+";
        str = str.replaceAll(pattern, "");
        System.out.println(str);
    }
}
