package c_a;

import java.io.FileNotFoundException;
import java.io.IOException;

public class string_FSA extends mp {

    String lexeme;
    String token;

    //Variable to store the character most recently read
    char character;

    //Flags to keep track of certain program states
//    Boolean loop;
    Boolean eof = false;

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
        state = State.START;
//        loop = true;

        int c;

        while (eof == false/* && loop == true*/) {
            c = MPscanner.pbr.read();
            if (c == -1 ) {
                eof = true;
            }
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
                    /* while looping on S0 if we read a second ' enter state */
                    if (Character.toString(character).equals("'")) {
                        character = (char) MPscanner.pbr.read();

                        /* check to see if there is another ' immediately after */
                        if (Character.toString(character).equals("'")) {
                            lexeme = lexeme.concat("'");
                            Character.toString(character).replace(character, '\0');
                        } else {
                            //if we are not escaping a quote, go to the string accept state
                            MPscanner.pbr.unread(character);

                            state = State.STRINGACCEPT;
                        }
                    } else if (character == 10) {
                        //we have gotten to the end of line without seeing second quote
                        MPscanner.pbr.unread(character);
                        state = State.RUNONSTRING;

                    } else if (character != 10) {
                        lexeme = lexeme.concat(Character.toString(character));
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
//                    loop = false;
//                    MPscanner.pbr.unread(character);

                    token = "MP_STRING_LIT";

                    /* return to dispatcher */
                    return token;
                //END STRINGACCEPT

                /* 
                 * RUNONSTRING state indicates that two '' have not been read before 
                 * the end of line
                 */
                case RUNONSTRING:
//                    loop = false;
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
        return token;
    }

}
