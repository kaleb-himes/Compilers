/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class comment_FSA extends mp {

    String lexeme;
    String token;
    char character;

    Boolean closedComment;
    Boolean runOnDetector;
    Boolean loop;

    public enum State {

        START, S0, COMMENTACCEPT, RUNONCOMMENT
    }

    State state;

    /**
     *
     * @return @throws FileNotFoundException
     * @throws IOException
     */
    public Character readFile() throws FileNotFoundException, IOException {
        lexeme = "";
        token = "";
        closedComment = false;
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
                /* START state indicates that nothing has been scanned yet */
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

                    //checks if we read a { before a }
                    if (Character.compare(character, '{') == 0) {
                        runOnDetector = true;
                        MPscanner.pbr.unread(character);
                    }

                    if (Character.compare(character, '}') == 0) {
                          closedComment = true;
                    }
                    
                    //We have not yet read a closing brace, so keep concatenating the
                    //intermediate characters and consider them part of the comment
                    if (closedComment == false  && runOnDetector == false) {
                        //if (Character.compare(character, '}') == 0) {
                        //    closedComment = true;
                        //}

                        if (character == 10) {
                            character = 32;
                            mp.lineNumber++;
                            mp.colNumber = 0;
                        }
                        mp.colNumber++;
                        lexeme = lexeme.concat(Character.toString(character));
                    } else if (closedComment == true && runOnDetector == false) {
                        //go to the comment accept state, as you have read one { or }                       
                        MPscanner.pbr.unread(character);
                        state = State.COMMENTACCEPT;
                    } else if (runOnDetector == true) {
                        MPscanner.pbr.unread(character);
                        state = State.RUNONCOMMENT;

                        token = "MP_RUN_COMMENT";

                        System.out.print(token);
                        System.out.print("          " + Dispatcher.markLine);
                        System.out.print("     " + Dispatcher.markCol);
                        System.out.println("     " + lexeme);
                        
                    }

                    /* END S0 */
                    break;

                /* 
                 * S0 state indicates all valid characters have been read and 
                 * we encountered something not legal. This could mean either
                 * a valid identifier has been read or an error occured
                 * let the dispatcher handle it
                 */
                case COMMENTACCEPT:
                    loop = false;
                    token = "MP_COMMENT";
                    /* test print-outs */
                    /* This stuff needs to be written to a file like C does
                     * for assembly stuff, our file would eventually be passed 
                     * over by a linker which would output Machine Code and an
                     * executable program */
                    System.out.print(token);
                    System.out.print("          " + Dispatcher.markLine);
                    System.out.print("     " + Dispatcher.markCol);
                    System.out.println("     " + lexeme);

                    /* return to dispatcher */
                    return character;

                /* 
                 * S0 state indicates all valid characters have been read and 
                 * we encountered something not legal. This could mean either
                 * a valid identifier has been read or an error occured
                 * let the dispatcher handle it
                 */
                case RUNONCOMMENT:
                    //NEED TO GET THIS TO PRINT OUT 
                    loop = false;
                    
                    //lexeme = lexeme.concat(Character.toString(character));
                    
                    token = "MP_RUN_COMMENT";

                    System.out.print(token);
                    System.out.print("          " + Dispatcher.markLine);
                    System.out.print("     " + Dispatcher.markCol);
                    System.out.println("     " + lexeme);

                    state = State.S0;
                    /* return to dispatcher */
                return character;
                //break;

                default:
                    System.out.println("you failed default");
                    break;
            }
        }
        System.out.print(
                "at end - delte this comment when done");

        return '~';

    }
}
