package c_a;

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
class digit_FSA extends C_A {
//BE SURE TO INCLUDE PRE AND POST CONDITIONS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//ADD COMMENTS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    //Initializes the State variable to the START state
    State state = State.START;

    //Strings corresponding to the lexeme (i.e. 27) and token(i.e. MP_INTEGER_LIT)
    String lexeme = "";
    String token = "";

    //variable to store the character most recently read
    char character;

    //buffer to store a particular character
    char charBuffer[] = {0, 0, 0, 0, 0};

    //integers corresponding to space in the buffer for these 3 characters
    int decimalPt = 0;
    //int decimalPt2 = 3;
    int exponential = 1;
    int sign = 2;

    //flags to indicate whether or not a particular character has already been scanned
    Boolean readDecimalPt = false;
    Boolean readOperator = false; //need???????????????????????????????????????
    Boolean readExponential = false;

    //flags to indicate whether or not a particular state has already been reached
    Boolean fromSO = false;
    Boolean fromS1 = false;
    Boolean fromS2 = false;

//enumerated types for all possible states of FSA
    public enum State {

        START, INTACCEPT, S0,
        FIXEDACCEPT, S1, S2, FLOATACCEPT
    }

    public void readFile() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fLocation),
                        Charset.forName("UTF-8")));

        //Initializes a pushback reader, so that characters can be put back in the reader
        PushbackReader pbr = new PushbackReader(reader, 5);

        //need this??????????????????????????????????????????????????????????????
        int c;

        while ((c = pbr.read()) != -1) {
            //unreads this character, which is just checking if we are at end of file
            pbr.unread(c);
            c++;

            switch (state) {
                //START state indicates that nothing has been scanned yet
                case START:
                    /* Read in the first character, which is (as specified by the 
                     * dispatcher) an integer.
                     */
                    character = (char) pbr.read();

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

                    //checks if we are coming from a state further in the FSA
                    if (fromSO == false) {
                        //read the next character     
                        character = (char) pbr.read();
                    } else {
                        pbr.unread(character);
                        pbr.unread(charBuffer[decimalPt]);

                        token = "MP_INTEGER_LIT";

                        //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);

                        //for testing only, remove before combining!!!!!!!!!!!!!!!
                        character = (char) pbr.read();
                        System.out.println("--------Reader is at");
                        System.out.println(Character.toString(character));
                        //////////////////////////////////

                        //exits the FSA, as we have found a valid token
                        System.exit(0);
                    }

                    if (Character.isDigit(character)) {
                        //if it was an integer, concatenate it to the lexeme
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (character == '.') {
                        //no period has been read previously, so move to next state
                        if (readDecimalPt == false) {
                            readDecimalPt = true;
                            charBuffer[decimalPt] = character;
                            lexeme = lexeme.concat(Character.toString(character));

                            //as we have read a period, move toward float/fixed states
                            state = State.S0;
                        } else {
                            //a period has already been read, reset the reader, change token, fix lexeme
                            pbr.unread(character);

                            token = "MP_INTEGER_LIT";

                            //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                            System.out.println(state);
                            System.out.println(lexeme);
                            System.out.println(token);

                            //for testing only, remove before combining!!!!!!!!!!!!!!!
                            character = (char) pbr.read();
                            System.out.println("--------Reader is at");
                            System.out.println(Character.toString(character));
                             //////////////////////////////////

                            //exits the FSA, as we have found a valid token
                            System.exit(0);
                        }
                    } else {
                        //unread the last character, which does not contribute to a valid token 
                        pbr.unread(character);
//                        
                        token = "MP_INTEGER_LIT";

                        //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);

                        //for testing only, remove before combining!!!!!!!!!!!!!!!
                        character = (char) pbr.read();
                        System.out.println("--------Reader is at");
                        System.out.println(Character.toString(character));
                        //////////////////////////////////

                        //exits the FSA, as we have found a valid token
                        System.exit(0);
                    }

                    //end of INTACCEPT case
                    break;

                //S0 State, indicates (at least) 1 digit and (exactly) 1 period read        
                case S0:
                    fromSO = true;

                    //push decimal point into buffer before we read new character, charBuffer[0] = .
                    //read the next character
                    character = (char) pbr.read();

                    //Check that the character read is of a valid type
                    if (Character.isDigit(character)) {

                        //concatenate the digit after the decimal point 
                        lexeme = lexeme.concat(Character.toString(character));

                        //change states, to indicate that it is a fixed pt. number
                        state = State.FIXEDACCEPT;
                    } else {
                        fromSO = true;
                        //sets info back to last traversed accept state
                        pbr.unread(character);

                        //remove the last character, set to correct token
                        lexeme = lexeme.substring(0, lexeme.length() - 1);

                        //sets info back to last traversed accept state
                        state = State.INTACCEPT;
                    }

                    //end of S0 case
                    break;

                //FIXEDACCEPT, indicates (at least) 1 digit and (exactly) 1 period 
                //and (at least one digit) read  
                case FIXEDACCEPT:
                    System.out.println(fromS2);

                    if (fromS1 == false && fromS2 == false) {
                        //read the next character     
                        character = (char) pbr.read();
                    } else {
                        lexeme = lexeme.substring(0, lexeme.length() - 1);

                        pbr.unread(character);
                        pbr.unread(charBuffer[exponential]);

                        if (fromS2 == true) {
                            lexeme = lexeme.substring(0, lexeme.length() - 1);
                        }
                        token = "MP_FIXED_LIT";

                        //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);

                        //for testing only, remove before combining!!!!!!!!!!!!!!!
                        character = (char) pbr.read();
                        System.out.println("--------Reader is at");
                        System.out.println(Character.toString(character));
                         //////////////////////////////////

                        //exits the FSA, as we have found a valid token
                        System.exit(0);
                    }
                    //read the next character     
                    //character = (char) pbr.read();

                    if (Character.isDigit(character)) {
                        //if it was an integer, concatenate it to the lexeme
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (character == 'e' || character == 'E') {

                        if (readExponential == false) {
                            charBuffer[exponential] = character;
                            readExponential = true;
                            //readOperator = true;
                            lexeme = lexeme.concat(Character.toString(character));
                            state = State.S1;
                        } else {

                            //add flag to prevent ee, eE, EE, or Ee!!!!!!!!!!!!!!!!!!!!!
                            //checks if the character is an e or E, which would indicate a 
                            //floating point number
                            pbr.unread(character);

//                            if (charBuffer[2] != 0) {
//                            
                            //remove the last character, set to correct token
                            //lexeme = lexeme.substring(0, lexeme.length() - 1);
                            token = "MP_FIXED_LIT";

                            //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                            System.out.println(state);
                            System.out.println(lexeme);
                            System.out.println(token);

                            //for testing only, remove before combining!!!!!!!!!!!!!!!
                            character = (char) pbr.read();
                            System.out.println("--------Reader is at");
                            System.out.println(Character.toString(character));
                         //////////////////////////////////

                            //exits the FSA, as we have found a valid token
                            System.exit(0);
                        }

                    } else {
                        //not a digit or e|E, set the reader back and exit

                        pbr.unread(character);

                        token = "MP_FIXED_LIT";

                        //for testing only, can delete this!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);

                        //for testing only, remove before combining!!!!!!!!!!!!!!!
                        character = (char) pbr.read();
                        System.out.println("--------Reader is at");
                        System.out.println(Character.toString(character));
                        //////////////////////////////////

                        //exits the FSA, as we have found a valid token
                        System.exit(0);
                    }

                    //end of FIXEDACCEPT case
                    break;

                //S1 state indicates that a fixed point number has been read
                //along with an E or e, which would indicate a floating point    
                case S1:
                    fromS1 = true;

                    //read the next character
                    character = (char) pbr.read();

                    //Check that the character read is of a valid type
                    if (character == '+' || character == '-') {
                        //charBuffer[2] = character;
                        readOperator = true;
                        lexeme = lexeme.concat(Character.toString(character));
                        //change states
                        state = State.S2;
                    } else if (character == 'E' || character == 'e') {
                        pbr.unread(character);
                        //lexeme = lexeme.substring(0, lexeme.length() - 1);
                        //charBuffer[3] = character;
                        //sets info back to last traversed accept state
                        state = State.FIXEDACCEPT;
                    } else {
                        //not a digit, set the reader back and exit
                        pbr.unread(character);
                        System.out.println("Character" + Character.toString(character));
                        //sets info back to last traversed accept state
                        state = State.FIXEDACCEPT;
                    }

                    //end of S1 case
                    break;

                case S2:
                    fromS2 = true;
                    //????????????????????????????????????????????
                    //fromS1 = false;

                    //store the operator in a variable
                    String operator = Character.toString(character);
                    System.out.println("Character is " + character);

                    //read the next character
                    character = (char) pbr.read();

                    //Check that the character read is of a valid type
                    if (Character.isDigit(character)) {
                        //if it was a digit, concatenate the exponential notation to the lexeme
                        //lexeme = lexeme.concat(operator);

                        //concatenate the digit after the exponential notation
                        lexeme = lexeme.concat(Character.toString(character));

                        //change states
                        state = State.FLOATACCEPT;
                    } else {
                        //not a digit, set the reader back and exit
                        pbr.unread(character);
                        //sets info back to last traversed accept state
                        state = State.FIXEDACCEPT;
                    }

                    //end of S2
                    break;

                case FLOATACCEPT:
                    //read the next character     
                    character = (char) pbr.read();

                    if (Character.isDigit(character)) {
                        //if it was an integer, concatenate it to the lexeme
                        lexeme = lexeme.concat(Character.toString(character));
                    } else {
                        //not a digit, set the reader back and exit
                        pbr.unread(character);
                        token = "MP_FLOAT_LIT";

                        //for testing only, remove!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);

                        //for testing only, remove before combining!!!!!!!!!!!!!!!
                        character = (char) pbr.read();
                        System.out.println("--------Reader is at");
                        System.out.println(Character.toString(character));

                        //exits the FSA, as we have found a valid token
                        System.exit(0);
                    }
                    //end of FLOATACCEPT 
                    break;
            }
        }
    }
}
