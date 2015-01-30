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

    public enum State {

        START, INTACCEPT, S0, S1,
        FIXEDACCEPT, S2, S3, FLOATACCEPT
    }

    State state = State.START;
    String lexeme = "";
    String token = "";
    char character;

    public void readFile() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(fLocation),
                        Charset.forName("UTF-8")));

        int c;

        //marks place, so that we can undo the read 
        reader.mark(1);

        while ((c = reader.read()) != -1) {
            reader.reset();
            c++;

            switch (state) {
                //START state indicates that nothing has been scanned yet
                case START:
                    /* Read in the first character, which is (as specified by the 
                     * dispatcher) an integer.
                     */
                    character = (char) reader.read();

                    //puts the character in the lexeme
                    lexeme = Character.toString(character);

                    //transitions to INTACCEPT state (because an integer has been read)
                    state = State.INTACCEPT;

                    //end of START case
                    break;

                //Accept State for an Integer Value   
                case INTACCEPT:
                    //read the next character     
                    character = (char) reader.read();
                    
                    //Check the type of the read character, to make sure is valid
                //MT NOTE ===== DO I NEED TO CHECK THAT THE INT DOES NOT BEGIN WITH 0?
                //MT NOTE ===== WHAT ABOUT SIGNS, IS THIS COVERED IN THE SYMBOLS?                    
                    if (Character.isDigit(character)) {
                        //if it was an integer, concatenate it to the lexeme
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (character == '.') {
                        //checks if the character is a . which would indicate a 
                        //float or fixed point number
                        reader.mark(2);
                        state = State.S0;
                        //lexeme = lexeme.concat(Character.toString(character));
                        lexeme = lexeme;
                    } else {
                        //not a digit or a period, set the reader back and exit
                        reader.reset();
                        token = "MP_INTEGER";
                        System.out.println(state);
                        System.out.println(lexeme);
                        System.out.println(token);
                        //exits the FSA, as we have found a valid token
                        System.exit(0);
                    }
                    //end of INTACCEPT case
                    break;

                //S0 State, indicates (at least) 1 digit and (exactly) 1 period
                //has been read       
                case S0:
                    //read the next character
                    character = (char) reader.read();
                    
                    //Check that the character read is of a valid type
                    if (Character.isDigit(character)) {
                        //if it was a digit, concatenate it to the lexeme
                        lexeme = lexeme.concat(Character.toString(character));
                        //change states, to indicate that it is a fixed pt. number
                        state = State.FIXEDACCEPT;
                    } else {
                        //not a digit, set the reader back and exit
                        reader.reset();
                        
                        //sets info back to last traversed accept state
                        state = State.INTACCEPT;
                       
                    }
                    
                    //end of S0 case
                    break;

                case FIXEDACCEPT:
                    //read the next character     
                    character = (char) reader.read();
                    break;

                case S2:
                    break;

                case S3:
                    break;

                case FLOATACCEPT:
                    System.out.println("Weekends are best.");
                    break;
            }
            reader.mark(c);
        }
    }
}
