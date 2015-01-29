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

    public void readFile() throws FileNotFoundException, IOException {
        //ADD PRECONDITION:  the source file file pointer points to the first 
        //character of the lexeme corresponding to the next token
        //ADD POST CONDITION: the value of the Token attribute or variable has 
        //been set to the token just scanned, and
        //the value of the Lexeme attribute or variable has been set to the 
        //lexeme just scanned, and pointer is pointing to the first character 
        //after the token just scanned.
        //ADD COMMENTS re: intent of fsa
       
        
        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                new FileInputStream(fLocation),
                                Charset.forName("UTF-8")));
       
        public enum Day {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
        THURSDAY, FRIDAY, SATURDAY 
        }
        
        
        
        int c;
        while ((c = reader.read()) != -1) {
            char character = (char) c;
            System.out.println(character + " :: " + c);
            // Do something with your character
        }
    }
}

