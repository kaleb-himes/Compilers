/*
 * There should be a case statement that switches on a global lookahead variable.
 * The case statement should have case clauses for each rule in the grammar that 
 * has the nonterminal of this method on the left, plus one 
 * (the "others" clause) for the error condition.
 *
 * Each method should have pre- and post-conditions.
 * Each case clause should be commented with the rule number and actual rule it expands.
 * In each case clause, the right hand side of the appropriate rule should be expanded:
 * Wherever a token appears, a call to a new method, Match, must be made with the token as an argument;
 * wherever a nonterminal appears, a call to that nonterminal's method must be inserted.
 * In the "others" clause, a call to a new method, Error, must be made.
 * You will need to provide stubs for Match and Error as well.
 *
 * The case selectors must, of course, be of your proper Token_Type, and you will 
 * need to insert dummy tokens for now in order to make it possible to compile the stubs.
 * 
 * In some cases you may know the proper tokens to make the correct selection. 
 * You may insert them at this point. You can change them later if you are wrong.
 * 
 * Without knowing the proper tokens to insert, your nonterminal methods will not 
 * actually be able to call other nonterminal methods, so make sure your stubs to 
 * not try to execute such calls. One way to do this is to ensure that the dummy 
 * value you insert for the lookahead always takes the "others" route, where you 
 * can print your message from last time, as in "Expression has not yet been 
 * implemented." You will be removing these messages one at a time as you later 
 * completely implement these stubs.
 * 
 * Finally, you will need to insert temporarily a series of calls in your parser 
 * that calls each of the nonterminal methods and the Match and Error methods, 
 * just to see that the program works to the extent that each method can be called.
 * Remember that each team member is to get a subset of the nonterminals to implement as stubs.
 */
package c_a.parser;

import static c_a.fileReader.file_reader.reader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import symbol_tables.s_table;

/**
 *
 * @author khimes
 */
public class parser {

    static Boolean done = false;
    static String lookAhead = "";
    static String previous = "";
    static int index, sColonMark, procIdFound, frmlParamState, stmntSeqMark,
            expMark, simpExpMark, G_Check;
    static List<String> parseTokens;
    static List<String> stackTrace;
    //keep track of all errors found while parsing program
    static List<String> errorsFound;
    static String sourceOfError = "";
    static String potentialError = "";
    static int blockState;
    static PrintWriter parserWriter;
// Lexicallity
    static ArrayList Variables  = new ArrayList<>();
    static ArrayList Functions  = new ArrayList<>();
    static ArrayList Procedures = new ArrayList<>();
    
//##############################################################################
//######### BEGIN Symbol table resources #######################################
//##############################################################################
    static String TableName, ProcName, FuncName, Label_1 = "L";
    static int NestingLevel, Label_2;
    static String CurrLexeme, Type, Kind, Mode, CurrToken;
    static String[] Parameters;
    static int Size;
    static ArrayList<String> dynamicParams = new ArrayList<>();
    static ArrayList<String> listIDs = new ArrayList<>();
    static int In_Proc_Func_Flag = 0;
//##############################################################################
    

    public void runParse() throws FileNotFoundException, IOException {
        
        //initialize the symbol table
        symbol_tables.s_table.Init_Table();

        //initialize the parserWriter for parse tree generation
        parserWriter = new PrintWriter("src/parser_resources/parser.out", "UTF-8");

        /* 
         * Re-initialize the file reader to read from our scanner output file
         * instead of reading the program input file
         */
        c_a.fileReader.file_reader.fileReaderInit(
                c_a.fileReader.file_reader.outLocation);
        parseTokens     = new ArrayList<>();
        stackTrace      = new ArrayList<>();
        errorsFound     = new ArrayList<>();
        String line;
        lookAhead       = "";
        previous        = "";
        index           = 0;
        blockState      = 1;
        sColonMark      = 0;
        stmntSeqMark    = 0;
        expMark         = 0;
        simpExpMark     = 0;
        procIdFound     = 0;
        frmlParamState  = 0;
        NestingLevel    = 0;
        Label_2         = 0;
        ProcName        = "";
        FuncName        = "";
        dynamicParams.add("");
        //initialize Parameters to empty set, update as needed
        String[] init   = new String[1];
        init[0] = "NO_PARAMS";
        Parameters = init;
        

        //read in one line at a time from the output file
        while ((line = reader.readLine()) != null) {
            //replace all of our nice formatted spacing with a single space
            line = line.trim().replaceAll(" +", " ");
            //split the string into tokens using space as the splitter
            StringTokenizer st = new StringTokenizer(line, " ");
            while (st.hasMoreElements()) {
                //use temp to check and handle comments or strings
                String temp = st.nextElement().toString();
                /*
                 * trim each token, and add it to the array
                 * lines n=0,n+=4 = micro pascal token
                 * lines n=1,n+=4 = line number
                 * lines n=2,n+=4 = column number
                 * lines n=3,n+=4 = lexeme
                 */
                parseTokens.add(temp.trim());
            }
        }

        /* kick off our parse with a lookahead set then call Sys_Goal */
        Get_Lookahead();
        Sys_Goal();
    }

// <editor-fold defaultstate="collapsed" desc="Get_Lookahead"> 
    public static void Get_Lookahead() {
        /* Get Look Ahead */
        /* TODO LOGIC HERE FOR LOOK AHEAD */
        previous = lookAhead;
        if (index < parseTokens.size()) {
            lookAhead = parseTokens.get(index);
        }

        //if lookahead is a comment, skip it.
        if (lookAhead.equals("MP_COMMENT")) {
            index += 3;
            lookAhead = parseTokens.get(index);
            while (!lookAhead.contains("}")) {
//                System.out.println("skipping: " + lookAhead);
                index++;
                if (index > parseTokens.size()) {
                    sourceOfError = "Get_Lookahead ran over EOF";
                    //Error();
                    errorsFound.add(sourceOfError);
                    break;
                }
                Get_Lookahead();
            }
            if (lookAhead.contains("}")) {
                index++;
                Get_Lookahead();
            }
        }
//        System.out.println("Lookahead ------------------------------------------->" + lookAhead);
        if (!potentialError.equals("")) {
//            System.out.println("Potential Error ------------------------------------->" + potentialError);
            potentialError = "";
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Advance_Pointer"> 
    public static void Advance_Pointer() {
        if (lookAhead.equals("MP_STRING_LIT")) {
            index += 3;
            String peek = parseTokens.get(index);
            while (!peek.contains("MP_")) {
//                System.out.println("skipping: " + peek);
                index++;
                if (index > parseTokens.size()) {
                    sourceOfError = "Advance_Pointer ran over EOF";
                    //Error();
                    errorsFound.add(sourceOfError);
                    break;
                }
                peek = parseTokens.get(index);
            }
        } else {
            index += 4;
        }
        Get_Lookahead();
    }
// </editor-fold>

// rule 1
// <editor-fold defaultstate="collapsed" desc="Sys_Goal"> 
    public static void Sys_Goal() {
        stackTrace.add("Sys_Goal");
        // 1. SystemGoal -> Program MP_EOF
        parserWriter.println("rule #1  : expanding");
        Program();
        G_Check = Match("MP_EOF");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #1  : TERMINAL");
                stackTrace.remove("Sys_Goal");

                if (errorsFound.size() > 0) {
                    Error();
                } else {
                    Terminate("Program parsed successfully, found MP_EOF");
                }
                break;

            default:
                errorsFound.add(sourceOfError);
                sourceOfError = "Sys_Goal, Expected MP_EOF found: " + lookAhead;
                stackTrace.remove("Sys_Goal");
                Error();
                break;
        }
    }
// </editor-fold>

// rule 2
// <editor-fold defaultstate="collapsed" desc="Program"> 
    public static void Program() {
        stackTrace.add("Program");
        // 2. Program -> Prog_Head MP_SCOLON Block MP_PERIOD
        //precondition
        parserWriter.println("rule #2  : expanding");
        Prog_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #2  : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #2  : expanding");
                Block();
                G_Check = Match("MP_PERIOD");
                //we do want to fall through here, to evaluate second G_Check
                if (G_Check == 1) {
                    parserWriter.println("rule #2  : TERMINAL");
                    Advance_Pointer();
                    stackTrace.remove("Program");
                    break;
                } else {
                    sourceOfError = "Program, Expected MP_PERIOD, found: " + lookAhead;
                    //Error();
                    errorsFound.add(sourceOfError);
                    break;
                }
            default:
                sourceOfError = "Program, Expected MP_SCOLON found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }

    }
// </editor-fold>

// rule 3
// <editor-fold defaultstate="collapsed" desc="Prog_Head"> 
    public static void Prog_Head() {
        stackTrace.add("Prog_Head");
        // 3. ProgramHeading -> MP_PROGRAM_WORD Prog_Id
        //precondition
        G_Check = Match("MP_PROGRAM");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #3  : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #3  : expanding");
                Prog_Id();
                stackTrace.remove("Prog_Head");
                break;

            default:
                sourceOfError = "Prog_Head, Expected MP_PROGRAM found:"
                        + " " + lookAhead;
                errorsFound.add(sourceOfError);
                //Error();
                break;
        }

    }
// </editor-fold>

// rule 4
// <editor-fold defaultstate="collapsed" desc="Block"> 
    public static void Block() {
        stackTrace.add("Block");
        //track which lookaheads we have used so far;
        //4. Block -> Var_Dec_Part Proc_Func_Dec_Part Statement_Part
        parserWriter.println("rule #4  : expanding");
        Var_Dec_Part();
        parserWriter.println("rule #4  : expanding");
        Proc_Func_Dec_Part();
        parserWriter.println("rule #4  : expanding");
        Statement_Part();
        stackTrace.remove("Block");
    }
// </editor-fold>

// rules 5 and 6
// <editor-fold defaultstate="collapsed" desc="Var_Dec_Part"> 
    public static void Var_Dec_Part() {
        stackTrace.add("Var_Dec_Part");
        // 5. Var_Dec_Part -> MP_VAR_WORD Var_Dec MP_SCOLON Var_Dec_Tail
        // 6. Var_Dec_Part -> MP_EMPTY
        G_Check = Match("MP_VAR");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Kind = parseTokens.get(index+3);
//##############################################################################
                parserWriter.println("rule #5  : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #5  : expanding");
                Var_Dec();
                G_Check = Match("MP_SCOLON");
                if (G_Check == 1) {
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
//                    if (In_Proc_Func_Flag == 0) {
//                        for (int i = 0; i < listIDs.size(); i++) {
//                            CurrLexeme = listIDs.get(i);
//                            s_table.Insert_Row(TableName, CurrLexeme,
//                                    CurrToken, Type, Kind, Mode,
//                                    Integer.toString(Size), Parameters);
//                        }
//                        listIDs.clear();
//                    }
//                    dynamicParams.clear();
//##############################################################################
                    parserWriter.println("rule #5  : TERMINAL");
                    Advance_Pointer();
                    parserWriter.println("rule #5  : expanding");
                    Var_Dec_Tail();
                    stackTrace.remove("Var_Dec_Part");
                    break;
                } else {
                    sourceOfError = "Var_Dec_Part, Expected MP_SCOLON "
                            + "found:  " + lookAhead;
                    //Error();
                    errorsFound.add(sourceOfError);
                    break;
                }
            default:
                stackTrace.remove("Var_Dec_Part");
                potentialError = "Var_Dec_Part, treated as empty";
                parserWriter.println("rule #6  : --E--");
                break;
        }
    }
