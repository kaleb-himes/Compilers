package c_a;

import java.io.FileNotFoundException;
import java.io.IOException;

public class string_FSA extends mp {

    String lexeme;
    String token;

    //Variable to store the character most recently read
    char character;

    //Flags to keep track of certain program states
    Boolean closedString;
    Boolean runOnDetector;
    Boolean loop;

    public enum State {

        START, S0, STRINGACCEPT, RUNONSTRING
    }

    public String getToken() {
        return token;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return colNumber;
    }

    State state;

    //Precondition: the source file file pointer points to the first character 
    //of the lexeme corresponding to the next token
    public String readFile() throws FileNotFoundException, IOException {
        lexeme = "";
        token = "";
        closedString = false;
        runOnDetector = false;
        state = State.START;
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
                //START state indicates that no part of the lexeme has been 
                //scanned yet
                case START:
                    /* 
                     * Read in the first character, which is (as specified by the 
                     * dispatcher) a single quote.
                     */
                    character = (char) MPscanner.pbr.read();

                    if (Character.toString(character).equals("'")) {

                        /* 
                         * transitions to S0 state 
                         * (because an opening string symbol has been read)
                         */
                        state = State.S0;
                    }
                    /* end of START case */
                    break;

                /* Intermediate state, indicates we have read an open ' but not yet a close ' */
                case S0:
                    /* read the next character */
                    character = (char) MPscanner.pbr.read();

                    //we might need to worry about escaped characters
                    //this would be our second quote
                    if (Character.toString(character).equals("'")) {
                        character = (char) MPscanner.pbr.read();
                        if (Character.toString(character).equals("'")) {
                            character = (char) MPscanner.pbr.read();
                            if (Character.toString(character).equals("'")) {
                                Character.toString(character).replace(character, '\0');
                            }
                        } else {
                            MPscanner.pbr.unread(character);
                            runOnDetector = true;
                            state = State.STRINGACCEPT;
                        }

                    } else if (character == 10 && runOnDetector == false) {
                        //reduce line number by one so when scanner sees it error will print on same line.

                        MPscanner.pbr.unread(character);
                        state = State.STRINGACCEPT;

                    } else if (character != 10 && runOnDetector == false) {
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (runOnDetector == true) {
                        MPscanner.pbr.unread(character);
                        //detects the presence of a run on string
                        runOnDetector = true;
                        state = State.RUNONSTRING;
                    }

                    /* END S0 */
                    break;

                /* 
                 * STRINGACCEPT state, accepts anything that starts with a 
                 * { and ends with a }. Nested comments are not allowed
                 * in Micro Pascal.
                 */
                case STRINGACCEPT:
                    //stop looping, as we are in an accept state
                    loop = false;

                    //unread the last character, to get the reader in the right place
                    //MPscanner.pbr.unread(character);
                    token = "MP_STRING_LIT";

                    /* return to dispatcher */
                    return token;
                //END STRINGACCEPT

                /* 
                 * RUNONSTRING state indicates that two '' have not been read before 
                 * the end of line
                 */
                case RUNONSTRING:
                    token = "MP_RUN_STRING";
                    return token;

            }  //Post Condition: The input file pointer is pointing at the first 
            //character after the current token.  
        } //end while

        if (state != State.STRINGACCEPT) {
            if (state != State.RUNONSTRING) {
                token = "MP_ERROR";
            }
        }
        if (state != State.RUNONSTRING) {
            token = "MP_RUN_STRING";
        }
        return token;
    }
}
