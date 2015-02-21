package c_a;

import static c_a.mp.fLocation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class MPscanner extends mp {

    protected final Dispatcher dispatch = new Dispatcher();
    public static String lexeme;
    public static char item;
    public static int cNum;
    public static int lNum;
    public static String testFile = mp.argument;

    public static BufferedReader reader;
    public static PushbackReader pbr;
    public static PrintStream out;
    private static boolean begin = false;

    public static void scanFile() throws IOException {
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        System.out.println("Enter path to file that will be compiled.");
        System.out.print("Current Path: " + helper + "/");
        fLocation = "src/testStuff/" + testFile;
        /*scan.nextLine();*/
        
        File f = new File(fLocation);

        if (f.exists() && !f.isDirectory()) {
            char nextT = getNextToken();
//            System.out.println("nextT = " + nextT);
            Dispatcher.handleToken(nextT);
        } else {
            System.out.println("Error: file not found exception.");
        }
        System.setOut(out);
        out.close();
    }

    public static synchronized char getNextToken() throws FileNotFoundException, IOException {
        if (begin == false) {
            Initialize();
            begin = true;
        }

        int c;
        boolean legitToken = false;
        //Eats up white space and illegal items
        while (legitToken == false) {
            c = pbr.read();

            if (c != -1 && c > 32 && c < 127) {
                //unreads the first legit Token and returns.
                pbr.unread(c);
                item = (char) pbr.read();
                pbr.unread(item);
//                mp.colNumber++;
                legitToken = true;
            } else if (c == -1) {
                System.out.println("\nScanning Finished");
                System.exit(0);
            } 
            else if (c == 10) {
                mp.lineNumber++;
                mp.colNumber = 1;
            } else {
                mp.colNumber++;
            }
        }
        return item;
    }

    private static synchronized void Initialize() throws FileNotFoundException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(fLocation), Charset.forName("UTF-8")));
        pbr = new PushbackReader(reader, 5);
    }
}
