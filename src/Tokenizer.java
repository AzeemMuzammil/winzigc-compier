import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Tokenizer {

    private final String _source;

    private List<TokenData> _tokenDatas = new ArrayList<>();

    private int _char_position;

    private char getChar() {
        return (_char_position >= _source.length()) ? '\0' : _source.charAt(_char_position);
    }

    private boolean isNum(char c) {
        return ('0' <= c && c <= '9') ? true : false;
    }

    private boolean isWhiteSpace(char c) {
        char[] spaceChars = { ' ', '\f', '\r', '\t' };
        for (char spaceChar : spaceChars) {
            if (spaceChar == c) {
                return true;
            }
        }
        return false;
    }

    private TokenData lexComment() {

        Stack<Character> nestedCommentsTrace = new Stack<>();

        int start = _char_position;
        if (getChar() == '{') {
            _char_position++;
            nestedCommentsTrace.push('{');

            while (!nestedCommentsTrace.empty()) {
                switch (getChar()) {
                    case '\n':
                        break;
                    case '{':
                        nestedCommentsTrace.push('{');
                        break;
                    case '}':
                        if (nestedCommentsTrace.peek().equals('{')) {
                            nestedCommentsTrace.pop();
                        } else {
                            System.out.println("MISSING: ->|{|<-");
                            throw new Error();
                        }
                        break;
                }
                _char_position++;
            }

            return new TokenData(Token.COMMENT, start, _source.substring(start, _char_position), "#COMMENT");
        }
        if (getChar() == '#') {
            _char_position++;
            while (getChar() != '\n') {
                _char_position++;
            }
            return new TokenData(Token.COMMENT, start, _source.substring(start, _char_position), "#COMMENT");
        }
        return null;
    }

    private TokenData lexNewLine() {
        int start = _char_position;
        if (getChar() == '\n') {
            _char_position++;
            return new TokenData(Token.NEW_LINE, start, "\n", "NEWLINE");
        }
        return null;
    }

    private TokenData lexWhiteSpace() {
        int start = _char_position;
        if (isWhiteSpace(getChar())) {
            _char_position++;
            while (isWhiteSpace(getChar())) {
                _char_position++;
            }
            return new TokenData(Token.WHITE_SPACE, start, _source.substring(start, _char_position), "WHITESPACE");
        }
        return null;
    }

    private TokenData lexInteger() {
        int start = _char_position;
        if (isNum(getChar())) {
            _char_position++;
            while (isNum(getChar())) {
                _char_position++;
            }
            return new TokenData(Token.TYPE_INT, start, _source.substring(start, _char_position), "<integer>");
        }
        return null;
    }

    private TokenData lexChars() {
        int start = _char_position;
        if (getChar() == '\'' & _source.charAt(_char_position + 2) == '\''
                & _source.charAt(_char_position + 1) != '\'') {
            _char_position += 3;
            return new TokenData(Token.TYPE_CHAR, start, _source.substring(start, _char_position), "<char>");
        }
        return null;
    }

    private TokenData lexStrings() {
        int start = _char_position;
        if (getChar() == '"') {
            _char_position++;
            while (getChar() != '"') {
                _char_position++;
            }
            _char_position++;
            return new TokenData(Token.TYPE_STRING, start, _source.substring(start, _char_position), "<string>");
        }
        return null;
    }

    private TokenData lexIdentifier() {
        String identifierToken = null;
        String identiferChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
        int start = _char_position;

        if (identiferChars.indexOf(getChar()) >= 10) {
            _char_position++;

            while (identiferChars.indexOf(getChar()) >= 0) {
                _char_position++;
            }
            identifierToken = _source.substring(start, _char_position);
        }

        if (identifierToken != null) {
            switch (identifierToken) {
                case "program":
                    return new TokenData(Token.KWORD_PROGRAM, _char_position, identifierToken, "program");
                case "var":
                    return new TokenData(Token.KWORD_VAR, _char_position, identifierToken, "var");
                case "const":
                    return new TokenData(Token.KWORD_CONST, _char_position, identifierToken, "const");
                case "type":
                    return new TokenData(Token.KWORD_TYPE, _char_position, identifierToken, "type");
                case "function":
                    return new TokenData(Token.KWORD_FUNCTION, _char_position, identifierToken, "function");
                case "return":
                    return new TokenData(Token.KWORD_RETURN, _char_position, identifierToken, "return");
                case "begin":
                    return new TokenData(Token.KWORD_BEGIN, _char_position, identifierToken, "begin");
                case "end":
                    return new TokenData(Token.KWORD_END, _char_position, identifierToken, "end");
                case "output":
                    return new TokenData(Token.KWORD_OUTPUT, _char_position, identifierToken, "output");
                case "if":
                    return new TokenData(Token.KWORD_IF, _char_position, identifierToken, "if");
                case "then":
                    return new TokenData(Token.KWORD_THEN, _char_position, identifierToken, "then");
                case "else":
                    return new TokenData(Token.KWORD_ELSE, _char_position, identifierToken, "else");
                case "while":
                    return new TokenData(Token.KWORD_WHILE, _char_position, identifierToken, "while");
                case "do":
                    return new TokenData(Token.KWORD_DO, _char_position, identifierToken, "do");
                case "case":
                    return new TokenData(Token.KWORD_CASE, _char_position, identifierToken, "case");
                case "of":
                    return new TokenData(Token.KWORD_OF, _char_position, identifierToken, "of");
                case "otherwise":
                    return new TokenData(Token.KWORD_OTHERWISE, _char_position, identifierToken, "otherwise");
                case "repeat":
                    return new TokenData(Token.KWORD_REPEAT, _char_position, identifierToken, "repeat");
                case "for":
                    return new TokenData(Token.KWORD_FOR, _char_position, identifierToken, "for");
                case "until":
                    return new TokenData(Token.KWORD_UNTIL, _char_position, identifierToken, "until");
                case "loop":
                    return new TokenData(Token.KWORD_LOOP, _char_position, identifierToken, "loop");
                case "pool":
                    return new TokenData(Token.KWORD_POOL, _char_position, identifierToken, "pool");
                case "exit":
                    return new TokenData(Token.KWORD_EXIT, _char_position, identifierToken, "exit");
                case "mod":
                    return new TokenData(Token.OP_MOD, _char_position, identifierToken, "mod");
                case "or":
                    return new TokenData(Token.OP_OR, _char_position, identifierToken, "or");
                case "and":
                    return new TokenData(Token.OP_AND, _char_position, identifierToken, "and");
                case "not":
                    return new TokenData(Token.OP_NOT, _char_position, identifierToken, "not");
                case "read":
                    return new TokenData(Token.KWORD_READ, _char_position, identifierToken, "read");
                case "succ":
                    return new TokenData(Token.KWORD_SUCC, _char_position, identifierToken, "succ");
                case "pred":
                    return new TokenData(Token.KWORD_PRED, _char_position, identifierToken, "pred");
                case "chr":
                    return new TokenData(Token.KWORD_CHAR, _char_position, identifierToken, "chr");
                case "ord":
                    return new TokenData(Token.KWORD_ORD, _char_position, identifierToken, "ord");
                case "eof":
                    return new TokenData(Token.EOF, _char_position, identifierToken, "eof");
                default:
                    return new TokenData(Token.IDENTIFIER, _char_position, identifierToken, "<identifier>");
            }
        }
        return null;
    }

    private TokenData findNextToken() {

        if (_char_position >= _source.length()) {
            return new TokenData(Token.KWORD_EOP, _char_position, "\0", null);
        }

        // identifier
        TokenData kword = lexIdentifier();
        if (kword != null) {
            return kword;
        }

        // comment
        TokenData comment = lexComment();
        if (comment != null) {
            return comment;
        }

        // new line
        TokenData newLine = lexNewLine();
        if (newLine != null) {
            return newLine;
        }

        // white spaces
        TokenData whiteSpace = lexWhiteSpace();
        if (whiteSpace != null) {
            return whiteSpace;
        }

        // integers
        TokenData integers = lexInteger();
        if (integers != null) {
            return integers;
        }

        // chars
        TokenData chars = lexChars();
        if (chars != null) {
            return chars;
        }

        // strings
        TokenData strings = lexStrings();
        if (strings != null) {
            return strings;
        }

        // swap :=:
        String syntax_len_3 = _source.substring(_char_position, _char_position + 3);
        if (syntax_len_3.equals(":=:")) {
            return new TokenData(Token.KWORD_SWAP, _char_position += 3, syntax_len_3, ":=:");
        }

        // ":=", "..", "<=", "<>", ">="
        String syntax_len_2 = _source.substring(_char_position, _char_position + 2);
        switch (syntax_len_2) {
            case ":=":
                return new TokenData(Token.KWORD_ASSIGN, _char_position += 2, syntax_len_2, ":=");
            case "..":
                return new TokenData(Token.KWORD_CASE_EXP, _char_position += 2, syntax_len_2, "..");
            case "<=":
                return new TokenData(Token.OP_LTEQ, _char_position += 2, syntax_len_2, "<=");
            case "<>":
                return new TokenData(Token.OP_NOTEQ, _char_position += 2, syntax_len_2, "<>");
            case ">=":
                return new TokenData(Token.OP_GTEQ, _char_position += 2, syntax_len_2, ">=");
        }

        Character next = getChar();
        // ":", ".", "<", ">", "=", ";", ",", "(", ")", "+", "-", "*", "/"
        switch (next) {
            case ':':
                return new TokenData(Token.SYM_COLLON, ++_char_position, next.toString(), ":");
            case '.':
                return new TokenData(Token.SYM_DOT, ++_char_position, next.toString(), ".");
            case '<':
                return new TokenData(Token.OP_LT, ++_char_position, next.toString(), "<");
            case '>':
                return new TokenData(Token.OP_GT, ++_char_position, next.toString(), ">");
            case '=':
                return new TokenData(Token.OP_EQ, ++_char_position, next.toString(), "=");
            case ';':
                return new TokenData(Token.SYM_SEMI_COLLON, ++_char_position, next.toString(), ";");
            case ',':
                return new TokenData(Token.SYM_COMMA, ++_char_position, next.toString(), ",");
            case '(':
                return new TokenData(Token.BRACKET_LPARAN, ++_char_position, next.toString(), "(");
            case ')':
                return new TokenData(Token.BRACKET_RPARAN, ++_char_position, next.toString(), ")");
            case '+':
                return new TokenData(Token.OP_PLUS, ++_char_position, next.toString(), "+");
            case '-':
                return new TokenData(Token.OP_MINUS, ++_char_position, next.toString(), "-");
            case '*':
                return new TokenData(Token.OP_MUL, ++_char_position, next.toString(), "*");
            case '/':
                return new TokenData(Token.OP_DIV, ++_char_position, next.toString(), "/");
        }

        return new TokenData(Token.UNKNOWN, _char_position, null, "UNKNOWN_TOKEN");
    }

    public Tokenizer(String source) {
        _source = source;
    }

    public List<TokenData> tokenize() {
        Token token;
        do {
            TokenData tokenData = findNextToken();
            if (tokenData.token != Token.COMMENT & tokenData.token != Token.WHITE_SPACE
                    & tokenData.token != Token.NEW_LINE & tokenData.token != Token.KWORD_EOP) {
                _tokenDatas.add(tokenData);
            }
            token = tokenData.token;
        } while (token != Token.KWORD_EOP);

        return _tokenDatas;
    }
}