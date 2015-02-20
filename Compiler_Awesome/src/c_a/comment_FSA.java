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
 *
 * A finite state automaton, that is responsible for reading in a comment.
 */
public class comment_FSA extends mp {

    String lexeme;
    String token;

    //Variable to store the character most recently read
    char character;

    //Flags to keep track of certain program states
    Boolean closedComment;
    Boolean runOnDetector;
//    Boolean loop;
    Boolean eof = false;
    
    public enum State {

        START, S0, COMMENTACCEPT, RUNONCOMMENT
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
    public synchronized String readFile() throws FileNotFoundException, IOException {
        lexeme = "";
        token = "";
        closedComment = false;
        runOnDetector = false;
        returnLineTally = 0;
        state = State.START;
//        loop = true;
        int c;

        while (eof == false /*&& loop == true*/) {
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
                //START state indicates that no part of the lexeme has been 
                //scanned yet
                case START:
                    /* 
                     * Read in the first character, which is (as specified by the 
                     * dispatcher) a '{'.
                     */
                    character = (char) MPscanner.pbr.read();

                    if (Character.compare(character, '{') == 0) {
                        /* puts the character in the lexeme */
                        lexeme = Character.toString(character);

                        /* 
                         * transitions to S0 state 
                         * (because an opening comment symbol has been read)
                         */
                        state = State.S0;
                    }
                    /* end of START case */
                    break;

                /* Intermediate state, indicates we have read a { but not yet a } */
                case S0:
                    /* read the next character */
                    character = (char) MPscanner.pbr.read();
                    /*
                     * Does a check to see if we read a { before a } per 
                     * http://cobweb.cs.uga.edu/~kochut/Teaching/x570/micro-pascal/micro-pascal.html
                     * nested comments are not permitted
                     */
                    if (Character.compare(character, '{') == 0) {
                        //detects the presence of a run on comment
                        runOnDetector = true;
                    }

                    // We have not yet read a closing brace, so keep concatenating the
                    // intermediate characters and consider them part of the comment
                    if (closedComment == false && runOnDetector == false) {
                        if (Character.compare(character, '}') == 0) {
                            closedComment = true;
                        }

                        //check for end of line characters (for comments that span lines)
                        if (character == 10) {
                            returnLineTally++;
                        }
                    } else if (closedComment == true && runOnDetector == false) {
                        //go to the comment accept state, as you have read one { and }                       
                        state = State.COMMENTACCEPT;
                    } else if (runOnDetector == true) {
                        MPscanner.pbr.unread(character);
                        state = State.RUNONCOMMENT;
                    }
                    //update lexeme
                    lexeme = lexeme.concat(Character.toString(character));

                    /* END S0 */
                    break;

                /* 
                 * COMMENTACCEPT state, accepts anything that starts with a 
                 * { and ends with a }. Nested comments are not allowed
                 * in Micro Pascal.
                 */
                case COMMENTACCEPT:
                    //stop looping, as we are in an accept state
//                    loop = false;

                    //unread the last character, to get the reader in the right place
                    MPscanner.pbr.unread(character);
                    token = "COMMENT";

                    /* return to dispatcher */
                    return token;
                //END COMMENTACCEPT

                /* 
                 * RUNONCOMMENT state indicates that two {{ have been read (which 
                 * is not allowed in MicroPascal.   
                 */
                case RUNONCOMMENT:
                    character = (char) MPscanner.pbr.read();
                    token = "MP_RUN_COMMENT";
                    //end of RUNONCOMMENT
                    break;

            }  //Post Condition: The input file pointer is pointing at the first 
            //character after the current token.  
        } //end while

        if (state != State.COMMENTACCEPT) {
            if (state != State.RUNONCOMMENT) {
                token = "MP_ERROR";
            }
        }
        if (state != State.RUNONCOMMENT) {
            token = "MP_RUN_COMMENT";
        }
        return token;
    }
    public static int returnLineTally;
}
