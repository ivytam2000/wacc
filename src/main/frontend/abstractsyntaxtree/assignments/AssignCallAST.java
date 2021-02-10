package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.List;

public class AssignCallAST extends AssignRHSAST {

  private String funcName;
  private ArgListAST args;

  public AssignCallAST(String funcName, SymbolTable symtab, ArgListAST args) {
    super(symtab.lookupAll(funcName), symtab);
    this.funcName = funcName;
    this.args = args;
  }

  @Override
  public void check() {
    Identifier funcID = this.symtab.lookupAll(funcName);

    if (funcID == null) {
      SemanticErrorCollector.addError(funcName + " is not defined.");
    } else {
      if (funcID instanceof FuncID) {
        List<TypeID> params = ((FuncID) funcID).getParams();
        List<Node> argsAST = args.getArguments();
        int paramSize = params.size();
        int argsSize = argsAST.size();
        if (paramSize != argsSize) {
          SemanticErrorCollector
              .addError(funcName + " expected " + paramSize + " arguments but got " + argsSize);
        } else {
          for (int i = 0; i < paramSize; i++) {
            TypeID currParam = params.get(i);
            Node currArg = argsAST.get(i);
            String paramType = currParam.getTypeName();
            String argType = currArg.getIdentifier().getType().getTypeName();
            if (!paramType.equals(argType)) {
              SemanticErrorCollector.addError(
                  funcName + " argument " + i + " expected type " + paramType + " but got "
                      + argType);
            }
          }
        }
      } else {
        SemanticErrorCollector.addError(funcName + " is not a function!");
      }
    }

  }
}
