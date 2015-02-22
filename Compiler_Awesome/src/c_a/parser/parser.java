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

    Boolean done = false;
    String lookahead = "MP_TESTING";
    int index;
    List<String> parseTokens;
    String sourceOfError;
    
    public enum State {
        Sys_Goal, Program, Prog_Head, Block, Var_Dec_Part,
        Var_Dec_Tail, Var_Dec, Type, 
        Proc_Func_Dec_Part, Proc_Dec, Func_Dec,
        Proc_Head, Func_Head, Opt_Formal_Param_List,
        Formal_Param_Sec_Tail, Formal_Param_Sec, Val_Param_Sec,
        Var_Param_Sec, Statement_Part, Compound_Statement, Statement_Seq,
        Statement_Tail, Statement, Empty_Statement, Read_Statement,
        Read_Param_Tail, Read_Param, Write_Statement, Write_Param_Tail,
        Write_Param, Assign_Statement, If_Statement, Opt_Else_Part,
        Repeat_Statement, While_Statement, For_Statement, Control_Var,
        Init_Val, Step_Val, Final_Val, Proc_Statement, Opt_Actual_Param_List,
        Actual_Param_Tail, Actual_Param, Expression, Opt_Relational_Part,
        Relational_Op, Simple_Expression, Term_Tail, Optional_Sign, Add_Op, 
        Term, Factor_Tail, Multiply_Op, Factor, Prog_Id, Var_Id, Proc_Id, 
        Function_Id, Boolean_Expression, Ordinal_Expression, Id_List, Id_Tail,
        Error, Terminate
    }
    State state;
    public void runParse() throws FileNotFoundException, IOException {
        
        /* 
         * Re-initialize the file reader to read from our scanner output file
         * instead of reading the program input file
         */
        c_a.fileReader.file_reader.fileReaderInit(c_a.fileReader.file_reader.outLocation);
        parseTokens = new ArrayList<String>();
        String line = null;
        lookahead = "";
        index = 0;
        state = State.Sys_Goal;
        //read in one line at a time from the output file
        while ((line = reader.readLine()) != null) {
            //replace all of our nice formatted spacing with a single space
            line = line.trim().replaceAll(" +", " ");
            //split the string into tokens using space as the splitter
            StringTokenizer st = new StringTokenizer(line, " ");
            while (st.hasMoreElements()) {
                /*
                 * trim each token, and add it to the array
                 * lines n=0,n+=4 = micro pascal token
                 * lines n=1,n+=4 = line number
                 * lines n=2,n+=4 = column number
                 * lines n=3,n+=4 = lexeme
                 */
                parseTokens.add(st.nextElement().toString().trim());
            }
        }
        //testing printout
//        for(int i = 0; i < lines.size(); i++) {
//            System.out.println(lines.get(i));
//        }
        while (done == false) {
            /* Get Look Ahead */
            /* TODO LOGIC HERE FOR LOOK AHEAD */
            lookahead = parseTokens.get(index);
            /* switch case on look ahead */
            switch(state) {
                case Sys_Goal:
                    // 1. SystemGoal -> Program MP_EOF
                    //precondition
                    if (lookahead.equals("MP_EOF")) {
                        state = State.Terminate;
                    }
                    //postcondition
                    else
                        state = State.Program;
                    break;
                case Program:
                    // 2. Program -> Prog_Head MP_SCOLON Block MP_PERIOD
                    //precondition
                    if (lookahead.equals("MP_SCOLON")) {
                        index += 4;
                        state = State.Block;
                    }
                    else if (lookahead.equals("MP_PERIOD")) {
                        index += 4;
                        state = State.Sys_Goal;
                    }
                    //postcondition
                    else
                        state = State.Prog_Head;
                    break;
                case Prog_Head:
                    // 3. ProgramHeading -> MP_PROGRAM_WORD Prog_Id
                    //precondition
//                    System.out.println("lookahead in Prog_Head = " + lookahead);
                    if (lookahead.equals("MP_PROGRAM")) {
                        index += 4;
                        state = State.Prog_Id;
                    }
                    else if (lookahead.equals("MP_IDENTIFIER")) {
                        state = State.Prog_Id;
                    }
                    //postcondition
                    else {
                        sourceOfError = "Prog_Head";
                        state = State.Error;
                    }
                    break;
                case Block:
                    System.out.println("Got to Block");
                    state = State.Terminate;
//                    state = State.Var_Dec_Part;
                    break;
                case Var_Dec_Part:
                    // 5. Var_Dec_Part -> MP_VAR_WORD Var_Dec MP_SCOLON Var_Dec_Tail
                    
                    // 6. Var_Dec_Part -> MP_EMPTY 
                    break;
                case Var_Dec_Tail:
                    // 7. Var_Dec_Tail -> Var_Dec MP_SCOLON Var_Dec_Tail 
                    // 8. Var_Dec_Tail -> MP_EMPTY 
                    break;
                case Var_Dec:
                    // 9. Id_List MP_COLON Type 
                    break;
                case Type:
                    // 10. Type -> MP_INTEGER_WORD
                    // 11. Type -> MP_FLOAT_WORD
                    // 12. Type -> MP_STRING_WORD
                    // 13. Type -> MP_BOOLEAN_WORD
                    break;
                case Proc_Func_Dec_Part:
                    // 14. Proc_Func_Dec_Part -> Proc_Dec Proc_Func_Dec_Part 
                    // 15. Proc_Func_Dec_Part -> Func_Dec Proc_Func_Dec_Part 
                    // 16. Proc_Func_Dec_Part -> MP_EMPTY 
                    break;
                case Proc_Dec:
                    // 17. Proc_Dec -> Proc_Head MP_SCOLON Block MP_SCOLON 
                    break;
                case Func_Dec:
                    // 18. Func_Dec -> Func_Head MP_SCOLON Block MP_SCOLON 
                    break;
                case Proc_Head:
                    // 19. Proc_Head -> MP_PROCEDURE Proc_Id Opt_Formal_Param_List 
                    break;
                case Func_Head:
                    // 20. Func_Head -> MP_FUNCTION Func_Id Opt_Formal_Param_List 
                    break;
                case Opt_Formal_Param_List:
                    // 21. Opt_Formal_Param_List -> MP_LPARAN Formal_Param_Sec Formal_Param_Sec_Tail MP_RPARAN 
                    // 22. Opt_Formal_Param_List -> MP_EMPTY 
                    break;
                case Formal_Param_Sec_Tail:
                    // 23. Formal_Param_Sec_Tail -> MP_SCOLON Formal_Param_Sec Formal_Param_Sec_Tail
                    // 24. Formal_Param_Sec_Tail -> MP_EMPTY
                    break;
                case Formal_Param_Sec:
                    // 25. Formal_Param_Sec -> Val_Param_Sec
                    // 26. Formal_Param_Sec -> Var_Param_Sec
                    break;
                case Val_Param_Sec:
                    // 27. Val_Param_Sec -> Id_List MP_COLON Type
                    break;
                case Var_Param_Sec:
                    // 28. Var_Param_Sec -> MP_VAR Id_List MP_COLON Type
                    break;
                case Statement_Part:
                    // 29. Statement_Part -> Compound_Statement
                    break;
                case Compound_Statement:
                    // 30. Compound_Statement -> MP_BEGIN Statement_Seq MP_END
                    break;
                case Statement_Seq:
                    // 31. Statement_Seq -> Statement Statement_Tail
                    break;
                case Statement_Tail:
                    // 32. Statement_Tail -> MP_SCOLON Statement Statement_Tail
                    // 33. Statement_Tail -> MP_EMPTY
                    break;
                case Statement:
                    // 34. Statement -> Empty_Statement
                    // 35. Statement -> Compound_Statement
                    // 36. Statement -> Read_Statement
                    // 37. Statement -> Write_Statement
                    // 38. Statement -> Assign_Statement
                    // 39. Statement -> If_Statement
                    // 40. Statement -> While_Statement
                    // 41. Statement -> Repeat_Statement
                    // 42. Statement -> For_Statement
                    // 43. Statement -> Procedure_Statement
                    break;
                case Empty_Statement:
                    // 44. Empty_Statement -> MP_EMPTY
                    break;
                case Read_Statement:
                    // 45. Read_Statement -> MP_READ_WORD MP_LPARAN Read_Param Read_Param_Tail MP_RPARAN
                    break;
                case Read_Param_Tail:
                    // 46. Read_Param_Tail -> MP_COMMA Read_Param Read_Param_Tail
                    // 47. Read_Param_Tail -> MP_EMPTY
                    break;
                case Read_Param:
                    // 48. Read_Param -> Var_Id
                    break;
                case Write_Statement:
                    // 49. Write_Statement -> MP_WRITE_WORD MP_LPARAN Write_Param Write_Param_Tail MP_RPARAN
                    // 50. Write_Statement -> MP_WRITELN_WORD MP_LPARAN Write_Param Write_Param_Tail MP_RPARAN
                    break;
                case Write_Param_Tail:
                    // 51. Write_Param_Tail -> MP_COMMA Write_Param Write_Param_Tail
                    // 52. Write_Param_Tail -> MP_EMPTY
                    break;
                case Write_Param:
                    // 53. Write_Param -> Ordinal_Expression
                    break;
                case Assign_Statement:
                    // 54. Assign_Statement -> Var_Id MP_ASSIGN Expression
                    // 55. Assign_Statement -> Func_Id MP_ASSIGN Expression
                    break;
                case If_Statement:
                    // 56. If_Statement -> MP_IF_WORD Boolean_Expression MP_THEN Statement Opt_Else_Part
                    break;
                case Opt_Else_Part:
                    // 57. Opt_Else_Part -> MP_ELSE_WORD Statement
                    // 58. Opt_Else_Part -> MP_EMPTY
                    break;
                case Repeat_Statement:
                    // 59. Repeat_Statement -> MP_REPEAT_WORD Statement_Seq MP_UNTIL Boolean_Expression
                    break;
                case While_Statement:
                    // 60. While_Statement -> MP_WHILE_WORD Boolean_Expression MP_DO Statement
                    break;
                case For_Statement:
                    // 61. For_Statement -> MP_FOR_WORD Control_Var MP_ASSIGN Init_Val Step_Val Final_Val MP_DO Statement
                    break;
                case Control_Var:
                    // 62. Control_Var -> Var_Id
                    break;
                case Init_Val:
                    // 63. Init_Val -> Ordinal_Expression
                    break;
                case Step_Val:
                    // 64. Step_Val -> MP_TO_WORD
                    // 65. Step_Val -> MP_DOWNTO_WORD
                    break;
                case Final_Val:
                    // 66. Final_Val -> Ordinal_Expression
                    break;
                case Proc_Statement:
                    // 67. Proc_Statement -> Proc_Id Opt_Actual_Param_List
                    break;
                case Opt_Actual_Param_List:
                    // 68. Opt_Actual_Param_List -> MP_LPARAN Actual_Param Actual_Param_Tail MP_RPARAN
                    // 69. Opt_Actual_Param_List -> MP_EMPTY
                    break;
                case Actual_Param_Tail:
                    // 70. Actual_Param_Tail -> MP_COMMA Actual_Param Actual_Param_Tail
                    // 71. Actual_Param_Tail -> MP_EMPTY
                    break;
                case Actual_Param:
                    // 72. Actual_Param -> Ordinal_Expression
                    break;
                case Expression:
                    // 73. Expression -> Simple_Expression Opt_Relational_Part
                    break;
                case Opt_Relational_Part:
                    // 74. Opt_Relational_Part -> Relational_Op Simple_Expression
                    // 75. Opt_Relational_Part -> MP_EMPTY
                    break;
                case Relational_Op:
                    // 76. Relational_Op -> MP_EQUAL
                    // 77. Relational_Op -> MP_LTHAN
                    // 78. Relational_Op -> MP_GTHAN
                    // 79. Relational_Op -> MP_LEQUAL
                    // 80. Relational_Op -> MP_GEQUAL
                    // 81. Relational_Op -> MP_NEQUAL
                    break;
                case Simple_Expression:
                    // 82. Simple_Expression -> Optional_Sign Term Term_Tail
                    break;
                case Term_Tail:
                    // 83. Term_Tail -> Add_Op Term Term_Tail
                    // 84. Term_Tail -> MP_EMPTY
                    break;
                case Optional_Sign:
                    // 85. Optional_Sign -> MP_PLUS
                    // 86. Optional_Sign -> MP_MINUS
                    // 87. Optional_Sign -> MP_EMPTY
                    break;
                case Add_Op:
                    // 88. Add_Op -> MP_PLUS
                    // 89. Add_Op -> MP_MINUS
                    // 90. Add_Op -> MP_OR
                    break;
                case Term:
                    // 91. Term -> Factor Factor_Tail
                    break;
                case Factor_Tail:
                    // 92. Factor_Tail -> Multiply_Op Factor Factor_Tail
                    // 93. Factor_Tail -> MP_EMPTY
                    break;
                case Multiply_Op:
                    // 94. Multiply_Op -> MP_TIMES
                    // 95. Multiply_Op -> MP_FORWARD_SLASH /* (different then div)*/
                    // 96. Multiply_Op -> MP_DIV_WORD
                    // 97. Multiply_Op -> MP_MOD_WORD
                    // 98. Multiply_Op -> MP_AND_WORD
                    break;
                case Factor:
                    // 99.  Factor -> MP_INTEGER (unsigned int)
                    // 100. Factor -> MP_FLOAT   (unsigned float)
                    // 101. Factor -> MP_STRING_LIT
                    // 102. Factor -> MP_TRUE_WORD
                    // 103. Factor -> MP_FALSE_WORD
                    // 104. Factor -> MP_NOT_WORD Factor
                    // 105. Factor -> MP_LPARAN Expression MP_RPARAN
                    // 106. Factor -> Func_Id Opt_Actual_Param_List
                    break;
                case Prog_Id:
                    // 107. Prog_Id -> MP_IDENTIFIER
                    //precondition
                    if (lookahead.equals("MP_IDENTIFIER")) {
                        state = State.Program;
                    }
                    //postcondition
                    else {
                        sourceOfError = "Prog_Id";
                        state = State.Error;
                    }
                    break;
                case Var_Id:
                    // 108. Var_Id -> MP_IDENTIFIER
                    break;
                case Proc_Id:
                    // 109. Proc_Id -> MP_IDENTIFIER
                    break;
                case Function_Id:
                    // 110. Function_Id -> MP_IDENTIFIER
                    break;
                case Boolean_Expression:
                    // 111. Boolean_Expression -> Expression
                    break;
                case Ordinal_Expression:
                    // 112. Ordinal_Expression -> Expression
                    break;
                case Id_List:
                    // 113. Id_List -> MP_IDENTIFIER Id_Tail
                    break;
                case Id_Tail:
                    // 114. Id_Tail -> MP_COMMA MP_IDENTIFIER Id_Tail
                    // 115. Id_Tail -> MP_EMPTY
                    break;
                case Error:
                    // if found error enter this case
                    /* TODO LOGIC HERE FOR ERROR */
                    System.out.println("Error in state: " + sourceOfError);
                    state = State.Terminate;
                    break;
                case Terminate:
                    /* 
                     * Print anything we want to before exiting parser then 
                     * exit program 
                     */
                    done = true;
                    System.exit(0);
                    break;
                default:
                    System.out.println("Parser Default");
                    break;
            }
        }
    }
    /*
     * @param in: the value of lookahead at the time it is called.
     * @function match: matches the token to see if it is a reserved word
     *                  or variable.
     */
    public static void match(String in) {
        
    }
}
