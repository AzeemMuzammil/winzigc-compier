public class TokenData {
    Token token;
    String type;
    String literal;
    int Index;

    public TokenData(Token token, int Index, String literal, String type) {
        this.token = token;
        this.type = type;
        this.literal = literal;
        this.Index = Index;
    }
}