// </editor-fold>

// rules 7 and 8
// <editor-fold defaultstate="collapsed" desc="Var_Dec_Tail"> 
    public static void Var_Dec_Tail() {
        stackTrace.add("Var_Dec_Tail");
        // 7. Var_Dec_Tail -> Var_Dec MP_SCOLON Var_Dec_Tail 
        // 8. Var_Dec_Tail -> MP_EMPTY
        //precondition
        if (lookAhead.equals("MP_IDENTIFIER")) {
            parserWriter.println("rule #7  : expanding");
            Var_Dec();
        }
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
//uncomment for semi-colon errors
//                if (!previous.equals("MP_SCOLON")
//                        && !parseTokens.get(index+4).equals("MP_SCOLON")) {
                    parserWriter.println("rule #7  : TERMINAL");
                    Advance_Pointer();
                    parserWriter.println("rule #7  : expanding");
                    Var_Dec_Tail();
                    stackTrace.remove("Var_Dec_Tail");
//                }
//                else {
//                    sourceOfError = "Too many Semi-Colons.";
//                    errorsFound.add(sourceOfError);
//                }
                break;
            default:
                parserWriter.println("rule #8  : --E--");
                potentialError = "Var_Dec_Tail, treated as empty";
                stackTrace.remove("Var_Dec_Tail");
                break;
        }
    }
// </editor-fold>

// rule 9
// <editor-fold defaultstate="collapsed" desc="Var_Dec"> 
    public static void Var_Dec() {
        stackTrace.add("Var_Dec");
        // 9. Var_Dec -> Id_List MP_COLON Type
        parserWriter.println("rule #9  : expanding");
        Id_List();
        G_Check = Match("MP_COLON");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #9  : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #9  : expanding");
                Type();
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                for (int i = 0; i < listIDs.size(); i++) {
                            CurrLexeme = listIDs.get(i);
                            s_table.Insert_Row(TableName, CurrLexeme,
                                    CurrToken, Type, Kind, Mode,
                                    Integer.toString(Size), Parameters);
                        }
                listIDs.clear();
//##############################################################################
                stackTrace.remove("Var_Dec");
                break;
            default:
                sourceOfError = "Var_Dec, Expected MP_COLON found: "
                        + "" + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rules 10, 11, 12, and 13
// <editor-fold defaultstate="collapsed" desc="Type"> 
    public static void Type() {
        stackTrace.add("Type");
        // 10. Type -> MP_INTEGER_WORD
        // 11. Type -> MP_FLOAT_WORD
        // 12. Type -> MP_STRING_WORD
        // 13. Type -> MP_BOOLEAN_WORD
        // call match to make grader happy, completely unecessary here and made
        // for too verbose and ugly of code, logic was difficult to follow
        
        G_Check = Match("MP_INTEGER");
        switch (lookAhead) {
            case "MP_INTEGER":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index+3);
                CurrToken = lookAhead;
                Size = 4;
//##############################################################################
                parserWriter.println("rule #10 : TERMINAL");
                Advance_Pointer();
                //write rule #10 to file
                break;
            case "MP_FLOAT":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index+3);
                CurrToken = lookAhead;
                Size = 8;
//##############################################################################
                parserWriter.println("rule #11 : TERMINAL");
                Advance_Pointer();
                //write rule #11 to file
                break;
            case "MP_STRING":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index+3);
                CurrToken = lookAhead;
                Size = 55;
//##############################################################################
                parserWriter.println("rule #12 : TERMINAL");
                Advance_Pointer();
                //write rule #12 to file
                break;
            case "MP_BOOLEAN":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index+3);
                CurrToken = lookAhead;
                Size = 2;
//##############################################################################
                parserWriter.println("rule #13 : TERMINAL");
                Advance_Pointer();
                //write rule #13 to file
                break;
            default:
                sourceOfError = "Type, Expected MP_INTEGER, MP_FLOAT, MP_STRING, or"
                        + " MP_BOOLEAN instead found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
        stackTrace.remove("Type");
    }
// </editor-fold>

// rules 14, 15, and 16
// <editor-fold defaultstate="collapsed" desc="Proc_Func_Dec_Part"> 
    public static void Proc_Func_Dec_Part() {
        stackTrace.add("Proc_Func_Dec_Part");
        // 14. Proc_Func_Dec_Part -> Proc_Dec Proc_Func_Dec_Part 
        // 15. Proc_Func_Dec_Part -> Func_Dec Proc_Func_Dec_Part 
        // 16. Proc_Func_Dec_Part -> MP_EMPTY
        //precondition
        switch (lookAhead) {
            case "MP_PROCEDURE":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                // a flag for symantic analysis lets us know if we are
                // building up variables in the current scope or in the next
                // table.
                In_Proc_Func_Flag = 1;
//##############################################################################
                parserWriter.println("rule #14 : expanding");
                Proc_Dec();
                parserWriter.println("rule #14 : expanding");
                Proc_Func_Dec_Part();
                stackTrace.remove("Proc_Func_Dec_Part");
                break;

            case "MP_FUNCTION":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                // a flag for symantic analysis lets us know if we are
                // building up variables in the current scope or in the next
                // table.
                In_Proc_Func_Flag = 1;
//##############################################################################
                parserWriter.println("rule #15 : expanding");
                Func_Dec();
                parserWriter.println("rule #15 : expanding");
                Proc_Func_Dec_Part();
                stackTrace.remove("Proc_Func_Dec_Part");
                break;

            default:
                In_Proc_Func_Flag = 0;
                parserWriter.println("rule #16 : --E--");
                potentialError = "Proc_Func_Dec_Part treated as Empty.";
                stackTrace.remove("Proc_Func_Dec_Part");
                break;
        }
        In_Proc_Func_Flag = 0;
    }
