package c_a;

import java.io.IOException;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class Dispatcher {

//    public static final String MP_AND = "and";
//    public static final String MP_BEGIN = "begin";
//    public static final String MP_BOOLEAN = "Boolean";
//    public static final String MP_DIV = "div";
//    public static final String MP_DO = "do";
//    public static final String MP_DOWNTO = "downto";
//    public static final String MP_ELSE = "else";
//    public static final String MP_END = "end";
//    public static final String MP_FALSE = "false";
//    public static final String MP_FIXED = "fixed";
//    public static final String MP_FLOAT = "float";
//    public static final String MP_FOR = "for";
//    public static final String MP_FUNCTION = "function";
//    public static final String MP_IF = "if";
//    public static final String MP_INTEGER = "integer";
//    public static final String MP_MOD = "mod";
//    public static final String MP_NOT = "not";
//    public static final String MP_OR = "or";
//    public static final String MP_PROCEDURE = "procedure";
//    public static final String MP_PROGRAM = "program";
//    public static final String MP_READ = "read";
//    public static final String MP_REPEAT = "repeat";
//    public static final String MP_STRING = "string";
//    public static final String MP_THEN = "then";
//    public static final String MP_TRUE = "true";
//    public static final String MP_TO = "to";
//    public static final String MP_TYPE = "type";
//    public static final String MP_UNTIL = "until";
//    public static final String MP_VAR = "var";
//    public static final String MP_WHILE = "while";
//    public static final String MP_WRITE = "write";
//    public static final String MP_WRITELN = "writeln";
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

    protected final static identifier_FSA ident = new identifier_FSA();
    protected final static digit_FSA dig = new digit_FSA();
    protected final static string_FSA str = new string_FSA();
    protected final static symbol_FSA symb = new symbol_FSA();
    protected final static comment_FSA comm = new comment_FSA();

    public static boolean loop = true;
    public static String token = "";
    public static String lexeme = "";
    public static int lineNo;
    public static int colNo;

    public static String whitespace = "";

    public enum State {

        START, IDEN, DIGIT, STR_LIT, QUOTE, SYMBOL, COMMENT, ERROR
    }
    private static State state = State.START;

    public static void handleToken(char item) throws IOException {
        boolean runOnQuote = false;
        //System.out.println("\nScanner Output\n");

        while (loop == true) {
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

                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND STARTING AT LINE %d, COLUMN %d. RESUMING SCAN AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
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

                    if (token.equals("MP_FIXED_LIT") || token.equals("MP_FLOAT_LIT")) {
                        whitespace = "   ";
                    } else if (token.equals("MP_INTEGER_LIT")) {
                        whitespace = " ";
                    }

                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND STARTING AT LINE %d, COLUMN %d. RESUMING SCAN AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
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

                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND STARTING AT LINE %d, COLUMN %d. RESUMING SCAN AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
                        MPscanner.pbr.read();
                    } else if (token.equals("MP_RUN_STRING")) {
                   System.out.format("\033[31mERROR: RUN ON STRING DETECTED AT LINE %d, COLUMN %d.\n\033[0m", lineNo, colNo);      
                    }else {
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

                    if (token.equals("MP_ERROR")) {
                        System.out.format("\033[31mERROR: INVALID TOKEN FOUND STARTING AT LINE %d, COLUMN %d.\n\033[0m", lineNo, colNo);
                        MPscanner.pbr.read();
                    } else if (token.equals("MP_RUN_COMMENT")) {
                        System.out.format("\033[31mERROR: RUN ON COMMENT DETECTED AT LINE %d, COLUMN %d.\n\033[0m", lineNo, colNo);
                    } else {
                        MPscanner.fWriter.print(token + "     ");
                        MPscanner.fWriter.print(lineNo + "   ");
                        MPscanner.fWriter.print(colNo + "   ");
                        MPscanner.fWriter.println(lexeme);
                    }
                    break;

                case SYMBOL:
//                    System.out.println("LOOKING AT THIS -----------------> " + item);
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
                        System.out.format("\033[31m ERROR: INVALID TOKEN FOUND STARTING AT LINE %d, COLUMN %d. RESUMING SCAN AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
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

                    System.out.format("\033[31mERROR: INVALID CHARACTER AT LINE %d, COLUMN %d. RESUMING SCAN AT NEXT CHARACTER.\n\033[0m", lineNo, colNo);
                    System.out.println();

                    //item = MPscanner.getNextToken();
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
