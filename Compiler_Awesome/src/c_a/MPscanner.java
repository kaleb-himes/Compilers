package c_a;

import static c_a.mp.fLocation;
import c_a.parser.parser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.PushbackReader;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class MPscanner extends mp {

    static parser parse = new parser();
    protected final Dispatcher dispatch = new Dispatcher();
    public static String lexeme;
    public static char item;
    public static int cNum;
    public static int lNum;
    public static String testFile = mp.argument;

    public static PrintWriter fWriter;
    
    public static PushbackReader pbr;
    public static PrintStream out;
    private static boolean begin = false;

    public static void scanFile() throws IOException {
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        fLocation = "src/testStuff/" + testFile;
        System.out.println("Scanning: " + helper + "/" + fLocation);
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
            c_a.fileReader.file_reader.Initialize();
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
                legitToken = true;
            } else if (c == -1) {
                
                System.out.println("\nScanning Finished");
                /*** change ***/
                // write out the token for EOF
                String token = "MP_EOF";
                lexeme = "-1";
                int lineNo = MPscanner.lineNumber;
                int colNo = MPscanner.colNumber;
                String whitespace = "         ";
                fWriter.print(token + whitespace);
                fWriter.print(lineNo + "   ");
                fWriter.print(colNo + "   ");
                fWriter.println(lexeme);
                //stop the file writer
                fWriter.close();
                
                //run the parser
                parse.runParse();
                /*** change ***/
//                System.exit(0);
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
}
