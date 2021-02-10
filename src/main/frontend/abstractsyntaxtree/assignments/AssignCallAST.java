package frontend.abstractsyntaxtree.assignments;

import antlr.WaccParser.Call_assignRHSContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.List;

public class AssignCallAST extends AssignRHSAST {

  private final String funcName;
  private final ArgListAST args;
  private final Call_assignRHSContext ctx;

  public AssignCallAST(String funcName, SymbolTable symtab, ArgListAST args,
      Call_assignRHSContext ctx) {
    super(symtab.lookupAll(funcName), symtab);
    this.funcName = funcName;
    this.args = args;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    Identifier funcID = this.symtab.lookupAll(funcName);

    if (funcID == null) {
      SemanticErrorCollector.addFunctionUndefined(funcName, ctx.getStart().getLine(),
          ctx.IDENT().getSymbol().getCharPositionInLine());
    } else {
      if (funcID instanceof FuncID) {
        List<TypeID> params = ((FuncID) funcID).getParams();
        List<Node> argsAST = args.getArguments();
        int paramSize = params.size();
        int argsSize = argsAST.size();
        if (paramSize != argsSize) {
          String errorMsg = String
              .format("line %d:%d -- Function %s expected %d arguments but got %d arguments",
                  ctx.getStart().getLine(), ctx.argList().getStart().getCharPositionInLine(),
                  funcName, paramSize, argsSize);
          SemanticErrorCollector.addError(errorMsg);
        } else {
          for (int i = 0; i < paramSize; i++) {
            TypeID currParam = params.get(i);
            Node currArg = argsAST.get(i);
            String paramType = currParam.getTypeName();
            String argType = currArg.getIdentifier().getType().getTypeName();
            if (!paramType.equals(argType)) {
              String errorMsg = String.format(
                  "line %d:%d -- Function %s argument %d expected type: %s but got actual type: %s",
                  ctx.getStart().getLine(),
                  ctx.argList().expr(i).getStart().getCharPositionInLine(), funcName, i, paramType,
                  argType);
              SemanticErrorCollector.addError(errorMsg);
            }
          }
        }
      } else {
        String errorMsg = String.format("line %d:%d -- %s is not a function, it is a %s",
            ctx.getStart().getLine(), ctx.IDENT().getSymbol().getCharPositionInLine(), funcName,
            funcID.getType().getTypeName());
        SemanticErrorCollector.addError(errorMsg);
      }
    }

  }
}
