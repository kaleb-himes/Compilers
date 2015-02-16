package c_a;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class identifier_FSA extends mp {

    String lexeme;
    String token;
    char character;

    /* 
     * flags to indicate whether or not a particular character
     * has already been scanned
     */
    Boolean readPeriod = false;
    Boolean readOperator = false;

    public enum State {

        START, IDACCEPT, S0
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
    State state;

    public String readFile() throws FileNotFoundException, IOException {
        lexeme = "";
        token = "";
        state = State.START;
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
                     * dispatcher) either a letter or underscore.
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

                    if (Character.isAlphabetic((int) character)
                            || Character.isDigit((int) character)
                            || character == '_') {

                        /* if 0-9 | a-z | A-Z | $ | _ then concat to lexeme */
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (!Character.isAlphabetic(character)
                            && !Character.isDigit(character)
                            && character != '_') {
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

                        token = "MP_IDENTIFIER";

                        /* return to dispatcher */
                        return token;

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
                    token = "MP_IDENTIFIER";

                    /* return to dispatcher */
                    return token;
            } //Post Condition: The input file pointer is pointing at the first 
            //character after the current token. 
        } //end while

        if (state != State.IDACCEPT) {
            if (state != State.S0) {
                token = "MP_ERROR";
            }
        }
        return token;
    }

}
