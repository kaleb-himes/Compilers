package c_a;

/**
 *
 * @author khimes
 */
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class string_FSA extends mp {

    String lexeme = "";
    String token = "";
    char character;
    boolean second_quote = false;
    public static boolean foundRunOn = false;
    public static int runOnColMark;

    /* 
     * flags to indicate whether or not a particular character
     * has already been scanned
     */
    Boolean readPeriod = false;
    Boolean readOperator = false;

    public enum State {

        START, STRINGACCEPT, S0, S_ERROR
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

    /* Initializes the State variable to the START state */
    State state = State.START;

    public synchronized String readFile() throws FileNotFoundException, IOException {

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
                    if (character != 10) {
                        character = (char) MPscanner.pbr.read();
                    }
                    else {
                        MPscanner.pbr.unread(character);
                    }

                    /* puts the character in the lexeme */
                    lexeme = Character.toString(character);

                    /* 
                     * transitions to STRINGACCEPT state 
                     * (because a letter or underscore has been read)
                     */
                    state = State.STRINGACCEPT;

                    /* end of START case */
                    break;

                /* Accept State for an Identifier Value */
                case STRINGACCEPT:
                    /* read the next character */
                    character = (char) MPscanner.pbr.read();

                    if (second_quote == false) {
                        if (Character.toString(character).equals("'")) {
                            character = (char) MPscanner.pbr.read();
                            if (Character.toString(character).equals("'")) {
                                Character.toString(character).replace(character, '\0');
                            } else {
                                MPscanner.pbr.unread(character);
                                second_quote = true;
                            }
                        } else if (character == 10) {
                            //reduce line number by one so when scanner sees it error will print on same line.
                            foundRunOn = true;
                            //+2 account for the return mid string that won't get counted otherwise
                            mp.lineNumber+=2;
                            MPscanner.pbr.unread(character);
                            state = State.S_ERROR;
                        } else {
                            /* if 0-9 | a-z | A-Z | $ | _ then concat to lexeme */
                            mp.colNumber++;
                            lexeme = lexeme.concat(Character.toString(character));
                        }
                    } else if (second_quote == true && foundRunOn == false) {
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

                        token = "MP_STRING_LIT";

                        /* return to dispatcher */
                        return token;
                    }

                    /* END STRINGACCEPT */
                    break;

                /* 
                 * S0 state indicates all valid characters have been read and 
                 * we encountered something not legal. This could mean either
                 * a valid identifier has been read or an error occured
                 * let the dispatcher handle it
                 */
                case S0:
                    token = "MP_STRING_LIT";
                    /* return to dispatcher  */
                    return token;
                case S_ERROR:
                    token = "MP_ERROR";
                    /* return to dispatcher  */
                    return token;

            }
        } //end while
        if (state != State.STRINGACCEPT) {
            if (state != State.S0) {
                token = "MP_ERROR";
            }
        }
        return token;
    }

}
