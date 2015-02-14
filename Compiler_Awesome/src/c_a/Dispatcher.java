package c_a;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class Dispatcher {

////////////    public enum Token {
////////////
////////////        //NOTES TO MONICA - CHANGE ALL FSA'S SO THEY WORK WITH THESE TOKENS!!!!!!!!!!!!!
////////////        //MAKE SURE EVERYTHING ON TABLE IS REPRESENTED!
////////////        //INCLUDE ENUM IN ENUM FOR RESERVED WORDS? RECHECK SPELLING, SAW SOME ERRORS.
////////////
////////////        MP_AND("and"), MP_BEGIN("begin"), MP_BOOLEAN("Boolean"), MP_DIV("div"),
////////////        MP_DO("do"), MP_DOWNTO("downto"), MP_ELSE("else"), MP_END("end"), MP_FALSE("false"),
////////////        MP_FIXED("fixed"), MP_FLOAT("float"), MP_FOR("for"), MP_FUNCTION("function"),
////////////        MP_IF("if"), MP_INTEGER("integer"), MP_MOD("mod"), MP_NOT("not"), MP_OR("or"),
////////////        MP_PROCEDURE("procedure"), MP_PROGRAM("program"), MP_READ("read"), MP_REPEAT("repeat"),
////////////        MP_STRING("string"), MP_THEN("then"), MP_TRUE("true"), MP_TO("to"), MP_TYPE("type"),
////////////        MP_UNTIL("until"), MP_VAR("var"), MP_WHILE("while"), MP_WRITE("write"), MP_WRITELN("writeln"),
////////////        MP_ASSIGN(":="), MP_COLON(":"), MP_COMMA(","), MP_EQUAL("="), MP_FLOAT_DIVIDE("/"),
////////////        MP_GEQUAL(">="), MP_GTHAN(">"), MP_LEQUAL("<="), MP_LPAREN("("), MP_LTHAN("<"), MP_MINUS("-"),
////////////        MP_NEQUAL("<>"), MP_PERIOD("."), MP_PLUS("+"), MP_RPAREN(")"), MP_SCOLON(";"),
////////////        MP_TIMES("*"), MP_UNDERSCORE("_"), MP_QUOTE("\'");
////////////
////////////        //BELOW ARE NOT ON THE LIST as tokens, we should come up with a better way to handle these!!!
////////////        //MP_UNDERSCORE(), MP_QUOTE();
////////////        private final String val;
////////////
////////////        //constructor for token enum
////////////
////////////        Token(String val) {
////////////            this.val = val;
////////////        }
////////////
////////////        //put get back in later?
////////////        //public String getDesc() {
////////////        //    return desc;
////////////    }
////////////
////////////    Token token;

    public static final String MP_AND    = "and";
    public static final String MP_BEGIN    = "begin";
    public static final String MP_BOOLEAN  = "Boolean";
    public static final String MP_DIV      = "div";
    public static final String MP_DO       = "do";
    public static final String MP_DOWNTO   = "downto";
    public static final String MP_ELSE     = "else";
    public static final String MP_END      = "end";
    public static final String MP_FALSE    = "false";
    public static final String MP_FIXED    = "fixed";
    public static final String MP_FLOAT    = "float";
    public static final String MP_FOR      = "for";
    public static final String MP_FUNTION  = "function";
    public static final String MP_IF       = "if";
    public static final String MP_INTEGER  = "integer";
    public static final String MP_MOD      = "mod";
    public static final String MP_NOT      = "not";
    public static final String MP_OR       = "or";
    public static final String MP_PROCEDURE= "procedure";
    public static final String MP_PROGRAM  = "program";
    public static final String MP_READ     = "read";
    public static final String MP_REPEAT   = "repeat";
    public static final String MP_STRING   = "string";
    public static final String MP_THEN     = "then";
    public static final String MP_TRUE     = "true";
    public static final String MP_TO       = "to";
    public static final String MP_TYPE     = "type";
    public static final String MP_UNTIL    = "until";
    public static final String MP_VAR      = "var";
    public static final String MP_WHILE    = "while";
    public static final String MP_WRITE    = "write";
    public static final String MP_WRITELN  = "writeln";
    //String MP_IDENTIFIER= (letter | _ (letter | digit)){[_](letter | digit)}
    //String MP_INTEGER_LIT= digit{digit}
    //String MP_FIXED_LIT = digit{digit} . digit{digit}
    //String MP_FLOAT_LIT = (digit{digit} | digit{digit} . digit{digit}) (e | E)
    //String MP_STRING_LIT = ' { ' | Any Character Except Apostrophe or EOL} '
    public static final String MP_ASSIGN   = ":=";
    public static final String MP_COLON    = ":";
    public static final String MP_COMMA    = ",";
    public static final String MP_EQUAL    = "=";
    public static final String MP_FLOAT_DIVIDE = "/";
    public static final String MP_GEQUAL   = ">=";
    public static final String MP_GTHAN    = ">";
    public static final String MP_LEQUAL   = "<=";
    public static final String MP_LPAREN   = "(";
    public static final String MP_LTHAN    = "<";
    public static final String MP_MINUS    = "-";
    public static final String MP_NEQUAL   = "<>";
    public static final String MP_PERIOD   = ".";
    public static final String MP_PLUS     = "+";
    public static final String MP_RPAREN   = ")";
    public static final String MP_SCOLON   = ";";
    public static final String MP_TIMES    = "*";
    public static final String MP_UNDERSCORE = "_";
    public static final String MP_QUOTE    = "\'";
    public static final String MP_SQUIGGLE = "{";
    //MP_EOF      = item for end of file character
    //MP_RUN_COMMENT  item for run on comment error
    //MP_RUN_STRING   item for run on string error
    //MP_ERROR        item for other scan errors
//    public static final String MP_AND      = "and";
//    public static final String MP_BEGIN    = "begin";
//    public static final String MP_BOOLEAN  = "Boolean";
//    public static final String MP_DIV      = "div";
//    public static final String MP_DO       = "do";
//    public static final String MP_DOWNTO   = "downto";
//    public static final String MP_ELSE     = "else";
//    public static final String MP_END      = "end";
//    public static final String MP_FALSE    = "false";
//    public static final String MP_FIXED    = "fixed";
//    public static final String MP_FLOAT    = "float";
//    public static final String MP_FOR      = "for";
//    public static final String MP_FUNTION  = "function";
//    public static final String MP_IF       = "if";
//    public static final String MP_INTEGER  = "integer";
//    public static final String MP_MOD      = "mod";
//    public static final String MP_NOT      = "not";
//    public static final String MP_OR       = "or";
//    public static final String MP_PROCEDURE= "procedure";
//    public static final String MP_PROGRAM  = "program";
//    public static final String MP_READ     = "read";
//    public static final String MP_REPEAT   = "repeat";
//    public static final String MP_STRING   = "string";
//    public static final String MP_THEN     = "then";
//    public static final String MP_TRUE     = "true";
//    public static final String MP_TO       = "to";
//    public static final String MP_TYPE     = "type";
//    public static final String MP_UNTIL    = "until";
//    public static final String MP_VAR      = "var";
//    public static final String MP_WHILE    = "while";
//    public static final String MP_WRITE    = "write";
//    public static final String MP_WRITELN  = "writeln";
//    //String MP_IDENTIFIER= (letter | _ (letter | digit)){[_](letter | digit)}
//    //String MP_INTEGER_LIT= digit{digit}
//    //String MP_FIXED_LIT = digit{digit} . digit{digit}
//    //String MP_FLOAT_LIT = (digit{digit} | digit{digit} . digit{digit}) (e | E)
//    //String MP_STRING_LIT = ' { ' | Any Character Except Apostrophe or EOL} '
//    public static final String MP_ASSIGN   = ":=";
//    public static final String MP_COLON    = ":";
//    public static final String MP_COMMA    = ",";
//    public static final String MP_EQUAL    = "=";
//    public static final String MP_FLOAT_DIVIDE = "/";
//    public static final String MP_GEQUAL   = ">=";
//    public static final String MP_GTHAN    = ">";
//    public static final String MP_LEQUAL   = "<=";
//    public static final String MP_LPAREN   = "(";
//    public static final String MP_LTHAN    = "<";
//    public static final String MP_MINUS    = "-";
//    public static final String MP_NEQUAL   = "<>";
//    public static final String MP_PERIOD   = ".";
//    public static final String MP_PLUS     = "+";
//    public static final String MP_RPAREN   = ")";
//    public static final String MP_SCOLON   = ";";
//    public static final String MP_TIMES    = "*";
//    public static final String MP_UNDERSCORE = "_";
//    public static final String MP_QUOTE    = "\'";
    //MP_EOF      = item for end of file character
    //MP_RUN_COMMENT  item for run on comment error
    //MP_RUN_STRING   item for run on string error
    //MP_ERROR        item for other scan errors

    //make these enums as well?
    public static final identifier_FSA ident = new identifier_FSA();
    public static final digit_FSA dig = new digit_FSA();
    public static final string_FSA str = new string_FSA();
    public static final symbol_FSA symb = new symbol_FSA();
    public static final comment_FSA comm = new comment_FSA();

    public static boolean loop = true;

    public enum State {

        START, IDEN, DIGIT, STR_LIT, SYMBOL, COMMENT
    }
    private static State state = State.START;

    public static void handleToken(char item) throws IOException {
        while (loop == true) {
            if (item == 10) {
                System.out.println("New line ------------------------------"+mp.lineNumber);
            }
            switch (state) {
                case START:
                    //send to IDEN_FSA
                    if (Character.isAlphabetic((int) item)
                            || Character.toString(item).equals(MP_UNDERSCORE)) {
                        state = State.IDEN;
                    } //send to DIGIT_FSA
                    else if (Character.isDigit((int) item)) {
                        state = State.DIGIT;
                    } //send to STR_FSA
                    else if (Character.toString(item).equals(MP_QUOTE)) {
                        state = State.STR_LIT;
                    } //send to SYMBOL_FSA
                    else if (Character.toString(item).equals(MP_SQUIGGLE)){
                        state = State.COMMENT;
                    }
                    else {
                        state = State.SYMBOL;
                    }
                    break;
                case IDEN:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    ident.readFile();
                    item = MPscanner.getToken();
                    state = State.START;
                    break;
                case DIGIT:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    dig.readFile();
                    item = MPscanner.getToken();
                    state = State.START;
                    break;
                case STR_LIT:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    str.readFile();
                    item = MPscanner.getToken();
                    state = State.START;
                    break;
                case COMMENT:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    comm.readFile();
                    item = MPscanner.getToken();
                    state = State.START;
                    break;
                case SYMBOL:
                    markLine = mp.lineNumber;
                    markCol = mp.colNumber;
                    symb.readFile();
                    item = MPscanner.getToken();
                    state = State.START;
                    break;
                default:
                    item = MPscanner.getToken();
                    if (item == -1) {
                        loop = false;
                    }
                    state = state.START;
                    throw new AssertionError(state.name());
            }
        }
    }
    public static int markCol;
    public static int markLine;
}
