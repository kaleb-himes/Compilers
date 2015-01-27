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
 *
 * @author khimes
 */
class identifier_FSA extends C_A {

    private static void readFile() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                new FileInputStream(fName),
                                Charset.forName("UTF-8")));
        int c;
        while ((c = reader.read()) != -1) {
            char character = (char) c;
            System.out.println(character);
            // Do something with your character
        }
    }
}
