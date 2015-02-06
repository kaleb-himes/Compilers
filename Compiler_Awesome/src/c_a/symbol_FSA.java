/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_a;

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
public class symbol_FSA extends C_A {

    String lexeme = "";
    String token = "";
    char character;
    String[] chars = {":=", "<=", ">=", ":", ",", "=", "/", ">", "<", "(", "-", ".", "+", ")", ";", "*"};

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

    public void readFile() throws FileNotFoundException, IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fLocation),
                        Charset.forName("UTF-8")));

        /* 
         * Initializes a pushback reader, so that characters 
         * can be put back in the reader
         */
        PushbackReader pbr = new PushbackReader(reader, 2);

        int c;

        while ((c = pbr.read()) != -1) {
            /* 
             * unreads this character, which is just checking
             * if we are at end of file
             */
            pbr.unread(c);
            c++;

            switch (state) {
                /* START state indicates that nothing has been scanned yet */
                case START:
                    /* 
                     * Read in the first character, which is (as specified by the 
                     * dispatcher) one of the symbols.
                     */
                    character = (char) pbr.read();

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
                    character = (char) pbr.read();

                    for (int i = 0; i < chars.length; i++) {
                        
                        // if the character is in the array of characters above do this
                        if (Character.toString(character) == chars[i]) {
                            if (character == ':') {
                                //read the next character
                                character = (char) pbr.read();

                                //check to see if the next character is =
                                if (character == '=') {
                                    token = "MP_ASSIGN";
                                } else {
                                    token = "MP_COLON";
                                }
                            } else if (character == ',') {
                                token = "MP_COMMA";
                            } else if (character == '=') {
                                token = "MP_EQUAL";
                            } else if (character == '/') {
                                token = "MP_FLOAT_DIVIDE";
                            } else if (character == '>') {
                                //read the next character
                                character = (char) pbr.read();

                                //check to see if the next character is =
                                if (character == '=') {
                                    token = "MP_GEQUAL";
                                } else {
                                    token = "MP_GTHAN";
                                }
                            } else if (character == '<') {
                                //read the next character
                                character = (char) pbr.read();

                                //check to see if the next character is =
                                if (character == '=') {
                                    token = "MP_LEQUAL";
                                //check to see if the next character is >
                                } else if (character == '>') {
                                    token = "MP_NEQUAL";
                                } else {
                                    token = "MP_LTHAN";
                                }
                            } else if (character == '(') {
                                token = "MP_LPAREN";
                            } else if (character == '-') {
                                token = "MP_MINUS";
                            } else if (character == '.') {
                                token = "MP_PERIOD";
                            } else if (character == '+') { 
                                token = "MP_PLUS";
                            } else if (character == ')') { 
                                token = "MP_RPAREN";
                            } else if (character == ';') { 
                                token = "MP_SCOLON";
                            } else { 
                                token = "MP_TIMES";
                            }
                            /* if 0-9 | a-z | A-Z | $ | _ then concat to lexeme */
                            lexeme = lexeme.concat(Character.toString(character));
                        } else if (!Character.isAlphabetic(character)
                                && !Character.isDigit(character)
                                && character != '_') {
                            /*
                             * Checks if character is anything but acceptable
                             * Identifier value and ensures it has not been read
                             * previously
                             */
                            if (readPeriod == false) {
                                pbr.unread(character);
                                state = State.S0;
                            } else {
                                /* a bad value has already been read */
                                pbr.unread(1);

                                token = "MP_IDENTIFIER";

                                /* test print-outs */
                                System.out.println(state);
                                System.out.println(lexeme);
                                System.out.println(token);

                                /* test print-outs */
                                System.out.println("----------------");
                                character = (char) pbr.read();
                                System.out.println(character);

                                /* need to return to dispatcher here but for now exit */
                                System.exit(0);
                            }
                        } else {
                            /* invalid next character, reset */
                            pbr.unread(character);

                            token = "MP_IDENTIFIER";

                            /* test print-outs */
                            System.out.println(state);
                            System.out.println(lexeme);
                            System.out.println(token);

                            /* test print-outs */
                            character = (char) pbr.read();
                            System.out.println("--------Reader is at");
                            System.out.println(Character.toString(character));

                            /* need to return to dispatcher here but for now exit */
                            System.exit(0);
                        }

                        /* END IDACCEPT */
                    }
                    break;

                /* 
                 * S0 state indicates all valid characters have been read and 
                 * we encountered something not legal. This could mean either
                 * a valid identifier has been read or an error occured
                 * let the dispatcher handle it
                 */
                case S0:
                    token = "MP_IDENTIFIER";

                    /* test print-outs */
                    System.out.println(state);
                    System.out.println(lexeme);
                    System.out.println(token);

                    /* test print-outs */
                    character = (char) pbr.read();
                    System.out.println("--------Reader is at");
                    System.out.println(Character.toString(character));
                    /* need to return to dispatcher here but for now exit */
                    System.exit(0);
            }
        }
    }
}
