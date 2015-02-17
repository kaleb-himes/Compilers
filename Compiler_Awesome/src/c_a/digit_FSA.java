package c_a;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 *
 * A finite state automaton, that is responsible for reading in either an
 * integer value, a fixed point number or a floating point number. The FSA
 * returns the longest lexeme that matches any of the 3 previously mentioned
 * types, as well as the column and line number, and the appropriate token.
 *
 */
public class digit_FSA extends mp {

    State state;

    //Strings corresponding to the lexeme (i.e. 27) and token(i.e. MP_INTEGER_LIT)
    String lexeme;
    String token;

    //Variable to store the character most recently read
    char character;

    //integers corresponding to space in the buffer for these particular characters 
    //(for unreading)
    int decimalPt;
    int exponential;

    //flags to indicate whether or not a particular character has already been scanned
    //(for unreading and traversing states)
    Boolean readDecimalPt;
    Boolean readExponential;

    //flags to indicate whether or not a particular state has already been reached
    //(for unreading and traversing states)
    Boolean fromS0;
    Boolean fromS1;
    Boolean fromS2;

//enumerated types for all possible states of FSA
    public enum State {

        START, INTACCEPT, S0,
        FIXEDACCEPT, S1, S2, FLOATACCEPT
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

    //Precondition: the source file file pointer points to the first character 
    //of the lexeme corresponding to the next token
    public synchronized String readFile() throws FileNotFoundException, IOException {
        lexeme = "";
        token = "";
        state = State.START;
        fromS0 = false;
        fromS1 = false;
        fromS2 = false;
        readExponential = false;
        readDecimalPt = false;
        decimalPt = 0;
        exponential = 1;
        //Buffer to store particular characters
        char charBuffer[] = {0, 0, 0};
        int c;

        while ((c = MPscanner.pbr.read()) != -1) {
            //unreads this character, in order to check if we are at end of file
            MPscanner.pbr.unread(c);
            c++;

            switch (state) {
                //START state indicates that no part of the lexeme has been 
                //scanned yet
                case START:
                    /* Read in the first character, which is (as specified by the 
                     * dispatcher) an integer.
                     */
                    character = (char) MPscanner.pbr.read();

                    if (Character.isDigit(character)) {
                        //puts the character in the lexeme
                        lexeme = Character.toString(character);

                        //transitions to INTACCEPT state (because an integer has been read)
                        state = State.INTACCEPT;
                    }

                    //end of START case
                    break;

                //Accept State for an Integer Value   
                case INTACCEPT:
                    //checks if we are coming back from S0, a state further on in the FSA
                    if (fromS0 == false) {
                        //read the next character     
                        character = (char) MPscanner.pbr.read();
                    } else {
                        //if we are coming from S0, we need to push at least one 
                        //character back to the reader
                        MPscanner.pbr.unread(character);
                        if (charBuffer[decimalPt] != 0) {
                            MPscanner.pbr.unread(charBuffer[decimalPt]);
                            charBuffer[decimalPt] = 0;
                        }
                        token = "MP_INTEGER_LIT";

                        //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.print(token);
                        System.out.print("      " + Dispatcher.markLine);
                        System.out.print("     " + Dispatcher.markCol);
                        System.out.println("     " + lexeme);

                        //exits the FSA, as we have found a valid token
                        return token;
                    }

                    if (Character.isDigit(character)) {
                        //if character read was an integer, concatenate it to the lexeme
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (character == '.') {
                        //no decimal point has been read previously, so move to next state
                        if (readDecimalPt == false) {
                            readDecimalPt = true;
                            //store the decimal point in the buffer of characters
                            //(for unreading)
                            charBuffer[decimalPt] = character;
                            charBuffer[decimalPt] = 0;
                            mp.colNumber++;
                            lexeme = lexeme.concat(Character.toString(character));

                            //as we have read a period, move toward float/fixed states
                            state = State.S0;
                        } else {
                            //a period has already been read, reset the reader, change token, fix lexeme
                            MPscanner.pbr.unread(character);

                            token = "MP_INTEGER_LIT";

                            //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                            System.out.print(token);
                            System.out.print("      " + Dispatcher.markLine);
                            System.out.print("     " + Dispatcher.markCol);
                            System.out.println("     " + lexeme);

                            //exits the FSA, as we have found a valid token
                            return token;
                        }
                    } else {
                        //unread the last character, which does not contribute to a valid token 
                        MPscanner.pbr.unread(character);

                        token = "MP_INTEGER_LIT";

                        //exits the FSA, as we have found a valid token
                        return token;
                    }

                    //end of INTACCEPT case
                    break;

                //S0 State, indicates (at least) 1 digit and (exactly) 1 period read        
                case S0:
                    //sets the Boolean variable to indicate we have come from S0
                    fromS0 = true;

                    //read the next character
                    character = (char) MPscanner.pbr.read();

                    //Check that the character read is of a valid type
                    if (Character.isDigit(character)) {
                        //concatenate the digit after the decimal point
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));

                        //change states, to indicate that it is a fixed pt. number
                        state = State.FIXEDACCEPT;
                    } else {
                        //unreads the invalid character, fixes the lexeme and goes
                        //back to the appropriate accept state (INTACCEPT)
                        MPscanner.pbr.unread(character);

                        //remove the last character, set to correct token
                        lexeme = lexeme.substring(0, lexeme.length() - 1);

                        //sets info back to last traversed accept state
                        state = State.INTACCEPT;
                    }

                    //end of S0 case
                    break;

                //FIXEDACCEPT, indicates (at least) 1 digit and (exactly) 1 period 
                //and one digit has been read  
                case FIXEDACCEPT:
                    //indicates if we have come from a state further on in the FSA
                    if (fromS1 == false && fromS2 == false) {
                        //read the next character     
                        character = (char) MPscanner.pbr.read();
                    } else {
                        //unreads characters and takes the invalid characters out of the lexeme
                        MPscanner.pbr.unread(character);
                        if (charBuffer[exponential] != 0) {
                            MPscanner.pbr.unread(charBuffer[exponential]);
                            charBuffer[exponential] = 0;
                        }
                        lexeme = lexeme.substring(0, lexeme.length() - 1);

                        //checks if two characters should be removed from the lexeme
                        if (fromS2 == true) {
                            lexeme = lexeme.substring(0, lexeme.length() - 1);
                        }
                        token = "MP_FIXED_LIT";

                        //exits the FSA, as we have found a valid token
                        return token;
                    }

                    if (Character.isDigit(character)) {
                        //if a digit was read, concatenate it to the lexeme
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (character == 'e' || character == 'E') {
                        //checks if an exponential character (E or e) has already been read
                        if (readExponential == false) {
                            //if an e or E has not been read, set it in buffer
                            charBuffer[exponential] = character;
                            readExponential = true;
                            mp.colNumber++;
                            lexeme = lexeme.concat(Character.toString(character));
                            state = State.S1;
                        } else {
                            MPscanner.pbr.unread(character);

                            token = "MP_FIXED_LIT";

                            //exits the FSA, as we have found a valid token
                            return token;
                        }

                    } else {
                        //not a digit or e|E, set the reader back and exit
                        MPscanner.pbr.unread(character);

                        token = "MP_FIXED_LIT";

                        //exits the FSA, as we have found a valid token
                        return token;
                    }

                    //end of FIXEDACCEPT case
                    break;

                //S1 state indicates that a fixed point number has been read
                //along with an E or e, which would indicate a possible floating 
                //point number  
                case S1:
                    //records that this state has been visited, in case an 
                    //earlier state needs to be revisited
                    fromS1 = true;

                    //read the next character
                    character = (char) MPscanner.pbr.read();

                    //Check that the character read is of a valid type
                    if (character == '+' || character == '-') {
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));
                        //change states
                        state = State.S2;
                    } else if (character == 'E' || character == 'e') {
                        MPscanner.pbr.unread(character);

                        //sets info back to last traversed accept state
                        state = State.FIXEDACCEPT;
                    } else {
                        //not a valid character (digit), set the reader back and exit
                        MPscanner.pbr.unread(character);
                        System.out.println("Character" + Character.toString(character));

                        //sets info back to last traversed accept state
                        state = State.FIXEDACCEPT;
                    }

                    //end of S1 case
                    break;

                //S2 state indicates that a fixed point number has been read
                //along with an E or e and a + or -, which would indicate a possible floating 
                //point number     
                case S2:
                    //records that this state has been visited, in case an 
                    //earlier state needs to be revisited
                    fromS2 = true;

                    //read the next character
                    character = (char) MPscanner.pbr.read();

                    //Check that the character read is of a valid type
                    if (Character.isDigit(character)) {
                        //concatenate the digit after the + or -
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));

