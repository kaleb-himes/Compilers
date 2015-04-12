/*
 * 
 * The parser takes the information from the scanner (i.e. token, lineNo, colNo, 
 * lexeme) and using recursive descent and one token of lookahead makes sure 
 * that the program provided follows the rule of this particular version of 
 * microPascal.
 * 
 * For each rule there is either a case statement that switches on a global 
 * lookahead variable, or a series of if/else statements. Each rule (that could
 * lead to an error has appropriate error handling, via insertion into an array.
 * Relevant errors are printed out to assist the user with debugging. 
 * 
 * When we encounter a terminal, we call "match" on it, and that information is 
 * added to the symbol table, which keeps track of various information for each
 * associated lexeme. 
 * 
 */
package c_a.parser;

import c_a.semantics.s_analyzer;
import static c_a.fileReader.file_reader.reader;
import static c_a.semantics.assembly_builder.assemblyWriter;
import static c_a.semantics.assembly_builder.close_assembly_writer;
import static c_a.semantics.assembly_builder.init_assembly_writer;
import static c_a.semantics.s_analyzer.Offset;
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
    public static int index, sColonMark, procIdFound, frmlParamState, stmntSeqMark,
            expMark, simpExpMark, G_Check, whichWrite;
    public static List<String> parseTokens;

    //keep track of all errors found while parsing program
    public static List<String> errorsFound;
    public static String sourceOfError = "";
    public static List<String> errorLocation;
    static int endOfErrors = 0;

    //variables to keep track of error reporting info
    public static String lineNo;
    public static String colNo;

    static String potentialError = "";
    static int blockState;
    static PrintWriter parserWriter;

    // Lexicallity
    public static ArrayList Variables = new ArrayList<>();
    public static ArrayList Functions = new ArrayList<>();
    static ArrayList Procedures = new ArrayList<>();

//##############################################################################
//######### BEGIN Symbol table resources #######################################
//##############################################################################
    public static String TableName, ProcName, FuncName, Label_1 = "L";
    static int NestingLevel, Label_2;
    public static String CurrLexeme, Type, Kind, Mode, CurrToken;
    static String[] Parameters;
    static int Size;
    static ArrayList<String> dynamicParams = new ArrayList<>();
    static ArrayList<String> listIDs = new ArrayList<>();
    static int In_Proc_Func_Flag = 0;
    static String[] init = new String[1];
    public static int destroyPointer = 0;   //will limit the scope of lookup
//##############################################################################

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$ SYMANTIC ANALYSIS STUFF $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    public static ArrayList<String> lookUpArray = new ArrayList<>();
    public static String finalType = "NO_TYPE";    //should be integer, boolean, float, or string
    public static String assignee = "NO_ASSIGNEE"; //the variable being modified
    public static String tempType = "";            //the type that will be set if finalType is set
    public static int checkFuncArgs = 0;            //alert Var_Id to check function types
    public static String rememberTableName = "NO_TABLE";
    public static int comingFromRead = 0;
    public static int comingFromWrite = 0;
    static int comingFromIf = 0;
    public static int comingFromAssignStatement = 0;
    public static int comingFromFactor_NotFuncOrVar = 0;
    public static ArrayList<String> lineOfAssemblyCode = new ArrayList<>();
    public static ArrayList<String> operationsArray = new ArrayList<>();
    public static ArrayList<String> writeStatementStringArray = new ArrayList<>();
    public static int ExpressionCounter = 0;
    public static int OperationsCounter = 0;
    static int guessOffset = 1;
    static int labelCounter = 1;                    //drop labels in if statements etc
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

    public void runParse() throws FileNotFoundException, IOException {

        //initialize the symbol table
        symbol_tables.s_table.Init_Table();

        //initialize the parserWriter for parse tree generation
        parserWriter = new PrintWriter("src/parser_resources/parser.out", "UTF-8");
        /*
         * if you get an error from this line, right-click 
         * "Source Packages -> New -> Java Package", name it "semantic_resources"
         * right-click semantic_resouces -> New -> Empty File, name it "assembly.il"
         */
        init_assembly_writer();

        /* 
         * Re-initialize the file reader to read from our scanner output file
         * instead of reading the program input file
         */
        c_a.fileReader.file_reader.fileReaderInit(
                c_a.fileReader.file_reader.outLocation);
        parseTokens = new ArrayList<>();
        errorsFound = new ArrayList<>();
        errorLocation = new ArrayList<>();
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
        NestingLevel = 0;
        Label_2 = 0;
        ProcName = "";
        FuncName = "";
        dynamicParams.add("");
        Size = 0;
        //initialize Parameters to empty set, update as needed
        init[0] = "NO_PARAMS";
        Parameters = init;

        //read in one line at a time from the output file
        while ((line = reader.readLine()) != null) {
            //replace all of our nice formatted spacing with a single space
            line = line.trim().replaceAll(" +", " ");

            //if the line is a string, terminate the line with three apostrophes
            //in a row. This can't exist in the string as provided by the 
            //scanner, so cannot occur in scanner.out 
            if (line.contains("MP_STRING_LIT")) {
                line = line.concat(" '''");
            }

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
        previous = lookAhead;
        if (index < parseTokens.size()) {
            lookAhead = parseTokens.get(index);
        }

        //if lookahead is a comment, skip it.
        if (lookAhead.equals("MP_COMMENT")) {
            index += 3;
            lookAhead = parseTokens.get(index);
            while (!lookAhead.contains("}")) {
                index++;
                if (index > parseTokens.size()) {
                    sourceOfError = "Get_Lookahead ran over EOF";
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
        if (!potentialError.equals("")) {
            potentialError = "";
        }
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Advance_Pointer"> 
    public static void Advance_Pointer() {
        if (lookAhead.equals("MP_STRING_LIT")) {
            index += 3;
            int i = 0;
            String peek = parseTokens.get(index);
            writeStatementStringArray.add(peek);
            writeStatementStringArray.set(i, " " + writeStatementStringArray.get(i));
            //as long as we are not at the last part of the string
            while (!peek.contains("'''")) {
                //will be used to print out in write statements

                index++;
                i++;
                if (index > parseTokens.size()) {
                    sourceOfError = "Advance_Pointer ran over EOF";
                    errorsFound.add(sourceOfError);
                    break;
                }
                peek = parseTokens.get(index);
                writeStatementStringArray.add(peek);
            }

            //if we are at the last part of the string, update parseTokens to 
            //remove the ''' we added. If we are at the last part of the string,
            //we know a token will be next. 
            if (peek.contains("'''")) {
                parseTokens.add(index, parseTokens.get(index).substring(0, parseTokens.get(index).length() - 3));
                index++;
            }

            index++;
            peek = parseTokens.get(index);
        } else {
            index += 4;
        }
        Get_Lookahead();
    }
// </editor-fold>

// rule 1
// <editor-fold defaultstate="collapsed" desc="Sys_Goal"> 
    public static void Sys_Goal() {
        // 1. SystemGoal -> Program MP_EOF
        parserWriter.println("rule #1  : expanding");
        Program();
        G_Check = Match("MP_EOF");
        switch (G_Check) {
            case 1:
                if (errorsFound.size() > 0) {
                    Error();
                } else {
                    Terminate("Program parsed successfully, found MP_EOF");
                }
                parserWriter.println("rule #1  : TERMINAL");
                break;

            default:
                sourceOfError = "Sys_Goal, Expected MP_EOF found: " + lookAhead;
                errorsFound.add(sourceOfError);
                //establish index number of lookahead           
                //add line no corresponding to error
                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                Error();
                break;
        }
    }
// </editor-fold>

// rule 2
// <editor-fold defaultstate="collapsed" desc="Program"> 
    public static void Program() {

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
                    destroyPointer--;
                    NestingLevel--;
                    parserWriter.println("rule #2  : TERMINAL");
                    Advance_Pointer();
                    break;
                } else {
                    sourceOfError = "Program, Expected MP_PERIOD, found: " + lookAhead;
                    errorsFound.add(sourceOfError);

                    //add line no corresponding to error
                    lineNo = parseTokens.get(index + 1);
                    errorLocation.add(lineNo);

                    //add col no corresponding to error
                    colNo = parseTokens.get(index + 2);
                    errorLocation.add(colNo);
                    break;
                }
            default:
                sourceOfError = "Program, Expected MP_SCOLON found: " + lookAhead;
                errorsFound.add(sourceOfError);

                //add line no corresponding to error
                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }

    }
// </editor-fold>

// rule 3
// <editor-fold defaultstate="collapsed" desc="Prog_Head"> 
    public static void Prog_Head() {
        // 3. ProgramHeading -> MP_PROGRAM_WORD Prog_Id
        //precondition
        G_Check = Match("MP_PROGRAM");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #3  : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #3  : expanding");
                Prog_Id();
                break;

            default:
                sourceOfError = "Prog_Head, Expected MP_PROGRAM found:"
                        + " " + lookAhead;
                errorsFound.add(sourceOfError);

                //add line no corresponding to error
                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }

    }
// </editor-fold>

// rule 4
// <editor-fold defaultstate="collapsed" desc="Block"> 
    public static void Block() {
        //track which lookaheads we have used so far;
        //4. Block -> Var_Dec_Part Proc_Func_Dec_Part Statement_Part
        parserWriter.println("rule #4  : expanding");
        Var_Dec_Part();
        parserWriter.println("rule #4  : expanding");
        Proc_Func_Dec_Part();
        parserWriter.println("rule #4  : expanding");
        Statement_Part();
    }
// </editor-fold>

// rules 5 and 6
// <editor-fold defaultstate="collapsed" desc="Var_Dec_Part"> 
    public static void Var_Dec_Part() {
        // 5. Var_Dec_Part -> MP_VAR_WORD Var_Dec MP_SCOLON Var_Dec_Tail
        // 6. Var_Dec_Part -> MP_EMPTY
        G_Check = Match("MP_VAR");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Kind = parseTokens.get(index + 3);
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
                    if (In_Proc_Func_Flag == 0) {
                        for (int i = 0; i < listIDs.size(); i++) {
                            CurrLexeme = listIDs.get(i);
                            s_table.Insert_Row(TableName, CurrLexeme,
                                    CurrToken, Type, Kind, Mode,
                                    Integer.toString(Size), Parameters);
                        }
                        listIDs.clear();
                    }
//##############################################################################
                    parserWriter.println("rule #5  : TERMINAL");
                    Advance_Pointer();
                    parserWriter.println("rule #5  : expanding");
                    Var_Dec_Tail();
                    break;
                } else {
                    sourceOfError = "Var_Dec_Part, Expected MP_SCOLON "
                            + "found:  " + lookAhead;
                    errorsFound.add(sourceOfError);
                    lineNo = parseTokens.get(index + 1);
                    errorLocation.add(lineNo);

                    //add col no corresponding to error
                    colNo = parseTokens.get(index + 2);
                    errorLocation.add(colNo);
                    break;
                }
            default:
                potentialError = "Var_Dec_Part, treated as empty";
                parserWriter.println("rule #6  : --E--");
                guessOffset = 0;
                break;
        }
    }
