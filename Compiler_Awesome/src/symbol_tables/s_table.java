/*
 * Builds symbol tables, with terminals from the parser and rules provided 
 * by the grammar. 
 *
 */
package symbol_tables;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * @author khimes
 */
public class s_table extends c_a.parser.parser {

    public static LinkedHashMap<String, ArrayList<String>> tables;
    static ArrayList<String> Rows;
    static String Previous;

    public static void Init_Table() {
        tables = new LinkedHashMap<>();
    }

    //create a new table to take on values.
    public static void New_Table(String TableName,
            String NestingLevel, String Label) {
        //HEY DUMMIES HERE IS YOUR ISSUE, DESTROY POINTER ISN"T POINTING
        //AT THE RIGHT TABLE WHEN THIS IS CALLED
//        destroyPointer++;
        Rows = new ArrayList<>();
        Rows.add(NestingLevel);
        //set the value to the label currently in use
        Rows.add(Label + "\n");
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
    public static void Insert_Row(String TableName, String Lexeme, String Token,
            String Type, String Kind, String Mode, String Size,
            String[] Parameters) {
        if (!Lexeme.equals("")) {
            //add the lexeme
            tables.get(TableName).add(Lexeme);
            //add the token
            tables.get(TableName).add(Token);
            //add the Type
            tables.get(TableName).add(Type);
            //add the Kind
            tables.get(TableName).add(Kind);
            //add the Mode
            tables.get(TableName).add(Mode);
            //add the Size
            if (Size.equals("0")) {
                Size = null;
            }
            tables.get(TableName).add(Size);
            //add Parameters
            //need to remember how many were added as we iterate over this later
            for (int i = 0; i < Parameters.length; i++) {
                if (i < Parameters.length - 1) {
                    tables.get(TableName).add(Parameters[i]);
                } else {
                    tables.get(TableName).add(Parameters[i] + "\n");
                }
            }
        }
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
            ArrayList empty = new ArrayList<>();
            empty.clear();
            System.err.println("Error, no such table exists: " + name);
            return empty;
        }
    }

    //if record exists in primary HashMap it will be removed.
    public static void Destroy(String name) {
        if (tables.containsKey(name)) {
            destroyPointer--;
        } else {
            System.err.println("Error, table has already been destroyed.");
        }
    }

    public static void Print_Tables() {
        for (String name : tables.keySet()) {
            String value = tables.get(name).toString();
            System.out.println(name + " " + value);
        }
    }

    public static String Get_Offset(String TableName, String Variable) {
//        System.out.println("TableName = " + TableName);
//        System.out.println("Variable = " + Variable);
        String result = "DEFAULT_GET_OFFSET";
        if (tables.containsKey(TableName)) {
            int getVal = tables.get(TableName).indexOf(Variable);
//            System.out.println("getVal = " + getVal);
            if (getVal != -1) {
                result = tables.get(TableName).get(getVal + 5);
            }
//            System.out.println("result = " + result);
        }
        return result;
    }

    public static String Get_NestingLevel(String TableName) {
        String getVal = "DEFAULT";

        if (tables.containsKey(TableName)) {
            getVal = tables.get(TableName).get(0);

        }

        return getVal;
    }

    public static String Get_Token(String TableName, String Variable) {
        String result = "DEFAULT";
        if (tables.containsKey(TableName)) {
            int getVal = tables.get(TableName).indexOf(Variable);
//            System.out.println("getVal = " + getVal);
            if (getVal != -1) {
                result = tables.get(TableName).get(getVal + 1);
            }
//            System.out.println("result = " + result);
        }
        return result;
    }

    
    public static String Get_Lexeme(String TableName, String Lexeme){
        String result = "DEFAULT";
        if (tables.containsKey(TableName)) {
            int getVal = tables.get(TableName).indexOf(Lexeme);
//            System.out.println("getVal = " + getVal);
            if (getVal != -1) {
                result = tables.get(TableName).get(getVal);
            }
//            System.out.println("result = " + result);
        }
        return result;
    }
    
    public static String Get_Type(String TableName, String Lexeme){
        String result = "DEFAULT_TYPE";
        if (tables.containsKey(TableName)) {
            int getVal = tables.get(TableName).indexOf(Lexeme);
//            System.out.println("getVal = " + getVal);
            if (getVal != -1) {
                result = tables.get(TableName).get(getVal+4);
            }
//            System.out.println("result = " + result);
        }
        return result;
    }
}
