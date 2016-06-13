package plugins.pA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OcrString {

    static public String getString(String fileName) {

        Process p;
        String res = null;
        try {
            p = Runtime.getRuntime().exec("plugins/Project/gocr/gocr plugins/img.pgm");
            p.waitFor();
            if (p.getInputStream().available() > 0) {
                res = inputStreamAsStrings(p.getInputStream());
                /*IJ.log("gocr: " + res);
                res = "";*/
            }
            p.destroy();

            //info Dateien löschen
            p = Runtime.getRuntime().exec("/bin/rm plugins/img.pgm");
            p.waitFor();
            p.destroy();
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