// </editor-fold>

// rules 7 and 8
// <editor-fold defaultstate="collapsed" desc="Var_Dec_Tail"> 
    public static void Var_Dec_Tail() {
        // 7. Var_Dec_Tail -> Var_Dec MP_SCOLON Var_Dec_Tail 
        // 8. Var_Dec_Tail -> MP_EMPTY
        //precondition
        if (lookAhead.equals("MP_IDENTIFIER")) {
            parserWriter.println("rule #7  : expanding");
            guessOffset++;
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
//                }
//                else {
//                    sourceOfError = "Too many Semi-Colons.";
//                    errorsFound.add(sourceOfError);
                //establish index number of lookahead           
//                    lookAheadIndex = parseTokens.indexOf(lookAhead);
//                    //add line no corresponding to error
//                    lineNo = parseTokens.get(lookAheadIndex + 1);
//                    errorLocation.add(lineNo);
//
//                    //add col no corresponding to error
//                    colNo = parseTokens.get(lookAheadIndex + 2);
//                    errorLocation.add(colNo);
//                }
                break;
            default:
                parserWriter.println("rule #8  : --E--");
                potentialError = "Var_Dec_Tail, treated as empty";
                break;
        }
    }
// </editor-fold>

// rule 9
// <editor-fold defaultstate="collapsed" desc="Var_Dec"> 
    public static void Var_Dec() {
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
                break;
            default:
                sourceOfError = "Var_Dec, Expected MP_COLON found: "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rules 10, 11, 12, and 13
// <editor-fold defaultstate="collapsed" desc="Type"> 
    public static void Type() {
        // 10. Type -> MP_INTEGER_WORD
        // 11. Type -> MP_FLOAT_WORD
        // 12. Type -> MP_STRING_WORD
        // 13. Type -> MP_BOOLEAN_WORD
        // call match to make grader happy, completely unecessary here and made
        // for too verbose and ugly of code, logic was difficult to follow
        String offset;
        G_Check = Match("MP_INTEGER");
        switch (lookAhead) {
            case "MP_INTEGER":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index + 3);
                CurrToken = lookAhead;
                if (In_Proc_Func_Flag == 1) {
                    dynamicParams.add(CurrToken);
                }
                Size++;
//##############################################################################
                parserWriter.println("rule #10 : TERMINAL");
                lineOfAssemblyCode.clear();
                lineOfAssemblyCode.add("MOV #0 ");
                offset = Integer.toString(guessOffset);
                lineOfAssemblyCode.add(offset);
                lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
                for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                    assemblyWriter.print(lineOfAssemblyCode.get(i));
                }
                lineOfAssemblyCode.clear();
                Advance_Pointer();
                //write rule #10 to file
                break;
            case "MP_FLOAT":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index + 3);
                CurrToken = lookAhead;
                if (In_Proc_Func_Flag == 1) {
                    dynamicParams.add(CurrToken);
                }
                Size++;
//##############################################################################
                parserWriter.println("rule #11 : TERMINAL");
                lineOfAssemblyCode.clear();
                lineOfAssemblyCode.add("MOV #0.0 ");
                offset = Integer.toString(guessOffset);
                lineOfAssemblyCode.add(offset);
                lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
                for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                    assemblyWriter.print(lineOfAssemblyCode.get(i));
                }
                lineOfAssemblyCode.clear();
                Advance_Pointer();
                //write rule #11 to file
                break;
            case "MP_STRING":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index + 3);
                CurrToken = lookAhead;
                if (In_Proc_Func_Flag == 1) {
                    dynamicParams.add(CurrToken);
                }
                Size++;
//##############################################################################
                parserWriter.println("rule #12 : TERMINAL");
//                lineOfAssemblyCode.clear();
//                lineOfAssemblyCode.add("MOV #\"\" ");
//                offset = Integer.toString(guessOffset);
//                lineOfAssemblyCode.add(offset);
//                lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
//                lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
//                for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
//                    assemblyWriter.print(lineOfAssemblyCode.get(i));
//                }
//                lineOfAssemblyCode.clear();
                Advance_Pointer();
                //write rule #12 to file
                break;
            case "MP_BOOLEAN":
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Type = parseTokens.get(index + 3);
                CurrToken = lookAhead;
                if (In_Proc_Func_Flag == 1) {
                    dynamicParams.add(CurrToken);
                }
                Size++;
//##############################################################################
                parserWriter.println("rule #13 : TERMINAL");
                lineOfAssemblyCode.clear();
                lineOfAssemblyCode.add("MOV #0 ");
                offset = Integer.toString(guessOffset);
                lineOfAssemblyCode.add(offset);
                lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
                for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                    assemblyWriter.print(lineOfAssemblyCode.get(i));
                }
                lineOfAssemblyCode.clear();
                Advance_Pointer();
                //write rule #13 to file
                break;
            default:
                sourceOfError = "Type, Expected MP_INTEGER, MP_FLOAT, MP_STRING, or"
                        + " MP_BOOLEAN instead found: " + lookAhead;
                errorsFound.add(sourceOfError);
                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rules 14, 15, and 16
// <editor-fold defaultstate="collapsed" desc="Proc_Func_Dec_Part"> 
    public static void Proc_Func_Dec_Part() {
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
                break;

            default:
                In_Proc_Func_Flag = 0;
                parserWriter.println("rule #16 : --E--");
                potentialError = "Proc_Func_Dec_Part treated as Empty.";
                break;
        }
        In_Proc_Func_Flag = 0;
    }
// </editor-fold>

// rule 17
// <editor-fold defaultstate="collapsed" desc="Proc_Dec"> 
    public static void Proc_Dec() {
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
                    CurrToken = "MP_PROCEDURE";
                    Type = "null";
                    Kind = "null";
                    Mode = "in";
                    int tempSize = 0;
                    // procedures cannot return things so only offset by number
                    // of incoming parameters
                    for (int i = 0; i < dynamicParams.size(); i++) {
                        if (!dynamicParams.get(i).contains("MP_")) {
                            tempSize++;
                        }
                    }
                    Size = tempSize;
                    Convert_To_String_Array(dynamicParams);
                    s_table.Insert_Row(TableName, CurrLexeme, CurrToken, Type, Kind,
                            Mode, Integer.toString(Size), Parameters);
                }
