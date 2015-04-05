/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_a.semantics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author khimes
 */
public class assembly_builder {
    public static PrintWriter assemblyWriter;
    
    public static void init_assembly_writer() 
                    throws FileNotFoundException, UnsupportedEncodingException {
        
        assemblyWriter = new PrintWriter("src/semantic_resources/assembly.il", "UTF-8");
        //a test printout
//        assemblyWriter.println("Test printout");
        
    }
    
    public static void close_assembly_writer() {
        assemblyWriter.close();
    }
    
    
}
