//  compile     : make
//  run         : java winzigc –ast winzig_test_programs/winzig_01 > tree.01
//  test        : diff tree.01 winzig_test_programs/winzig_01.tree

// Or can compile and test all using the script tests.sh

import java.io.*;
import java.util.List;

public class winzigc {

    public static void main(String[] args) {
        String flag = args[0];
        switch (flag) {
            case "-ast":
                String sourcePath = args[1];

                String source = null;
                try {
                    source = readSource(sourcePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Tokenizer tokenizer = new Tokenizer(source);
                List<TokenData> tokens = tokenizer.tokenize();

                Parser parser = new Parser(tokens);
                parser.generateParseTree();

                break;
            default:
                System.out.println("The supported arg is -ast.");
        }
    }

    private static String readSource(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString() + "   ";
        } finally {
            reader.close();
        }
    }
}
