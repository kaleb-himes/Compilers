/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symbol_tables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author khimes
 */
public class s_table {
    public static HashMap<String, ArrayList<String>> tables;
    static ArrayList<String> Rows = new ArrayList<>();
    
    public static void Init_Table() {
        tables = new HashMap<>();
    }
    //create a new table to take on values.
    public static void New_Table(String TableName, 
                                            String NestingLevel, String Label) {
        //reset Rows
        Rows.clear();
        //create string array that will hold only one value
        Rows.add(NestingLevel);
        //set the value to the label currently in use
        Rows.add(Label);
        //put the key (nesting level) and array (label) in for Table name key
        tables.put(TableName, Rows);
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
    public static void Insert_Row(String TableName, String Lexeme, String Type, 
                           String Kind, String Mode, String Size, 
                                                          String[] Parameters) {
        //add the lexeme
        tables.get(TableName).add(Lexeme);
        //add the Type
        tables.get(TableName).add(Type);
        //add the Kind
        tables.get(TableName).add(Kind);
        //add the Mode
        tables.get(TableName).add(Mode);
        //add the Size
        tables.get(TableName).add(Size);
        //add Parameters
        for (int i = 0; i < Parameters.length; i++) {
            tables.get(TableName).add(Parameters[i]);
        }

        //for testing purposes print what we just inserted
//        for (int i = 0; i < Rows.size(); i++) {
//            System.out.println(Rows.get(i));
//        }
    }
    
    //returns the 

    /*
     * @param name the name of the key you're trying to fetch
     * @return the key location
     */
    public static ArrayList Lookup(String name) {
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            System.out.println("Error, no such table exists.");
            return null;
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
    
    public static void Print_Tables() {
        for (String name: tables.keySet()){
            String value = tables.get(name).toString();  
            System.out.println(name + " " + value);
        } 
    }
}