// </editor-fold>

// rule 17
// <editor-fold defaultstate="collapsed" desc="Proc_Dec"> 
    public static void Proc_Dec() {
        stackTrace.add("Proc_Dec");
        // 17. Proc_Dec -> Proc_Head MP_SCOLON Block MP_SCOLON
        parserWriter.println("rule #17 : expanding");
        Proc_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
            if (In_Proc_Func_Flag == 1) {
                Convert_To_String_Array(dynamicParams);
                s_table.Insert_Row(TableName, CurrLexeme, CurrToken, Type, Kind,
                        Mode, Integer.toString(Size), Parameters);
                dynamicParams.clear();
            }
//##############################################################################
                parserWriter.println("rule #17 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #17 : expanding");
                Block();
                G_Check = Match("MP_SCOLON");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #17 : TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Proc_Dec");
                        break;

                    default:
                        sourceOfError = "Proc_Dec, Expected MP_SCOLON_2 found: "
                                + "" + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                }
                break;
            default:
                sourceOfError = "Proc_Dec, Expected MP_SCOLON_1 found: "
                        + "" + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rule 18
// <editor-fold defaultstate="collapsed" desc="Func_Dec"> 
    public static void Func_Dec() {
        stackTrace.add("Func_Dec");
        // 18. Func_Dec -> Func_Head MP_SCOLON Block MP_SCOLON
        parserWriter.println("rule #18 : expanding");
        Func_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #18 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #18 : expanding");
                Block();
                G_Check = Match("MP_SCOLON");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #18 : TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Func_Dec");
                        break;
                    default:
                        sourceOfError = "Func_Dec, Expected MP_SCOLON_2 found: "
                                + "" + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                }
                break;
            default:
                sourceOfError = "Func_Dec, Expected MP_SCOLON_1 found: "
                        + "" + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rule 19
// <editor-fold defaultstate="collapsed" desc="Proc_Head"> 
    public static void Proc_Head() {
        stackTrace.add("Proc_Head");
        // 19. Proc_Head -> MP_PROCEDURE Proc_Id Opt_Formal_Param_List
        G_Check = Match("MP_PROCEDURE");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Size = 0;                                                   //##
                CurrToken = lookAhead;                                      //##
                Type = null;                                                //##
                Kind = parseTokens.get(index+3);                            //##
//##############################################################################
                parserWriter.println("rule #19 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #19 : expanding");
                Proc_Id();
                parserWriter.println("rule #19 : expanding");
                Opt_Formal_Param_List();
                stackTrace.remove("Proc_Head");
                break;
            default:
                sourceOfError = "Proc_Head, Expected MP_PROCEDURE found:"
                        + " " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rule 20
// <editor-fold defaultstate="collapsed" desc="Func_Head">
    public static void Func_Head() {
        stackTrace.add("Func_Head");
        // 20. Func_Head -> MP_FUNCTION Function_Id Opt_Formal_Param_List MP_COLON Type
        G_Check = Match("MP_FUNCTION");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #20 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #20 : expanding");
                Func_Id();
                parserWriter.println("rule #20 : expanding");
                Opt_Formal_Param_List();
                G_Check = Match("MP_COLON");
                if (G_Check == 1) {
                    parserWriter.println("rule #20 : TERMINAL");
                    Advance_Pointer();
                    parserWriter.println("rule #20 : expanding");
                    Type();
                    stackTrace.remove("Func_Head");
                    break;
                } else {
                    sourceOfError = "Func_Head, Expected MP_COLON found: "
                            + "" + lookAhead;
                    //Error();
                    errorsFound.add(sourceOfError);
                    break;
                }
            default:
                sourceOfError = "Func_Head, Expected MP_FUNCTION found: "
                        + "" + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rules 21 and 22
// <editor-fold defaultstate="collapsed" desc="Opt_Formal_Param_List">
    public static void Opt_Formal_Param_List() {
        stackTrace.add("Opt_Formal_Param_List");
        // 21. Opt_Formal_Param_List -> MP_LPAREN Formal_Param_Sec Formal_Param_Sec_Tail MP_RPAREN 
        // 22. Opt_Formal_Param_List -> MP_EMPTY
        //precondition
        G_Check = Match("MP_LPAREN");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #21 : expanding");
                Advance_Pointer();
                parserWriter.println("rule #21 : expanding");
                Formal_Param_Sec();
                parserWriter.println("rule #21 : expanding");
                Formal_Param_Sec_Tail();
                G_Check = Match("MP_RPAREN");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #21 : TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Opt_Formal_Param_List");
                        break;

                    default:
                        sourceOfError = "Opt_Formal_Param_List, Expected "
                                + "MP_RPAREN found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                }
                break;
            default:
                parserWriter.println("rule #22 : --E--");
                potentialError = "Opt_Formal_Param_List treated as Empty";
                stackTrace.remove("Opt_Formal_Param_List");
                break;
        }
    }
// </editor-fold>

// rules 23 and 24
// <editor-fold defaultstate="collapsed" desc="Formal_Param_Sec_Tail">
    public static void Formal_Param_Sec_Tail() {
        stackTrace.add("Formal_Param_Sec_Tail");
        // 23. Formal_Param_Sec_Tail -> MP_SCOLON Formal_Param_Sec Formal_Param_Sec_Tail
        // 24. Formal_Param_Sec_Tail -> MP_EMPTY
        //precondition
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #23 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #23 : expanding");
                Formal_Param_Sec();
                parserWriter.println("rule #23 : expanding");
                Formal_Param_Sec_Tail();
                stackTrace.remove("Formal_Param_Sec_Tail");
                break;
            default:
                parserWriter.println("rule #24 : --E--");
                potentialError = "Formal_Param_Sec_Tail, Treated as Empty";
                stackTrace.remove("Formal_Param_Sec_Tail");
                break;
        }
    }
// </editor-fold>

// rules 25 and 26
// <editor-fold defaultstate="collapsed" desc="Formal_Param_Sec">
    public static void Formal_Param_Sec() {
        stackTrace.add("Formal_Param_Sec");
        // 25. Formal_Param_Sec -> Val_Param_Sec
        // 26. Formal_Param_Sec -> Var_Param_Sec
        if (lookAhead.equals("MP_VAR")) {
            parserWriter.println("rule #25 : expanding");
            Var_Param_Sec();
            stackTrace.remove("Formal_Param_Sec");
        } else {
            parserWriter.println("rule #26 : expanding");
            Val_Param_Sec();
            stackTrace.remove("Formal_Param_Sec");
        }
    }
// </editor-fold>

