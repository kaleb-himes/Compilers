package c_a;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class Token {

    public enum TokenNames {

        // Symbols
        MP_EQUAL, MP_PLUS, MP_MINUS, MP_TIMES, MP_FP_DIV,
        MP_LPAREN, MP_RPAREN, MP_LEQUAL, MP_NEQUAL, MP_LTHAN,
        MP_GEQUAL, MP_GTHAN, MP_PERIOD, MP_COMMA, MP_SCOLON,
        MP_ASSIGN, MP_COLON,
        // Error
        MP_ERROR
    }

    private TokenNames token;
    private int line;
    private int column;
    private String lexeme;

    Token(TokenNames inputToken, int inputLine, int inputColumn, String inputLexeme) {
        token = inputToken;
        line = inputLine;
        column = inputColumn;
        lexeme = inputLexeme;
    }

    public TokenNames getToken() {
        return token;
    }

    public int getLineNumber() {
        return line;
    }

    public int getColumnNumber() {
        return column;
    }

    public String getLexeme() {
        return lexeme;
    }
}
