// import ANTLR's runtime libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Driver {
  public static void main(String[] args) throws Exception {
    // create a CharStream that reads from standard input
    CharStream input = CharStreams.fromFileName(args[0]);
    // create a lexer that feeds off of input CharStream
    AlanLexer lexer = new AlanLexer(input);
    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    // create a parser that feeds off the tokens buffer
    AlanParser parser = new AlanParser(tokens);
    ParseTree tree = parser.source(); // begin parsing at init rule

    ParseTreeWalker walker = new ParseTreeWalker();

    // create listener then feed to walker
    AlanPrettyPrintListener prettier = new AlanPrettyPrintListener(40, 2);

    walker.walk(prettier, tree); // walk parse tree

    System.out.println(tree.toStringTree(parser)); // print LISP-style tree
  }
}