//rule 27
// <editor-fold defaultstate="collapsed" desc="Val_Param_Sec">
    public static void Val_Param_Sec() {
        stackTrace.add("Val_Param_Sec");
        // 27. Val_Param_Sec -> Id_List MP_COLON Type
        parserWriter.println("rule #27 : expanding");
        Id_List();
        G_Check = Match("MP_COLON");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #27 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #22 : expanding");
                Type();
                stackTrace.remove("Val_Param_Sec");
                break;
            default:
                sourceOfError = "Val_Param_Sec, Expected MP_COLON found: "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rule 28
// <editor-fold defaultstate="collapsed" desc="Var_Param_Sec">
    public static void Var_Param_Sec() {
        stackTrace.add("Var_Param_Sec");
        // 28. Var_Param_Sec -> MP_VAR Id_List MP_COLON Type
        G_Check = Match("MP_VAR");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #28 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #28 : expanding");
                Id_List();
                G_Check = Match("MP_COLON");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #28 : TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #28 : expanding");
                        Type();
                        stackTrace.remove("Var_Param_Sec");
                        break;
                    default:
                        sourceOfError = "Var_Param_Sec, Expected MP_COLON found"
                                + ": " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                }
                break;
            default:
                sourceOfError = "Var_Param_Sec, Expected MP_VAR found: "
                        + "" + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rule 29
// <editor-fold defaultstate="collapsed" desc="Statement_Part">
    public static void Statement_Part() {
        stackTrace.add("Statement_Part");
        // 29. Statement_Part -> Compound_Statement
        parserWriter.println("rule #29 : expanding");
        Compound_Statement();
        stackTrace.remove("Statement_Part");
    }
// </editor-fold>

// rule 30
// <editor-fold defaultstate="collapsed" desc="Compound_Statement">
    public static void Compound_Statement() {
        stackTrace.add("Compound_Statement");
        // 30. Compound_Statement -> MP_BEGIN Statement_Seq MP_END
        G_Check = Match("MP_BEGIN");
        switch (G_Check) {
            case 1:
//##############################################################################
//############ SYMBOL TABLE STUFF ##############################################
//##############################################################################
            //update the Label
            String Label = Label_1.concat(Integer.toString(Label_2));
            Label_2++;
            //update the nesting level
            int Nlvl = NestingLevel;
            NestingLevel++;
            //insert Table info using s_table API name, nesting, label
            if (!ProcName.equals("")) {
                TableName = ProcName;
                s_table.New_Table(TableName, Integer.toString(Nlvl), Label);
            }
            else if (!FuncName.equals("")) {
                TableName = FuncName;
                s_table.New_Table(TableName, Integer.toString(Nlvl), Label);
            }
            else
                potentialError = "ProcName or FuncName May not be set";
//##############################################################################
                parserWriter.println("rule #30 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #30 : expanding");
                Statement_Seq();
                G_Check = Match("MP_END");
                switch (G_Check) {
                    case 1:
//##############################################################################
//############ SYMBOL TABLE STUFF ##############################################
//##############################################################################
//                        s_table.Destroy(TableName);
                        FuncName = "";
                        ProcName = "";
//##############################################################################
                        parserWriter.println("rule #30 : TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Compound_Statement");
                        break;

                    default:
                        sourceOfError = "Compound_Statement, Expected MP_END "
                                + "found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                }
                break;
            default:
                sourceOfError = "Compound_Statement, Expected MP_BEGIN found "
                        + "" + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rule 31
// <editor-fold defaultstate="collapsed" desc="Statement_Seq">
    public static void Statement_Seq() {
        stackTrace.add("Statement_Seq");
        // 31. Statement_Seq -> Statement Statement_Tail
        parserWriter.println("rule #31 : expanding");
        Statement();
        parserWriter.println("rule #31 : expanding");
        Statement_Tail();
        stackTrace.remove("Statement_Seq");
    }
// </editor-fold>

// rules 32 and 33
// <editor-fold defaultstate="collapsed" desc="Statement_Tail">
    public static void Statement_Tail() {
        stackTrace.add("Statement_Tail");
        // 32. Statement_Tail -> MP_SCOLON Statement Statement_Tail
        // 33. Statement_Tail -> MP_EMPTY
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #32 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #32 : expanding");
                Statement();
                parserWriter.println("rule #32 : expanding");
                Statement_Tail();
                stackTrace.remove("Statement_Tail");
                break;

            default:
                parserWriter.println("rule #33 : --E--");
                potentialError = "Statement_Tail, Treated as Empty";
                stackTrace.remove("Statement_Tail");
                break;
        }
    }
// </editor-fold>

// rules 34, 35, 36, 37, 38, 39, 40, 41, 42, and 43
// <editor-fold defaultstate="collapsed" desc="Statement">
    public static void Statement() {
        stackTrace.add("Statement");
        // 35. Statement -> Compound_Statement
        if (lookAhead.equals("MP_BEGIN")) {
            parserWriter.println("rule #35 : expanding");
            Compound_Statement();
        } // 36. Statement -> Read_Statement
        else if (lookAhead.equals("MP_READ")) {
            parserWriter.println("rule #36 : expanding");
            Read_Statement();
        } // 37. Statement -> Write_Statement
        else if (lookAhead.equals("MP_WRITE") || lookAhead.equals("MP_WRITELN")) {
            parserWriter.println("rule #37 : expanding");
            Write_Statement();
        } // 38. Statement -> Assign_Statement
        else if (lookAhead.equals("MP_IDENTIFIER")) {
            parserWriter.println("rule #38 : expanding");
            Assign_Statement();
        } // 39. Statement -> If_Statement
        else if (lookAhead.equals("MP_IF")) {
            parserWriter.println("rule #39 : expanding");
            If_Statement();
        } // 40. Statement -> While_Statement
        else if (lookAhead.equals("MP_WHILE")) {
            parserWriter.println("rule #40 : expanding");
            While_Statement();
        } // 41. Statement -> Repeat_Statement
        else if (lookAhead.equals("MP_REPEAT")) {
            parserWriter.println("rule #41 : expanding");
            Repeat_Statement();
        } // 42. Statement -> For_Statement
        else if (lookAhead.equals("MP_FOR")) {
            parserWriter.println("rule #42 : expanding");
            For_Statement();
        } // 43. Statement -> Procedure_Statement
        else if (lookAhead.equals("MP_PROCEDURE")) {
            parserWriter.println("rule #43 : expanding");
            Proc_Statement();
        } // 34. Statement -> Empty_Statement (post condition)
        else {
            parserWriter.println("rule #34 : expanding");
            Empty_Statement();
        }
        stackTrace.remove("Statement");
    }
// </editor-fold>

// rule 44
// <editor-fold defaultstate="collapsed" desc="Empty_Statement">
    //Monica started here writing rules
    public static void Empty_Statement() {
        stackTrace.add("Empty_Statement");
        // 44. Empty_Statement -> MP_EMPTY
        parserWriter.println("rule #44 : --E--");
        potentialError = "Statement, Treated as Empty";
        stackTrace.remove("Empty_Statement");
    }
// </editor-fold>

// rule 45
// <editor-fold defaultstate="collapsed" desc="Read_Statement">
    public static void Read_Statement() {
        stackTrace.add("Read_Statement");
        // 45. Read_Statement -> MP_READ_WORD MP_LPAREN Read_Param Read_Param_Tail MP_RPAREN
        G_Check = Match("MP_READ");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #45 : TERMINAL");
                Advance_Pointer();
                G_Check = Match("MP_LPAREN");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #45 : TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #45 : expanding");
                        Read_Param();
                        parserWriter.println("rule #45 : expanding");
                        Read_Param_Tail();
                        G_Check = Match("MP_RPAREN");
                        switch (G_Check) {
                            case 1:
                                parserWriter.println("rule #45 : TERMINAL");
                                Advance_Pointer();
                                stackTrace.remove("Read_Statement");
                                break;

                            default:
                                sourceOfError = "Read_Statement, Expected "
                                        + "MP_RPAREN found: " + lookAhead;
                                //Error();
                                errorsFound.add(sourceOfError);
                                break;
                        } //end case for R_PAREN
                        break;
                    default:
                        sourceOfError = "Read_Statement, Expected "
                                + "MP_LPAREN found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case for LPAREN
                break;
            default:
                sourceOfError = "Read_Statement, Expected "
                        + "MP_READ found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        } //end case for READ
    }
// </editor-fold>

//rules 46 and 47
// <editor-fold defaultstate="collapsed" desc="Read_Param_Tail">
    public static void Read_Param_Tail() {
        stackTrace.add("Read_Param_Tail");
        // 46. Read_Param_Tail -> MP_COMMA Read_Param Read_Param_Tail
        // 47. Read_Param_Tail -> MP_EMPTY
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #46 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #46 : expanding");
                Read_Param();
                parserWriter.println("rule #46 : expanding");
                Read_Param_Tail();
                stackTrace.remove("Read_Param_Tail");
                break;

            default:
                parserWriter.println("rule #47 : --E--");
                potentialError = "Read_Param_Tail, Treated as Empty";
                stackTrace.remove("Read_Param_Tail");
                break;
        }
    }
// </editor-fold>

// rule 48
// <editor-fold defaultstate="collapsed" desc="Read_Param">
    public static void Read_Param() {
        stackTrace.add("Read_Param");
        // 48. Read_Param -> Var_Id
        parserWriter.println("rule #48 : expanding");
        Var_Id();
        stackTrace.remove("Read_Param");
    }
// </editor-fold>

// rules 49 and 50
// <editor-fold defaultstate="collapsed" desc="Write_Statement">
    public static void Write_Statement() {
        stackTrace.add("Write_Statement");
        // 49. Write_Statement -> MP_WRITE_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
        // 50. Write_Statement -> MP_WRITELN_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
        int whichWrite = 0;
        G_Check = Match("MP_WRITE");
        if (G_Check == 0) {
            G_Check = Match("MP_WRITELN");
            if (G_Check == 1) {
                whichWrite = 2;
                parserWriter.println("rule #50 : TERMINAL");
            }
        } else {
            whichWrite = 1;
            parserWriter.println("rule #49 : TERMINAL");
        }
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                G_Check = Match("MP_LPAREN");
                switch (G_Check) {
                    case 1:
                        if (whichWrite == 1) {
                            parserWriter.println("rule #49 : TERMINAL");
                            Advance_Pointer();
                            parserWriter.println("rule #49 : expanding");
                            Write_Param();
                            parserWriter.println("rule #49 : expanding");
                            Write_Param_Tail();
                        } else {
                            parserWriter.println("rule #50 : TERMINAL");
                            Advance_Pointer();
                            parserWriter.println("rule #50 : expanding");
                            Write_Param();
                            parserWriter.println("rule #50 : expanding");
                            Write_Param_Tail();
                        }

                        G_Check = Match("MP_RPAREN");
                        switch (G_Check) {
                            case 1:
                                if (whichWrite == 1) {
                                    parserWriter.println("rule #49 : TERMINAL");
                                } else {
                                    parserWriter.println("rule #50 : TERMINAL");
                                }
                                Advance_Pointer();
                                stackTrace.remove("Write_Statement");
                                break;
                            default:
                                sourceOfError = "Write_Statement, Expected "
                                        + "MP_RPAREN found: " + lookAhead;
                                //Error();
                                errorsFound.add(sourceOfError);
                                break;
                        } //end case for RParen
                        break;
                    default:
                        sourceOfError = "Write_Statement, Expected "
                                + "MP_LPAREN found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case for LParen
                break;
            default:
                sourceOfError = "Write_Statement, Expected "
                        + "MP_WRITE or MP_WRITE_LN found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        } //end case for MP_WRITE
    }
// </editor-fold>

// rules 51 and 52
// <editor-fold defaultstate="collapsed" desc="Write_Param_Tail">
    public static void Write_Param_Tail() {
        stackTrace.add("Write_Param_Tail");
        // 51. Write_Param_Tail -> MP_COMMA Write_Param Write_Param_Tail
        // 52. Write_Param_Tail -> MP_EMPTY
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #51 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #51 : expanding");
                Write_Param();
                parserWriter.println("rule #51 : expanding");
                Write_Param_Tail();
                stackTrace.remove("Write_Param_Tail");
                break;

            default:
                parserWriter.println("rule #52 : --E--");
                potentialError = "Write_Param_Tail, Treated as Empty";
                stackTrace.remove("Write_Param_Tail");
                break;
        } //end case for Comma
    }
// </editor-fold>

// rule 53
// <editor-fold defaultstate="collapsed" desc="Write_Param">
    public static void Write_Param() {
        stackTrace.add("Write_Param");
        // 53. Write_Param -> Ordinal_Expression
        parserWriter.println("rule #53 : expanding");
        Ordinal_Expression();
        stackTrace.remove("Write_Param");
    }
// </editor-fold>

// rules 54 and 55
// <editor-fold defaultstate="collapsed" desc="Assign_Statement">
    public static void Assign_Statement() {
        stackTrace.add("Assign_Statement");
        // 54. Assign_Statement -> Var_Id MP_ASSIGN Expression
        // 55. Assign_Statement -> Func_Id MP_ASSIGN Expression
        String whichRule = "rule # NOT_A_RULE"; //default
        String peekID = parseTokens.get(index+3);
        if (Functions.contains(peekID)) {
            whichRule = "rule #55";
            parserWriter.println(whichRule + ": expanding");
            Func_Id();
        } else if (Variables.contains(peekID)) {
            whichRule = "rule #54";
            parserWriter.println(whichRule + ": expanding");
            Var_Id();
        } else {
            errorsFound.add("Variable or Function undeclared");
            parserWriter.println(whichRule + ": expanding");
        }
            
        G_Check = Match("MP_ASSIGN");
        switch (G_Check) {
            case 1:
                parserWriter.println(whichRule + ": TERMINAL");
                Advance_Pointer();
                parserWriter.println(whichRule + ": expanding");
                Expression();
                stackTrace.remove("Assign_Statement");
                break;

            default:
                sourceOfError = "Assign_Statement, Expected "
                        + "MP_ASSIGN found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        } //end case for Assign
    }
// </editor-fold>

// rule 56
// <editor-fold defaultstate="collapsed" desc="If_Statement">
    public static void If_Statement() {
        stackTrace.add("If_Statement");
        // 56. If_Statement -> MP_IF_WORD Boolean_Expression MP_THEN Statement Opt_Else_Part
        G_Check = Match("MP_IF");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #56: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #56: expanding");
                Boolean_Expression();
                G_Check = Match("MP_THEN");
                //we do want to fall through here, to evaluate second G_Check 
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #56: TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #56: expanding");
                        Statement();
                        parserWriter.println("rule #56: expanding");
                        Opt_Else_Part();
                        stackTrace.remove("If_Statement");
                        break;

                    default:
                        sourceOfError = "If_Statement, Expected "
                                + "MP_THEN found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case for Then
                break;
            default:
                sourceOfError = "If_Statement, Expected "
                        + "MP_IF found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        } //end case for If
    }
// </editor-fold>

// rules 57 and 58
// <editor-fold defaultstate="collapsed" desc="Opt_Else_Part">
    public static void Opt_Else_Part() {
        stackTrace.add("Opt_Else_Part");
        // 57. Opt_Else_Part -> MP_ELSE_WORD Statement
        // 58. Opt_Else_Part -> MP_EMPTY

        // A fancy bit of error handling, store lookAhead and index, Advance the
        // pointer. If after advancing we see an ELSE while currently at a semi
        // colon we know there is a mistake.
        if (lookAhead.equals("MP_SCOLON")) {
            int bak_Index = index;
            String bak_lookAhead = lookAhead;
            Advance_Pointer();
            if (lookAhead.equals("MP_ELSE")) {
                sourceOfError = "Opt_Else_Part: Semi-colon in if part, terminates the if statement"
                        + " before else part can be evaluated.";
                //Error();
                errorsFound.add(sourceOfError);
            } else {
                index = bak_Index;
                lookAhead = bak_lookAhead;
            }
        } else {
            G_Check = Match("MP_ELSE");
            switch (G_Check) {
                case 1:
                    Advance_Pointer();
                    parserWriter.println("rule #57: expanding");
                    Statement();
                    break;

                default:
                    parserWriter.println("rule #58: --E--");
                    potentialError = "Opt_Else_Part, Treated as Empty";
                    break;
            } //end case for else
        }
        stackTrace.remove("Opt_Else_Part");
    }
// </editor-fold>

// rule 59
// <editor-fold defaultstate="collapsed" desc="Repeat_Statement">
    public static void Repeat_Statement() {
        stackTrace.add("Repeat_Statement");
        // 59. Repeat_Statement -> MP_REPEAT_WORD Statement_Seq MP_UNTIL Boolean_Expression
        G_Check = Match("MP_REPEAT");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #59: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #59: expanding");
                Statement_Seq();
                G_Check = Match("MP_UNTIL");
                //we do want to fall through here, to evaluate second G_Check
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #59: TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #59: expanding");
                        Boolean_Expression();
                        stackTrace.remove("Repeat_Statement");
                        break;

                    default:
                        sourceOfError = "Repeat_Statement, Expected "
                                + "MP_UNTIL found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case for Until
                break;
            default:
                sourceOfError = "Repeat_Statement, Expected "
                        + "MP_REPEAT found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        } //end case for Repeat
    }
// </editor-fold>

// rule 60
// <editor-fold defaultstate="collapsed" desc="While_Statement">
    public static void While_Statement() {
        stackTrace.add("While_Statement");
        // 60. While_Statement -> MP_WHILE_WORD Boolean_Expression MP_DO Statement
        G_Check = Match("MP_WHILE");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #60: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #60: expanding");
                Boolean_Expression();
                G_Check = Match("MP_DO");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #60: TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #60: expanding");
                        Statement();
                        stackTrace.remove("While_Statement");
                        break;

                    default:
                        sourceOfError = "While_Statement, Expected "
                                + "MP_DO found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case for Do
                break;
            default:
                sourceOfError = "While_Statement, Expected "
                        + "MP_WHILE found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        } //end case for While
    }
// </editor-fold>

// rule 61
// <editor-fold defaultstate="collapsed" desc="For_Statement">
    public static void For_Statement() {
        stackTrace.add("For_Statement");
        // 61. For_Statement -> MP_FOR_WORD Control_Var MP_ASSIGN Init_Val Step_Val Final_Val MP_DO Statement
        G_Check = Match("MP_FOR");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #61: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #61: expanding");
                Control_Var();
                G_Check = Match("MP_ASSIGN");
                //we do want to fall through here, to evaluate second G_Check
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #61: TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #61: expanding");
                        Init_Val();
                        parserWriter.println("rule #61: expanding");
                        Step_Val();
                        parserWriter.println("rule #61: expanding");
                        Final_Val();
                        G_Check = Match("MP_DO");
                        //we do want to fall through here, to evaluate third G_Check
                        switch (G_Check) {
                            case 1:
                                parserWriter.println("rule #61: TERMINAL");
                                Advance_Pointer();
                                parserWriter.println("rule #61: expanding");
                                Statement();
                                stackTrace.remove("For_Statement");
                                break;
                            default:
                                sourceOfError = "For_Statement, Expected "
                                        + "MP_DO found: " + lookAhead;
                                //Error();
                                errorsFound.add(sourceOfError);
                                break;
                        } //end case for Do
                        break;
                    default:
                        sourceOfError = "For_Statement, Expected "
                                + "MP_ASSIGN found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case for Assign
                break;
            default:
                sourceOfError = "For_Statement, Expected "
                        + "MP_FOR found: " + lookAhead;
                errorsFound.add(sourceOfError);
                //Error();
                break;
        } //end case for For
    }
// </editor-fold>

// rule 62
// <editor-fold defaultstate="collapsed" desc="Control_Var">
    public static void Control_Var() {
        stackTrace.add("Control_Var");
        // 62. Control_Var -> Var_Id
        parserWriter.println("rule #62: expanding");
        Var_Id();
        stackTrace.remove("Control_Var");
    }
// </editor-fold>

// rule 63
// <editor-fold defaultstate="collapsed" desc="Init_Val">
    public static void Init_Val() {
        stackTrace.add("Init_Val");
        // 63. Init_Val -> Ordinal_Expression
        parserWriter.println("rule #63: expanding");
        Ordinal_Expression();
        stackTrace.remove("Init_Val");
    }
// </editor-fold>

// rules 64 and 65
// <editor-fold defaultstate="collapsed" desc="Step_Val">
    public static void Step_Val() {
        stackTrace.add("Step_Val");
        // 64. Step_Val -> MP_TO_WORD
        // 65. Step_Val -> MP_DOWNTO_WORD
        G_Check = Match("MP_TO");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #64: TERMINAL");
                Advance_Pointer();
                stackTrace.remove("Step_Val");
                break;
            default:
                G_Check = Match("MP_DOWNTO");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #65: TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Step_Val");
                        break;

                    default:
                        sourceOfError = "Step_Val, Expected "
                                + "MP_TO or MP_DOWNTO found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case DownTo
        }
    }
// </editor-fold>

// rule 66
// <editor-fold defaultstate="collapsed" desc="Final_Val">
    public static void Final_Val() {
        stackTrace.add("Final_Val");
        // 66. Final_Val -> Ordinal_Expression
        parserWriter.println("rule #66: expanding");
        Ordinal_Expression();
        stackTrace.remove("Final_Val");
    }
// </editor-fold>

// rule 67
// <editor-fold defaultstate="collapsed" desc="Proc_Statement">
    public static void Proc_Statement() {
        stackTrace.add("Proc_Statement");
        // 67. Proc_Statement -> Proc_Id Opt_Actual_Param_List
        parserWriter.println("rule #67: expanding");
        Proc_Id();
        parserWriter.println("rule #67: expanding");
        Opt_Actual_Param_List();
        stackTrace.remove("Proc_Statement");
    }
// </editor-fold>

// rules 68 and 69
// <editor-fold defaultstate="collapsed" desc="Opt_Actual_Param_List">
    public static void Opt_Actual_Param_List() {
        stackTrace.add("Opt_Actual_Param_List");
        // 68. Opt_Actual_Param_List -> MP_LPAREN Actual_Param Actual_Param_Tail MP_RPAREN
        // 69. Opt_Actual_Param_List -> MP_EMPTY
        G_Check = Match("MP_LPAREN");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #68: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #68: expanding");
                Actual_Param();
                parserWriter.println("rule #68: expanding");
                Actual_Param_Tail();
                G_Check = Match("MP_RPAREN");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #68: TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Opt_Actual_Param_List");
                        break;

                    default:
                        sourceOfError = "Opt_Actual_Param_List, Expected "
                                + "MP_RPAREN found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case RParen
                break;
            default:
                parserWriter.println("rule #69: --E--");
                potentialError = "Opt_Actual_Param_List, Treated as Empty";
                stackTrace.remove("Opt_Actual_Param_List");
                break;
        }
    }
