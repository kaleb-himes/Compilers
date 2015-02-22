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
    Boolean eof = false;

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

    public synchronized String readFile() throws FileNotFoundException, IOException {
        lexeme = "";
        token = "";
        state = State.START;
        int c;

        while (eof == false) {
            c = MPscanner.pbr.read();
            if (c == -1) {
                eof = true;
            }
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
                        lexeme = lexeme.concat(Character.toString(character));
                    } else {
                        /*
                         * Checks if character is anything but acceptable
                         * Identifier value and ensures it has not been read
                         * previously
                         */

                        MPscanner.pbr.unread(character);
                        state = State.S0;
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
                    //check for reserved word with parser.match()
                    token = c_a.parser.parser.match(lexeme);
                    
//                    System.out.println("after match: " + token);
                    //if no match was found return MP_IDENTIFIER otherwise
                    //return the reserved word identifier
                    if (token.equals("MP_NO_MATCH")) {
                        token = "MP_IDENTIFIER";
                    }

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
