package c_a;

/**
 *
 * @author mthornton
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 *
 * A finite state automaton, that is responsible for reading in symbols. The FSA
 * returns the longest lexeme that matches any of the tokens, as well as the
 * column and line number, and the appropriate token.
 *
 */
public class symbol_FSA extends mp {
    /* Initializes the State variable to the START state */

    State state;

    //Strings corresponding to the lexeme (i.e. "+") and token(i.e. MP_PLUS)
    String lexeme;
    String token;
    char character;

    //The string containing building blocks of all valid symbols
    String[] validSymbols = new String[]{":", ",", "=", "/", ">", "<", "(", "-", ".", "+", ")", ";", "*"};

    public enum State {

        START, SINGLEACCEPT, GEQACCEPT, ASSIGNACCEPT, LEQACCEPT, NEQACCEPT
    }

    public String getToken() {
        return token;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLineNumber() {
        return Dispatcher.markLine;
    }

    public int getColumnNumber() {
        return Dispatcher.markCol;
    }

    //Precondition: the source file file pointer points to the first character 
    //of the lexeme corresponding to the next token
    public String readFile() throws FileNotFoundException, IOException {
        state = State.START;
        lexeme = "";
        token = "";

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
                     * dispatcher) one of the symbols.
                     */
                    character = (char) MPscanner.pbr.read();

                    //Checks to see if symbols stored in character is a valid
                    //symbol
                    if (Arrays.asList(validSymbols).contains(Character.toString(character))) {
                        // Puts the character in the lexeme 
                        lexeme = Character.toString(character);

                        //Moves to an accept state, as you have read at 
                        //least one valid symbol
                        state = State.SINGLEACCEPT;
                    }
                    /* end of START case */
                    break;

                /* Accept State for a valid Symbol Value of length 1*/
                case SINGLEACCEPT:
                    //Accept all valid single character tokens                
                    if (Character.compare(character, ',') == 0) {
                        token = "MP_COMMA";
                    } else if (Character.compare(character, '=') == 0) {
                        token = "MP_EQUAL";
                    } else if (Character.compare(character, '/') == 0) {
                        token = "MP_FLOAT_DIVIDE";
                    } else if (Character.compare(character, '(') == 0) {
                        token = "MP_LPAREN";
                    } else if (Character.compare(character, '-') == 0) {
                        token = "MP_MINUS";
                    } else if (Character.compare(character, '.') == 0) {
                        token = "MP_PERIOD";
                    } else if (Character.compare(character, '+') == 0) {
                        token = "MP_PLUS";
                    } else if (Character.compare(character, ')') == 0) {
                        token = "MP_RPAREN";
                    } else if (Character.compare(character, ';') == 0) {
                        token = "MP_SCOLON";
                    } else if (Character.compare(character, '*') == 0) {
                        token = "MP_TIMES";
                        //FSA has handled all single character accept cases
                    } else {
                        //FSA may need to handle a multiple character accept case
                        if (Character.compare(character, '>') == 0) {
                            //Determine if the next character is =, otherwise accept at >
                            //and set the reader back
                            character = (char) MPscanner.pbr.read();
                            if (Character.compare(character, '=') == 0) {
                                mp.colNumber++;
                                lexeme = lexeme.concat(Character.toString(character));
                                state = State.GEQACCEPT;
                            } else {
                                //= was the only valid symbol to follow >, so 
                                //unread the invalid character
                                MPscanner.pbr.unread(character);
                                token = "MP_GTHAN";
                            }
                        } else if (Character.compare(character, ':') == 0) {
                            character = (char) MPscanner.pbr.read();
                            if (Character.compare(character, '=') == 0) {
                                mp.colNumber++;
                                lexeme = lexeme.concat(Character.toString(character));
                                state = State.ASSIGNACCEPT;
                            } else {
                                //= was the only valid symbol to follow :, so 
                                //unread the invalid character                                
                                MPscanner.pbr.unread(character);
                                token = "MP_COLON";
                            }
                        } else if (Character.compare(character, '<') == 0) {
                            character = (char) MPscanner.pbr.read();
                            if (Character.compare(character, '=') == 0) {
                                mp.colNumber++;
                                lexeme = lexeme.concat(Character.toString(character));
                                state = State.LEQACCEPT;
                            } else if (Character.compare(character, '>') == 0) {
                                mp.colNumber++;
                                lexeme = lexeme.concat(Character.toString(character));
                                state = State.NEQACCEPT;
                            } else {
                                //= and > were the only valid symbols to follow <, so 
                                //unread the invalid character
                                MPscanner.pbr.unread(character);
                                token = "MP_LTHAN";
                            }
                        }
                    } //End of if statement that sets token for all valid symbols

                    if (state == State.SINGLEACCEPT) {


                        /* Return to the dispatcher */
                        return token;
                    }
                    // END SINGLEACCEPT
                    break;

                /* 
                 * GEQACCEPT state indicates that we have read a > and an =.
                 */
                case GEQACCEPT:

                    /* return to dispatcher*/
                    return token;
                // END GEQACCEPT

                case LEQACCEPT:
                    token = "MP_LEQUAL";

                    /* need to return to dispatcher*/
                    return token;
                // END LEQACCEPT

                case ASSIGNACCEPT:
                    token = "MP_ASSIGN";

                    /* need to return to dispatcher */
                    return token;
                // END ASSIGNACCEPT

                case NEQACCEPT:
                    token = "MP_NEQUAL";

                    /* return to dispatcher*/
                    return token;
                // END NEQACCEPT

            }
            //Post Condition: The input file pointer is pointing at the first 
            //character after the current token  
        } //end while

        if (state != State.ASSIGNACCEPT) {
            if (state != State.GEQACCEPT) {
                if (state != State.LEQACCEPT) {
                    if (state != State.NEQACCEPT) {
                        if (state != State.SINGLEACCEPT) {
                            token = "MP_ERROR";
                        }
                    }
                }
            }
        }

        return token;
    }
}
