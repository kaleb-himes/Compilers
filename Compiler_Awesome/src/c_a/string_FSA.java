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
public class string_FSA extends C_A {
    
    String lexeme = "";
    String token = "";
    char character;
    boolean second_quote = false;

    /* 
     * flags to indicate whether or not a particular character
     * has already been scanned
     */
    Boolean readPeriod = false;
    Boolean readOperator = false;

    public enum State {

        START, IDACCEPT, S0
    }

    /* Initializes the State variable to the START state */
    State state = State.START;

    public Character readFile() throws FileNotFoundException, IOException {

        int c;

        while ((c = MPscanner.pbr.read()) != -1) {
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
                     * transitions to IDACCEPT state 
                     * (because a letter or underscore has been read)
                     */
                    state = State.IDACCEPT;

                    /* end of START case */
                    break;

                /* Accept State for an Identifier Value */
                case IDACCEPT:
                    /* read the next character */
                    character = (char) MPscanner.pbr.read();

                    if (second_quote == false) {
                        if (character == '\'') {
                            second_quote = true;
                        }
                        /* if 0-9 | a-z | A-Z | $ | _ then concat to lexeme */
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (second_quote == true) {
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

                        token = "MP_STRINGLITERAL";

                        /* test print-outs */
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);

                        /* test print-outs */
                        character = (char) MPscanner.pbr.read();
                        System.out.println("--------Reader is at");
                        System.out.println(Character.toString(character));

                        /* need to return to dispatcher here but for now exit */
                        System.exit(0);
                    }

                    /* END IDACCEPT */
                    break;

                /* 
                 * S0 state indicates all valid characters have been read and 
                 * we encountered something not legal. This could mean either
                 * a valid identifier has been read or an error occured
                 * let the dispatcher handle it
                 */
                case S0:
                    token = "MP_STRINGLITERAL";
                    /* test print-outs */
                    System.out.println(state);
                    System.out.println(lexeme);
                    System.out.println(token);

                    /* test print-outs */
                    character = (char) MPscanner.pbr.read();
                    System.out.println("--------Reader is at");
                    System.out.println(Character.toString(character));
//                    pbr.unread(character);
                    /* need to return to dispatcher here but for now exit */
                    return character;
            }
        }
        return ' ';
    }

}
