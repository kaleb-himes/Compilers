package c_a;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class C_A {
    /* The file location and names example:
     * 
     * computer/somewhere/file.java
     *
     * fLocation = computer/somewhere/
     * fName = file.java
     */

    public static String fLocation = "";

    /**
     * @param args the command line arguments if any
     */
    public static void main(String[] args) throws IOException {
        /* Scanner will get user input */
        Scanner reader = new Scanner(System.in);

        System.out.println("Enter the file relevant to Compiler_Awesome");
        System.out.println("Example: src/testStuff/test.file");
        
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        
        System.out.println(helper);
        System.out.println("Enter the file Location: ");
        fLocation = reader.nextLine();

        File f = new File(fLocation);
       
        //Monica added, just for testing the digi FSA
        digit_FSA digi = new digit_FSA();
        digi.readFile();
       
        /* If user entered a real file and file is not a directory */
        if (f.exists() && !f.isDirectory()) {
            /* initialize the identifier since file exists */
            //Monica blocked out to make sure mine was working
            //identifier_FSA ident = new identifier_FSA();
            //ident.readFile();
        } else {
            System.out.println("Error: file not found exception.");
        }
    }
}
