import java.util.List;

public class Parser {

    private TreeNode rootNode;

    private List<TokenData> tokens;

    private int index;

    private TokenData nextToken;

    public Parser(List<TokenData> tokens) {
        this.tokens = tokens;
    }

    public void generateParseTree() {
        getNextToken();
        Winzig();
    }

    private void getNextToken() {
        nextToken = tokens.get(index);
        index++;
    }

    private String peek() {
        if (index <= tokens.size() - 1) {
            return tokens.get(index).type;
        }
        System.out.println("An Error Occured: No More Tokens Available");
        throw new Error();
    }

    private void Winzig() {
        rootNode = new TreeNode("program");

        read("program");
        Name(rootNode);
        read(":");
        Consts(rootNode);
        Types(rootNode);
        Dclns(rootNode);
        SubProgs(rootNode);
        Body(rootNode);
        Name(rootNode);
        read(".");

        rootNode.Traverse(0);
    }

    private void Name(TreeNode parent) {
        read(Token.IDENTIFIER, parent);
    }

    private void Consts(TreeNode parent) {
        if (nextToken.type == "const") {
            TreeNode constsNode = addTreeNode(parent, "consts");
            read("const");
            Const(constsNode);
            while (nextToken.type != ";") {
                read(",");
                Const(constsNode);
            }
            read(";");
        } else {
            addTreeNode(parent, "consts");
        }
    }

    private void Const(TreeNode parent) {
        Name(parent);
        read("=");
        ConstValue(parent);
    }

    private void ConstValue(TreeNode parent) {
        if (nextToken.type.equals("<char>") || nextToken.type.equals("<integer>")) {
            read(nextToken.token, parent);
        }

        if (nextToken.type == "<identifier>") {
            Name(parent);
        }
    }

    private void Types(TreeNode parent) {
        if (nextToken.type == "type") {
            TreeNode typesNode = addTreeNode(parent, "types");
            read("type");

            while (nextToken.type == "<identifier>") {
                Type(typesNode);
                read(";");
            }
        } else {
            addTreeNode(parent, "types");
        }
    }

    private void Type(TreeNode parent) {
        TreeNode typeNode = new TreeNode("type");
        parent.addChildNode(typeNode);

        if (nextToken.type == "<identifier>") {
            read(Token.IDENTIFIER, typeNode);
            read("=");
            LitList(typeNode);
        }
    }

    private void LitList(TreeNode parent) {
        TreeNode litNode = new TreeNode("lit");
        parent.addChildNode(litNode);

        read("(");
        Name(litNode);
        while (nextToken.type != ")") {
            read(",");
            Name(litNode);
        }
        read(")");
    }

    private void Dclns(TreeNode parent) {
        if (nextToken.type == "var") {
            TreeNode dclnsNode = addTreeNode(parent, "dclns");
            read("var");
            Dcln(dclnsNode);
            read(";");
            while (nextToken.type == "<identifier>") {
                Dcln(dclnsNode);
                read(";");
            }
        } else {
            addTreeNode(parent, "dclns");
        }
    }

    private void Dcln(TreeNode parent) {
        TreeNode varNode = addTreeNode(parent, "var");
        Name(varNode);
        while (nextToken.type != ":") {
            read(",");
            Name(varNode);
        }
        read(":");
        Name(varNode);
    }

    private void SubProgs(TreeNode parent) {
        TreeNode subprogsNode = addTreeNode(parent, "subprogs");
        while (nextToken.token == Token.KWORD_FUNCTION) {
            Fcn(subprogsNode);
        }
    }

    private void Fcn(TreeNode parent) {
        TreeNode fcnNode = addTreeNode(parent, "fcn");
        read("function");
        Name(fcnNode);
        read("(");
        Params(fcnNode);
        read(")");
        read(":");
        Name(fcnNode);
        read(";");
        Consts(fcnNode);
        Types(fcnNode);
        Dclns(fcnNode);
        Body(fcnNode);
        Name(fcnNode);
        read(";");
    }

