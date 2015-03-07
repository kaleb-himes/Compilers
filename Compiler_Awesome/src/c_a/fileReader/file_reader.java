/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_a.fileReader;

import static c_a.MPscanner.fWriter;
import static c_a.MPscanner.pbr;
import static c_a.mp.fLocation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 *
 * @author khimes
 */
public class file_reader {
    public static BufferedReader reader;
    public static String outLocation;
    public static String outFile;
    public static synchronized void Initialize() throws FileNotFoundException, UnsupportedEncodingException {
        
        /* file reader for reading in program to be compiled */
        fileReaderInit(fLocation);
        
        pbr = new PushbackReader(reader, 5);
        
        
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        outLocation = "src/parser_resources/" + outFile;
        /*scan.nextLine();*/
        
        File f = new File(outLocation);

        if (f.exists() && !f.isDirectory()) {
            fWriter = new PrintWriter(outLocation, "Ascii");
            System.out.println("\nOutput file initialized for Scanner");
            System.out.println("Scanning output to: " + helper + "/" + outLocation);
        } else {
            System.out.println("Error: output file missing please \"touch"
                    + "scanner.out\" in parser_resources directory.");
        }
    }
    
    public static synchronized void fileReaderInit(String fileName) throws FileNotFoundException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("Ascii")));
    }
}
