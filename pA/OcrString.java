package plugins.pA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OcrString {

    static public String getString(String path, String imgName) {

        Process p;
        String res = null;
        try {
            p = Runtime.getRuntime().exec(path + "/nconvert/nconvert -out ppm -o " + path + "/img.ppm " + path + "/img2.png");
            /*p = Runtime.getRuntime().exec("pwd");
            if (p.getInputStream().available() > 0) {
                res = inputStreamAsStrings(p.getInputStream());
                System.out.println("PWD: " + res);
            }*/
            p.waitFor();
//            p.destroy();

            p = Runtime.getRuntime().exec(path + "/gocr/gocr " + path + "/img.ppm");
            p.waitFor();
            if (p.getInputStream().available() > 0) {
                res = inputStreamAsStrings(p.getInputStream());
                System.out.println("RES: " + res);
            }
//            p.destroy();

            //info Dateien löschen
            p = Runtime.getRuntime().exec("/bin/rm " + path + "/img.ppm");
            p.waitFor();
//            p.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
        return res;
    }

    static private String inputStreamAsStrings(InputStream stream) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null)
            sb.append(line + "\n");

        br.close();
        return sb.toString();
    }
}