    private void Params(TreeNode parent) {
        TreeNode paramsNode = addTreeNode(parent, "params");
        Dcln(paramsNode);
        while (nextToken.type == ";") {
            read(";");
            Dcln(paramsNode);
        }
    }

    private void Body(TreeNode parent) {
        TreeNode blockNode = addTreeNode(parent, "block");
        read("begin");
        Statement(blockNode);
        while (nextToken.type == ";") {
            read(";");
            Statement(blockNode);
        }
        read("end");

    }

    private void Statement(TreeNode parent) {

        switch (nextToken.type) {
            case "if":
                TreeNode ifNode = addTreeNode(parent, "if");
                read("if");
                Expression(ifNode);
                read("then");
                Statement(ifNode);
                if (nextToken.type == "else") {
                    read("else");
                    Statement(ifNode);
                }
                break;
            case "for":
                TreeNode forNode = addTreeNode(parent, "for");
                read("for");
                read("(");
                ForStat(forNode);
                read(";");
                ForExp(forNode);
                read(";");
                ForStat(forNode);
                read(")");
                Statement(forNode);
                break;
            case "while":
                TreeNode whileNode = addTreeNode(parent, "while");
                read("while");
                Expression(whileNode);
                read("do");
                Statement(whileNode);
                break;
            case "repeat":
                TreeNode repeatNode = addTreeNode(parent, "repeat");
                read("repeat");
                Statement(repeatNode);
                while (nextToken.type == ";") {
                    read(";");
                    Statement(repeatNode);
                }
                read("until");
                Expression(repeatNode);
                break;
            case "loop":
                TreeNode loopNode = addTreeNode(parent, "loop");
                read("loop");
                Statement(loopNode);
                while (nextToken.type == ";") {
                    read(";");
                    Statement(loopNode);
                }
                read("pool");
                break;
            case "output":
                TreeNode outputNode = addTreeNode(parent, "output");
                read("output");
                read("(");
                OutEXp(outputNode);
                while (nextToken.type == ",") {
                    read(",");
                    OutEXp(outputNode);
                }
                read(")");
                break;
            case "exit":
                addTreeNode(parent, "exit");
                read("exit");
                break;
            case "return":
                TreeNode returnNode = addTreeNode(parent, "return");
                read("return");
                Expression(returnNode);
                break;
            case "read":
                TreeNode readNode = addTreeNode(parent, "read");
                read("read");
                read("(");
                Name(readNode);
                while (nextToken.type == ",") {
                    read(",");
                    Name(readNode);
                }
                read(")");
                break;
            case "case":
                TreeNode caseNode = addTreeNode(parent, "case");
                read("case");
                Expression(caseNode);
                read("of");
                Caseclauses(caseNode);
                OtherwiseClause(caseNode);
                read("end");
                break;
            case "<identifier>":
                Assignment(parent);
                break;
            case "begin":
                Body(parent);
                break;
            default:
                addTreeNode(parent, "<null>");
                break;
        }
    }

    private void Caseclauses(TreeNode parent) {
        Caseclause(parent);
        read(";");
        while (nextToken.type == "<integer>" || nextToken.type == "<char>" || nextToken.type == "<identifier>") {
            Caseclause(parent);
            read(";");
        }
    }

    private void Caseclause(TreeNode parent) {
        TreeNode case_clauseNode = addTreeNode(parent, "case_clause");
        CaseExpression(case_clauseNode);
        while (nextToken.type == ",") {
            read(",");
            CaseExpression(case_clauseNode);
        }
        read(":");
        Statement(case_clauseNode);
    }

    private void CaseExpression(TreeNode parent) {
        ConstValue(parent);
        if (nextToken.type == "..") {
            TreeNode doubleDot = addTreeNode(parent, "..");
            read("..");
            doubleDot.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
            ConstValue(doubleDot);
        }
    }

