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

  private final String funcName;

  public AssignCallAST(String funcName, SymbolTable symtab, ArgListAST argListAST) {
    super(symtab.lookupAll(funcName), symtab, argListAST.getExpressions());
    this.funcName = funcName;
  }

  @Override
  public void check() {
    Identifier funcID = this.symtab.lookupAll(funcName);

    if (funcID == null) {
      SemanticErrorCollector.addError(funcName + " is not defined.");
    } else {
      if (funcID instanceof FuncID) {
        List<TypeID> params = ((FuncID) funcID).getParams();
        int paramSize = params.size();
        int childrenSize = children.size();
        if (paramSize != childrenSize) {
          SemanticErrorCollector
              .addError(funcName + " expected " + paramSize + " arguments but got " + childrenSize);
        } else {
          for (int i = 0; i < paramSize; i++) {
            TypeID currParam = params.get(i);
            Node currChild = children.get(i);
            String paramType = currParam.getTypeName();
            String childType = currChild.getIdentifier().getType().getTypeName();
            if (!paramType.equals(childType)) {
              SemanticErrorCollector.addError(
                  funcName + " argument " + i + " expected type " + paramType + " but got "
                      + childType);
            }
          }
        }
      } else {
        SemanticErrorCollector.addError(funcName + " is not a function!");
      }
    }

  }
}