//##############################################################################
                parserWriter.println("rule #17 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #17 : expanding");
                Block();
                G_Check = Match("MP_SCOLON");
                switch (G_Check) {
                    case 1:
//##############################################################################
//############ SYMBOL TABLE STUFF ##############################################
//##############################################################################
//                        System.out.println("TABLENAME: " + TableName);
                        // uncomment DESTROY METHOD when done with testing and 
                        // ready to clear 
                        // out tables that are no longer in scope

                        //uncomment this if statement to see main table at end
//                        if (!TableName.equals("Tester"))
//                        System.out.println("Destroyed table: " +TableName);
                        destroyPointer--;
                        NestingLevel--;
                        TableName = lookUpArray.get(destroyPointer);
//                        System.out.println("Table is now: " +TableName);

//##############################################################################
                        parserWriter.println("rule #17 : TERMINAL");
                        Advance_Pointer();
                        break;

                    default:
                        sourceOfError = "Proc_Dec, Expected MP_SCOLON_2 found: "
                                + "" + lookAhead;
                        errorsFound.add(sourceOfError);
                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                }
                break;
            default:
                sourceOfError = "Proc_Dec, Expected MP_SCOLON_1 found: "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rule 18
// <editor-fold defaultstate="collapsed" desc="Func_Dec"> 
    public static void Func_Dec() {
        // 18. Func_Dec -> Func_Head MP_SCOLON Block MP_SCOLON
        parserWriter.println("rule #18 : expanding");
        Func_Head();
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                if (In_Proc_Func_Flag == 1) {
                    int tempSize = 0;
                    Type = "null";
                    Kind = "null";
                    int checkLast = dynamicParams.size();
                    if (dynamicParams.get(checkLast - 1).contains("MP_")
                            && dynamicParams.get(checkLast - 2).contains("MP_")) {
                        Mode = "in / out";
                        // offset for the return variable 
                        tempSize++;
                    } else {
                        Mode = "in";
                    }
                    for (int i = 0; i < dynamicParams.size(); i++) {
                        if (!dynamicParams.get(i).contains("MP_")) {
                            // offsets for the number of parameters
                            tempSize++;
                        }
                    }
                    Size = tempSize;
                    Convert_To_String_Array(dynamicParams);
                    s_table.Insert_Row(TableName, CurrLexeme, CurrToken, Type, Kind,
                            Mode, Integer.toString(Size), Parameters);
                }
//##############################################################################
                parserWriter.println("rule #18 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #18 : expanding");
                Block();
                G_Check = Match("MP_SCOLON");
                switch (G_Check) {
                    case 1:
//##############################################################################
//############ SYMBOL TABLE STUFF ##############################################
//##############################################################################
//                        System.out.println("TABLENAME: " + TableName);
                        // uncomment DESTROY METHOD when done with testing and 
                        // ready to clear 
                        // out tables that are no longer in scope

                        //uncomment this if statement to see main table at end
//                        if (!TableName.equals("Tester"))
//                        System.out.println("Destroyed table: " +TableName);
                        destroyPointer--;
                        NestingLevel--;
                        TableName = lookUpArray.get(destroyPointer);

                        Mode = "";
//                        System.out.println("Table is now: " +TableName);

//##############################################################################
                        parserWriter.println("rule #18 : TERMINAL");
                        Advance_Pointer();
                        break;
                    default:
                        sourceOfError = "Func_Dec, Expected MP_SCOLON_2 found: "
                                + "" + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                }
                break;
            default:
                sourceOfError = "Func_Dec, Expected MP_SCOLON_1 found: "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rule 19
// <editor-fold defaultstate="collapsed" desc="Proc_Head"> 
    public static void Proc_Head() {
        // 19. Proc_Head -> MP_PROCEDURE Proc_Id Opt_Formal_Param_List
        G_Check = Match("MP_PROCEDURE");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                Size = 0;
                CurrToken = lookAhead;
                Type = null;
                Kind = parseTokens.get(index + 3);
//##############################################################################
                parserWriter.println("rule #19 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #19 : expanding");
                Proc_Id();
                parserWriter.println("rule #19 : expanding");
                Opt_Formal_Param_List();
                break;
            default:
                sourceOfError = "Proc_Head, Expected MP_PROCEDURE found:"
                        + " " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rule 20
// <editor-fold defaultstate="collapsed" desc="Func_Head">
    public static void Func_Head() {
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
                    break;
                } else {
                    sourceOfError = "Func_Head, Expected MP_COLON found: "
                            + "" + lookAhead;
                    errorsFound.add(sourceOfError);
                    lineNo = parseTokens.get(index + 1);
                    errorLocation.add(lineNo);

                    //add col no corresponding to error
                    colNo = parseTokens.get(index + 2);
                    errorLocation.add(colNo);
                    break;
                }
            default:
                sourceOfError = "Func_Head, Expected MP_FUNCTION found: "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);
                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rules 21 and 22
// <editor-fold defaultstate="collapsed" desc="Opt_Formal_Param_List">
    public static void Opt_Formal_Param_List() {
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
                        break;

                    default:
                        sourceOfError = "Opt_Formal_Param_List, Expected "
                                + "MP_RPAREN found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                }
                break;
            default:
                parserWriter.println("rule #22 : --E--");
                potentialError = "Opt_Formal_Param_List treated as Empty";
                break;
        }
    }
// </editor-fold>

// rules 23 and 24
// <editor-fold defaultstate="collapsed" desc="Formal_Param_Sec_Tail">
    public static void Formal_Param_Sec_Tail() {
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
                break;
            default:
                parserWriter.println("rule #24 : --E--");
                potentialError = "Formal_Param_Sec_Tail, Treated as Empty";
                break;
        }
    }
// </editor-fold>

// rules 25 and 26
// <editor-fold defaultstate="collapsed" desc="Formal_Param_Sec">
    public static void Formal_Param_Sec() {
        // 25. Formal_Param_Sec -> Val_Param_Sec
        // 26. Formal_Param_Sec -> Var_Param_Sec
        if (lookAhead.equals("MP_VAR")) {
            parserWriter.println("rule #25 : expanding");
            Var_Param_Sec();
        } else {
            parserWriter.println("rule #26 : expanding");
            Val_Param_Sec();
        }
    }
// </editor-fold>

