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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author khimes
 */
public class parser {

    static Boolean done = false;
    static String lookahead = "";
    static int index, sColonMark, procIdFound, frmlParamState, stmntSeqMark,
            expMark, simpExpMark, G_Check;
    static List<String> parseTokens;
    static String sourceOfError, potentialError;
    static int blockState;

//    public enum State {
//
//        Sys_Goal, Program, Prog_Head, Block, Var_Dec_Part,
//        Var_Dec_Tail, Var_Dec, Type,
//        Proc_Func_Dec_Part, Proc_Dec, Func_Dec,
//        Proc_Head, Func_Head, Opt_Formal_Param_List,
//        Formal_Param_Sec_Tail, Formal_Param_Sec, Val_Param_Sec,
//        Var_Param_Sec, Statement_Part, Compound_Statement, Statement_Seq,
//        Statement_Tail, Statement, Empty_Statement, Read_Statement,
//        Read_Param_Tail, Read_Param, Write_Statement, Write_Param_Tail,
//        Write_Param, Assign_Statement, If_Statement, Opt_Else_Part,
//        Repeat_Statement, While_Statement, For_Statement, Control_Var,
//        Init_Val, Step_Val, Final_Val, Proc_Statement, Opt_Actual_Param_List,
//        Actual_Param_Tail, Actual_Param, Expression, Opt_Relational_Part,
//        Relational_Op, Simple_Expression, Term_Tail, Optional_Sign, Add_Op,
//        Term, Factor_Tail, Multiply_Op, Factor, Prog_Id, Var_Id, Proc_Id,
//        Function_Id, Boolean_Expression, Ordinal_Expression, Id_List, Id_Tail,
//        Error, Terminate
//    }
//    State state;
//    State returnToState;
    public void runParse() throws FileNotFoundException, IOException {

        /* 
         * Re-initialize the file reader to read from our scanner output file
         * instead of reading the program input file
         */
        c_a.fileReader.file_reader.fileReaderInit(
                c_a.fileReader.file_reader.outLocation);
        parseTokens = new ArrayList<String>();
        String line = null;
        lookahead = "";
        index = 0;
        blockState = 1;
        sColonMark = 0;
        stmntSeqMark = 0;
        expMark = 0;
        simpExpMark = 0;
        procIdFound = 0;
        frmlParamState = 0;
//        Sys_Goal;
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

    public static void Get_Lookahead() {
        /* Get Look Ahead */
        /* TODO LOGIC HERE FOR LOOK AHEAD */
        lookahead = parseTokens.get(index);

        //skipping over comments and strings.
        if (lookahead.equals("MP_COMMENT")
                || lookahead.equals("MP_STRING_LIT")) {
            index += 3;
            lookahead = parseTokens.get(index);
            while (!lookahead.contains("MP_")) {
                System.out.println("skipping: " + lookahead);
                index++;
                lookahead = parseTokens.get(index);
            }
        }
    }

    public static void Advance_Pointer() {
        index += 4;
        Get_Lookahead();
    }

    public static void Sys_Goal() {
        // 1. SystemGoal -> Program MP_EOF
        Program();
        G_Check = Match("MP_EOF");
        switch (G_Check) {
            case 1:
                Terminate("Program parsed successfully, found MP_EOF");
            default:
                sourceOfError = "Sys_Goal, Expected MP_EO found: " + lookahead;
        }
//                    System.out.println("Parser Default");
    }

    public static void Program() {
        // 2. Program -> Prog_Head MP_SCOLON Block MP_PERIOD
        //precondition
        Prog_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Block();
                G_Check = Match("MP_PERIOD");
                switch (G_Check) {
                    case 0:
                        sourceOfError = "Prog_Head, Expected MP_PERIOD found:"
                                + " " + lookahead;
                        Error();
                    default:
                        Advance_Pointer();
                        Sys_Goal();
                }
            default:
                sourceOfError = "Program, Expected MP_SCOLON found: "
                        + lookahead;
                Error();
        }
    }

