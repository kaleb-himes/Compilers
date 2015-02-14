package c_a;

import static c_a.mp.fLocation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
    
    Dispatcher dispatch = new Dispatcher();
    public static String lexeme;    
    public static char item;
    public static int cNum;
    public static int lNum;
    public static String testFile = "k_id.file";
    
    public static BufferedReader reader;
    public static PushbackReader pbr;
    private static boolean begin = false;
    
    public static void scanFile() throws IOException{
        Scanner scan = new Scanner(System.in);
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        System.out.println("Enter path to file that will be compiled.");
        System.out.print("Current Path: " +helper+"/");
        fLocation = "src/testStuff/" +testFile;/*scan.nextLine();*/

        File f = new File(fLocation);
        
        if (f.exists() && !f.isDirectory()) {
            char nextT = getToken();
//            System.out.println("nextT = " + nextT);
            Dispatcher.handleToken(nextT);
        } else {
            System.out.println("Error: file not found exception.");
        }
        
    }
    
    public static char getToken() throws FileNotFoundException, IOException{
        if (begin == false) {
            System.out.println("\nFrst lne ------------------------------"+mp.lineNumber);
            Initialize();
            begin = true;
        }
        
        int c;
        boolean legitToken = false;
        //Eats up white space and illegal items
        while(legitToken == false) {
            c = pbr.read();
            if (c != -1 && c > 32 && c < 127) {
                //unreads the first legit Token and returns.
                mp.colNumber++;
                pbr.unread(c);
                item = (char) pbr.read();
                pbr.unread(item);
                legitToken = true;
                break;
            }
            else if (c == -1) {
                System.out.println("\n\nProgram Parsed Successfully!\n\n");
                System.exit(0);
            }
            else if (c == 10) {
                mp.lineNumber++;
                mp.colNumber = 0;
                System.out.println("New line ------------------------------"+mp.lineNumber);
            }
            else {
                  mp.colNumber++;
//                System.out.println("Scanner saw this ------------------> " + (int) c);
            }
        }
        return item;
    }
    public static String getLexeme(){
        return lexeme;
    }
    public static int getLineNumber(){
        return lNum;
    }
    public static int getColumnNumber(){
        return cNum;
    }
    private static synchronized void Initialize() throws FileNotFoundException {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(fLocation),Charset.forName("UTF-8")));
        pbr = new PushbackReader(reader, 5);
    }
}