//rule 27
// <editor-fold defaultstate="collapsed" desc="Val_Param_Sec">
    public static void Val_Param_Sec() {
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
                break;
            default:
                sourceOfError = "Val_Param_Sec, Expected MP_COLON found: "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rule 28
// <editor-fold defaultstate="collapsed" desc="Var_Param_Sec">
    public static void Var_Param_Sec() {
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
                        break;
                    default:
                        sourceOfError = "Var_Param_Sec, Expected MP_COLON found"
                                + ": " + lookAhead;
                        errorsFound.add(sourceOfError);
                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                }
                break;
            default:
                sourceOfError = "Var_Param_Sec, Expected MP_VAR found: "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rule 29
// <editor-fold defaultstate="collapsed" desc="Statement_Part">
    public static void Statement_Part() {
        // 29. Statement_Part -> Compound_Statement
        parserWriter.println("rule #29 : expanding");
        Compound_Statement();
    }
// </editor-fold>

// rule 30
// <editor-fold defaultstate="collapsed" desc="Compound_Statement">
    public static void Compound_Statement() {
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
                    //increment the destroyPointer
                    destroyPointer++;
                    //add the Tablename to lookuparray so we can iterate of the
                    //tables later in symantic analysis and see if any of the
                    //existing tables contain our variable
                    lookUpArray.add(TableName);
                    Parameters = init;
                    for (int i = 0; i < dynamicParams.size(); i++) {
                        // get the current lexeme for row creation
                        String tempLexeme = dynamicParams.get(i);

                        int tempi = i;
                        while (!dynamicParams.get(tempi).contains("MP_")) {
                            tempi++;
                        }
                        String tempToken = dynamicParams.get(tempi);
                        //if tempLexeme is not the token then insert row
                        // and the token is non-empty
                        if (!tempLexeme.contains("MP_") && !tempLexeme.equals("")) {
                            s_table.Insert_Row(TableName, tempLexeme, tempToken,
                                    Type, Kind, Mode, "1", Parameters);
                        }
                    }
                    //reset procedure name to null
                    ProcName = "";
                    dynamicParams.clear();
                } else if (!FuncName.equals("")) {
                    TableName = FuncName;
                    s_table.New_Table(TableName, Integer.toString(Nlvl), Label);
                    destroyPointer++;
                    //add the Tablename to lookuparray so we can iterate of the
                    //tables later in symantic analysis and see if any of the
                    //existing tables contain our variable
                    lookUpArray.add(TableName);
                    Parameters = init;
                    for (int i = 0; i < dynamicParams.size(); i++) {
                        // get the current lexeme for row creation
                        String tempLexeme = dynamicParams.get(i);

                        int tempi = i;
                        while (!dynamicParams.get(tempi).contains("MP_")) {
                            tempi++;
                        }
                        String tempToken = dynamicParams.get(tempi);
                        if (!tempLexeme.contains("MP_")) {
                            s_table.Insert_Row(TableName, tempLexeme, tempToken,
                                    Type, Kind, Mode, "1", Parameters);
                        }
                    }
                    //reset function name to null
                    FuncName = "";
                    dynamicParams.clear();
                } else {
                    potentialError = "ProcName or FuncName May not be set";
                }
//##############################################################################
                parserWriter.println("rule #30 : TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #30 : expanding");
                Statement_Seq();
                G_Check = Match("MP_END");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #30 : TERMINAL");
                        Advance_Pointer();
                        break;

                    default:
                        sourceOfError = "Compound_Statement, Expected MP_END "
                                + "found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                }
                break;
            default:
                sourceOfError = "Compound_Statement, Expected MP_BEGIN found "
                        + "" + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rule 31
// <editor-fold defaultstate="collapsed" desc="Statement_Seq">
    public static void Statement_Seq() {
        // 31. Statement_Seq -> Statement Statement_Tail
        parserWriter.println("rule #31 : expanding");
        Statement();
        parserWriter.println("rule #31 : expanding");
        Statement_Tail();
    }
// </editor-fold>

// rules 32 and 33
// <editor-fold defaultstate="collapsed" desc="Statement_Tail">
    public static void Statement_Tail() {
        // 32. Statement_Tail -> MP_SCOLON Statement Statement_Tail
        // 33. Statement_Tail -> MP_EMPTY
        G_Check = Match("MP_SCOLON");
        switch (G_Check) {
            case 1:
                for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                    assemblyWriter.print(lineOfAssemblyCode.get(i));
                }
//                assemblyWriter.println();
                parserWriter.println("rule #32 : TERMINAL");
                // Ok we found the MP_SCOLON, reset the semantics
                assignee = "NO_ASSIGNEE";
                finalType = "NO_TYPE";
                Advance_Pointer();
                parserWriter.println("rule #32 : expanding");
                Statement();
                parserWriter.println("rule #32 : expanding");
                Statement_Tail();
                break;

            default:
                parserWriter.println("rule #33 : --E--");
                potentialError = "Statement_Tail, Treated as Empty";
                break;
        }
    }
// </editor-fold>

// rules 34, 35, 36, 37, 38, 39, 40, 41, 42, and 43
// <editor-fold defaultstate="collapsed" desc="Statement">
    public static void Statement() {
        lineOfAssemblyCode.clear();
        // 35. Statement -> Compound_Statement
        if (lookAhead.equals("MP_BEGIN")) {
            parserWriter.println("rule #35 : expanding");
            Compound_Statement();
        } // 36. Statement -> Read_Statement
        else if (lookAhead.equals("MP_READ")) {
            parserWriter.println("rule #36 : expanding");
            comingFromRead = 1;
            Read_Statement();
            comingFromRead = 0;
        } // 37. Statement -> Write_Statement
        else if (lookAhead.equals("MP_WRITE") || lookAhead.equals("MP_WRITELN")) {
            parserWriter.println("rule #37 : expanding");
            comingFromWrite = 1;
            Write_Statement();
            comingFromWrite = 0;
        } // 38. Statement -> Assign_Statement
        else if (lookAhead.equals("MP_IDENTIFIER")) {
            parserWriter.println("rule #38 : expanding");
            Assign_Statement();
        } // 39. Statement -> If_Statement
        else if (lookAhead.equals("MP_IF")) {
            comingFromIf = 1;
            parserWriter.println("rule #39 : expanding");
            If_Statement();
            comingFromIf = 0;
        } // 40. Statement -> While_Statement
        else if (lookAhead.equals("MP_WHAssign_SILE")) {
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
    }
// </editor-fold>

// rule 44
// <editor-fold defaultstate="collapsed" desc="Empty_Statement">
    //Monica started here writing rules
    public static void Empty_Statement() {
        // 44. Empty_Statement -> MP_EMPTY
        parserWriter.println("rule #44 : --E--");
        potentialError = "Statement, Treated as Empty";
    }
// </editor-fold>

// rule 45
// <editor-fold defaultstate="collapsed" desc="Read_Statement">
    public static void Read_Statement() {
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
                                break;

                            default:
                                sourceOfError = "Read_Statement, Expected "
                                        + "MP_RPAREN found: " + lookAhead;
                                errorsFound.add(sourceOfError);

                                lineNo = parseTokens.get(index + 1);
                                errorLocation.add(lineNo);

                                //add col no corresponding to error
                                colNo = parseTokens.get(index + 2);
                                errorLocation.add(colNo);
                                break;
                        } //end case for R_PAREN
                        break;
                    default:
                        sourceOfError = "Read_Statement, Expected "
                                + "MP_LPAREN found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case for LPAREN
                break;
            default:
                sourceOfError = "Read_Statement, Expected "
                        + "MP_READ found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        } //end case for READ
    }
// </editor-fold>

//rules 46 and 47
// <editor-fold defaultstate="collapsed" desc="Read_Param_Tail">
    public static void Read_Param_Tail() {
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
                break;

            default:
                parserWriter.println("rule #47 : --E--");
                potentialError = "Read_Param_Tail, Treated as Empty";
                break;
        }
    }
// </editor-fold>

// rule 48
// <editor-fold defaultstate="collapsed" desc="Read_Param">
    public static void Read_Param() {
        // 48. Read_Param -> Var_Id
        String[] tempString;
        tempString = Var_Id();
        parserWriter.println("rule #48 : expanding");
        lineOfAssemblyCode.clear();
        lineOfAssemblyCode.add("RD ");
        String offset = s_table.Get_Offset(TableName, tempString[0]);
        lineOfAssemblyCode.add(offset);
        lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
        lineOfAssemblyCode.add("                   ;" + tempString[0] + "\n");
        for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
            assemblyWriter.print(lineOfAssemblyCode.get(i));
        }
        lineOfAssemblyCode.clear();
        
    }
// </editor-fold>

// rules 49 and 50
// <editor-fold defaultstate="collapsed" desc="Write_Statement">
    public static void Write_Statement() {
        // 49. Write_Statement -> MP_WRITE_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
        // 50. Write_Statement -> MP_WRITELN_WORD MP_LPAREN Write_Param Write_Param_Tail MP_RPAREN
        whichWrite = 0;
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
                                break;
                            default:
                                sourceOfError = "Write_Statement, Expected "
                                        + "MP_RPAREN found: " + lookAhead;
                                errorsFound.add(sourceOfError);

                                lineNo = parseTokens.get(index + 1);
                                errorLocation.add(lineNo);

                                //add col no corresponding to error
                                colNo = parseTokens.get(index + 2);
                                errorLocation.add(colNo);
                                break;
                        } //end case for RParen
                        break;
                    default:
                        sourceOfError = "Write_Statement, Expected "
                                + "MP_LPAREN found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case for LParen
                break;
            default:
                sourceOfError = "Write_Statement, Expected "
                        + "MP_WRITE or MP_WRITE_LN found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        } //end case for MP_WRITE
    }
// </editor-fold>

// rules 51 and 52
// <editor-fold defaultstate="collapsed" desc="Write_Param_Tail">
    public static void Write_Param_Tail() {
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
                break;

            default:
                parserWriter.println("rule #52 : --E--");
                potentialError = "Write_Param_Tail, Treated as Empty";
                break;
        } //end case for Comma
    }
// </editor-fold>

// rule 53
// <editor-fold defaultstate="collapsed" desc="Write_Param">
    public static void Write_Param() {
        // 53. Write_Param -> Ordinal_Expression
        parserWriter.println("rule #53 : expanding");
        Ordinal_Expression();
    }
// </editor-fold>