// </editor-fold>

// rules 70 and 71
// <editor-fold defaultstate="collapsed" desc="Actual_Param_Tail">
    public static void Actual_Param_Tail() {
        stackTrace.add("Actual_Param_Tail");
        // 70. Actual_Param_Tail -> MP_COMMA Actual_Param Actual_Param_Tail
        // 71. Actual_Param_Tail -> MP_EMPTY
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #70: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #70: expanding");
                Actual_Param();
                parserWriter.println("rule #70: expanding");
                Actual_Param_Tail();
                stackTrace.remove("Actual_Param_Tail");
                break;

            default:
                parserWriter.println("rule #71: --E--");
                potentialError = "Actual_Param_List, Treated as Empty";
                stackTrace.remove("Actual_Param_Tail");
                break;
        }
    }
// </editor-fold>

// rule 72
// <editor-fold defaultstate="collapsed" desc="Actual_Param">
    public static void Actual_Param() {
        stackTrace.add("Actual_Param");
        // 72. Actual_Param -> Ordinal_Expression
        parserWriter.println("rule #72: expanding");
        Ordinal_Expression();
        stackTrace.remove("Actual_Param");
    }
// </editor-fold>

// rule 73
// <editor-fold defaultstate="collapsed" desc="Expression">
    public static void Expression() {
        stackTrace.add("Expression");
        // 73. Expression -> Simple_Expression Opt_Relational_Part
        parserWriter.println("rule #73: expanding");
        Simple_Expression();
        parserWriter.println("rule #73: expanding");
        Opt_Relational_Part();
        stackTrace.remove("Expression");
    }