                        //move to the Float Accept state, as a floating point
                        //number has been read
                        state = State.FLOATACCEPT;
                    } else {
                        //not a digit, set the reader back and exit
                        MPscanner.pbr.unread(character);

                        //sets info back to last traversed accept state
                        state = State.FIXEDACCEPT;
                    }

                    //end of S2
                    break;

                /* 
                 * FLOATACCEPT state indicates that a floating point number has been
                 * read - one or more digits, a decimal point, one or more digits, an
                 * E or e, a + or -, and one (or more) digits
                 */
                case FLOATACCEPT:
                    //read the next character     
                    character = (char) MPscanner.pbr.read();

                    if (Character.isDigit(character)) {
                        //if it was a digit, concatenate it to the lexeme
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));
                    } else {
                        //not a digit, set the reader back and exit
                        MPscanner.pbr.unread(character);

                        token = "MP_FLOAT_LIT";

                        //return whatever character was last read
                        return token;
                    }
                    //end of FLOATACCEPT 
                    break;
            }
            //Post Condition: The input file pointer is pointing at the first 
            //character after the current token.  
        }

        if (state != State.INTACCEPT) {
            if (state != State.FIXEDACCEPT) {
                if (state != State.FLOATACCEPT) {
                    token = "MP_ERROR";
                }
            }
        }
        return token;
    }
}