// rules 54 and 55
// <editor-fold defaultstate="collapsed" desc="Assign_Statement">
    public static void Assign_Statement() {
        // 54. Assign_Statement -> Var_Id MP_ASSIGN Expression
        // 55. Assign_Statement -> Func_Id MP_ASSIGN Expression
        comingFromAssignStatement = 1;
        String whichRule = "rule # NOT_A_RULE"; //default
        String peekID = parseTokens.get(index + 3);
        String[] tempString = {"DEFAULT", "DEFAULT"};
        if (Functions.contains(peekID)) {
            whichRule = "rule #55";
            parserWriter.println(whichRule + ": expanding");
            Func_Id();
        } else if (Variables.contains(peekID)) {
            whichRule = "rule #54";
            parserWriter.println(whichRule + ": expanding");
            tempString = Var_Id();
            lineOfAssemblyCode.clear();
            lineOfAssemblyCode.add("PUSH ");
            String offset = s_table.Get_Offset(TableName, tempString[0]);
            lineOfAssemblyCode.add(offset);
            lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
            lineOfAssemblyCode.add("                   ;" + tempString[0] + "\n");
            for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                assemblyWriter.print(lineOfAssemblyCode.get(i));
            }
            lineOfAssemblyCode.clear();

        } else {
            potentialError = "Variable or Function undeclared";
        }

        G_Check = Match("MP_ASSIGN");
        switch (G_Check) {
            case 1:
                lineOfAssemblyCode.add("MOV ");

                parserWriter.println(whichRule + ": TERMINAL");
                Advance_Pointer();
                parserWriter.println(whichRule + ": expanding");

                ExpressionCounter = Expression();

                if (ExpressionCounter == 1 && OperationsCounter == 0) {
//                    assemblyWriter.println("ExpressionCounter = " + ExpressionCounter);
                    assemblyWriter.println("ADDS");
                    lineOfAssemblyCode.clear();
                    lineOfAssemblyCode.add("POP ");
                    String offset = s_table.Get_Offset(TableName, tempString[0]);
                    lineOfAssemblyCode.add(offset);
                    lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                    lineOfAssemblyCode.add("                   ;" + tempString[0] + "\n");
                    for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                        assemblyWriter.print(lineOfAssemblyCode.get(i));
                    }
                    lineOfAssemblyCode.clear();
                } else {
                    lineOfAssemblyCode.clear();
                    lineOfAssemblyCode.add("POP ");
                    String offset = s_table.Get_Offset(TableName, tempString[0]);
                    lineOfAssemblyCode.add(offset);
                    lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                    lineOfAssemblyCode.add("                   ;" + tempString[0] + "\n");
                    for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                        assemblyWriter.print(lineOfAssemblyCode.get(i));
                    }
                    lineOfAssemblyCode.clear();
                }
                ExpressionCounter = 0;
                break;

            default:
                sourceOfError = "Assign_Statement, Expected "
                        + "MP_ASSIGN found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        } //end case for Assign
        comingFromAssignStatement = 0;
    }
// </editor-fold>

// rule 56
// <editor-fold defaultstate="collapsed" desc="If_Statement">
    public static void If_Statement() {
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
                        //branch to next label
                        Opt_Else_Part();
                        
                        break;

                    default:
                        sourceOfError = "If_Statement, Expected "
                                + "MP_THEN found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case for Then
                break;
            default:
                sourceOfError = "If_Statement, Expected "
                        + "MP_IF found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        } //end case for If
    }
// </editor-fold>

// rules 57 and 58
// <editor-fold defaultstate="collapsed" desc="Opt_Else_Part">
    public static void Opt_Else_Part() {
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
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
            } else {
                index = bak_Index;
                lookAhead = bak_lookAhead;
            }
        } else {
            G_Check = Match("MP_ELSE");
            switch (G_Check) {
                case 1:
                    // terminate the previous if part with a branch to...
                    String tempString = "BR L" + Integer.toString(labelCounter + 1);
                        assemblyWriter.println(tempString);
                    //drop a label
                    assemblyWriter.println("L" + labelCounter + ":");
                    labelCounter++;
                    System.out.println("labelCounter = " + labelCounter);
                    Advance_Pointer();
                    parserWriter.println("rule #57: expanding");
                    Statement();
                    // end by adding the branch to label from 8 lines up
                    assemblyWriter.println("L" + labelCounter + ":");
                    break;

                default:
                    parserWriter.println("rule #58: --E--");
                    potentialError = "Opt_Else_Part, Treated as Empty";
                    break;
            } //end case for else
        }
    }
// </editor-fold>

// rule 59
// <editor-fold defaultstate="collapsed" desc="Repeat_Statement">
    public static void Repeat_Statement() {
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
                        break;

                    default:
                        sourceOfError = "Repeat_Statement, Expected "
                                + "MP_UNTIL found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case for Until
                break;
            default:
                sourceOfError = "Repeat_Statement, Expected "
                        + "MP_REPEAT found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        } //end case for Repeat
    }
// </editor-fold>

// rule 60
// <editor-fold defaultstate="collapsed" desc="While_Statement">
    public static void While_Statement() {
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
                        break;

                    default:
                        sourceOfError = "While_Statement, Expected "
                                + "MP_DO found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case for Do
                break;
            default:
                sourceOfError = "While_Statement, Expected "
                        + "MP_WHILE found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        } //end case for While
    }
// </editor-fold>

// rule 61
// <editor-fold defaultstate="collapsed" desc="For_Statement">
    public static void For_Statement() {
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
                    case 1:                //Error();
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
                                break;
                            default:
                                sourceOfError = "For_Statement, Expected "
                                        + "MP_DO found: " + lookAhead;
                                errorsFound.add(sourceOfError);

                                lineNo = parseTokens.get(index + 1);
                                errorLocation.add(lineNo);

                                //add col no corresponding to error
                                colNo = parseTokens.get(index + 2);
                                errorLocation.add(colNo);
                                break;
                        } //end case for Do
                        break;
                    default:
                        sourceOfError = "For_Statement, Expected "
                                + "MP_ASSIGN found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case for Assign
                break;
            default:
                sourceOfError = "For_Statement, Expected "
                        + "MP_FOR found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        } //end case for For
    }
// </editor-fold>

// rule 62
// <editor-fold defaultstate="collapsed" desc="Control_Var">
    public static void Control_Var() {
        // 62. Control_Var -> Var_Id
        parserWriter.println("rule #62: expanding");
        Var_Id();
    }
// </editor-fold>

// rule 63
// <editor-fold defaultstate="collapsed" desc="Init_Val">
    public static void Init_Val() {
        // 63. Init_Val -> Ordinal_Expression
        parserWriter.println("rule #63: expanding");
        Ordinal_Expression();
    }
// </editor-fold>

// rules 64 and 65
// <editor-fold defaultstate="collapsed" desc="Step_Val">
    public static void Step_Val() {
        // 64. Step_Val -> MP_TO_WORD
        // 65. Step_Val -> MP_DOWNTO_WORD
        G_Check = Match("MP_TO");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #64: TERMINAL");
                Advance_Pointer();
                break;
            default:
                G_Check = Match("MP_DOWNTO");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #65: TERMINAL");
                        Advance_Pointer();
                        break;

                    default:
                        sourceOfError = "Step_Val, Expected "
                                + "MP_TO or MP_DOWNTO found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //                        //establish index number of lookahead           
//                        lookAheadIndex = parseTokens.indexOf(lookAhead);
            //add line no corresponding to errorend case DownTo
        }
    }
// </editor-fold>

// rule 66
// <editor-fold defaultstate="collapsed" desc="Final_Val">
    public static void Final_Val() {
        // 66. Final_Val -> Ordinal_Expression
        parserWriter.println("rule #66: expanding");
        Ordinal_Expression();
    }
// </editor-fold>

// rule 67
// <editor-fold defaultstate="collapsed" desc="Proc_Statement">
    public static void Proc_Statement() {
        // 67. Proc_Statement -> Proc_Id Opt_Actual_Param_List
        parserWriter.println("rule #67: expanding");
        Proc_Id();
        parserWriter.println("rule #67: expanding");
        Opt_Actual_Param_List();
    }
// </editor-fold>

// rules 68 and 69
// <editor-fold defaultstate="collapsed" desc="Opt_Actual_Param_List">
    public static void Opt_Actual_Param_List() {
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
                        break;

                    default:
                        sourceOfError = "Opt_Actual_Param_List, Expected "
                                + "MP_RPAREN found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case RParen
                break;
            default:
                parserWriter.println("rule #69: --E--");
                potentialError = "Opt_Actual_Param_List, Treated as Empty";
                break;
        }
    }
// </editor-fold>