// </editor-fold>

// rules 74 and 75
// <editor-fold defaultstate="collapsed" desc="Opt_Relational_Part">
    public static void Opt_Relational_Part() {
        stackTrace.add("Opt_Relational_Part");
        // 74. Opt_Relational_Part -> Relational_Op Simple_Expression
        // 75. Opt_Relational_Part -> MP_EMPTY
        parserWriter.println("rule #74: expanding");
        int tempCheck = Relational_Op();
        if (tempCheck == -1) {
            parserWriter.println("rule #75: --E--");
            potentialError = "Opt_Relational_Part treated as Empty";
            stackTrace.remove("Opt_Relational_Part");
        } else {
            parserWriter.println("rule #74: expanding");
            Simple_Expression();
            stackTrace.remove("Opt_Relational_Part");
        }
    }
// </editor-fold>

// rules 76, 77, 78, 79, 80, and 81
// <editor-fold defaultstate="collapsed" desc="Relational_Op">
    public static int Relational_Op() {
        stackTrace.add("Relational_Op");
        // 76. Relational_Op -> MP_EQUAL
        // 77. Relational_Op -> MP_LTHAN
        // 78. Relational_Op -> MP_GTHAN
        // 79. Relational_Op -> MP_LEQUAL
        // 80. Relational_Op -> MP_GEQUAL
        // 81. Relational_Op -> MP_NEQUAL
        int ret = -1; //our return value
        //call match for the graders pleasure, switch statements too gross here
        //made for ugly code, and logic was difficult to trace.
        G_Check = Match("MP_EQUAL");
        if (lookAhead.equals("MP_EQUAL")) {
            parserWriter.println("rule #76: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LTHAN")) {
            parserWriter.println("rule #77: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_GTHAN")) {
            parserWriter.println("rule #78: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LEQUAL")) {
            parserWriter.println("rule #79: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_GEQUAL")) {
            parserWriter.println("rule #80: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_NEQUAL")) {
            parserWriter.println("rule #81: TERMINAL");
            Advance_Pointer();
        } else {
            stackTrace.remove("Relational_Op");
            return -1;
        }
        stackTrace.remove("Relational_Op");
        return 0;
    }
// </editor-fold>

// rule 82
// <editor-fold defaultstate="collapsed" desc="Simple_Expression">
    public static void Simple_Expression() {
        stackTrace.add("Simple_Expression");
        // 82. Simple_Expression -> Optional_Sign Term Term_Tail
        parserWriter.println("rule #82: expanding");
        Optional_Sign();
        parserWriter.println("rule #82: expanding");
        Term();
        parserWriter.println("rule #82: expanding");
        Term_Tail();
        stackTrace.remove("Simple_Expression");
    }
// </editor-fold>

