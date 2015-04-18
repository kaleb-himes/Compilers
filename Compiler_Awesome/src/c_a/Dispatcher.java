/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 *
 * The dispatcher determines, based on the input - which of the FSAs (digit,
 * identifier, string, or symbol) to call in order to tokenize the text. Once a
 * determination has been made, the FSA is called, and the FSA goes through the
 * text until terminating with a token, lexeme, lineNo and colNo.
 *
 */
package c_a;

import java.io.IOException;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class Dispatcher {

    //list of all symbols (and ', which will be used to demarcate strings)

    public static final String MP_ASSIGN = ":=";
    public static final String MP_COLON = ":";
    public static final String MP_COMMA = ",";
    public static final String MP_EQUAL = "=";
    public static final String MP_FLOAT_DIVIDE = "/";
    public static final String MP_GEQUAL = ">=";
    public static final String MP_GTHAN = ">";
    public static final String MP_LEQUAL = "<=";
    public static final String MP_LPAREN = "(";
    public static final String MP_LTHAN = "<";
    public static final String MP_MINUS = "-";
    public static final String MP_NEQUAL = "<>";
    public static final String MP_PERIOD = ".";
    public static final String MP_PLUS = "+";
    public static final String MP_RPAREN = ")";
    public static final String MP_SCOLON = ";";
    public static final String MP_TIMES = "*";
    public static final String UNDERSCORE = "_";
    public static final String QUOTE = "'";
    public static final String LBRACKET = "{";

    //all of the FSAs, which will be called by the dispatcher when appropriate
    protected final static identifier_FSA ident = new identifier_FSA();
    protected final static digit_FSA dig = new digit_FSA();
    protected final static string_FSA str = new string_FSA();
    protected final static symbol_FSA symb = new symbol_FSA();
    protected final static comment_FSA comm = new comment_FSA();

    //variable to keep looping until EOF
    public static boolean loop = true;

    //the values that will be returned from the FSAs
    public static String token = "";
    public static String lexeme = "";
    public static int lineNo;
    public static int colNo;

    //whitespace is not important in this language, so this is used to increment
    //counter to appropriate col numbers
    public static String whitespace = "";

    public enum State {
        //an FSA for the possible states that could be encountered
        START, IDEN, DIGIT, STR_LIT, QUOTE, SYMBOL, COMMENT, ERROR
    }
    private static State state = State.START;

    public static void handleToken(char item) throws IOException {
        boolean runOnQuote = false;

        //while the EOF character is not reached
        while (loop == true) {
            //get the token from the scanner
            item = MPscanner.getNextToken();

            switch (state) {
                case START:

                    //send to IDEN_FSA
                    if (Character.isAlphabetic((int) item)
                            || Character.toString(item).equals(UNDERSCORE)) {
                        state = State.IDEN;
                    } //send to DIGIT_FSA
                    else if (Character.isDigit((int) item)) {
                        state = State.DIGIT;
                    } //send to STR_FSA
                    else if (Character.toString(item).equals(QUOTE)) {
                        state = State.STR_LIT;
                    } //send to COMMENT_FSA
                    else if (Character.toString(item).equals(LBRACKET)) {
                        state = State.COMMENT;
                    } else if (Character.compare(item, ':') == 0 || Character.compare(item, ',') == 0
                            || Character.compare(item, '=') == 0 || Character.compare(item, '/') == 0
                            || Character.compare(item, '>') == 0 || Character.compare(item, '<') == 0
                            || Character.compare(item, '(') == 0 || Character.compare(item, '-') == 0
                            || Character.compare(item, '.') == 0 || Character.compare(item, '+') == 0
                            || Character.compare(item, ')') == 0 || Character.compare(item, ';') == 0
                            || Character.compare(item, '*') == 0) {
                        state = State.SYMBOL;
                    } else {
                        //the scanner did not find a valid FSA for this token, 
                        //therefore throw an error
                        state = State.ERROR;
                    }
                    break;

                case IDEN:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    ident.readFile();
                    state = State.START;

                    token = ident.getToken();
                    lexeme = ident.getLexeme();
                    lineNo = ident.getLineNumber();
                    colNo = ident.getColumnNumber();
                    /* Increase columns by size of lexeme */
                    mp.colNumber += lexeme.length();

                    //indicates an error token was returned from the FSA, prints
                    //message to the user, so they can go back and fix it to 
                    //avoid unintended consequences
                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND "
                                + "STARTING AT LINE %d, COLUMN %d. RESUMING SCAN "
                                + "AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
                        MPscanner.pbr.read();
                    } else {
                        MPscanner.fWriter.print(token + "  ");
                        MPscanner.fWriter.print(lineNo + "   ");
                        MPscanner.fWriter.print(colNo + "   ");
                        MPscanner.fWriter.println(lexeme);
                    }
                    break;

                case DIGIT:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    dig.readFile();
                    state = State.START;

                    token = dig.getToken();
                    lexeme = dig.getLexeme();
                    lineNo = dig.getLineNumber();
                    colNo = dig.getColumnNumber();
                    /* Increase columns by size of lexeme */
                    mp.colNumber += lexeme.length();

                    //provides the right amount of white space for nice formatting
                    if (token.equals("MP_FIXED_LIT") || token.equals("MP_FLOAT_LIT")) {
                        whitespace = "   ";
                    } else if (token.equals("MP_INTEGER_LIT")) {
                        whitespace = " ";
                    }
                    //indicates an error token was returned from the FSA, prints
                    //message to the user, so they can go back and fix it to 
                    //avoid unintended consequences
                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND "
                                + "STARTING AT LINE %d, COLUMN %d. RESUMING SCAN "
                                + "AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
                        MPscanner.pbr.read();
                    } else {
                        MPscanner.fWriter.print(token + whitespace);
                        MPscanner.fWriter.print(lineNo + "   ");
                        MPscanner.fWriter.print(colNo + "   ");
                        MPscanner.fWriter.println(lexeme);
                    }
                    break;

                case STR_LIT:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    str.readFile();
                    state = State.START;
                
                    token = str.getToken();
                    lexeme = str.getLexeme();
                    lineNo = str.getLineNumber();
                    colNo = str.getColumnNumber();
                    /* Increase columns by size of lexeme */
                    mp.colNumber += lexeme.length();
                    //indicates an error token was returned from the FSA, prints
                    //message to the user, so they can go back and fix it to 
                    //avoid unintended consequences. Additionally, checks for
                    //a run on string token, prints error if found.
                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND "
                                + "STARTING AT LINE %d, COLUMN %d. RESUMING SCAN "
                                + "AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
                        MPscanner.pbr.read();
                    } else if (token.equals("MP_RUN_STRING")) {
                        System.out.format("\033[31mERROR: RUN ON STRING DETECTED "
                                + "AT LINE %d, COLUMN %d.\n\033[0m", lineNo, colNo);
                    } else {
                        MPscanner.fWriter.print(token + "  ");
                        MPscanner.fWriter.print(lineNo + "   ");
                        MPscanner.fWriter.print(colNo + "   ");
                        MPscanner.fWriter.println(lexeme);
                    }
                    break;

                case COMMENT:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    comm.readFile();
                    state = State.START;

                    token = comm.getToken();
                    lexeme = comm.getLexeme();
                    lineNo = comm.getLineNumber();
                    colNo = comm.getColumnNumber();
                    /* Increase columns by size of lexeme */
                    mp.colNumber += lexeme.length();
                    mp.lineNumber += comment_FSA.returnLineTally;

                    //indicates an error token was returned from the FSA, prints
                    //message to the user, so they can go back and fix it to 
                    //avoid unintended consequences. Additionally, checks for
                    //a run on string token, prints error if found.
                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND "
                                + "STARTING AT LINE %d, "
                                + "COLUMN %d.\n\033[0m", lineNo, colNo);
                        MPscanner.pbr.read();
                    } else if (token.equals("MP_RUN_COMMENT")) {
                        System.out.format("\033[31mERROR: RUN ON COMMENT DETECTED "
                                + "AT LINE %d, COLUMN %d.\n\033[0m", lineNo, colNo);
                    } else {
                        MPscanner.fWriter.print(token + "     ");
                        MPscanner.fWriter.print(lineNo + "   ");
                        MPscanner.fWriter.print(colNo + "   ");
                        MPscanner.fWriter.println(lexeme);
                    }
                    break;

                case SYMBOL:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    symb.readFile();
                    state = State.START;

                    token = symb.getToken();
                    lexeme = symb.getLexeme();
                    lineNo = symb.getLineNumber();
                    colNo = symb.getColumnNumber();
                    /* Increase columns by size of lexeme */
                    mp.colNumber += lexeme.length();

                    if (token.equals("MP_LPAREN") || token.equals("MP_RPAREN") || token.equals("MP_NEQUALS") || token.equals("MP_SCOLON") || token.equals("MP_ASSIGN") || token.equals("MP_GEQUAL") || token.equals("MP_LEQUAL") || token.equals("MP_PERIOD")) {
                        whitespace = "      ";
                    } else if (token.equals("MP_COLON") || token.equals("MP_COMMA") || token.equals("MP_EQUAL") || token.equals("MP_GTHAN") || token.equals("MP_LTHAN") || token.equals("MP_MINUS") || token.equals("MP_TIMES")) {
                        whitespace = "       ";
                    } else if (token.equals("MP_PLUS")) {
                        whitespace = "        ";
                    } else if (token.equals("MP_FLOAT_DIVIDE")) {
                        whitespace = " ";
                    }

                    if (token.equals("MP_ERROR")) {
                        MPscanner.pbr.read();
                        System.out.format("\033[31m ERROR: INVALID TOKEN FOUND "
                                + "STARTING AT LINE %d, COLUMN %d. RESUMING SCAN "
                                + "AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
                    } else {
                        MPscanner.fWriter.print(token + whitespace);
                        MPscanner.fWriter.print(lineNo + "   ");
                        MPscanner.fWriter.print(colNo + "   ");
                        MPscanner.fWriter.println(lexeme);
                    }
                    break;

                case ERROR:
                    token = "MP_ERROR";
                    MPscanner.pbr.read();

                    System.out.format("\033[31mERROR: INVALID CHARACTER AT LINE "
                            + "%d, COLUMN %d. RESUMING SCAN AT "
                            + "NEXT CHARACTER.\n\033[0m", lineNo, colNo);
                    System.out.println();

                    state = State.START;
                    break;

                default:
                    item = MPscanner.getNextToken();
                    if (item == -1) {
                        loop = false;
                    }
                    state = State.START;
                    throw new AssertionError(state.name());
            }
        }
    }
    public static int markCol;
    public static int markLine;
}