// rules 70 and 71
// <editor-fold defaultstate="collapsed" desc="Actual_Param_Tail">
    public static void Actual_Param_Tail() {
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
                break;

            default:
                parserWriter.println("rule #71: --E--");
                potentialError = "Actual_Param_List, Treated as Empty";
                break;
        }
    }
// </editor-fold>

// rule 72
// <editor-fold defaultstate="collapsed" desc="Actual_Param">
    public static void Actual_Param() {
        // 72. Actual_Param -> Ordinal_Expression
        parserWriter.println("rule #72: expanding");
        Ordinal_Expression();
    }
// </editor-fold>

// rule 73
// <editor-fold defaultstate="collapsed" desc="Expression">
    public static int Expression() {
        operationsArray.clear();
        ExpressionCounter++;
//        System.out.println("Expression Iteration = " + ExpressionCounter);
        // 73. Expression -> Simple_Expression Opt_Relational_Part
        parserWriter.println("rule #73: expanding");
        Simple_Expression();
        parserWriter.println("rule #73: expanding");
        Opt_Relational_Part();

        //print out operations array in reverse order with assemblyWriter.
        for (int i = operationsArray.size() - 1; i >= 0; i--) {
            assemblyWriter.println(operationsArray.get(i));
            operationsArray.remove(i);
        }
        int returnExpressionCounter = ExpressionCounter;
        return returnExpressionCounter;
    }
// </editor-fold>

// rules 74 and 75
// <editor-fold defaultstate="collapsed" desc="Opt_Relational_Part">
    public static void Opt_Relational_Part() {
        // 74. Opt_Relational_Part -> Relational_Op Simple_Expression
        // 75. Opt_Relational_Part -> MP_EMPTY
        parserWriter.println("rule #74: expanding");
        int tempCheck = Relational_Op();
        if (tempCheck == -1) {
            parserWriter.println("rule #75: --E--");
            potentialError = "Opt_Relational_Part treated as Empty";
        } else {
            parserWriter.println("rule #74: expanding");
            Simple_Expression();
        }
    }
// </editor-fold>

// rules 76, 77, 78, 79, 80, and 81
// <editor-fold defaultstate="collapsed" desc="Relational_Op">
    public static int Relational_Op() {
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
            assemblyWriter.println("EQUALS");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LTHAN")) {
            parserWriter.println("rule #77: TERMINAL");
            operationsArray.add("CMPLTS");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_GTHAN")) {
            parserWriter.println("rule #78: TERMINAL");
            //will be popped in reverse so branch first then "compare greater than stack"
            String tempString = ("BRFS L" + labelCounter);
            operationsArray.add(tempString);
            operationsArray.add("CMPGTS");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_LEQUAL")) {
            parserWriter.println("rule #79: TERMINAL");
            assemblyWriter.println("LESS_THAN_OR_EQUAL_TO");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_GEQUAL")) {
            parserWriter.println("rule #80: TERMINAL");
            assemblyWriter.println("GREATER_THAN_OR_EQUAL_TO");
            Advance_Pointer();
        } else if (lookAhead.equals("MP_NEQUAL")) {
            parserWriter.println("rule #81: TERMINAL");
            assemblyWriter.println("NOT_EQUAL");
            Advance_Pointer();
        } else {
            return -1;
        }
        return 0;
    }
// </editor-fold>

// rule 82
// <editor-fold defaultstate="collapsed" desc="Simple_Expression">
    public static void Simple_Expression() {
        // 82. Simple_Expression -> Optional_Sign Term Term_Tail
        parserWriter.println("rule #82: expanding");
        Optional_Sign();
        parserWriter.println("rule #82: expanding");
        Term();
        parserWriter.println("rule #82: expanding");
        Term_Tail();
    }
// </editor-fold>

// rules 83 and 84
// <editor-fold defaultstate="collapsed" desc="Term_Tail">
    public static void Term_Tail() {
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
    }
// </editor-fold>

// rules 85, 86, and 87
// <editor-fold defaultstate="collapsed" desc="Optional_Sign">
    public static void Optional_Sign() {
        // 85. Optional_Sign -> MP_PLUS
        // 86. Optional_Sign -> MP_MINUS
        // 87. Optional_Sign -> MP_EMPTY
        G_Check = Match("MP_PLUS");
        switch (G_Check) {
            case 1:
                parserWriter.println("rule #85: TERMINAL");
                Advance_Pointer();
                break;

            default:
                G_Check = Match("MP_MINUS");
                switch (G_Check) {
                    case 1:
                        parserWriter.println("rule #86: TERMINAL");
                        Advance_Pointer();
                        break;
                    default:
                        parserWriter.println("rule #87: --E--");
                        potentialError = "Optional_Sign treated as Empty";
                } //end case Minus
                break;
        } //end case Plus
    }
// </editor-fold>

// rules 88, 89, and 90
// <editor-fold defaultstate="collapsed" desc="Add_Op">
    public static int Add_Op() {
        // 88. Add_Op -> MP_PLUS
        // 89. Add_Op -> MP_MINUS
        // 90. Add_Op -> MP_OR
        G_Check = Match("MP_PLUS");
        switch (G_Check) {
            case 1:
                OperationsCounter++;
                operationsArray.add("ADDS");
                parserWriter.println("rule #88: TERMINAL");
                Advance_Pointer();
                return 0;
            default:
                G_Check = Match("MP_MINUS");
                switch (G_Check) {
                    case 1:
                        OperationsCounter++;
                        operationsArray.add("SUBS");
                        parserWriter.println("rule #89: TERMINAL");
                        Advance_Pointer();
                        return 0;
                    default:
                        G_Check = Match("MP_OR");
                        switch (G_Check) {
                            case 1:
                                OperationsCounter++;
                                operationsArray.add("ORS");
                                parserWriter.println("rule #90: TERMINAL");
                                Advance_Pointer();
                                return 0;
                            default:
                                return -1;
                        } //end case OR
                } //end case Minus
        } //end case Plus
    }
// </editor-fold>

// rule 91
// <editor-fold defaultstate="collapsed" desc="Term">
    public static void Term() {
        // 91. Term -> Factor Factor_Tail
        parserWriter.println("rule #91: expanding");
        Factor();
        parserWriter.println("rule #91: expanding");
        Factor_Tail();
    }
// </editor-fold>

// rules 92 and 93
// <editor-fold defaultstate="collapsed" desc="Factor_Tail">
    public static void Factor_Tail() {
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
    }
// </editor-fold>