    private void OtherwiseClause(TreeNode parent) {
        if (nextToken.type == "otherwise") {
            TreeNode otherwiseNode = addTreeNode(parent, "otherwise");
            read("otherwise");
            Statement(otherwiseNode);
        } else {

        }
    }

    private void OutEXp(TreeNode parent) {
        if (nextToken.type == "<string>") {
            StringNode(parent);
        } else {
            TreeNode integerNode = addTreeNode(parent, "integer");
            Expression(integerNode);
        }
    }

    private void StringNode(TreeNode parent) {
        read(Token.TYPE_STRING, parent);
    }

    private void ForStat(TreeNode parent) {
        if (nextToken.type == ";") {
            addTreeNode(parent, "<null>");
        } else {
            Assignment(parent);
        }
    }

    private void ForExp(TreeNode parent) {
        if (nextToken.type == ";") {
            addTreeNode(parent, "true");
        } else {
            Expression(parent);
        }
    }

    private void Assignment(TreeNode parent) {

        switch (peek()) {
            case ":=":
                TreeNode assignNode = addTreeNode(parent, "assign");
                Name(assignNode);
                read(":=");
                Expression(assignNode);
                break;
            case ":=:":
                TreeNode swapNode = addTreeNode(parent, "swap");
                Name(swapNode);
                read(":=:");
                Name(swapNode);
                break;
            default:
                System.out.println("An Error Occured While Peek : " + peek());
                System.out.println("An Error Occured While Next: " + nextToken.type);
                throw new Error();
        }

    }

    private void Expression(TreeNode parent) {
        Term(parent);
        if (nextToken.type == "<=" || nextToken.type == "<" || nextToken.type == ">=" || nextToken.type == ">"
                || nextToken.type == "=" || nextToken.type == "<>") {
            switch (nextToken.type) {
                case "<=":
                    read("<=");
                    TreeNode lessOrEqNode = addTreeNode(parent, "<=");
                    lessOrEqNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Term(lessOrEqNode);
                    break;
                case "<":
                    read("<");
                    TreeNode lessNode = addTreeNode(parent, "<");
                    lessNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Term(lessNode);
                    break;
                case ">=":
                    read(">=");
                    TreeNode greaterOrEqNode = addTreeNode(parent, ">=");
                    greaterOrEqNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Term(greaterOrEqNode);
                    break;
                case ">":
                    read(">");
                    TreeNode greaterNode = addTreeNode(parent, ">");
                    greaterNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Term(greaterNode);
                    break;
                case "=":
                    read("=");
                    TreeNode equalNode = addTreeNode(parent, "=");
                    equalNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Term(equalNode);
                    break;
                case "<>":
                    read("<>");
                    TreeNode InequalNode = addTreeNode(parent, "<>");
                    InequalNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Term(InequalNode);
                    break;
                default:
                    System.out.println("There is error(s) in expression");
                    System.out.println("The token was: " + nextToken.type);
                    rootNode.Traverse(0);
                    throw new Error();
            }
        }
    }

    private void Term(TreeNode parent) {
        Factor(parent);
        while (nextToken.type == "+" || nextToken.type == "-" || nextToken.type == "or") {
            switch (nextToken.type) {
                case "+":
                    read("+");
                    TreeNode addNode = addTreeNode(parent, "+");
                    addNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Factor(addNode);
                    break;
                case "-":
                    read("-");
                    TreeNode minusNode = addTreeNode(parent, "-");
                    minusNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Factor(minusNode);
                    break;
                case "or":
                    read("or");
                    TreeNode orNode = addTreeNode(parent, "or");
                    orNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Factor(orNode);
                    break;
                default:
                    System.out.println("An Error Occured In Term");
                    throw new Error();
            }
        }
    }

