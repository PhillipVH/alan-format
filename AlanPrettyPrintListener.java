import java.io.PrintWriter;

import de.uka.ilkd.pp.*;

public class AlanPrettyPrintListener extends AlanBaseListener {

  private final Layouter<NoExceptions> layouter;
  private final StringBackend backend;

  private final StringBuilder buffer;

  /* I'm going to love myself for this at the end of this project. */
  private final Layouter<NoExceptions> l;

  public AlanPrettyPrintListener(int lineWidth, int tabStop) {
    buffer = new StringBuilder();

    
    backend = new StringBackend(buffer, 20);
    layouter = new Layouter<>(backend, 2);

    l = layouter;
  }

  @Override
  // source_ : 'source' ID funcdef* body;
  public void enterSource_(AlanParser.Source_Context ctx) {
    String id = ctx.ID().getText();

    // 'source' ID
    l.beginC(2)
     .print("source")
     .brk(1, 0)
     .print(id)
     .brk(1, 0)
     .nl()
     .end();

    // funcdef*
    ctx.funcdef().forEach(funcdef -> enterFuncdef(funcdef));

    // body
    enterBody(ctx.body());

    /* Print out result. */
    System.out.println(backend.getString());
    l.close();
    System.exit(0);
  }

  @Override
  // body : 'begin' vardecl* statements 'end';
  public void enterBody(AlanParser.BodyContext ctx) {
  }

  @Override
  // type : ('boolean' | 'integer') 'array'?;
  public void enterType(AlanParser.TypeContext ctx) {

    String text = ctx.getText();

    // ('boolean' | 'integer')
    l.beginC();

    if (text.startsWith("b")) {
      l.print("boolean");
    } else {
      l.print("integer");
    }

    l.brk(1, 0);

    // 'array'?
    if (text.endsWith("y")) {
      l.print("array")
       .brk(1, 0);
    }

    l.end();
  }

  @Override
  // funcdef : 'function' ID '(' (type ID (',' type ID)*)? ')' ('to' type)? body;

  // Type 1: 1 x ID
  // Type 2: 1 + n x ID, n x type
  // Type 3: 1 x ID, 1 x type
  // Type 4: 1 + n x ID, n + 1 type
  public void enterFuncdef(AlanParser.FuncdefContext ctx) {
    String funcName = ctx.ID(0).getText();

    int nID = ctx.ID().size();
    int nType = ctx.type().size();

    /* Calculate the type of the funcdef. */
    /* TODO: This will be cumbersome should the language update. */
    int type;
    if (nID == 1 && nType == 0) {
      type = 1;
    } else if ((nID - 1) == nType) {
      type = 2;
    } else if ((nID == 1) && (nType  == 1)) {
      type = 3;
    } else {
      type = 4;
    }

    // 'function' ID '(' 
    l.beginC(2)
     .print("function")
     .brk(1, 0)
     .print(funcName)
     .print("(");

    // (type ID (',' type ID)*)? ')'
    l.beginI(2);
    switch (type) {
      case 1:
        l.print(")");
        break;
      case 2:
        for (int i = 0; i < nType; i++) {
          enterType(ctx.type(i));
        }
    }

    l.end(); // endI()
    l.end(); // endC()
  }
}