// rules 94, 95, 96, 97, and 98
// <editor-fold defaultstate="collapsed" desc="Multiply_Op">
    public static int Multiply_Op() {
        // 94. Multiply_Op -> MP_TIMES
        // 95. Multiply_Op -> MP_FORWARD_SLASH /* (different then div)*/
        // 96. Multiply_Op -> MP_DIV_WORD
        // 97. Multiply_Op -> MP_MOD_WORD
        // 98. Multiply_Op -> MP_AND_WORD
        G_Check = Match("MP_TIMES");
        switch (G_Check) {
            case 1:
                OperationsCounter++;
                operationsArray.add("MULS");
                parserWriter.println("rule #94: TERMINAL");
                Advance_Pointer();
                return 0;
            default:
                G_Check = Match("MP_FORWARD_SLASH\n");
                switch (G_Check) {
                    case 1:
                        OperationsCounter++;
                        operationsArray.add("DIVS");
                        parserWriter.println("rule #95: TERMINAL");
                        Advance_Pointer();
                        return 0;
                    default:
                        G_Check = Match("MP_DIV");
                        switch (G_Check) {
                            case 1:
                                OperationsCounter++;
                                operationsArray.add("DIVS");
                                parserWriter.println("rule #96: TERMINAL");
                                Advance_Pointer();
                                return 0;
                            default:
                                G_Check = Match("MP_MOD");
                                switch (G_Check) {
                                    case 1:
                                        OperationsCounter++;
                                        operationsArray.add("MODS");
                                        parserWriter.println("rule #97: TERMINAL");
                                        Advance_Pointer();
                                        return 0;
                                    default:
                                        G_Check = Match("MP_AND");
                                        switch (G_Check) {
                                            case 1:
                                                OperationsCounter++;
                                                operationsArray.add("ANDS");
                                                parserWriter.println("rule #98: TERMINAL");
                                                Advance_Pointer();
                                                return 0;
                                            default:
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
        ArrayList<String> tempList = new ArrayList<>();
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
            CurrLexeme = parseTokens.get(index + 3);
            lineOfAssemblyCode.clear();
            lineOfAssemblyCode.add("PUSH ");
            String offset = "#";
            lineOfAssemblyCode.add(offset);
            lineOfAssemblyCode.add(CurrLexeme + "\n");
            for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                assemblyWriter.print(lineOfAssemblyCode.get(i));
            }
            lineOfAssemblyCode.clear();

            comingFromFactor_NotFuncOrVar = 1;
            Advance_Pointer();
        } else if (lookAhead.equals("MP_FLOAT")) {
            parserWriter.println("rule #100: TERMINAL");
            CurrLexeme = parseTokens.get(index + 3);
            lineOfAssemblyCode.clear();
            lineOfAssemblyCode.add("PUSH ");
            String offset = "#";
            lineOfAssemblyCode.add(offset);
            lineOfAssemblyCode.add(CurrLexeme + "\n");
            for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                assemblyWriter.print(lineOfAssemblyCode.get(i));
            }
            lineOfAssemblyCode.clear();
            comingFromFactor_NotFuncOrVar = 1;
            Advance_Pointer();
        } else if (lookAhead.equals("MP_STRING_LIT")) {
            parserWriter.println("rule #101: TERMINAL");
            comingFromFactor_NotFuncOrVar = 1;
            writeStatementStringArray.clear();
            Advance_Pointer();
            assemblyWriter.print("WRT #\"");
            for (int i = 0; i < writeStatementStringArray.size(); i++) {
                String temp1 = writeStatementStringArray.get(i);
                if (temp1.contains("'''")) {
                    String temp2 = temp1.replace("'''", "");
                    temp1 = temp2;
                }
                assemblyWriter.print(temp1 + " ");
            }
            assemblyWriter.println("\"");
        } else if (lookAhead.equals("MP_TRUE")) {
            parserWriter.println("rule #102: TERMINAL");
            lineOfAssemblyCode.clear();
            lineOfAssemblyCode.add("PUSH ");
            String offset = "#";
            lineOfAssemblyCode.add(offset);
            lineOfAssemblyCode.add("1\n");
            for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                assemblyWriter.print(lineOfAssemblyCode.get(i));
            }
            lineOfAssemblyCode.clear();
            comingFromFactor_NotFuncOrVar = 1;
            Advance_Pointer();
        } else if (lookAhead.equals("MP_FALSE")) {
            parserWriter.println("rule #103: TERMINAL");
            lineOfAssemblyCode.clear();
            lineOfAssemblyCode.add("PUSH ");
            String offset = "#";
            lineOfAssemblyCode.add(offset);
            lineOfAssemblyCode.add("0\n");
            for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                assemblyWriter.print(lineOfAssemblyCode.get(i));
            }
            lineOfAssemblyCode.clear();
            comingFromFactor_NotFuncOrVar = 1;
            Advance_Pointer();
        } else if (lookAhead.equals("MP_NOT")) {
            parserWriter.println("rule #104: TERMINAL");
            comingFromFactor_NotFuncOrVar = 1;
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
                    break;
                default:
                    sourceOfError = "Factor, expected MP_RPAREN found: " + lookAhead;
                    errorsFound.add(sourceOfError);

                    lineNo = parseTokens.get(index + 1);
                    errorLocation.add(lineNo);

                    //add col no corresponding to error
                    colNo = parseTokens.get(index + 2);
                    errorLocation.add(colNo);
                    break;
            }
        } else {
            //use a flag of some sort?
            String peekID = parseTokens.get(index + 3);
            if (Functions.contains(peekID)) {
                parserWriter.println("rule #106: expanding");
                Func_Id();
                parserWriter.println("rule #106: expanding");
                Opt_Actual_Param_List();
            } else if (Variables.contains(peekID)) {
                parserWriter.println("rule #116: expanding");
                String[] tempString;
                tempString = Var_Id();
                if ((comingFromAssignStatement == 1 
                        || comingFromIf == 1)
                        && comingFromWrite == 0) {
                    lineOfAssemblyCode.clear();
                    lineOfAssemblyCode.add("PUSH ");
                    String offset = s_table.Get_Offset(TableName, tempString[0]);
                    lineOfAssemblyCode.add(offset);
                    lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                    lineOfAssemblyCode.add("                   ;" + tempString[0] + "\n");
                    for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                        assemblyWriter.print(lineOfAssemblyCode.get(i));
                    }
                    lineOfAssemblyCode.clear();
                } else if (comingFromWrite == 1) {
                    lineOfAssemblyCode.clear();
                    lineOfAssemblyCode.add("WRT ");
                    String offset = s_table.Get_Offset(TableName, tempString[0]);
                    lineOfAssemblyCode.add(offset);
                    lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                    lineOfAssemblyCode.add("                   ;" + tempString[0] + "\n");
                    for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                        assemblyWriter.print(lineOfAssemblyCode.get(i));
                    }
                    lineOfAssemblyCode.clear();
                }
            } else {
                sourceOfError = "Expected Variable or Function call found:"
                        + " " + peekID + "\n attempting to continue parsing";
                errorsFound.add(sourceOfError);
                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                parserWriter.println("rule #116: expanding");
            }
        }
    }
// </editor-fold>

// rule 107
// <editor-fold defaultstate="collapsed" desc="Prog_Id">
    public static void Prog_Id() {
        // 107. Prog_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
//##############################################################################
//############ SYMBOL TABLE STUFF ##############################################
//##############################################################################
            //Set the TableName
            TableName = parseTokens.get(index + 3);
            //put tablename in as key1 for tables
            //update the Label
            String Label = Label_1.concat(Integer.toString(Label_2));
            Label_2++;
            //update the nesting level
            int Nlvl = NestingLevel;
            NestingLevel++;
            //insert Table info using s_table API name, nesting, label
            s_table.New_Table(TableName, Integer.toString(Nlvl), Label);
            //add the Tablename to lookuparray so we can iterate of the
            //tables later in symantic analysis and see if any of the
            //existing tables contain our variable
            lookUpArray.add(TableName);
//##############################################################################
            parserWriter.println("rule #107: TERMINAL");
            //reisterDept = s_table.getNestingLevel();
//            assemblyWriter.println("PUSH " + registerDepth);
            assemblyWriter.println("MOV SP D0");
            assemblyWriter.println("ADD SP #1 SP");
//            lineOfAssemblyCode.add("PUSH " + Offset + "(D" + s_table.Get_NestingLevel(TableName) + ")");
//            lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
            Advance_Pointer();
        } else {
            sourceOfError = "Prog_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            errorsFound.add(sourceOfError);

            lineNo = parseTokens.get(index + 1);
            errorLocation.add(lineNo);

            //add col no corresponding to error
            colNo = parseTokens.get(index + 2);
            errorLocation.add(colNo);
        }
    }
// </editor-fold>

    /*
     * Thanks to Tabetha's catch on array bounds this "hey dummies" no longer
     * applies. Thank you Tabetha.
     */
// rule 108
// <editor-fold defaultstate="collapsed" desc="Var_Id">
    public static String[] Var_Id() {
        // 108. Var_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
            /*
             * Ok we've encountered an Identifier, we should look it up in 
             * the symbol table and see if it has been declared.
             */
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$ SYMANTIC ANALYSIS STUFF $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            //Check current table then up so go in reverse order
            s_analyzer.analyze_variable();
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            parserWriter.println("rule #108: TERMINAL");
            Advance_Pointer();
        } else {
            sourceOfError = "Var_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            errorsFound.add(sourceOfError);
            //add line no corresponding to error
            lineNo = parseTokens.get(index + 1);
            errorLocation.add(lineNo);

            //add col no corresponding to error
            colNo = parseTokens.get(index + 2);
            errorLocation.add(colNo);
            Advance_Pointer();
        } //END of G_CHECK
        String[] tempString = new String[2];
        tempString[0] = CurrLexeme;
        tempString[1] = s_table.Get_Token(TableName, CurrLexeme);
        return tempString;
    }
// </editor-fold>

// rule 109
// <editor-fold defaultstate="collapsed" desc="Proc_Id">
    public static void Proc_Id() {
        // 109. Proc_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
            CurrLexeme = parseTokens.get(index + 3);
            ProcName = CurrLexeme;
            Procedures.add(CurrLexeme);
//            System.out.println("Set ProcName: " + ProcName);
//##############################################################################
            parserWriter.println("rule #109: TERMINAL");

            lineOfAssemblyCode.add("PUSH " + Offset + "(D" + s_table.Get_NestingLevel(TableName) + ")");
            lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
            Advance_Pointer();
        } else {
            sourceOfError = "Proc_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            //Error();
            errorsFound.add(sourceOfError);

            lineNo = parseTokens.get(index + 1);
            errorLocation.add(lineNo);

            //add col no corresponding to error
            colNo = parseTokens.get(index + 2);
            errorLocation.add(colNo);
        }
    }