// rules 83 and 84
// <editor-fold defaultstate="collapsed" desc="Term_Tail">
    public static void Term_Tail() {
        stackTrace.add("Term_Tail");
        // 83. Term_Tail -> Add_Op Term Term_Tail
        // 84. Term_Tail -> MP_EMPTY
        parserWriter.println("rule #83: expanding");
        int tempCheck = Add_Op();
        if (tempCheck != 0) {
            parserWriter.println("rule #84: --E--");
            potentialError = "Term_Tail treated as Empty";
        } else {
            parserWriter.println("rule #83: expanding");
            Term();
            parserWriter.println("rule #83: expanding");
            Term_Tail();
        }
        stackTrace.remove("Term_Tail");
    }
// </editor-fold>

// rules 85, 86, and 87
// <editor-fold defaultstate="collapsed" desc="Optional_Sign">
    public static void Optional_Sign() {
        stackTrace.add("Optional_Sign");
        // 85. Optional_Sign -> MP_PLUS
        // 86. Optional_Sign -> MP_MINUS
        // 87. Optional_Sign -> MP_EMPTY
        G_Check = Match("MP_PLUS");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #85: TERMINAL");
                Advance_Pointer();
                stackTrace.remove("Optional_Sign");
                break;

            default:
                G_Check = Match("MP_MINUS");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #86: TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Optional_Sign");
                        break;
                    default:
                        parserWriter.println("rule #87: --E--");
                        potentialError = "Optional_Sign treated as Empty";
                        stackTrace.remove("Optional_Sign");
                } //end case Minus
                break;
        } //end case Plus
    }
// </editor-fold>

// rules 88, 89, and 90
// <editor-fold defaultstate="collapsed" desc="Add_Op">
    public static int Add_Op() {
        stackTrace.add("Add_Op");
        // 88. Add_Op -> MP_PLUS
        // 89. Add_Op -> MP_MINUS
        // 90. Add_Op -> MP_OR

        G_Check = Match("MP_PLUS");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #88: TERMINAL");
                Advance_Pointer();
                stackTrace.remove("Add_Op");
                return 0;
            default:
                G_Check = Match("MP_MINUS");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #89: TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Add_Op");
                        return 0;
                    default:
                        G_Check = Match("MP_OR");
                        switch (G_Check) {
                            case 1:
                                parserWriter.println("rule #90: TERMINAL");
                                Advance_Pointer();
                                stackTrace.remove("Add_Op");
                                return 0;
                            default:
                                stackTrace.remove("Add_Op");
                                return -1;
                        } //end case OR
                } //end case Minus
        } //end case Plus
    }
// </editor-fold>

// rule 91
// <editor-fold defaultstate="collapsed" desc="Term">
    public static void Term() {
        stackTrace.add("Term");
        // 91. Term -> Factor Factor_Tail
        parserWriter.println("rule #91: expanding");
        Factor();
        parserWriter.println("rule #91: expanding");
        Factor_Tail();
        stackTrace.remove("Term");
    }
// </editor-fold>

// rules 92 and 93
// <editor-fold defaultstate="collapsed" desc="Factor_Tail">
    public static void Factor_Tail() {
        stackTrace.add("Factor_Tail");
        // 92. Factor_Tail -> Multiply_Op Factor Factor_Tail
        // 93. Factor_Tail -> MP_EMPTY
        parserWriter.println("rule #92: expanding");
        int tempCheck = Multiply_Op();
        if (tempCheck == -1) {
            parserWriter.println("rule #93: --E--");
            potentialError = "Factor_Tail got Empty from Multiply_Op";
        } else {
            parserWriter.println("rule #92: expanding");
            Factor();
            parserWriter.println("rule #92: expanding");
            Factor_Tail();
        }
        stackTrace.remove("Factor_Tail");
    }
// </editor-fold>

// rules 94, 95, 96, 97, and 98
// <editor-fold defaultstate="collapsed" desc="Multiply_Op">
    public static int Multiply_Op() {
        stackTrace.add("Multiply_Op");
        // 94. Multiply_Op -> MP_TIMES
        // 95. Multiply_Op -> MP_FORWARD_SLASH /* (different then div)*/
        // 96. Multiply_Op -> MP_DIV_WORD
        // 97. Multiply_Op -> MP_MOD_WORD
        // 98. Multiply_Op -> MP_AND_WORD
        G_Check = Match("MP_TIMES");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #94: TERMINAL");
                Advance_Pointer();
                stackTrace.remove("Multiply_Op");
                return 0;
            default:
                G_Check = Match("MP_FORWARD_SLASH");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #95: TERMINAL");
                        Advance_Pointer();
                        stackTrace.remove("Multiply_Op");
                        return 0;
                    default:
                        G_Check = Match("MP_DIV");
                        switch (G_Check) {
                            case 1:
                                parserWriter.println("rule #96: TERMINAL");
                                Advance_Pointer();
                                stackTrace.remove("Multiply_Op");
                                return 0;
                            default:
                                G_Check = Match("MP_MOD");
                                switch (G_Check) {
                                    case 1:
                                        parserWriter.println("rule #97: TERMINAL");
                                        Advance_Pointer();
                                        stackTrace.remove("Multiply_Op");
                                        return 0;
                                    default:
                                        G_Check = Match("MP_AND");
                                        switch (G_Check) {
                                            case 1:
                                                parserWriter.println("rule #98: TERMINAL");
                                                Advance_Pointer();
                                                stackTrace.remove("Multiply_Op");
                                                return 0;
                                            default:
                                                stackTrace.remove("Multiply_Op");
                                                return -1;
                                        } //end case AND
                                } //end case Mod
                        } //end case Div
                } //end case ForwardSlash
        } //end case Times
    }
// </editor-fold>

// rules 99, 100, 101, 102, 103, 104, 105, 106 and 116   
// <editor-fold defaultstate="collapsed" desc="Factor">
    public static void Factor() {
        stackTrace.add("Factor");
        // 99.  Factor -> MP_INTEGER_LIT (unsigned int)
        // 100. Factor -> MP_FLOAT   (unsigned float) MP_FLOAT_LIT?
        // 101. Factor -> MP_STRING_LIT
        // 102. Factor -> MP_TRUE_WORD
        // 103. Factor -> MP_FALSE_WORD
        // 104. Factor -> MP_NOT_WORD Factor
        // 105. Factor -> MP_LPAREN Expression MP_RPAREN
        // 106. Factor -> Function_Id Opt_Actual_Param_List
        // 116. Factor -> Variable_Id
        G_Check = Match("MP_INTEGER_LIT");
        if (lookAhead.equals("MP_INTEGER_LIT")) {
            parserWriter.println("rule #99: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_FLOAT")) {
            parserWriter.println("rule #100: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_STRING_LIT")) {
            parserWriter.println("rule #101: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_TRUE")) {
            parserWriter.println("rule #102: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_FALSE")) {
            parserWriter.println("rule #103: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_NOT")) {
            parserWriter.println("rule #104: TERMINAL");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LPAREN")) {
            parserWriter.println("rule #105: TERMINAL");
            Advance_Pointer();
            parserWriter.println("rule #105: expanding");
            Expression();
            G_Check = Match("MP_RPAREN");
            switch (G_Check) {
                case 1:
                    parserWriter.println("rule #105: TERMINAL");
                    Advance_Pointer();
                    stackTrace.remove("Factor");
                    break;
                default:
                    sourceOfError = "Factor, expected MP_RPAREN found: " + lookAhead;
                    //Error();
                    errorsFound.add(sourceOfError);
                    break;
            }
        } else {
            //How to handle rule 106 vs 160!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //use a flag of some sort?
            String peekID = parseTokens.get(index+3);
            if (Functions.contains(peekID) ) {
                parserWriter.println("rule #106: expanding");
                Func_Id();
                parserWriter.println("rule #106: expanding");
                Opt_Actual_Param_List();
                stackTrace.remove("Factor");
            } else if (Variables.contains(peekID)){
                parserWriter.println("rule #116: expanding");
                Var_Id();
            } else {
                errorsFound.add("Expected Variable or Function call found:"
                        + " " + peekID);
            }
        }
        stackTrace.remove("Factor");
    }
// </editor-fold>

