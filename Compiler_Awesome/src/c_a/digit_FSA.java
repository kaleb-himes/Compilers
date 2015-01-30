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
                //comment these, add more info
                case START:   
                //read the first character, which is (as specified by the 
                //dispatcher) an integer
                character = (char) reader.read();
                
                //puts the string in the lexeme
                lexeme = Character.toString(character);
                
                //transitions to INTACCEPT state (because an integer has been read)
                state = State.INTACCEPT;
                
                //print statements, can get rid of these when done testing
                System.out.println(lexeme);
                System.out.println(state);
                break;                
              
                //at least 1 integer has been scanned    
                case INTACCEPT:
                //check next character is an integer
                //if yes, concat
                //if no, is it a period? if so, send to state s1    
                //if neither - back it on up and exit    
                character = (char) reader.read();
                lexeme = lexeme.concat(Character.toString(character));
                
                //for testing
                System.out.println(state);
                System.out.println(lexeme);
                break;
                
                //a period has been scanned    
                case S0:
                    break;

                case S1:
                    break;

                case FIXEDACCEPT:
                    System.out.println("Weekends are best.");
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
