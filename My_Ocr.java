/**
 * houghCircles_.java:
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @author Hemerson Pistori (pistori@ec.ucdb.br) and Eduardo Rocha Costa
 * @created 18 de Mar�o de 2004
 * <p>
 * The Hough Transform implementation was based on
 * Mark A. Schulze applet (http://www.markschulze.net/)
 */

//package sigus.templateMatching;
//import sigus.*;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class My_Ocr implements PlugIn{

    @Override
    public void run(String arg) {
        Process p;
        try {
            String res = null;
            p = Runtime.getRuntime().exec("plugins/IJPlugins/nconvert/nconvert -out ppm -o plugins/IJPlugins/img.ppm plugins/IJPlugins/img2.png");
            /*p = Runtime.getRuntime().exec("pwd");
            if (p.getInputStream().available() > 0) {
                res = inputStreamAsStrings(p.getInputStream());
                System.out.println("PWD: " + res);
            }*/

            p.waitFor();
//            p.destroy();
            p = Runtime.getRuntime().exec("plugins/IJPlugins/gocr/gocr plugins/IJPlugins/img.ppm");
            p.waitFor();
            if (p.getInputStream().available() > 0) {
                res = inputStreamAsStrings(p.getInputStream());
                System.out.println("RES: " + res);
            }
//            p.destroy();
            p = Runtime.getRuntime().exec("/bin/rm plugins/IJPlugins/img.ppm");
            if (p.getInputStream().available() > 0) {
                res = inputStreamAsStrings(p.getInputStream());
                System.out.println("rm: " + res);
            }
            p.waitFor();
//            p.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public String inputStreamAsStrings(InputStream stream) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null)
            sb.append(line + "\n");

        br.close();
        return sb.toString();
    }
}


