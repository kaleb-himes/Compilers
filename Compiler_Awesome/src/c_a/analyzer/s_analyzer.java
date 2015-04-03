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

    static ArrayList<String> checkFuncArgs_List = new ArrayList<>();
    static String rememberFunctionName = "NO_FUNCTION";
    static String rememberFunctionType = "NO_FUNC_TYPE";
    static int FuncCompare = 0;
    static int ArgCompare = 0;
    static int compareToArg = 0;
    static ArrayList<String> alreadyChecked = new ArrayList<>();

    public static void analyze_variable() {
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
        if (!Functions.contains(parseTokens.get(index + 3))) {
            CurrLexeme = parseTokens.get(index + 3);
            Variables.add(CurrLexeme);
//                System.out.println("Set VarID: " + CurrLexeme);
        }
//##############################################################################
        ArrayList<String> tempList;     // store a table temporarily for checks
        int foundId = 0;        // equals 1 if ID has been declared already
        for (int i = destroyPointer; i >= 0; i--) {
            tempList = s_table.Lookup(lookUpArray.get(i));
//          System.out.println("Looking in: " + lookUpArray.get(i) + "\nfor: " + CurrLexeme);
            if (!tempList.isEmpty() && tempList.contains(CurrLexeme)) {
//              System.out.println("Found it here:\n" + tempList);
                int getType = tempList.indexOf(CurrLexeme) + 1;
                //only set final type if it hasn't been set yet. We will
                //reset it once we see MP_SCOLON
                if (finalType.equals("NO_TYPE")) {
                    finalType = tempList.get(getType);
                    tempType = finalType;
//                  System.out.println("finalType set: " + finalType);
                    //assignee should only be set when type is set as well
                    //we can use one check to control both assigns
                    assignee = CurrLexeme;
                    foundId = 1;
                    break;
                } else {
                    tempType = tempList.get(getType);
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
//        System.out.println("checkFunArgs = " + checkFuncArgs);

        switch (checkFuncArgs) {

            case 0:
                if (finalType.compareTo(tempType) != 0) {
//                    System.out.println("lineNo: " + parseTokens.get(index + 1));
//                    System.out.println("colNo: " + parseTokens.get(index + 2));
//                    System.out.println("ERROR: finalType = " + finalType);
//                    System.out.println("ERROR: tempType = " + tempType);

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
            case 1:
                // handles read and write where comparisons are unneccessary
                if (comingFromRead == 1 || comingFromWrite == 1) {
                    break;
                }
                // check the function arguments instead
                ArrayList<String> tempList2;
                //no need to check for empty, we know it's there cause we're
                //remembering it from when it was added.
                tempList2 = s_table.Lookup(rememberTableName);
                int getLocation = tempList2.indexOf(rememberFunctionName);
                if (!alreadyChecked.contains(CurrLexeme)) {
                    for (int i = getLocation + 7; i < tempList2.size(); i++) {
                        if (tempList2.get(i).contains("MP_")) {
                            checkFuncArgs_List.add(tempList2.get(i));
//                            System.out.println("Adding: " + tempList2.get(i));
                        }

                    }
                }
                if (FuncCompare == 1 && !alreadyChecked.contains(CurrLexeme)) {
                    // what we're looking at
//                    System.out.println("current lexeme is: " + CurrLexeme);
//                    System.out.println("We're looking at in function: " + rememberFunctionType);

                    //remember which ones we've checked so we don't add them over
                    //and over
                    alreadyChecked.add(CurrLexeme);
                    int typeLocation = tempList2.indexOf(CurrLexeme);
                    String getType = tempList2.get(typeLocation + 1);
//                    System.out.println("We want to compare to: " + getType);
                    if (!rememberFunctionType.equals(getType)) {
                        errorsFound.add("Function " + rememberFunctionName + 
                                " returns a " + rememberFunctionType + 
                                " you tried to assign this to " + CurrLexeme +
                               " which is of type " + getType);
                        //add line no corresponding to error
                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                    }
                    ArgCompare = 1;
                } else if (ArgCompare == 1 && !alreadyChecked.contains(CurrLexeme)) {
                    // what we're looking at
                    int typeLocation = tempList2.indexOf(CurrLexeme);
                    String getType = tempList2.get(typeLocation + 1);
//                    System.out.println("current lexeme is: " + CurrLexeme);
//                    System.out.println("We're looking at in arg: " + getType);
                    //remember which ones we've checked so we don't add them over
                    //and over
                    alreadyChecked.add(CurrLexeme);

                    rememberFunctionType = checkFuncArgs_List.get(compareToArg);
                    String compareTo = checkFuncArgs_List.get(compareToArg);
//                    System.out.println("We want to compare to: " + compareTo);
                    if (!compareTo.equals(getType)) {
                        errorsFound.add("Trying pass argument of type "
                                + getType + " function " + rememberFunctionName 
                                + " wanted " + rememberFunctionType);
                        //add line no corresponding to error
                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                    }
                    compareToArg++;
//                    System.out.println("compareToArg = " + compareToArg);
                    if (compareToArg == checkFuncArgs_List.size() - 1) {
                        ArgCompare = 0;
                        FuncCompare = 1;
                        checkFuncArgs = 0;
                        rememberFunctionName = "NO_FUNCTION";
                        rememberFunctionType = "NO_FUNC_TYPE";
//                        System.out.println("\n\n");
                    }
                }
                // we have the print for "found it here"
                checkFuncArgs_List.clear();
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
        ArrayList<String> tempList;     // store a table temporarily for checks
//      int foundId = 0;        // equals 1 if ID has been declared already
        // Check current table then up so go in reverse order
        for (int i = destroyPointer; i >= 0; i--) {
            tempList = s_table.Lookup(lookUpArray.get(i));
//          System.out.println("Looking in: " + lookUpArray.get(i) + "\nfor: " + CurrLexeme);
            if (!tempList.isEmpty() && tempList.contains(CurrLexeme)) {
//              System.out.println("Found it here:\n" + tempList);
//              foundId = 1;

                // set flag so Var_Id knows to check function types instead
                compareToArg = 0;
//                System.out.println(parseTokens.get(index));
                checkFuncArgs = 1;
                if (FuncCompare == 0) {
                    FuncCompare = 1;
                } else {
                    FuncCompare = 0;

                }

                if (rememberTableName.equals("NO_TABLE") && tempList.contains(CurrLexeme)) {
                    rememberFunctionName = CurrLexeme;
                    int getPosition = tempList.indexOf(CurrLexeme);
                    rememberFunctionType = tempList.get(getPosition + 1);
                    rememberTableName = lookUpArray.get(i);
//                    System.out.println("rememberTableName = " + rememberTableName);
//                    System.out.println("rememberFunctionName = " + rememberFunctionName);
                }
//                System.out.println("Current tableName: " + rememberTableName);
                int getType = tempList.indexOf(CurrLexeme) + 1;
                // only set final type if it hasn't been set yet. We will
                // reset it once we seee MP_SCOLON
                if (finalType.equals("NO_TYPE")) {
                    finalType = tempList.get(getType);
                    tempType = finalType;
//                  System.out.println("finalType set: " + finalType);
                    // assignee should only be set when type is set as well
                    // we can use one check to control both assigns
                    assignee = CurrLexeme;
                    break;
                } else {
                    tempType = tempList.get(getType);
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
