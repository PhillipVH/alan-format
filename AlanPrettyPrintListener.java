import java.io.FileInputStream;
import java.util.Properties;

import de.uka.ilkd.pp.*;

public class AlanPrettyPrintListener extends AlanBaseListener {

  private final Layouter<NoExceptions> layouter;
  private final StringBackend backend;

  private final StringBuilder buffer;

  private Properties userConfig;


  /* I'm going to love myself for this at the end of this project. */
  private final Layouter<NoExceptions> l;

  public AlanPrettyPrintListener() {

    /* Load the config file */
    String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    String defaultConfigPath = rootPath + "alan-format.default.properties";
    String userConfigPath = rootPath + "alan-format.properties";

    userConfig = null; 
    try {
      Properties defaultConfig = new Properties();
      defaultConfig.load(new FileInputStream(defaultConfigPath));

      userConfig = new Properties(defaultConfig);
      userConfig.load(new FileInputStream(userConfigPath));
    } catch (Exception ignored) {}

    if (userConfig == null) {
      System.out.println("Please supply a configuration file.");
      System.exit(1);
    }

    int lineWidth = Integer.parseInt(userConfig.getProperty("linewidth"));
    int indent = Integer.parseInt(userConfig.getProperty("indent"));

    /* Setup layouter */
    buffer = new StringBuilder();
    backend = new StringBackend(buffer, lineWidth);
    layouter = new Layouter<>(backend, indent);

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
     .nl()
     .brk(1, 0)
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

    if (text.startsWith("b")) {
      l.print("boolean");
    } else {
      l.print("integer");
    }

    // 'array'?
    if (text.endsWith("y")) {
      l.brk(1, 0)
       .print("array");
    }

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

    // 'function' ID '(' 
    l.beginC(2)
     .print("function")
     .brk(1, 0)
     .print(funcName)
     .print("(");

    // (type ID (',' type ID)*)? ')'
    for (int i = 1; i < nID; i++) {
      enterType(ctx.type(i - 1));
      l.brk(1, 0)
       .print(ctx.ID(i).getText());
  
      if (i + 1 <= nID - 1) {
        l.print(",")
         .brk(1, 0);
      }

    }

    l.print(")")
     .brk(1, 0);

    // ('to' type)? 
    if (ctx.getText().contains("to")) {
      l.print("to")
       .brk(1, 0);
      enterType(ctx.type(nType - 1));
    }

    l.end(); // endC()
  }
}
