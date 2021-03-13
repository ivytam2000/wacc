package frontend.abstractsyntaxtree.classes;

import antlr.WaccParser.ClassInstantContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ClassID;
import frontend.symboltable.ConstructorID;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.List;

public class ClassInstantAST extends AssignRHSAST {

  private final String className;
  private final ArgListAST args;
  private final SymbolTable symtab;
  private final ClassInstantContext ctx;

  public ClassInstantAST(String className, SymbolTable symtab,
      ArgListAST args, ClassInstantContext ctx) {
    super(symtab.lookupAll("class " + className), symtab);
    this.className = className;
    this.args = args;
    this.symtab = symtab;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    // class not defined when doing lookupALL in constructor
    if (identifier == null) {
      SemanticErrorCollector.addClassNotDefined(className,
          ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      return;
    }

    // assume it will have to be class since we look up "class className"
    ClassID classIdent = (ClassID) identifier;
    // check class constructor
    SymbolTable classSymtab = classIdent.getSymtab();
    Identifier constructID = classSymtab.lookup(className);

    if (constructID instanceof ConstructorID) {
      List<TypeID> expected = ((ConstructorID) constructID).getParams();
      List<Node> actual = args.getArguments();
      int paramSize = expected.size();
      int argsSize = actual.size();

      // If given number of arguments are not the same as the number of params
      if (paramSize != argsSize) {
        SemanticErrorCollector.addClassConstructorInconsistentArgsError(
            ctx.getStart().getLine(),
            ctx.argList().getStart().getCharPositionInLine(),
            className,
            paramSize,
            argsSize);
      } else {
        for (int i = 0; i < paramSize; i++) {
          TypeID currParam = expected.get(i);
          Node currArg = actual.get(i);
          TypeID argType = currArg.getIdentifier().getType();
          if (!(argType instanceof NullID)) {

            // If argument and param types don't match
            if (currParam.getClass() != argType.getClass()) {
              SemanticErrorCollector.addFuncInconsistentArgTypeError(
                  ctx.getStart().getLine(),
                  ctx.argList().expr(i).getStart().getCharPositionInLine(),
                  className,
                  i,
                  currParam.getTypeName(),
                  argType.getTypeName());
            }
          }
        }
      }

    } else {
      // something went wrong in class constructor define
      SemanticErrorCollector.addClassConstructorNotDefinedError(
          className, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
  }

  @Override
  public void toAssembly() {

  }
}
