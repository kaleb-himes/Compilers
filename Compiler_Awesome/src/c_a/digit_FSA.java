/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    String lexeme = "";
    String token = "";
    char character;
    char charBuffer[] = new char[4];

    //flags to indicate whether or not a particular character has already been scanned
    Boolean readPeriod = false;
    Boolean readOperator = false;

    //enumerated types for all possible states of FSA
    public enum State {

        START, INTACCEPT, S0, S1,
        FIXEDACCEPT, S2, S3, FLOATACCEPT
    }

    //Initializes the State variable to the START state
    State state = State.START;

    public void readFile() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fLocation),
                        Charset.forName("UTF-8")));

        //Initializes a pushback reader, so that characters can be put back in the reader
        PushbackReader pbr = new PushbackReader(reader, 4);

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

                    //puts the character in the lexeme
                    lexeme = Character.toString(character);

                    //transitions to INTACCEPT state (because an integer has been read)
                    state = State.INTACCEPT;

                    //end of START case
                    break;

                //Accept State for an Integer Value   
                case INTACCEPT:
                    //read the next character     
                    character = (char) pbr.read();

                    //MT NOTE ===== WHAT ABOUT SIGNS, IS THIS COVERED IN THE SYMBOLS?                    
                    if (Character.isDigit(character)) {
                        //if it was an integer, concatenate it to the lexeme
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (character == '.') {
                        //checks if the character is a . which would indicate a 
                        //float or fixed point number, makes sure has not been read 
                        
                        //no period has been read previously, so move to next state
                        if (readPeriod == false) {
                            lexeme = lexeme.concat(Character.toString(character));
                            state = State.S0;
                        } else {
                            //a period has already been read, reset the reader and exit
                            charBuffer[2] = lexeme.charAt(lexeme.length() - 1);
                            pbr.unread(charBuffer, 2, 1);
                            
                            //remove the last period, set to correct token
                            lexeme = lexeme.substring(0, lexeme.length() - 1);
                            token = "MP_INTEGER";

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
                        //not a digit or a period, set the reader back and exit                      
                        charBuffer[2] = lexeme.charAt(lexeme.length() - 1);
                        pbr.unread(charBuffer, 2, 1);

                        //remove the last character, set to correct token
                        lexeme = lexeme.substring(0, lexeme.length() - 1);
                        token = "MP_INTEGER";

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
                    //read the next character
                    character = (char) pbr.read();

                    //Check that the character read is of a valid type
                    if (Character.isDigit(character)) {

                        //concatenate the digit after the decimal point 
                        lexeme = lexeme.concat(Character.toString(character));

                        //change states, to indicate that it is a fixed pt. number
                        state = State.FIXEDACCEPT;
                    } else if (character == '.') {
                        //in the case 2 consecutive periods have been read, unread and flag
                        pbr.unread(character);
                        readPeriod = true;
                        //move back to the last traversed accept state
                        state = State.INTACCEPT;
                    } else {
                        //sets info back to last traversed accept state
                        pbr.unread(character);

                        //sets info back to last traversed accept state
                        state = State.INTACCEPT;
                    }

                    //end of S0 case
                    break;
                    
                //FIXEDACCEPT, indicates (at least) 1 digit and (exactly) 1 period 
                //and (at least one digit) read  
                case FIXEDACCEPT:
                    //read the next character     
                    character = (char) pbr.read();

                    if (Character.isDigit(character)) {
                        //if it was an integer, concatenate it to the lexeme
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (character == 'e' || character == 'E') {
                        //add flag to prevent ee, eE, EE, or Ee!!!!!!!!!!!!!!!!!!!!!
                        //checks if the character is an e or E, which would indicate a 
                        //floating point number
                        lexeme = lexeme.concat(Character.toString(character));
                        //reader.mark(2);
                        state = State.S1;
                    } else {
                        //not a digit or e|E, set the reader back and exit
                        //reader.reset();
                        token = "MP_FIXED";
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);

                        //for testing only, remove before combining!!!!!!!!!!!!!!!
                        character = (char) pbr.read();
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
                    //reader.mark(1);

                    //read the next character
                    character = (char) pbr.read();

                    //Check that the character read is of a valid type
                    if (character == '+' || character == '-') {
                        readOperator = true;

                        //add flag to detect for ++ or -- or +- or -+    !!!!!!!!
                        //if it was a digit, concatenate the exponential notation to the lexeme

                        //change states
                        state = State.S2;

                    } else {
                        //not a digit, set the reader back and exit
                        //reader.reset();

                        //sets info back to last traversed accept state
                        state = State.FIXEDACCEPT;
                    }

                    //end of S1 case
                    break;

                case S2:

                    //store the operator in a variable
                    String operator = Character.toString(character);

                    //read the next character
                    character = (char) pbr.read();

                    //Check that the character read is of a valid type
                    if (Character.isDigit(character)) {
                        //if it was a digit, concatenate the exponential notation to the lexeme
                        lexeme = lexeme.concat(operator);

                        //concatenate the digit after the exponential notation
                        lexeme = lexeme.concat(Character.toString(character));

                        //change states
                        state = State.FLOATACCEPT;
                    } else {
                        //not a digit, set the reader back and exit
                        //reader.reset();

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
                        //reader.reset();
                        token = "MP_FLOAT";
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

            //reader.mark(1);
        }
    }
}
