/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_a;

/**
 *
 * @author khimes
 */
import static c_a.C_A.fLocation;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.Charset;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class comment_FSA extends C_A {

    String lexeme;
    String token;
    char character;
    boolean second_comment;

    /* 
     * flags to indicate whether or not a particular character
     * has already been scanned
     */
    Boolean readPeriod;
    Boolean readOperator;
    Boolean loop;

    public enum State {

        START, COMMENTACCEPT, S0
    }

    State state;

    /**
     *
     * @return @throws FileNotFoundException
     * @throws IOException
     */
    public Character readFile() throws FileNotFoundException, IOException {
        lexeme = "";
        token = "";
        second_comment = false;
        state = State.START;
        readPeriod = false;
        readOperator = false;
        loop = true;
        int c;

        while ((c = MPscanner.pbr.read()) != -1 && loop == true) {
            /* 
             * unreads this character, which is just checking
             * if we are at end of file
             */
            MPscanner.pbr.unread(c);
            c++;

            switch (state) {
                /* START state indicates that nothing has been scanned yet */
                case START:
                    /* 
                     * Read in the first character, which is (as specified by the 
                     * dispatcher) a double quote mark 1 (").
                     */
                    character = (char) MPscanner.pbr.read();

                    /* puts the character in the lexeme */
                    lexeme = Character.toString(character);

                    /* 
                     * transitions to COMMENTACCEPT state 
                     * (because a letter or underscore has been read)
                     */
                    state = State.COMMENTACCEPT;

                    /* end of START case */
                    break;

                /* Accept State for an Identifier Value */
                case COMMENTACCEPT:
                    /* read the next character */
                    character = (char) MPscanner.pbr.read();

                    if (second_comment == false) {
                        if (Character.toString(character).equals("}")) {
                            second_comment = true;
                        }
                        /* if 0-9 | a-z | A-Z | $ | _ then concat to lexeme */
                        if (character == 10) {
                            character = 32;
                            C_A.lineNumber++;
                            System.out.println("New line ------------------------------" + C_A.lineNumber);
                        }
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (second_comment == true) {
                        /*
                         * Checks if character is anything but acceptable
                         * Identifier value and ensures it has not been read
                         * previously
                         */
                        MPscanner.pbr.unread(character);
                        state = State.S0;
                    } else {
                        /* invalid nex character, reset */
                        MPscanner.pbr.unread(character);

                        token = "MP_COMMENT";

                        /* test print-outs */
                        System.out.print(token);
                        System.out.print("          " + C_A.lineNumber);
                        System.out.print("     " + C_A.colNumber);
//                    System.out.println(state);
                        System.out.println("     " + lexeme);

                        /* test print-outs */
//                        character = (char) MPscanner.pbr.read();
//                        System.out.println("--------Reader is at");
//                        System.out.println(Character.toString(character));
//                        MPscanner.pbr.unread(character);
                        /* need to return to dispatcher here but for now exit */
                        return character;
                    }

                    /* END COMMENTACCEPT */
                    break;

                /* 
                 * S0 state indicates all valid characters have been read and 
                 * we encountered something not legal. This could mean either
                 * a valid identifier has been read or an error occured
                 * let the dispatcher handle it
                 */
                case S0:
                    loop = false;
                    token = "MP_COMMENT";
                    /* test print-outs */
                    /* This stuff needs to be written to a file like C does
                     * for assembly stuff, our file would eventually be passed 
                     * over by a linker which would output Machine Code and an
                     * executable program */
                    System.out.print(token);
                    System.out.print("          " + C_A.lineNumber);
                    System.out.print("     " + C_A.colNumber);
//                    System.out.println(state);
                    System.out.println("     " + lexeme);


                    /* test print-outs */
//                    character = (char) MPscanner.pbr.read();
//                    System.out.println("--------Reader is at");
//                    System.out.println(Character.toString(character));
//                    MPscanner.pbr.unread(character);
                    /* need to return to dispatcher here but for now exit */
                    return character;

                default:
                    System.out.println("you failed default");
                    break;
            }
        }
        System.out.print("you failed");
        return '~';
    }

}
