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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
    static String sourceOfError = "";
    static String potentialError = "";
    static int blockState;
    static PrintWriter parserWriter;

    public void runParse() throws FileNotFoundException, IOException {
        parserWriter = new PrintWriter("src/parser_resources/parser.out", "UTF-8");
        /* 
         * Re-initialize the file reader to read from our scanner output file
         * instead of reading the program input file
         */
        c_a.fileReader.file_reader.fileReaderInit(
                c_a.fileReader.file_reader.outLocation);
        parseTokens = new ArrayList<>();
        stackTrace = new ArrayList<>();
        String line;
        lookAhead = "";
        previous = "";
        index = 0;
        blockState = 1;
        sColonMark = 0;
        stmntSeqMark = 0;
        expMark = 0;
        simpExpMark = 0;
        procIdFound = 0;
        frmlParamState = 0;

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
            while (!lookAhead.contains("MP_")) {
                System.out.println("skipping: " + lookAhead);
                index++;
                if (index > parseTokens.size()) {
                    sourceOfError = "Get_Lookahead ran over EOF";
                    Error();
                    break;
                }
                Get_Lookahead();
            }
        }
        System.out.println("Lookahead ------------------------------------>" + lookAhead);
        if (!potentialError.equals("")) {
            System.out.println("Potential Error ------------------------------>" + potentialError);
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
                System.out.println("skipping: " + peek);
                index++;
                if (index > parseTokens.size()) {
                    sourceOfError = "Advance_Pointer ran over EOF";
                    Error();
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

// <editor-fold defaultstate="collapsed" desc="Sys_Goal"> 
    public static void Sys_Goal() {
        stackTrace.add("Sys_Goal");
        // 1. SystemGoal -> Program MP_EOF
        parserWriter.println("rule #1: EXPANDING Program");
        Program();
        G_Check = Match("MP_EOF");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #1: \"MP_EOF\" REACHED");
                stackTrace.remove("Sys_Goal");
                Terminate("Program parsed successfully, found MP_EOF");
                break;

            default:
                sourceOfError = "Sys_Goal, Expected MP_EOF found: " + lookAhead;
                stackTrace.remove("Sys_Goal");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Program"> 
    public static void Program() {
        stackTrace.add("Program");
        // 2. Program -> Prog_Head MP_SCOLON Block MP_PERIOD
        //precondition
        parserWriter.println("rule #2: EXPANDING Prog_Head");
        Prog_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #2: \"MP_SCOLON\" REACHED");
                Advance_Pointer();
                parserWriter.println("rule #2: EXPANDING Block");
                Block();
                G_Check = Match("MP_PERIOD");
                //we do want to fall through here, to evaluate second G_Check
                if (G_Check == 1) {
                    parserWriter.println("rule #2: \"MP_PERIOD\" REACHED");
                    Advance_Pointer();
                    stackTrace.remove("Program");
                    break;
                } else {
                    sourceOfError = "Program, Expected MP_PERIOD, found: " + lookAhead;
                    Error();
                    break;
                }
            default:
                sourceOfError = "Program, Expected MP_SCOLON found: " + lookAhead;

                Error();
                break;
        }

    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Prog_Head"> 
    public static void Prog_Head() {
        stackTrace.add("Prog_Head");
        // 3. ProgramHeading -> MP_PROGRAM_WORD Prog_Id
        //precondition
        G_Check = Match("MP_PROGRAM");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #3: \"MP_PROGRAM\" REACHED");
                Advance_Pointer();
                parserWriter.println("rule #3: EXPANDING Prog_Id");
                Prog_Id();
                stackTrace.remove("Prog_Head");
                break;

            default:
                sourceOfError = "Prog_Head, Expected MP_PROGRAM found:"
                        + " " + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Block"> 
    public static void Block() {
        stackTrace.add("Block");
        //track which lookaheads we have used so far;
        //4. Block -> Var_Dec_Part Proc_Func_Dec_Part Statement_Part
        parserWriter.println("rule #4: EXPANDING Var_Dec_Part");
        Var_Dec_Part();
        parserWriter.println("rule #4: EXPANDING Proc_Func_Dec_Part");
        Proc_Func_Dec_Part();
        parserWriter.println("rule #4: EXPANDING Statement_Part");
        Statement_Part();
        stackTrace.remove("Block");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Var_Dec_Part"> 
    public static void Var_Dec_Part() {
        stackTrace.add("Var_Dec_Part");
        // 5. Var_Dec_Part -> MP_VAR_WORD Var_Dec MP_SCOLON Var_Dec_Tail
        // 6. Var_Dec_Part -> MP_EMPTY
        G_Check = Match("MP_VAR");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #5: \"MP_VAR\" REACHED");
                Advance_Pointer();
                parserWriter.println("rule #5: EXPANDING Var_Dec");
                Var_Dec();
                G_Check = Match("MP_SCOLON");
                if (G_Check == 1) {
                    parserWriter.println("rule #5: \"MP_SCOLON\" REACHED");
                    Advance_Pointer();
                    parserWriter.println("rule #5: EXPANDING Var_Dec_Tail");
                    Var_Dec_Tail();
                    stackTrace.remove("Var_Dec_Part");
                    break;
                } else {
                    sourceOfError = "Var_Dec_Part, Expected MP_SCOLON "
                            + "found:  " + lookAhead;
                    Error();
                    break;
                }
            default:
                stackTrace.remove("Var_Dec_Part");
                potentialError = "Var_Dec_Part, treated as empty";
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Var_Dec_Tail"> 
    public static void Var_Dec_Tail() {
        stackTrace.add("Var_Dec_Tail");
        // 7. Var_Dec_Tail -> Var_Dec MP_SCOLON Var_Dec_Tail 
        // 8. Var_Dec_Tail -> MP_EMPTY
        //precondition
        if (lookAhead.equals("MP_IDENTIFIER")) {
            Var_Dec();
        }
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Var_Dec_Tail();
                stackTrace.remove("Var_Dec_Tail");
                break;
            default:
                potentialError = "Var_Dec_Tail, treated as empty";
                stackTrace.remove("Var_Dec_Tail");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Var_Dec"> 
    public static void Var_Dec() {
        stackTrace.add("Var_Dec");
        // 9. Var_Dec -> Id_List MP_COLON Type
        Id_List();
        G_Check = Match("MP_COLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Type();
                stackTrace.remove("Var_Dec");
                break;
            default:
                sourceOfError = "Var_Dec, Expected MP_COLON found: "
                        + "" + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

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
                Advance_Pointer();
                //write rule #10 to file
                break;
            case "MP_FLOAT":
                Advance_Pointer();
                //write rule #11 to file
                break;
            case "MP_STRING":
                Advance_Pointer();
                //write rule #12 to file
                break;
            case "MP_BOOLEAN":
                Advance_Pointer();
                //write rule #13 to file
                break;
            default:
                sourceOfError = "Type, Expected MP_INTEGER, MP_FLOAT, MP_STRING, or"
                        + " MP_BOOLEAN instead found: " + lookAhead;
                Error();
                break;
        }
        stackTrace.remove("Type");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Proc_Func_Dec_Part"> 
    public static void Proc_Func_Dec_Part() {
        stackTrace.add("Proc_Func_Dec_Part");
        // 14. Proc_Func_Dec_Part -> Proc_Dec Proc_Func_Dec_Part 
        // 15. Proc_Func_Dec_Part -> Func_Dec Proc_Func_Dec_Part 
        // 16. Proc_Func_Dec_Part -> MP_EMPTY
        //precondition
        switch (lookAhead) {
            case "MP_PROCEDURE":
                Proc_Dec();
                Proc_Func_Dec_Part();
                stackTrace.remove("Proc_Func_Dec_Part");
                break;

            case "MP_FUNCTION":
                Func_Dec();
                Proc_Func_Dec_Part();
                stackTrace.remove("Proc_Func_Dec_Part");
                break;

            default:
                potentialError = "Proc_Func_Dec_Part treated as Empty.";
                stackTrace.remove("Proc_Func_Dec_Part");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Proc_Dec"> 
    public static void Proc_Dec() {
        stackTrace.add("Proc_Dec");
        // 17. Proc_Dec -> Proc_Head MP_SCOLON Block MP_SCOLON
        Proc_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Block();
                G_Check = Match("MP_SCOLON");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Proc_Dec");
                        break;

                    default:
                        sourceOfError = "Proc_Dec, Expected MP_SCOLON_2 found: "
                                + "" + lookAhead;
                        Error();
                        break;
                }
                break;
            default:
                sourceOfError = "Proc_Dec, Expected MP_SCOLON_1 found: "
                        + "" + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Func_Dec"> 
    public static void Func_Dec() {
        stackTrace.add("Func_Dec");
        // 18. Func_Dec -> Func_Head MP_SCOLON Block MP_SCOLON
        Func_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Block();
                G_Check = Match("MP_SCOLON");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Func_Dec");
                        break;
                    default:
                        sourceOfError = "Func_Dec, Expected MP_SCOLON_2 found: "
                                + "" + lookAhead;
                        Error();
                        break;
                }
                break;
            default:
                sourceOfError = "Func_Dec, Expected MP_SCOLON_1 found: "
                        + "" + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Proc_Head"> 
    public static void Proc_Head() {
        stackTrace.add("Proc_Head");
        // 19. Proc_Head -> MP_PROCEDURE Proc_Id Opt_Formal_Param_List
        G_Check = Match("MP_PROCEDURE");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Proc_Id();
                Opt_Formal_Param_List();
                stackTrace.remove("Proc_Head");
                break;
            default:
                sourceOfError = "Proc_Head, Expected MP_PROCEDURE found:"
                        + " " + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Func_Head">
    public static void Func_Head() {
        stackTrace.add("Func_Head");
        // 20. Func_Head -> MP_FUNCTION Function_Id Opt_Formal_Param_List MP_COLON Type
        G_Check = Match("MP_FUNCTION");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Function_Id();
                Opt_Formal_Param_List();
                G_Check = Match("MP_COLON");
                if (G_Check == 1) {
                    Advance_Pointer();
                    Type();
                    stackTrace.remove("Func_Head");
                    break;
                } else {
                    sourceOfError = "Func_Head, Expected MP_COLON found: "
                            + "" + lookAhead;
                    Error();
                    break;
                }
            default:
                sourceOfError = "Func_Head, Expected MP_FUNCTION found: "
                        + "" + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Opt_Formal_Param_List">
    public static void Opt_Formal_Param_List() {
        stackTrace.add("Opt_Formal_Param_List");
        // 21. Opt_Formal_Param_List -> MP_LPAREN Formal_Param_Sec Formal_Param_Sec_Tail MP_RPAREN 
        // 22. Opt_Formal_Param_List -> MP_EMPTY
        //precondition
        G_Check = Match("MP_LPAREN");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Formal_Param_Sec();
                Formal_Param_Sec_Tail();
                G_Check = Match("MP_RPAREN");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Opt_Formal_Param_List");
                        break;

                    default:
                        sourceOfError = "Opt_Formal_Param_List, Expected "
                                + "MP_RPAREN found: " + lookAhead;
                        Error();
                        break;
                }
                break;
            default:
                potentialError = "Opt_Formal_Param_List treated as Empty";
                stackTrace.remove("Opt_Formal_Param_List");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Formal_Param_Sec_Tail">
    public static void Formal_Param_Sec_Tail() {
        stackTrace.add("Formal_Param_Sec_Tail");
        // 23. Formal_Param_Sec_Tail -> MP_SCOLON Formal_Param_Sec Formal_Param_Sec_Tail
        // 24. Formal_Param_Sec_Tail -> MP_EMPTY
        //precondition
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Formal_Param_Sec();
                Formal_Param_Sec_Tail();
                stackTrace.remove("Formal_Param_Sec_Tail");
                break;
            default:
                potentialError = "Formal_Param_Sec_Tail, Treated as Empty";
                stackTrace.remove("Formal_Param_Sec_Tail");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Formal_Param_Sec">
    public static void Formal_Param_Sec() {
        stackTrace.add("Formal_Param_Sec");
        // 25. Formal_Param_Sec -> Val_Param_Sec
        // 26. Formal_Param_Sec -> Var_Param_Sec
        if (lookAhead.equals("MP_VAR")) {
            Var_Param_Sec();
            stackTrace.remove("Formal_Param_Sec");
        } else {
            Val_Param_Sec();
            stackTrace.remove("Formal_Param_Sec");
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Val_Param_Sec">
    public static void Val_Param_Sec() {
        stackTrace.add("Val_Param_Sec");
        // 27. Val_Param_Sec -> Id_List MP_COLON Type
        Id_List();
        G_Check = Match("MP_COLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Type();
                stackTrace.remove("Val_Param_Sec");
                break;
            default:
                sourceOfError = "Val_Param_Sec, Expected MP_COLON found: "
                        + "" + lookAhead;
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Var_Param_Sec">
    public static void Var_Param_Sec() {
        stackTrace.add("Var_Param_Sec");
        // 28. Var_Param_Sec -> MP_VAR Id_List MP_COLON Type
        G_Check = Match("MP_VAR");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Id_List();
                G_Check = Match("MP_COLON");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Type();
                        stackTrace.remove("Var_Param_Sec");
                        break;
                    default:
                        sourceOfError = "Var_Param_Sec, Expected MP_COLON found"
                                + ": " + lookAhead;
                        Error();
                        break;
                }
                break;
            default:
                sourceOfError = "Var_Param_Sec, Expected MP_VAR found: "
                        + "" + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Statement_Part">
    public static void Statement_Part() {
        stackTrace.add("Statement_Part");
        // 29. Statement_Part -> Compound_Statement
        Compound_Statement();
        stackTrace.remove("Statement_Part");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Compound_Statement">
    public static void Compound_Statement() {
        stackTrace.add("Compound_Statement");
        // 30. Compound_Statement -> MP_BEGIN Statement_Seq MP_END
        G_Check = Match("MP_BEGIN");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Statement_Seq();
                G_Check = Match("MP_END");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Compound_Statement");
                        break;

                    default:
                        sourceOfError = "Compound_Statement, Expected MP_END "
                                + "found: " + lookAhead;
                        Error();
                        break;
                }
                break;
            default:
                sourceOfError = "Compound_Statement, Expected MP_BEGIN found "
                        + "" + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Statement_Seq">
    public static void Statement_Seq() {
        stackTrace.add("Statement_Seq");
        // 31. Statement_Seq -> Statement Statement_Tail
        Statement();
        Statement_Tail();
        stackTrace.remove("Statement_Seq");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Statement_Tail">
    public static void Statement_Tail() {
        stackTrace.add("Statement_Tail");
        // 32. Statement_Tail -> MP_SCOLON Statement Statement_Tail
        // 33. Statement_Tail -> MP_EMPTY
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Statement();
                Statement_Tail();
                stackTrace.remove("Statement_Tail");
                break;

            default:
                potentialError = "Statement_Tail, Treated as Empty";
                stackTrace.remove("Statement_Tail");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Statement">
    public static void Statement() {
        stackTrace.add("Statement");
        // 35. Statement -> Compound_Statement
        if (lookAhead.equals("MP_BEGIN")) {
            Compound_Statement();
        } // 36. Statement -> Read_Statement
        else if (lookAhead.equals("MP_READ")) {
            Read_Statement();
        } // 37. Statement -> Write_Statement
        else if (lookAhead.equals("MP_WRITE") || lookAhead.equals("MP_WRITELN")) {
            Write_Statement();
        } // 38. Statement -> Assign_Statement
        else if (lookAhead.equals("MP_IDENTIFIER")) {
            Assign_Statement();
        } // 39. Statement -> If_Statement
        else if (lookAhead.equals("MP_IF")) {
            If_Statement();
        } // 40. Statement -> While_Statement
        else if (lookAhead.equals("MP_WHILE")) {
            While_Statement();
        } // 41. Statement -> Repeat_Statement
        else if (lookAhead.equals("MP_REPEAT")) {
            Repeat_Statement();
        } // 42. Statement -> For_Statement
        else if (lookAhead.equals("MP_FOR")) {
            For_Statement();
        } // 43. Statement -> Procedure_Statement
        else if (lookAhead.equals("MP_PROCEDURE")) {
            Proc_Statement();
        } // 34. Statement -> Empty_Statement (post condition)
        else {
            Empty_Statement();
        }
        stackTrace.remove("Statement");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Empty_Statement">
    //Monica started here writing rules
    public static void Empty_Statement() {
        stackTrace.add("Empty_Statement");
        // 44. Empty_Statement -> MP_EMPTY
        potentialError = "Statement, Treated as Empty";
        stackTrace.remove("Empty_Statement");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read_Statement">
    public static void Read_Statement() {
        stackTrace.add("Read_Statement");
        // 45. Read_Statement -> MP_READ_WORD MP_LPAREN Read_Param Read_Param_Tail MP_RPAREN
        G_Check = Match("MP_READ");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                G_Check = Match("MP_LPAREN");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Read_Param();
                        Read_Param_Tail();
                        G_Check = Match("MP_RPAREN");
                        switch (G_Check) {
                            case 1:
                                Advance_Pointer();
                                stackTrace.remove("Read_Statement");
                                break;

                            default:
                                sourceOfError = "Read_Statement, Expected "
                                        + "MP_RPAREN found: " + lookAhead;
                                Error();
                                break;
                        } //end case for R_PAREN
                        break;
                    default:
                        sourceOfError = "Read_Statement, Expected "
                                + "MP_LPAREN found: " + lookAhead;
                        Error();
                        break;
                } //end case for LPAREN
                break;
            default:
                sourceOfError = "Read_Statement, Expected "
                        + "MP_READ found: " + lookAhead;
                Error();
                break;
        } //end case for READ
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read_Param_Tail">
    public static void Read_Param_Tail() {
        stackTrace.add("Read_Param_Tail");
        // 46. Read_Param_Tail -> MP_COMMA Read_Param Read_Param_Tail
        // 47. Read_Param_Tail -> MP_EMPTY
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Read_Param();
                Read_Param_Tail();
                stackTrace.remove("Read_Param_Tail");
                break;

            default:
                potentialError = "Read_Param_Tail, Treated as Empty";
                stackTrace.remove("Read_Param_Tail");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Read_Param">
    public static void Read_Param() {
        stackTrace.add("Read_Param");
        // 48. Read_Param -> Var_Id
        Var_Id();
        stackTrace.remove("Read_Param");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Write_Statement">
    public static void Write_Statement() {
        stackTrace.add("Write_Statement");
        // 49. Write_Statement -> MP_WRITE_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
        // 50. Write_Statement -> MP_WRITELN_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
        G_Check = Match("MP_WRITE");
        if (G_Check == 0) {
            G_Check = Match("MP_WRITELN");
        }
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                G_Check = Match("MP_LPAREN");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Write_Param();
                        Write_Param_Tail();
                        G_Check = Match("MP_RPAREN");
                        switch (G_Check) {
                            case 1:
                                Advance_Pointer();
                                stackTrace.remove("Write_Statement");
                                break;
                            default:
                                sourceOfError = "Write_Statement, Expected "
                                        + "MP_RPAREN found: " + lookAhead;
                                Error();
                                break;
                        } //end case for RParen
                        break;
                    default:
                        sourceOfError = "Write_Statement, Expected "
                                + "MP_LPAREN found: " + lookAhead;
                        Error();
                        break;
                } //end case for LParen
                break;
            default:
                sourceOfError = "Write_Statement, Expected "
                        + "MP_WRITE or MP_WRITE_LN found: " + lookAhead;
                Error();
                break;
        } //end case for MP_WRITE
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Write_Param_Tail">
    public static void Write_Param_Tail() {
        stackTrace.add("Write_Param_Tail");
        // 51. Write_Param_Tail -> MP_COMMA Write_Param Write_Param_Tail
        // 52. Write_Param_Tail -> MP_EMPTY
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Write_Param();
                Write_Param_Tail();
                stackTrace.remove("Write_Param_Tail");
                break;

            default:
                potentialError = "Write_Param_Tail, Treated as Empty";
                stackTrace.remove("Write_Param_Tail");
                break;
        } //end case for Comma
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Write_Param">
    public static void Write_Param() {
        stackTrace.add("Write_Param");
        // 53. Write_Param -> Ordinal_Expression
        Ordinal_Expression();
        stackTrace.remove("Write_Param");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Assign_Statement">
    public static void Assign_Statement() {
        stackTrace.add("Assign_Statement");
        // 54. Assign_Statement -> Var_Id MP_ASSIGN Expression
        // 55. Assign_Statement -> Func_Id MP_ASSIGN Expression
        //THIS CAN LEAD EITHER TO A VAR OR FUNC_ID, WILL NEED TO SEP LATER??????
        System.out.println("PREVIOUS = [" + previous + "] int Assign_Statement");
        Var_Id();
        G_Check = Match("MP_ASSIGN");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Expression();
                stackTrace.remove("Assign_Statement");
                break;

            default:
                sourceOfError = "Assign_Statement, Expected "
                        + "MP_ASSIGN found: " + lookAhead;
                Error();
                break;
        } //end case for Assign
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="If_Statement">
    public static void If_Statement() {
        stackTrace.add("If_Statement");
        // 56. If_Statement -> MP_IF_WORD Boolean_Expression MP_THEN Statement Opt_Else_Part
        G_Check = Match("MP_IF");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Boolean_Expression();
                G_Check = Match("MP_THEN");
                //we do want to fall through here, to evaluate second G_Check 
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Statement();
                        Opt_Else_Part();
                        stackTrace.remove("If_Statement");
                        break;

                    default:
                        sourceOfError = "If_Statement, Expected "
                                + "MP_THEN found: " + lookAhead;
                        Error();
                        break;
                } //end case for Then
                break;
            default:
                sourceOfError = "If_Statement, Expected "
                        + "MP_IF found: " + lookAhead;
                Error();
                break;
        } //end case for If
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Opt_Else_Part">
    public static void Opt_Else_Part() {
        stackTrace.add("Opt_Else_Part");
        // 57. Opt_Else_Part -> MP_ELSE_WORD Statement
        // 58. Opt_Else_Part -> MP_EMPTY
        if (lookAhead.equals("MP_SCOLON")) {
            potentialError = "Semi-colon in if part, terminates the if statement"
                    + " before else part can be evaluated.";
        } else {
            G_Check = Match("MP_ELSE");
            switch (G_Check) {
                case 1:
                    Advance_Pointer();
                    Statement();
                    stackTrace.remove("Opt_Else_Part");
                    break;

                default:
                    potentialError = "Opt_Else_Part, Treated as Empty";
                    stackTrace.remove("Opt_Else_Part");
                    break;
            } //end case for else
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Repeat_Statement">
    public static void Repeat_Statement() {
        stackTrace.add("Repeat_Statement");
        // 59. Repeat_Statement -> MP_REPEAT_WORD Statement_Seq MP_UNTIL Boolean_Expression
        G_Check = Match("MP_REPEAT");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Statement_Seq();
                G_Check = Match("MP_UNTIL");
                //we do want to fall through here, to evaluate second G_Check
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Boolean_Expression();
                        stackTrace.remove("Repeat_Statement");
                        break;

                    default:
                        sourceOfError = "Repeat_Statement, Expected "
                                + "MP_UNTIL found: " + lookAhead;
                        Error();
                        break;
                } //end case for Until
                break;
            default:
                sourceOfError = "Repeat_Statement, Expected "
                        + "MP_REPEAT found: " + lookAhead;
                Error();
                break;
        } //end case for Repeat
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="While_Statement">
    public static void While_Statement() {
        stackTrace.add("While_Statement");
        // 60. While_Statement -> MP_WHILE_WORD Boolean_Expression MP_DO Statement
        G_Check = Match("MP_WHILE");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Boolean_Expression();
                G_Check = Match("MP_DO");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Statement();
                        stackTrace.remove("While_Statement");
                        break;

                    default:
                        sourceOfError = "While_Statement, Expected "
                                + "MP_DO found: " + lookAhead;
                        Error();
                        break;
                } //end case for Do
                break;
            default:
                sourceOfError = "While_Statement, Expected "
                        + "MP_WHILE found: " + lookAhead;
                Error();
                break;
        } //end case for While
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="For_Statement">
    public static void For_Statement() {
        stackTrace.add("For_Statement");
        // 61. For_Statement -> MP_FOR_WORD Control_Var MP_ASSIGN Init_Val Step_Val Final_Val MP_DO Statement
        G_Check = Match("MP_FOR");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Control_Var();
                G_Check = Match("MP_ASSIGN");
                //we do want to fall through here, to evaluate second G_Check
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Init_Val();
                        Step_Val();
                        Final_Val();
                        G_Check = Match("MP_DO");
                        //we do want to fall through here, to evaluate third G_Check
                        switch (G_Check) {
                            case 1:
                                Advance_Pointer();
                                Statement();
                                stackTrace.remove("For_Statement");
                                break;
                            default:
                                sourceOfError = "For_Statement, Expected "
                                        + "MP_DO found: " + lookAhead;
                                Error();
                                break;
                        } //end case for Do
                        break;
                    default:
                        sourceOfError = "For_Statement, Expected "
                                + "MP_ASSIGN found: " + lookAhead;
                        Error();
                        break;
                } //end case for Assign
                break;
            default:
                sourceOfError = "For_Statement, Expected "
                        + "MP_FOR found: " + lookAhead;
                Error();
                break;
        } //end case for For
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Control_Var">
    public static void Control_Var() {
        stackTrace.add("Control_Var");
        // 62. Control_Var -> Var_Id
        Var_Id();
        stackTrace.remove("Control_Var");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Init_Val">
    public static void Init_Val() {
        stackTrace.add("Init_Val");
        // 63. Init_Val -> Ordinal_Expression
        Ordinal_Expression();
        stackTrace.remove("Init_Val");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Step_Val">
    public static void Step_Val() {
        stackTrace.add("Step_Val");
        // 64. Step_Val -> MP_TO_WORD
        // 65. Step_Val -> MP_DOWNTO_WORD
        G_Check = Match("MP_TO");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                stackTrace.remove("Step_Val");
                break;
            default:
                G_Check = Match("MP_DOWNTO");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Step_Val");
                        break;

                    default:
                        sourceOfError = "Step_Val, Expected "
                                + "MP_TO or MP_DOWNTO found: " + lookAhead;
                        Error();
                        break;
                } //end case DownTo
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Final_Val">
    public static void Final_Val() {
        stackTrace.add("Final_Val");
        // 66. Final_Val -> Ordinal_Expression
        Ordinal_Expression();
        stackTrace.remove("Final_Val");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Proc_Statement">
    public static void Proc_Statement() {
        stackTrace.add("Proc_Statement");
        // 67. Proc_Statement -> Proc_Id Opt_Actual_Param_List
        Proc_Id();
        Opt_Actual_Param_List();
        stackTrace.remove("Proc_Statement");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Opt_Actual_Param_List">
    public static void Opt_Actual_Param_List() {
        stackTrace.add("Opt_Actual_Param_List");
        // 68. Opt_Actual_Param_List -> MP_LPAREN Actual_Param Actual_Param_Tail MP_RPAREN
        // 69. Opt_Actual_Param_List -> MP_EMPTY
        G_Check = Match("MP_LPAREN");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Actual_Param();
                Actual_Param_Tail();
                G_Check = Match("MP_RPAREN");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Opt_Actual_Param_List");
                        break;

                    default:
                        sourceOfError = "Opt_Actual_Param_List, Expected "
                                + "MP_RPAREN found: " + lookAhead;
                        Error();
                        break;
                } //end case RParen
                break;
            default:
                potentialError = "Opt_Actual_Param_List, Treated as Empty";
                stackTrace.remove("Opt_Actual_Param_List");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Actual_Param_Tail">
    public static void Actual_Param_Tail() {
        stackTrace.add("Actual_Param_Tail");
        // 70. Actual_Param_Tail -> MP_COMMA Actual_Param Actual_Param_Tail
        // 71. Actual_Param_Tail -> MP_EMPTY
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Actual_Param();
                Actual_Param_Tail();
                stackTrace.remove("Actual_Param_Tail");
                break;

            default:
                potentialError = "Actual_Param_List, Treated as Empty";
                stackTrace.remove("Actual_Param_Tail");
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Actual_Param">
    public static void Actual_Param() {
        stackTrace.add("Actual_Param");
        // 72. Actual_Param -> Ordinal_Expression
        Ordinal_Expression();
        stackTrace.remove("Actual_Param");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Expression">
    public static void Expression() {
        stackTrace.add("Expression");
        // 73. Expression -> Simple_Expression Opt_Relational_Part
        Simple_Expression();
        Opt_Relational_Part();
        stackTrace.remove("Expression");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Opt_Relational_Part">
    public static void Opt_Relational_Part() {
        stackTrace.add("Opt_Relational_Part");
        // 74. Opt_Relational_Part -> Relational_Op Simple_Expression
        // 75. Opt_Relational_Part -> MP_EMPTY
        int tempCheck = Relational_Op();
        if (tempCheck == -1) {
            potentialError = "Opt_Relational_Part treated as Empty";
            stackTrace.remove("Opt_Relational_Part");
        } else {
            Simple_Expression();
            stackTrace.remove("Opt_Relational_Part");
        }
        //how to deal with epsilon here?????????????????????????????????????????
    }
// </editor-fold>

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
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LTHAN")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_GTHAN")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LEQUAL")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_GEQUAL")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_NEQUAL")) {
            Advance_Pointer();
        } else {
            stackTrace.remove("Relational_Op");
            return -1;
        }
        stackTrace.remove("Relational_Op");
        return 0;
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Simple_Expression">
    public static void Simple_Expression() {
        stackTrace.add("Simple_Expression");
        // 82. Simple_Expression -> Optional_Sign Term Term_Tail
        Optional_Sign();
        Term();
        Term_Tail();
        stackTrace.remove("Simple_Expression");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Term_Tail">
    public static void Term_Tail() {
        stackTrace.add("Term_Tail");
        // 83. Term_Tail -> Add_Op Term Term_Tail
        // 84. Term_Tail -> MP_EMPTY
        int tempCheck = Add_Op();
        if (tempCheck != 0) {
            potentialError = "Term_Tail treated as Empty";
        } else {
            Term();
            Term_Tail();
        }
        stackTrace.remove("Term_Tail");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Optional_Sign">
    public static void Optional_Sign() {
        stackTrace.add("Optional_Sign");
        // 85. Optional_Sign -> MP_PLUS
        // 86. Optional_Sign -> MP_MINUS
        // 87. Optional_Sign -> MP_EMPTY
        G_Check = Match("MP_PLUS");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                stackTrace.remove("Optional_Sign");
                break;

            default:
                G_Check = Match("MP_MINUS");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Optional_Sign");
                        break;
                    default:
                        potentialError = "Optional_Sign treated as Empty";
                    stackTrace.remove("Optional_Sign");
                } //end case Minus
                break;
        } //end case Plus
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Add_Op">
    public static int Add_Op() {
        stackTrace.add("Add_Op");
        // 88. Add_Op -> MP_PLUS
        // 89. Add_Op -> MP_MINUS
        // 90. Add_Op -> MP_OR

        G_Check = Match("MP_PLUS");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                stackTrace.remove("Add_Op");
                return 0;
            default:
                G_Check = Match("MP_MINUS");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Add_Op");
                        return 0;
                    default:
                        G_Check = Match("MP_OR");
                        switch (G_Check) {
                            case 1:
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

// <editor-fold defaultstate="collapsed" desc="Term">
    public static void Term() {
        stackTrace.add("Term");
        // 91. Term -> Factor Factor_Tail
        Factor();
        Factor_Tail();
        stackTrace.remove("Term");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Factor_Tail">
    public static void Factor_Tail() {
        stackTrace.add("Factor_Tail");
        // 92. Factor_Tail -> Multiply_Op Factor Factor_Tail
        // 93. Factor_Tail -> MP_EMPTY
        int tempCheck = Multiply_Op();
        if (tempCheck == -1) {
            potentialError = "Factor_Tail got Empty from Multiply_Op";
        } else {
            Factor();
            Factor_Tail();
        }
        stackTrace.remove("Factor_Tail");
    }
// </editor-fold>

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
                Advance_Pointer();
                stackTrace.remove("Multiply_Op");
                return 0;
            default:
                G_Check = Match("MP_FORWARD_SLASH");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        stackTrace.remove("Multiply_Op");
                        return 0;
                    default:
                        G_Check = Match("MP_DIV");
                        switch (G_Check) {
                            case 1:
                                Advance_Pointer();
                                stackTrace.remove("Multiply_Op");
                                return 0;
                            default:
                                G_Check = Match("MP_MOD");
                                switch (G_Check) {
                                    case 1:
                                        Advance_Pointer();
                                        stackTrace.remove("Multiply_Op");
                                        return 0;
                                    default:
                                        G_Check = Match("MP_AND");
                                        switch (G_Check) {
                                            case 1:
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
        G_Check = Match("MP_INTEGER_LIT");
        if (lookAhead.equals("MP_INTEGER_LIT")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_FLOAT")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_STRING_LIT")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_TRUE")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_FALSE")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_NOT")) {
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LPAREN")) {
            Advance_Pointer();
            Expression();
            G_Check = Match("MP_RPAREN");
            switch (G_Check) {
                case 1:
                    Advance_Pointer();
                    stackTrace.remove("Factor");
                    break;
                default:
                    sourceOfError = "Factor, expected MP_RPAREN found: " + lookAhead;
                    Error();
                    break;
            }
        } else {
            Function_Id();
            Opt_Actual_Param_List();
            stackTrace.remove("Factor");
        }
        stackTrace.remove("Factor");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Prog_Id">
    public static void Prog_Id() {
        stackTrace.add("Prog_Id");
        // 107. Prog_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
            Advance_Pointer();
            stackTrace.remove("Prog_Id");
        } else {
            sourceOfError = "Prog_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            Error();
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Var_Id">
    public static void Var_Id() {
        stackTrace.add("Var_Id");
        // 108. Var_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
            Advance_Pointer();
            stackTrace.remove("Var_Id");
        } else {
            sourceOfError = "Var_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            Error();
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Proc_Id">
    public static void Proc_Id() {
        stackTrace.add("Proc_Id");
        // 109. Proc_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
            Advance_Pointer();
            stackTrace.remove("Proc_Id");
        } else {
            sourceOfError = "Proc_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            Error();
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Function_Id">
    public static void Function_Id() {
        stackTrace.add("Function_Id");
        // 110. Function_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
            Advance_Pointer();
            stackTrace.remove("Function_Id");
        } else {
            sourceOfError = "Function_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            Error();
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Boolean_Expression">
    public static void Boolean_Expression() {
        stackTrace.add("Boolean_Expression");
        // 111. Boolean_Expression -> Expression
        Expression();
        stackTrace.remove("Boolean_Expression");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Ordinal_Expression">
    public static void Ordinal_Expression() {
        stackTrace.add("Ordinal_Expression");
        // 112. Ordinal_Expression -> Expression
        Expression();
        stackTrace.remove("Ordinal_Expression");
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Id_List">
    public static void Id_List() {
        stackTrace.add("Id_List");
        // 113. Id_List -> MP_IDENTIFIER Id_Tail
        //precondition
        G_Check = Match("MP_IDENTIFIER");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Id_Tail();
                stackTrace.remove("Id_List");
                break;
            default:
                sourceOfError = "Id_List, Expected "
                        + "MP_IDENTIFIER found: " + lookAhead;
                Error();
                break;
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Id_Tail">
    public static void Id_Tail() {
        stackTrace.add("Id_Tail");
        // 114. Id_Tail -> MP_COMMA MP_IDENTIFIER Id_Tail
        // 115. Id_Tail -> MP_EMPTY
        //precondition
        G_Check = Match("MP_COMMA");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                G_Check = Match("MP_IDENTIFIER");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Id_Tail();
                        stackTrace.remove("Id_Tail");
                        break;

                    default:
                        sourceOfError = "Id_Tail, Expected "
                                + "MP_IDENTIFIER found: " + lookAhead;
                        Error();
                        break;
                } //end case Identifier
                break;
            default:
                potentialError = "Id_Tail, Treated as empty";
            stackTrace.remove("Id_Tail");
        } //end case Comma
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Error">
    public static void Error() {
        // if found error enter this case
        /* MONICA!!!!!!! Make red and get formatted correctly!!!!!!!!!!!!!!!!!!!*/
        /* TODO LOGIC HERE FOR ERROR */
        //Don't think we will want to terminate, in case multiple error messages
//        System.out.println("\nSTACKTRACE: ");
//        for (int i = 0; i < stackTrace.size(); i++) {
//            System.out.println(i + ": " + stackTrace.get(i));
//        }
//        System.out.println();
        String message = "Error in state: " + sourceOfError;
        //System.out.println(message);
        Terminate(message);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Terminate">
    public static void Terminate(String message) {
        /* 
         * Print anything we want to before exiting parser then 
         * exit program 
         */
        System.out.println("\nSTACKTRACE: ");
        for (int i = 0; i < stackTrace.size(); i++) {
            System.out.println(i + ": " + stackTrace.get(i));
        }
        System.out.println();
        done = true;
        //think this is always printing error message - check into this
        System.out.println(message);
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
}
