/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_a.analyzer;

import java.util.ArrayList;
import symbol_tables.s_table;

/**
 *
 * @author khimes
 */
public class s_analyzer extends c_a.parser.parser {

    public static void analyze_variable() {
        checkFuncArgs = 0;
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
            if (!Functions.contains(parseTokens.get(index + 3))) {
                CurrLexeme = parseTokens.get(index + 3);
                Variables.add(CurrLexeme);
//                System.out.println("Set VarID: " + CurrLexeme);
            }
//##############################################################################
        ArrayList tempList;     // store a table temporarily for checks
        int foundId = 0;        // equals 1 if ID has been declared already
        for (int i = lookUpArray.size() - 1; i >= 0; i--) {
            tempList = s_table.Lookup(lookUpArray.get(i));
//          System.out.println("Looking in: " + lookUpArray.get(i) + "\nfor: " + CurrLexeme);
            if (!tempList.isEmpty() && tempList.contains(CurrLexeme)) {
//              System.out.println("Found it here:\n" + tempList);
                int getType = tempList.indexOf(CurrLexeme) + 1;
                    //only set final type if it hasn't been set yet. We will
                //reset it once we seee MP_SCOLON
                if (finalType.equals("NO_TYPE")) {
                    finalType = (String) tempList.get(getType);
                    tempType = finalType;
//                  System.out.println("finalType set: " + finalType);
                    //assignee should only be set when type is set as well
                    //we can use one check to control both assigns
                    assignee = CurrLexeme;
                    foundId = 1;
                    break;
                } else {
                    tempType = (String) tempList.get(getType);
//                  System.out.println("tempType set: " + tempType);
                    foundId = 1;
                }
            }
        } // END of for loop

        if (foundId == 0) {
            sourceOfError = "Variable " + CurrLexeme
                    + " was never declared, or is out of scope";
            errorsFound.add(sourceOfError);
            //add line no corresponding to error
            lineNo = parseTokens.get(index + 1);
            errorLocation.add(lineNo);

            //add col no corresponding to error
            colNo = parseTokens.get(index + 2);
            errorLocation.add(colNo);
        }
        switch (checkFuncArgs) {
            case 0:
                if (finalType.compareTo(tempType) != 0) {
                    System.out.println("lineNo: " + parseTokens.get(index + 1));
                    System.out.println("colNo: " + parseTokens.get(index + 2));
                    System.out.println("ERROR: finalType = " + finalType);
                    System.out.println("ERROR: tempType = " + tempType);
                    
                    errorsFound.add("Trying to assign "
                            + tempType + " to "
                            + finalType);
                    //add line no corresponding to error
                    lineNo = parseTokens.get(index + 1);
                    errorLocation.add(lineNo);

                    //add col no corresponding to error
                    colNo = parseTokens.get(index + 2);
                    errorLocation.add(colNo);
                }
                break;
            default:
                // check the function arguments instead
                s_table.Lookup(rememberTableName);
                // we have the print for "found it here"
                break;
        }
    }

    public static void analyze_function() {
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
            CurrLexeme = parseTokens.get(index + 3);
            if (!Variables.contains(CurrLexeme)
                    && !Functions.contains(CurrLexeme)) {
                FuncName = CurrLexeme;
                Functions.add(CurrLexeme);
//            System.out.println("Set FuncName: " + CurrLexeme + " at line: " + 
//                    parseTokens.get(index + 1) + " and col: " 
//                    + parseTokens.get(index + 2));
            }
//##############################################################################
        ArrayList tempList;     // store a table temporarily for checks
//      int foundId = 0;        // equals 1 if ID has been declared already
        // Check current table then up so go in reverse order
        for (int i = lookUpArray.size() - 1; i >= 0; i--) {
            tempList = s_table.Lookup(lookUpArray.get(i));
//          System.out.println("Looking in: " + lookUpArray.get(i) + "\nfor: " + CurrLexeme);
            if (!tempList.isEmpty() && tempList.contains(CurrLexeme)) {
//              System.out.println("Found it here:\n" + tempList);
//              foundId = 1;
                
                // set flag so Var_Id knows to check function types instead
                checkFuncArgs = 1;
                if (rememberTableName.equals("NO_TABLE")) {
                    rememberTableName = TableName;
                }
//                System.out.println("Current tableName: " + rememberTableName);
                int getType = tempList.indexOf(CurrLexeme) + 1;
                // only set final type if it hasn't been set yet. We will
                // reset it once we seee MP_SCOLON
                if (finalType.equals("NO_TYPE")) {
                    finalType = (String) tempList.get(getType);
                    tempType = finalType;
//                  System.out.println("finalType set: " + finalType);
                    // assignee should only be set when type is set as well
                    // we can use one check to control both assigns
                    assignee = CurrLexeme;
                    break;
                } else {
                    tempType = (String) tempList.get(getType);
//                  System.out.println("tempType set: " + tempType);
                }
            }
        } // END of for loop

        if (!Functions.contains(CurrLexeme)) {
            sourceOfError = "Function " + CurrLexeme
                    + " was never declared, or is out of scope";
            errorsFound.add(sourceOfError);
            // add line no corresponding to error
            lineNo = parseTokens.get(index + 1);
            errorLocation.add(lineNo);

            // add col no corresponding to error
            colNo = parseTokens.get(index + 2);
            errorLocation.add(colNo);
        }
//
//            if (finalType.compareTo(tempType) != 0) {
//                System.out.println("lineNo: " + parseTokens.get(index + 1));
//                System.out.println("colNo: " + parseTokens.get(index + 2));
//                System.out.println("ERROR: finalType = " + finalType);
//                System.out.println("ERROR: tempType = " + tempType);
//                errorsFound.add("Trying to assign "
//                        + tempType + " to "
//                        + finalType);
//                // add line no corresponding to error
//                lineNo = parseTokens.get(index + 1);
//                errorLocation.add(lineNo);
//
//                // add col no corresponding to error
//                colNo = parseTokens.get(index + 2);
//                errorLocation.add(colNo);
//            }
    }
}