// </editor-fold>

// rule 110
// <editor-fold defaultstate="collapsed" desc="Function_Id">
    public static void Func_Id() {
        // 110. Function_Id -> MP_IDENTIFIER
        G_Check = Match("MP_IDENTIFIER");
        if (G_Check == 1) {
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$ SYMANTIC ANALYSIS STUFF $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
            s_analyzer.analyze_function();
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

            parserWriter.println("rule #110: TERMINAL");
//            lineOfAssemblyCode.add("PUSH " + Offset + "(D" + s_table.Get_NestingLevel(TableName) + ")");
//            lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
            Advance_Pointer();
        } else {
            sourceOfError = "Function_Id, Expected MP_IDENTIFIER found: " + lookAhead;
            errorsFound.add(sourceOfError);

            lineNo = parseTokens.get(index + 1);
            errorLocation.add(lineNo);

            //add col no corresponding to error
            colNo = parseTokens.get(index + 2);
            errorLocation.add(colNo);
        }
    }
// </editor-fold>            lineOfAssemblyCode.add("PUSH ");

// rule 111
// <editor-fold defaultstate="collapsed" desc="Boolean_Expression">
    public static void Boolean_Expression() {
        // 111. Boolean_Expression -> Expression
        parserWriter.println("rule #111: expanding");
        Expression();
    }
// </editor-fold>

// rule 112
// <editor-fold defaultstate="collapsed" desc="Ordinal_Expression">
    public static void Ordinal_Expression() {
        // 112. Ordinal_Expression -> Expression
        parserWriter.println("rule #112: expanding");
        Expression();
    }
// </editor-fold>

// rule 113
// <editor-fold defaultstate="collapsed" desc="Id_List">
    public static void Id_List() {
        // 113. Id_List -> MP_IDENTIFIER Id_Tail
        //precondition
        G_Check = Match("MP_IDENTIFIER");
        switch (G_Check) {
            case 1:
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                CurrLexeme = parseTokens.get(index + 3);
                if (In_Proc_Func_Flag == 1) {
                    dynamicParams.add(CurrLexeme);
                } else {
//                    System.out.println("Adding: " + parseTokens.get(index + 3) + " to the listIDs array");
                    listIDs.add(CurrLexeme);
                    Variables.add(CurrLexeme);
                }
//##############################################################################

                parserWriter.println("rule #113: TERMINAL");
                Advance_Pointer();
                parserWriter.println("rule #113: expanding");
                Id_Tail();
                break;
            default:
                sourceOfError = "Id_List, Expected "
                        + "MP_IDENTIFIER found: " + lookAhead;
                errorsFound.add(sourceOfError);

                lineNo = parseTokens.get(index + 1);
                errorLocation.add(lineNo);

                //add col no corresponding to error
                colNo = parseTokens.get(index + 2);
                errorLocation.add(colNo);
                break;
        }
    }
// </editor-fold>

// rules 114 and 115
// <editor-fold defaultstate="collapsed" desc="Id_Tail">
    public static void Id_Tail() {
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
                        CurrLexeme = parseTokens.get(index + 3);
//##############################################################################
//###### SYMBOL TABLE STUFF ####################################################
//##############################################################################
                        if (In_Proc_Func_Flag == 1) {
                            dynamicParams.add(CurrLexeme);
                        } else {
//                            System.out.println("Adding: " + parseTokens.get(index + 3) + " to the listIDs array");
                            listIDs.add(CurrLexeme);
                            Variables.add(CurrLexeme);
                        }
//##############################################################################

                        lineOfAssemblyCode.clear();
                        lineOfAssemblyCode.add("MOV #0 ");
                        String offset = Integer.toString(guessOffset);
                        lineOfAssemblyCode.add(offset);
                        lineOfAssemblyCode.add("(D" + s_table.Get_NestingLevel(TableName) + ")");
                        lineOfAssemblyCode.add("                   ;" + CurrLexeme + "\n");
                        for (int i = 0; i < lineOfAssemblyCode.size(); i++) {
                            assemblyWriter.print(lineOfAssemblyCode.get(i));
                        }
                        lineOfAssemblyCode.clear();

                        parserWriter.println("rule #114: TERMINAL");
                        Advance_Pointer();
                        parserWriter.println("rule #114: expanding");
                        Id_Tail();
                        break;

                    default:
                        sourceOfError = "Id_Tail, Expected "
                                + "MP_IDENTIFIER found: " + lookAhead;
                        errorsFound.add(sourceOfError);

                        lineNo = parseTokens.get(index + 1);
                        errorLocation.add(lineNo);

                        //add col no corresponding to error
                        colNo = parseTokens.get(index + 2);
                        errorLocation.add(colNo);
                        break;
                } //end case Identifier
                break;
            default:
                int tempSize = listIDs.size();
                assemblyWriter.println("ADD SP #" + tempSize + " SP");
                parserWriter.println("rule #115: --E--");
                potentialError = "Id_Tail, Treated as empty";
        } //end case Comma

    }
// </editor-fold>

//format errors
// <editor-fold defaultstate="collapsed" desc="Error">
    public static void Error() {
        // Move through stored list of errors, printing source of error to the
        //user so they can be corrected. Allow up to 20 errors to be printed, 
        //if more than 20 errors, print first 20 and exit. 

        if (errorsFound.size() <= 20) {
            int errorPlace = 0;
            for (int i = 0; i < errorsFound.size(); i++) {
                String message1 = "\033[31mERROR at line " + errorLocation.get(errorPlace)
                        //errorLocation[ line, col, line, col, ...]
                        //errorsFound message, message, message, ...]
                        + " column " + errorLocation.get(errorPlace + 1) + " in state: "
                        + "\033[0m";
                String message2 = "\033[31m" + errorsFound.get(i) + ".\n\033[0m";
                //while the reader is still open
                System.out.println();
                System.out.println(message1);
                System.out.println(message2);
                errorPlace += 2;
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
//            System.exit(0);
        }
        endOfErrors = 1;
        Terminate("");
    }
    // </editor-fold>

//terminate the parser
// <editor-fold defaultstate="collapsed" desc="Terminate">
    public static void Terminate(String message) {
        /* 
         * Prints a message before exiting the program 
         */
        if (errorsFound.size() > 0) {
            if (endOfErrors == 0) {
                Error();
            }
            if (errorsFound.size() == 1) {
                System.out.println("\033[31m****************************************************************");
                System.out.print("\033[31m" + (errorsFound.size()) + " ERROR"
                        + " FOUND, PLEASE CORRECT BEFORE PROGRAM CAN BE COMPILED. \n");
                System.out.println("****************************************************************\033[0m");
            } else {
                System.out.println("\033[31m****************************************************************");
                System.out.print("\033[31m" + (errorsFound.size()) + " ERRORS"
                        + " FOUND, PLEASE CORRECT BEFORE PROGRAM CAN BE COMPILED. \n");
                System.out.println("****************************************************************\033[0m");
            }
        } else {
            System.out.println(message);
            //uncomment to print out the tables for verification purposes
//            s_table.Print_Tables();
            /* 
             * NOTE: In Compound_Statement (line 1112) There are two lines
             * 1193 and 1194 uncomment 1193 (if statement) to see the main
             * table print out with all it's arguments. Comment out line 1194
             * (the Destroy method) to see all the tables generated
             *  --Attention: If the destroy method is commented out there are
             * rows that will be added to functions and procedures that should
             * actually be in main. This is expected as it runs dynamically. At
             * run time it adds the rows to the current table, if tables are not
             * being destroyed when they are out of scope then the row will be
             * inserted into the wrong table. This is not and Error, but a
             * consequence of not destroying the tables. Just be aware of it
             * if printing them out for verification purposes and testing.
             */
        }
        assemblyWriter.println("WRTLN #\"\"");
        assemblyWriter.println("POP D0\nHLT");
        parserWriter.close();
        close_assembly_writer();

        System.exit(0);
    }
// </editor-fold>
    //establish index number of lookahead           
//                        lookAheadIndex = parseTokens.indexOf(lookAhead);
    //add line no corresponding to error
//match a token
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

// convert an ArrayList to a String Array
// <editor-fold defaultstate="collapsed" desc="Convert_To_String_Array">  
    static void Convert_To_String_Array(ArrayList<String> M) {
        String[] temp = new String[M.size()];
        Parameters = temp;
        for (int i = 0; i < M.size(); i++) {
            Parameters[i] = M.get(i);
        }
    }
// </editor-fold>

}
