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

    //letter      = case Character.isAlphabetic((int) character)
    //digit       = case Character.isDigit((int) character)
    public static final String MP_AND      = "and";
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
    //MP_EOF      = item for end of file character
    //MP_RUN_COMMENT  item for run on comment error
    //MP_RUN_STRING   item for run on string error
    //MP_ERROR        item for other scan errors
    public static final identifier_FSA ident = new identifier_FSA();
    public static final digit_FSA dig = new digit_FSA();
    public static final string_FSA str = new string_FSA();
    public static final symbol_FSA symb = new symbol_FSA();
    
    public static boolean Dispatcher_quit = false;
    
    public enum State {
        START, IDEN, DIGIT, STR_LIT, SYMBOL
    }
    static State state = State.START;
    
    public static void handleToken(char item) throws IOException{
        while (Dispatcher_quit == false)
        switch (state){
            case START:
                
                
                String ret_idn; //returned identifier
                int    ret_int; //returned integer
                double ret_dbl; //returned double
                float  ret_flt; //returned float
                String ret_str; //returned string
                char   ret_sym; //returned symbol
                
                //send to IDEN_FSA
                if (Character.isAlphabetic((int) item) 
                        || Character.toString(item).equals(MP_UNDERSCORE)) {
                    state = State.IDEN;
                }
                //send to DIGIT_FSA
                else if (Character.isDigit((int) item)
                        || Character.toString(item).equals(MP_PERIOD)) {
                    state = State.DIGIT;
                }
                //send to STR_FSA
                else if (Character.toString(item).equals(MP_QUOTE)) {
                    state = State.STR_LIT;
                }
                //send to SYMBOL_FSA
                else {
                    state = State.SYMBOL;
                }
                break;
            case IDEN:
                ident.readFile(MPscanner.reader, MPscanner.pbr);
                break;
            case DIGIT:
                dig.readFile(MPscanner.reader, MPscanner.pbr);
                break;
            case STR_LIT:
                item = str.readFile();
                state = State.START;
                break;
            case SYMBOL:
                symb.readFile();
                break;
            default:
                Dispatcher_quit = true;
                throw new AssertionError(state.name());
        }
    }
}
