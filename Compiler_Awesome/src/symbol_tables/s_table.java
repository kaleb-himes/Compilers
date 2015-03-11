/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symbol_tables;

import java.util.HashMap;

/**
 *
 * @author khimes
 */
public class s_table {
    public static HashMap<String, HashMap<String, String[]>> tables;
    
    public static void Init_Table() {
        tables = new HashMap<>();
    }
    
    /*
     * @param curr_table: the name of the current table (key value in Hashmap 1)
     * @param lexeme: the lexeme that will be the key value for the row to being
     *                inserted
     * @param values: type, kind, mode, size, parameters
     */
    
    /*
     * Example of values: (lexeme)  int, var, null, 4, null
     * Example of values: (lexeme)  null, function, null, null, (a, b, c, mary)
     */
    public void Insert_Row(String name, String lexeme, String[] values) {
        String[] check = tables.get(name).put(lexeme, values);
        
        //for testing purposes print key name
        System.out.println(lexeme);
        //for testing purposes print what we just inserted
        for (int i = 0; i < check.length; i++) {
            System.out.println(check[i]);
        }
    }
    
    //returns the 

    /*
     * @param name the name of the key you're trying to fetch
     * @return the key location
     */
    public static HashMap Lookup(String name) {
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            System.out.println("Error, no such table exists.");
            return null;
        }
    }
    
    //creates a new key using name in the primary HashMap
    public static void New_Table(String name) {
        if (!tables.containsKey(name)) {
            tables.put(name, null);
        } else {
            System.out.println("Error, table already exists.");
        }
    }
    
    //if record exists in primary HashMap it will be removed.
    public static void Destroy(String name) {
        if (tables.containsKey(name)) {
            tables.remove(name);
        } else {
            System.out.println("Error, table has already been destroyed.");
        }
    }
}
