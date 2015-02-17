package c_a;

import java.io.IOException;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class mp {
    /* The file location and names example:
     * 
     * computer/somewhere/file.java
     *
     * fLocation = computer/somewhere/
     * fName = file.java
     */

    public static int colNumber = 0;
    public static int lineNumber = 1;
    public static String fLocation = "";
    public static String argument;

    /**
     * @param args the command line arguments if any
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        argument = args[0];

        final MPscanner scanner = new MPscanner();
        scanner.scanFile();

    }
}
