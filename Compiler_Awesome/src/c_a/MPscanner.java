package c_a;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @title c_a = compiler_awesome
 * @version 0.1
 * @author monica, tabetha, kaleb
 * @team ∀wesome
 */
public class MPscanner extends C_A {

    private int lineNumber = 1;
    private int columnNumber = 1;
    private int startLine;
    private int startColumn;
    private String file;
    private BufferedReader reader;
    private Dispatcher token;
    private List<String> line = new ArrayList<String>();
    private StringBuilder lexeme = new StringBuilder();
    private int checkedLine;
    private int checkedColumn;
    private String checkedLexeme = "";

    MPscanner() {

    }

    public Dispatcher getToken() {
        lexeme = new StringBuilder();
        startLine = lineNumber;
        startColumn = columnNumber;
        char ch = getNextCharacter();
        if (ch == '=') {
            return returnToken(Dispatcher.TokenNames.MP_EQUAL);
        } else if (ch == '+') {
            return returnToken(Dispatcher.TokenNames.MP_PLUS);
        } else if (ch == '-') {
            return returnToken(Dispatcher.TokenNames.MP_MINUS);
        } else if (ch == '*') {
            return returnToken(Dispatcher.TokenNames.MP_TIMES);
        } else if (ch == '/') {
            return returnToken(Dispatcher.TokenNames.MP_FP_DIV);
        } else if (ch == '(') {
            return returnToken(Dispatcher.TokenNames.MP_LPAREN);
        } else if (ch == ')') {
            return returnToken(Dispatcher.TokenNames.MP_RPAREN);
        } else if (ch == '<') {
            checkBuffer();
            ch = getNextCharacter();
            if (ch == '=') {
                return returnToken(Dispatcher.TokenNames.MP_LEQUAL);
            } else if (ch == '>') {
                return returnToken(Dispatcher.TokenNames.MP_NEQUAL);
            } else {
                resetBuffer();
                return returnToken(Dispatcher.TokenNames.MP_LTHAN);
            }
        } else if (ch == '>') {
            checkBuffer();
            ch = getNextCharacter();
            if (ch == '=') {
                return returnToken(Dispatcher.TokenNames.MP_GEQUAL);
            } else {
                resetBuffer();
                return returnToken(Dispatcher.TokenNames.MP_GTHAN);
            }
        } else if (ch == '.') {
            return returnToken(Dispatcher.TokenNames.MP_PERIOD);
        } else if (ch == ',') {
            return returnToken(Dispatcher.TokenNames.MP_COMMA);
        } else if (ch == ';') {
            return returnToken(Dispatcher.TokenNames.MP_SCOLON);
        } else if (ch == ':') {
            checkBuffer();
            ch = getNextCharacter();
            if (ch == '=') {
                return returnToken(Dispatcher.TokenNames.MP_ASSIGN);
            } else {
                resetBuffer();
                return returnToken(Dispatcher.TokenNames.MP_COLON);
            }
        } else {
            return returnToken(Dispatcher.TokenNames.MP_ERROR);
        }
    }

    private char getNextCharacter() {
        // get next character
        return 0;
    }

    public String getError(String inputFile, int inputLine, int inputColumn, String inputErrorName) {
        String error = "  File \"" + inputFile + "\", line " + inputLine + ":\n";
        error += "    " + getLine(inputLine) + "\n";
        error += String.format("    %1$" + (inputColumn + 1) + "s", "^\n");
        error += inputErrorName;
        return error;
    }

    public String getError(Dispatcher inputToken, String inputError) {
        return getError(file, inputToken.getLineNumber(), inputToken.getColumnNumber(), inputError);
    }

    private String getLine(int inputLine) {
        if (inputLine > line.size() - 1) {
            return "<The end of the file has been reached>";
        } else {
            return line.get(inputLine);
        }
    }

    public int getLineNumber() {
        if (token == null) {
            return 0;
        } else {
            return token.getLineNumber();
        }
    }

    public int getColumnNumber() {
        if (token == null) {
            return 0;
        } else {
            return token.getColumnNumber();
        }
    }

    public String getLexeme() {
        if (token == null) {
            return "";
        } else {
            return token.getLexeme();
        }
    }

    private void checkBuffer() {
        checkedLexeme = lexeme.toString();
        checkedLine = lineNumber;
        checkedColumn = columnNumber;
        try {
            reader.mark(512);
        } catch (IOException e) {
            System.err.println("Error: Failed to check reader");
            System.exit(1);
        }
    }

    private void resetBuffer() {
        lineNumber = checkedLine;
        columnNumber = checkedColumn;
        lexeme = new StringBuilder(checkedLexeme);

        try {
            reader.reset();
        } catch (IOException e) {
            System.err.println("Error: Failed to reset buffer");
            System.exit(1);
        }
    }

    private Dispatcher returnToken(Dispatcher.TokenNames inputTokenNames) {
        token = new Dispatcher(inputTokenNames, startLine, startColumn, lexeme.toString());
        return token;
    }
}
