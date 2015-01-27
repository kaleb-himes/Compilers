package c_a;

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
    public static String fName = "";

    /**
     * @param args the command line arguments if any
     */
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Example: C:/somedirectory/file.xxx");
        System.out.println("file Location = C:/somedirectory/");
        System.out.println("file Name = file.xxx\n");
        System.out.println("Enter the file Location: ");
        fLocation = reader.nextLine();
        System.out.println("Enter the file Name: ");
        fName = reader.nextLine();
        identifier_FSA ident = new identifier_FSA();
    }

}