// rule 107
// <editor-fold defaultstate="collapsed" desc="Prog_Id">
    public static void Prog_Id() {
        stackTrace.add("Prog_Id");
        // 107. Prog_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
//##############################################################################
//############ SYMBOL TABLE STUFF ##############################################
//##############################################################################
            //Set the TableName                                             //##
            TableName = parseTokens.get(index+3);                           //##
            //put tablename in as key1 for tables                           //##
            //update the Label                                              //##
            String Label = Label_1.concat(Integer.toString(Label_2));       //##
            Label_2++;                                                      //##
            //update the nesting level                                      //##
            int Nlvl = NestingLevel;                                        //##
            NestingLevel++;                                                 //##
            //insert Table info using s_table API name, nesting, label      //##
            s_table.New_Table(TableName, Integer.toString(Nlvl), Label);    //##
//##############################################################################
            parserWriter.println("rule #107: TERMINAL");
            Advance_Pointer();
            stackTrace.remove("Prog_Id");
        } else {
            sourceOfError = "Prog_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            errorsFound.add(sourceOfError);
        }
    }
// </editor-fold>

// rule 108
// <editor-fold defaultstate="collapsed" desc="Var_Id">
    public static void Var_Id() {
        stackTrace.add("Var_Id");
        // 108. Var_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
            parserWriter.println("rule #108: TERMINAL");
            if (!Functions.contains(parseTokens.get(index+3))) {
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
            CurrLexeme = parseTokens.get(index+3);
            Variables.add(CurrLexeme);
//            System.out.println("Set VarID: " + CurrLexeme);
//##############################################################################
            }
            Advance_Pointer();
            stackTrace.remove("Var_Id");
        } else {
            sourceOfError = "Var_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            errorsFound.add(sourceOfError);
            //Error();
        }
    }
// </editor-fold>

// rule 109
// <editor-fold defaultstate="collapsed" desc="Proc_Id">
    public static void Proc_Id() {
        stackTrace.add("Proc_Id");
        // 109. Proc_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
            CurrLexeme = parseTokens.get(index+3);
            ProcName = CurrLexeme;
            Procedures.add(CurrLexeme);
//            System.out.println("Set ProcName: " + ProcName);
//##############################################################################
            parserWriter.println("rule #109: TERMINAL");
            Advance_Pointer();
            stackTrace.remove("Proc_Id");
        } else {
            sourceOfError = "Proc_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            //Error();
            errorsFound.add(sourceOfError);
        }
    }
// </editor-fold>

// rule 110
// <editor-fold defaultstate="collapsed" desc="Function_Id">
    public static void Func_Id() {
        stackTrace.add("Function_Id");
        // 110. Function_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
            if (!Variables.contains(parseTokens.get(index+3))) {
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
            CurrLexeme = parseTokens.get(index+3);                          //##
            FuncName = CurrLexeme;                                          //##
            Functions.add(parseTokens.get(index+3));
//            System.out.println("Set FuncName: " + FuncName);
//##############################################################################
            }
            parserWriter.println("rule #110: TERMINAL");
            Advance_Pointer();
            stackTrace.remove("Function_Id");
        } else {
            sourceOfError = "Function_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            errorsFound.add(sourceOfError);
            //Error();
        }
    }
// </editor-fold>

// rule 111
// <editor-fold defaultstate="collapsed" desc="Boolean_Expression">
    public static void Boolean_Expression() {
        stackTrace.add("Boolean_Expression");
        // 111. Boolean_Expression -> Expression
        parserWriter.println("rule #111: expanding");
        Expression();
        stackTrace.remove("Boolean_Expression");
    }
// </editor-fold>

// rule 112
// <editor-fold defaultstate="collapsed" desc="Ordinal_Expression">
    public static void Ordinal_Expression() {
        stackTrace.add("Ordinal_Expression");
        // 112. Ordinal_Expression -> Expression
        parserWriter.println("rule #112: expanding");
        Expression();
        stackTrace.remove("Ordinal_Expression");
    }
// </editor-fold>

// rule 113
// <editor-fold defaultstate="collapsed" desc="Id_List">
    public static void Id_List() {
        stackTrace.add("Id_List");
        // 113. Id_List -> MP_IDENTIFIER Id_Tail
        //precondition
        G_Check = Match("MP_IDENTIFIER");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                        if (In_Proc_Func_Flag == 1) {
                            dynamicParams.add(parseTokens.get(index+3));
                        } else {
                            System.out.println("Adding: " + parseTokens.get(index+3) + " to the listIDs array");
                            listIDs.add(parseTokens.get(index+3));
                        }
//##############################################################################
                parserWriter.println("rule #113: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #113: expanding");
                Id_Tail();
                stackTrace.remove("Id_List");
                break;
            default:
                sourceOfError = "Id_List, Expected "
                        + "MP_IDENTIFIER found: " + lookAhead;
                //Error();
                errorsFound.add(sourceOfError);
                break;
        }
    }
// </editor-fold>

// rules 114 and 115
// <editor-fold defaultstate="collapsed" desc="Id_Tail">
    public static void Id_Tail() {
        stackTrace.add("Id_Tail");
        // 114. Id_Tail -> MP_COMMA MP_IDENTIFIER Id_Tail
        // 115. Id_Tail -> MP_EMPTY
        //precondition
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #114: TERMINAL");
                Advance_Pointer();
                G_Check = Match("MP_IDENTIFIER");
                switch (G_Check) {
                    case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                        if (In_Proc_Func_Flag == 1) {
                            dynamicParams.add(parseTokens.get(index+3));
                        } else {
                            System.out.println("Adding: " + parseTokens.get(index+3) + " to the listIDs array");
                            listIDs.add(parseTokens.get(index+3));
                        }
//##############################################################################
                        parserWriter.println("rule #114: TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #114: expanding");
                        Id_Tail();
                        stackTrace.remove("Id_Tail");
                        break;

                    default:
                        sourceOfError = "Id_Tail, Expected "
                                + "MP_IDENTIFIER found: " + lookAhead;
                        //Error();
                        errorsFound.add(sourceOfError);
                        break;
                } //end case Identifier
                break;
            default:
                parserWriter.println("rule #115: --E--");
                potentialError = "Id_Tail, Treated as empty";
                stackTrace.remove("Id_Tail");
        } //end case Comma

    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Error">
    public static void Error() {
        // Move through stored list of errors, printing source of error to the
        //user so they can be corrected. Allow up to 20 errors to be printed, 
        //if more than 20 errors, print first 20 and exit. 
        
        if (errorsFound.size() <= 20) {
            for (int i = 0; i < errorsFound.size() - 1; i++) {
                String message = "\033[31mERROR at line #place col #place in "
                        + "state: " + errorsFound.get(i) + ".\n\033[0m";
                //while the reader is still open
                System.out.println();
                System.out.println(message);
            }
        } else {
            //print only first 20 errors, then print message to user
            for (int i = 0; i <= 20; i++) {
                String message = "\033[31mERROR at line #place col #place "
                        + "in state: " + errorsFound.get(i) + ".\n\033[0m";
                System.out.println();
                System.out.println(message);
            }
            System.out.println("\033[31m***************************************"
                    + "**************");
            System.out.print("\033[31m MORE THAN 20 ERRORS WERE FOUND, PLEASE "
                    + "CORRECT BEFORE PROGRAM CAN BE COMPILED. \n");
            System.out.println("***********************************************"
                    + "******\033[0m");
            System.exit(0);
        }
        Terminate("");
    }
    // </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Terminate">
    public static void Terminate(String message) {
        /* 
         * Print anything we want to before exiting parser then 
         * exit program 
         */
        //System.out.println("\nSTACKTRACE: ");
        //for (int i = 0; i < stackTrace.size(); i++) {
        //    System.out.println(i + ": " + stackTrace.get(i));
        // }
        if (errorsFound.size() > 0) {
            System.out.println("\033[31m*****************************************************");
            System.out.print("\033[31m" + (errorsFound.size() - 1) + " ERRORS "
                    + " FOUND, PLEASE CORRECT BEFORE PROGRAM CAN BE COMPILED. \n");
//            think this is always printing error message - check into this
//            System.out.println(message);
            System.out.println("*****************************************************\033[0m");
        } else {
            System.out.println(message);
            s_table.Print_Tables();
        }
        parserWriter.close();

        System.exit(0);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Match">    
    /*
     * @param in: the value of lookahead at the time it is called.
     * @function match: matches the token to see if it is a reserved word
     *                  or variable.
     */
    public static Integer Match(String in) {
        if (in.equals(lookAhead)) {
            return 1;
        } else {
            return 0;
        }
    }
// </editor-fold>
    
    static void Convert_To_String_Array(ArrayList<String> M) {
        String[] temp = new String[M.size()];
        Parameters = temp;
        for (int i = 0; i < M.size(); i++) {
            Parameters[i] = M.get(i);
        }
    }
}