    public static void Prog_Head() {
        // 3. ProgramHeading -> MP_PROGRAM_WORD Prog_Id
        //precondition
        G_Check = Match("MP_PROGRAM");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Prog_Id();
            default:
                sourceOfError = "Prog_Head, Expected MP_PROGRAM found:"
                        + " " + lookahead;
        }

    }

    public static void Block() {
        //track which lookaheads we have used so far;
        //4. Block -> Var_Dec_Part Proc_Func_Dec_Part Statement_Part
        Var_Dec_Part();
        Proc_Func_Dec_Part();
        Statement_Part();
//                    Var_Dec_Part;
    }

    public static void Var_Dec_Part() {
        // 5. Var_Dec_Part -> MP_VAR_WORD Var_Dec MP_SCOLON Var_Dec_Tail
        // 6. Var_Dec_Part -> MP_EMPTY
        //precondition
        G_Check = Match("MP_VAR");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Var_Dec();
                G_Check = Match("MP_SCOLON");
                switch (G_Check) {
                    case 1:
                        Advance_Pointer();
                        Var_Dec_Tail();
                    default:
                        sourceOfError = "Var_Dec_Part, Expected MP_SCOLON "
                                + "found:  " + lookahead;
                }
            default:
                potentialError = "Var_Dec_Part, treated as empty";
        }
    }

    public static void Var_Dec_Tail() {
        // 7. Var_Dec_Tail -> Var_Dec MP_SCOLON Var_Dec_Tail 
        // 8. Var_Dec_Tail -> MP_EMPTY
        //precondition
        Var_Dec();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Var_Dec_Tail();
            default:
                potentialError = "Var_Dec_Tail, treated as empty";
        }
    }

    public static void Var_Dec() {
        // 9. Var_Dec -> Id_List MP_COLON Type
        //precondition
        Id_List();
        G_Check = Match("MP_COLON");
        switch (G_Check) {
            case 1:
                Advance_Pointer();
                Type();
            default:
                sourceOfError = "Var_Dec, Expected MP_COLON found: "
                        + "" + lookahead;
                Error();
        }
    }

    public static void Type() {
        // 10. Type -> MP_INTEGER_WORD
        // 11. Type -> MP_FLOAT_WORD
        // 12. Type -> MP_STRING_WORD
        // 13. Type -> MP_BOOLEAN_WORD
        //precondition
        G_Check = Match("MP_INTEGER");
        switch (G_Check) {
            case 0:
                G_Check = Match("MP_FLOAT");
                switch (G_Check) {
                    case 0:
                        G_Check = Match("MP_STRING_LIT");
                        switch (G_Check) {
                            case 0:
                                G_Check = Match("MP_BOOLEAN");
                                switch (G_Check) {
                                    case 0:
                                        sourceOfError = "Type, Expected "
                                                + "[MP_INTEGER | MP_FLOAT |"
                                                + " MP_STRING_LIT | MP_BOOLEAN."
                                                + " found: " + lookahead;
                                }
                        }
                }
        }
    }

    public static void Proc_Func_Dec_Part() {
        // 14. Proc_Func_Dec_Part -> Proc_Dec Proc_Func_Dec_Part 
        // 15. Proc_Func_Dec_Part -> Func_Dec Proc_Func_Dec_Part 
        // 16. Proc_Func_Dec_Part -> MP_EMPTY
        //precondition
        switch(lookahead) {
            case "MP_PROCEDURE":
                Proc_Dec();
                Proc_Func_Dec_Part();
            case "MP_FUNCTION":
                Func_Dec();
                Proc_Func_Dec_Part();
            default:
                /* Do Nothing */ 
        }
    }

    public static void Proc_Dec() {
        // 17. Proc_Dec -> Proc_Head MP_SCOLON Block MP_SCOLON
        //precondition
        Proc_Head();
        G_Check = Match("MP_SCOLON");
        switch(G_Check) {
            case 1:
                Advance_Pointer();
                Block();
                G_Check = Match("MP_SCOLON");
                switch(G_Check) {
                    case 1:
                        Advance_Pointer();
                    default:
                        sourceOfError = "Proc_Dec, Expected MP_SCOLON_2 found: "
                                + "" + lookahead;
                }
            default:
                sourceOfError = "Proc_Dec, Expected MP_SCOLON_1 found: "
                        + "" + lookahead;
                
                
        }
    }

    public static void Func_Dec() {
        // 18. Func_Dec -> Func_Head MP_SCOLON Block MP_SCOLON 
    }

    public static void Proc_Head() {
        // 19. Proc_Head -> MP_PROCEDURE Proc_Id Opt_Formal_Param_List
        if (lookahead.equals("MP_PROCEDURE")) {
            index += 4;
            Proc_Id();
        } else if (procIdFound == 1) {
            //reset the IdFound for future checks
            procIdFound = 0;
        }
    }

    public static void Func_Head() {
        // 20. Func_Head -> MP_FUNCTION Func_Id Opt_Formal_Param_List 
    }

    public static void Opt_Formal_Param_List() {
        // 21. Opt_Formal_Param_List -> MP_LPAREN Formal_Param_Sec Formal_Param_Sec_Tail MP_RPAREN 
        // 22. Opt_Formal_Param_List -> MP_EMPTY
        //precondition
        if (lookahead.equals("MP_LPAREN")) {
            index += 4;
            Formal_Param_Sec();
            frmlParamState = 1;
        } else if (lookahead.equals("MP_RPAREN")) {
            index += 4;
            Proc_Dec();
        } else if (frmlParamState == 1) {
            frmlParamState = 0;
//            returnToState = State.Opt_Formal_Param_List;
            Formal_Param_Sec_Tail();
        } else {
            //return to state on success or if empty
            Block();
        }
        //postcondition
    }

    public static void Formal_Param_Sec_Tail() {
        // 23. Formal_Param_Sec_Tail -> MP_SCOLON Formal_Param_Sec Formal_Param_Sec_Tail
        // 24. Formal_Param_Sec_Tail -> MP_EMPTY
        //precondition
        if (lookahead.equals("MP_SCOLON")) {
            index += 4;
            Formal_Param_Sec();
            frmlParamState = 1;
        } else if (frmlParamState == 1) {
            frmlParamState = 0;
            Formal_Param_Sec_Tail();
        } //postcondition
        else {
//            state = returnToState;
        }
    }

    public static void Formal_Param_Sec() {
        // 25. Formal_Param_Sec -> Val_Param_Sec
        // 26. Formal_Param_Sec -> Var_Param_Sec
        if (lookahead.equals("MP_VAL")) {
            Val_Param_Sec();
        } else {
            Var_Param_Sec();
        }
    }

    public static void Val_Param_Sec() {
        // 27. Val_Param_Sec -> Id_List MP_COLON Type
        if (lookahead.equals("MP_IDENTIFIER")) {
            Id_List();
        }
    }

    public static void Var_Param_Sec() {
        // 28. Var_Param_Sec -> MP_VAR Id_List MP_COLON Type
        if (lookahead.equals("MP_VAR")) {
            index += 4;
//            returnToState = State.Var_Param_Sec;
            Id_List();
        } else if (lookahead.equals("MP_COLON")) {
            index += 4;
//            returnToState = State.Opt_Formal_Param_List;
            Type();
        } else {
            sourceOfError = "Var_Param_Sec"
                    + "<usage> [VAR | VAL] example () { integer";
            Error();
        }
    }

    public static void Statement_Part() {
        // 29. Statement_Part -> Compound_Statement
        if (lookahead.equals("MP_BEGIN")) {
            Compound_Statement();
        } else {
            sourceOfError = "Statement_Part, no MP_BEGIN";
            Error();
        }
    }

    public static void Compound_Statement() {
        // 30. Compound_Statement -> MP_BEGIN Statement_Seq MP_END
        if (lookahead.equals("MP_BEGIN")) {
            index += 4;
            Statement_Seq();
        } else if (lookahead.equals("MP_END")) {
            index += 4;
            Get_Lookahead();
            Sys_Goal();
        }
    }

    public static void Statement_Seq() {
        // 31. Statement_Seq -> Statement Statement_Tail
        if (stmntSeqMark == 0) {
            stmntSeqMark = 1;
//            returnToState = State.Statement_Seq;
            Statement();
        } else if (stmntSeqMark == 1) {
            stmntSeqMark = 0;
            Statement_Tail();
        }
    }

    public static void Statement_Tail() {
        // 32. Statement_Tail -> MP_SCOLON Statement Statement_Tail
        // 33. Statement_Tail -> MP_EMPTY
        if (lookahead.equals("MP_SCOLON")) {
            index += 4;
            Statement();
//            returnToState = State.Statement_Tail;
        } else //empty statement
        {
            Compound_Statement();
        }

    }

    public static void Statement() {
//                    System.out.println("CHECKPOINT");
//                    System.out.println("LOOKAHEAD = " + lookahead);
        // 34. Statement -> Empty_Statement (post condition)
        // 35. Statement -> Compound_Statement
        if (lookahead.equals("MP_BEGIN")) {
            Compound_Statement();
        } // 36. Statement -> Read_Statement
        else if (lookahead.equals("MP_READ")) {
            Read_Statement();
        } // 37. Statement -> Write_Statement
        else if (lookahead.equals("MP_WRITE") || lookahead.equals("MP_WRITELN")) {
            Write_Statement();
        } // 38. Statement -> Assign_Statement
        else if (lookahead.equals("MP_ASSIGN")) {
            Assign_Statement();
        } // 39. Statement -> If_Statement
        else if (lookahead.equals("MP_IF")) {
            If_Statement();
        } // 40. Statement -> While_Statement
        else if (lookahead.equals("MP_WHILE")) {
            While_Statement();
        } // 41. Statement -> Repeat_Statement
        else if (lookahead.equals("MP_REPEAT")) {
            Repeat_Statement();
        } // 42. Statement -> For_Statement
        else if (lookahead.equals("MP_FOR")) {
            For_Statement();
        } // 43. Statement -> Procedure_Statement
        else if (lookahead.equals("MP_PROCEDURE")) {
            Proc_Statement();
        } //post condition
        else {
//            state = returnToState;
        }
    }

    public static void Empty_Statement() {
        // 44. Empty_Statement -> MP_EMPTY
    }

    public static void Read_Statement() {
        // 45. Read_Statement -> MP_READ_WORD MP_LPAREN Read_Param Read_Param_Tail MP_RPAREN
    }

    public static void Read_Param_Tail() {
        // 46. Read_Param_Tail -> MP_COMMA Read_Param Read_Param_Tail
        // 47. Read_Param_Tail -> MP_EMPTY
    }

    public static void Read_Param() {
        // 48. Read_Param -> Var_Id
    }

    public static void Write_Statement() {
        // 49. Write_Statement -> MP_WRITE_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
        if (lookahead.equals("MP_WRITE")) {
            index += 4;
            lookahead = parseTokens.get(index);

            if (lookahead.equals("MP_LPAREN")) {
                index += 4;
//                returnToState = State.Write_Statement;
                Write_Param();
            } else if (lookahead.equals("MP_RPAREN")) {
                index += 4;
            } else if (!lookahead.equals("MP_WRITE")
                    && !lookahead.equals("MP_LPAREN")
                    && !lookahead.equals("MP_RPAREN")) {
                Write_Param_Tail();
            }
        } else if (lookahead.equals("MP_WRITELN")) {
            // 50. Write_Statement -> MP_WRITELN_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
            index += 4;
            lookahead = parseTokens.get(index);
            System.out.println("LOOKAHEAD: " + lookahead);
            if (lookahead.equals("MP_LPAREN")) {
                index += 4;
//                returnToState = State.Write_Statement;
                Write_Param();
            } else if (lookahead.equals("MP_RPAREN")) {
                index += 4;
            } else if (!lookahead.equals("MP_WRITE")
                    && !lookahead.equals("MP_LPAREN")
                    && !lookahead.equals("MP_RPAREN")) {
                Write_Param_Tail();
            }
        } else {
            sourceOfError = "Write_Statement, something went wrong.\n"
                    + "<usage> [WRITE | WRITELN] (Write_Param Write_Param_Tail)";
            Error();
        }

    }

    public static void Write_Param_Tail() {
        Terminate("default exit, parser not yet complete");
        // 51. Write_Param_Tail -> MP_COMMA Write_Param Write_Param_Tail
        // 52. Write_Param_Tail -> MP_EMPTY
    }

    public static void Write_Param() {
        // 53. Write_Param -> Ordinal_Expression
        Ordinal_Expression();
    }

    public static void Assign_Statement() {
        // 54. Assign_Statement -> Var_Id MP_ASSIGN Expression
        // 55. Assign_Statement -> Func_Id MP_ASSIGN Expression
    }

    public static void If_Statement() {
        // 56. If_Statement -> MP_IF_WORD Boolean_Expression MP_THEN Statement Opt_Else_Part
    }

    public static void Opt_Else_Part() {
        // 57. Opt_Else_Part -> MP_ELSE_WORD Statement
        // 58. Opt_Else_Part -> MP_EMPTY
    }

    public static void Repeat_Statement() {
        // 59. Repeat_Statement -> MP_REPEAT_WORD Statement_Seq MP_UNTIL Boolean_Expression
    }

    public static void While_Statement() {
        // 60. While_Statement -> MP_WHILE_WORD Boolean_Expression MP_DO Statement
    }

    public static void For_Statement() {
        // 61. For_Statement -> MP_FOR_WORD Control_Var MP_ASSIGN Init_Val Step_Val Final_Val MP_DO Statement
    }

    public static void Control_Var() {
        // 62. Control_Var -> Var_Id
    }

    public static void Init_Val() {
        // 63. Init_Val -> Ordinal_Expression
    }

    public static void Step_Val() {
        // 64. Step_Val -> MP_TO_WORD
        // 65. Step_Val -> MP_DOWNTO_WORD
    }

    public static void Final_Val() {
        // 66. Final_Val -> Ordinal_Expression
    }

    public static void Proc_Statement() {
        // 67. Proc_Statement -> Proc_Id Opt_Actual_Param_List
    }

    public static void Opt_Actual_Param_List() {
        // 68. Opt_Actual_Param_List -> MP_LPAREN Actual_Param Actual_Param_Tail MP_RPAREN
        // 69. Opt_Actual_Param_List -> MP_EMPTY
    }

    public static void Actual_Param_Tail() {
        // 70. Actual_Param_Tail -> MP_COMMA Actual_Param Actual_Param_Tail
        // 71. Actual_Param_Tail -> MP_EMPTY
    }

    public static void Actual_Param() {
        // 72. Actual_Param -> Ordinal_Expression
    }

    public static void Expression() {
        // 73. Expression -> Simple_Expression Opt_Relational_Part
        if (expMark == 0) {
            expMark = 1;
            Simple_Expression();
        } else {
            expMark = 0;
            Opt_Relational_Part();
        }
    }

    public static void Opt_Relational_Part() {
        // 74. Opt_Relational_Part -> Relational_Op Simple_Expression
        // 75. Opt_Relational_Part -> MP_EMPTY
    }

    public static void Relational_Op() {
        // 76. Relational_Op -> MP_EQUAL
        // 77. Relational_Op -> MP_LTHAN
        // 78. Relational_Op -> MP_GTHAN
        // 79. Relational_Op -> MP_LEQUAL
        // 80. Relational_Op -> MP_GEQUAL
        // 81. Relational_Op -> MP_NEQUAL
    }

    public static void Simple_Expression() {
        // 82. Simple_Expression -> Optional_Sign Term Term_Tail
        if (simpExpMark == 0) {
            simpExpMark = 1;
            Optional_Sign();
        } else if (simpExpMark == 1) {
            simpExpMark = 2;
            Term();
        } else {
            simpExpMark = 0;
            Term_Tail();
        }
    }

    public static void Term_Tail() {
        // 83. Term_Tail -> Add_Op Term Term_Tail
        // 84. Term_Tail -> MP_EMPTY
    }

    public static void Optional_Sign() {
        // 85. Optional_Sign -> MP_PLUS
        // 86. Optional_Sign -> MP_MINUS
        // 87. Optional_Sign -> MP_EMPTY
    }

    public static void Add_Op() {
        // 88. Add_Op -> MP_PLUS
        // 89. Add_Op -> MP_MINUS
        // 90. Add_Op -> MP_OR
    }

    public static void Term() {
        // 91. Term -> Factor Factor_Tail
    }

    public static void Factor_Tail() {
        // 92. Factor_Tail -> Multiply_Op Factor Factor_Tail
        // 93. Factor_Tail -> MP_EMPTY
    }

    public static void Multiply_Op() {
        // 94. Multiply_Op -> MP_TIMES
        // 95. Multiply_Op -> MP_FORWARD_SLASH /* (different then div)*/
        // 96. Multiply_Op -> MP_DIV_WORD
        // 97. Multiply_Op -> MP_MOD_WORD
        // 98. Multiply_Op -> MP_AND_WORD
    }

    public static void Factor() {
        // 99.  Factor -> MP_INTEGER (unsigned int)
        // 100. Factor -> MP_FLOAT   (unsigned float)
        // 101. Factor -> MP_STRING_LIT
        // 102. Factor -> MP_TRUE_WORD
        // 103. Factor -> MP_FALSE_WORD
        // 104. Factor -> MP_NOT_WORD Factor
        // 105. Factor -> MP_LPAREN Expression MP_RPAREN
        // 106. Factor -> Func_Id Opt_Actual_Param_List
    }

    public static void Prog_Id() {
        // 107. Prog_Id -> MP_IDENTIFIER
        //precondition
        switch (lookahead) {
            case "MP_IDENTIFIER":
                index += 4;
                Get_Lookahead();
                Program();
            default:
                sourceOfError = "Prog_Id";
                Error();
        }
    }

    public static void Var_Id() {
        // 108. Var_Id -> MP_IDENTIFIER
    }

    public static void Proc_Id() {
        // 109. Proc_Id -> MP_IDENTIFIER
        if (lookahead.equals("MP_IDENTIFIER")) {
            procIdFound = 1;
            index += 4;
            Proc_Head();
        } else {
            sourceOfError = "Proc_Id, no identifier found";
            Error();
        }
    }

    public static void Function_Id() {
        // 110. Function_Id -> MP_IDENTIFIER
    }

    public static void Boolean_Expression() {
        // 111. Boolean_Expression -> Expression
    }

    public static void Ordinal_Expression() {
        // 112. Ordinal_Expression -> Expression
        Expression();
    }

    public static void Id_List() {
        // 113. Id_List -> MP_IDENTIFIER Id_Tail
        //precondition
        if (lookahead.equals("MP_IDENTIFIER")) {
            index += 4;
            Id_Tail();
        } //postcondition
        else {
            sourceOfError = "Id_List";
            Error();
        }
    }

    public static void Id_Tail() {
        // 114. Id_Tail -> MP_COMMA MP_IDENTIFIER Id_Tail
        // 115. Id_Tail -> MP_EMPTY
        //precondition
        if (lookahead.equals("MP_COMMA")) {
            index += 4;
            lookahead = parseTokens.get(index);
            if (lookahead.equals("MP_IDENTIFIER")) {
                index += 4;
                Id_Tail();
            } else {
                sourceOfError = "Id_Tail, no ID following comma";
                Error();
            }
        } //postcondition
        else {
//            state = returnToState;
        }
    }

    public static void Error() {
        // if found error enter this case
        /* MONICA!!!!!!! */
        /* TODO LOGIC HERE FOR ERROR */
        String message = "Error in state: " + sourceOfError;
        Terminate(message);
    }

    public static void Terminate(String message) {
        /* 
         * Print anything we want to before exiting parser then 
         * exit program 
         */
        done = true;
        System.out.println(message);
        System.exit(0);
    }
    /*
     * @param in: the value of lookahead at the time it is called.
     * @function match: matches the token to see if it is a reserved word
     *                  or variable.
     */

    public static Integer Match(String in) {
        if (in.equals(lookahead)) {
            return 1;
        } else {
            return 0;
        }
    }
}