    private void Factor(TreeNode parent) {
        Primary(parent);
        while (nextToken.type == "*" || nextToken.type == "/" || nextToken.type == "and" || nextToken.type == "mod") {
            switch (nextToken.type) {
                case "*":
                    read("*");
                    TreeNode mulNode = addTreeNode(parent, "*");
                    mulNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Factor(mulNode);
                    break;
                case "/":
                    read("/");
                    TreeNode divNode = addTreeNode(parent, "/");
                    divNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Factor(divNode);
                    break;
                case "and":
                    read("and");
                    TreeNode andNode = addTreeNode(parent, "and");
                    andNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Factor(andNode);
                    break;
                case "mod":
                    read("mod");
                    TreeNode modNode = addTreeNode(parent, "mod");
                    modNode.addChildAtIndex(0, parent.deleteTreeNode(parent.getChildNodesCount() - 2));
                    Factor(modNode);
                    break;
            }
        }
    }

    private void Primary(TreeNode parent) {

        switch (nextToken.type) {
            case "<char>":
                read(Token.TYPE_CHAR, parent);
                break;
            case "<integer>":
                read(Token.TYPE_INT, parent);
                break;
            case "eof":
                addTreeNode(parent, "eof");
                read("eof");
                break;
            case "-":
                read("-");
                TreeNode minusNode = addTreeNode(parent, "-");
                Primary(minusNode);
                break;
            case "+":
                read("+");
                TreeNode plusNode = addTreeNode(parent, "+");
                Primary(plusNode);
                break;
            case "not":
                read("not");
                TreeNode notNode = addTreeNode(parent, "not");
                Primary(notNode);
                break;
            case "(":
                read("(");
                Expression(parent);
                read(")");
                break;
            case "succ":
                read("succ");
                read("(");
                TreeNode succNode = addTreeNode(parent, "succ");
                Expression(succNode);
                read(")");
                break;
            case "pred":
                read("pred");
                read("(");
                TreeNode predNode = addTreeNode(parent, "pred");
                Expression(predNode);
                read(")");
                break;
            case "chr":
                read("chr");
                read("(");
                TreeNode chrNode = addTreeNode(parent, "chr");
                Expression(chrNode);
                read(")");
                break;
            case "ord":
                read("ord");
                read("(");
                TreeNode ordNode = addTreeNode(parent, "ord");
                Expression(ordNode);
                read(")");
                break;
            case "<identifier>":
                if (peek() == "(") {
                    TreeNode callNode = addTreeNode(parent, "call");
                    Name(callNode);
                    read("(");
                    Expression(callNode);
                    while (nextToken.type == ",") {
                        read(",");
                        Expression(callNode);
                    }
                    read(")");
                } else {
                    Name(parent);
                }
                break;
            default:
                System.out.println("An Error Occured While Parsing: " + nextToken.type);
                throw new Error();
        }

    }

    // add the new node to parent node
    private TreeNode addTreeNode(TreeNode parent, String node_label) {
        TreeNode node = new TreeNode(node_label);
        parent.addChildNode(node);
        return node;
    }

    private void read(String type) {
        if (nextToken.type != type) {
            System.out.println("The Expected Type Is : ->|" + type + "|<-");
            System.out.println("And The Found Type Is: " + nextToken.type + " " + nextToken.literal);
            throw new Error();
        }

        if (index <= tokens.size() - 1) {
            getNextToken();
        }
    }

    private void read(Token token, TreeNode parent) {

        if (nextToken.token != token) {
            System.out.println("The Expected Token Is : " + token);
            System.out.println("And The Found Token Is : " + nextToken.token + " " + nextToken.literal);
            rootNode.Traverse(0);
            throw new Error();
        }

        TreeNode node_1 = new TreeNode(nextToken.type);
        parent.addChildNode(node_1);

        TreeNode node_2 = new TreeNode(nextToken.literal);
        node_1.addChildNode(node_2);

        getNextToken();

    }
}
