package frontend.abstractsyntaxtree.assignments;

import antlr.WaccParser.Call_assignRHSContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
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
    int line = ctx.getStart().getLine();
    int identPos = ctx.IDENT().getSymbol().getCharPositionInLine();

    // function not defined yet
    if (funcID == null) {
      SemanticErrorCollector.addFunctionUndefined(funcName, line, identPos);
    } else {
      if (funcID instanceof FuncID) {
        List<TypeID> params = ((FuncID) funcID).getParams();
        List<Node> argsAST = args.getArguments();
        int paramSize = params.size();
        int argsSize = argsAST.size();

        // if given number of arguments are not the same as the number of params
        if (paramSize != argsSize) {
          SemanticErrorCollector
              .addFuncInconsistentArgsError(line, ctx.argList().getStart().getCharPositionInLine(),
                  funcName, paramSize, argsSize);
        } else {
          for (int i = 0; i < paramSize; i++) {
            TypeID currParam = params.get(i);
            Node currArg = argsAST.get(i);
            TypeID argType = currArg.getIdentifier().getType();
            if (!(argType instanceof NullID)) {

              // if argument and param types don't match
              if (currParam.getClass() != argType.getClass()) {
                SemanticErrorCollector.addFuncInconsistentArgTypeError(line,
                    ctx.argList().expr(i).getStart().getCharPositionInLine(), funcName, i,
                    currParam.getTypeName(), argType.getTypeName());
              }
            }
          }
        }
      } else {

        // given function name is not actually a function type
        SemanticErrorCollector.addIsNotFuncError(line, identPos, funcName, funcID.getType().getTypeName());
      }
    }

  }
}
