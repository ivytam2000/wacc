package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.List;

public class AssignCallAST extends AssignRHSAST {

  private String funcName;

  public AssignCallAST(String funcName, SymbolTable symtab, List<Node> params) {
    super(symtab, params);
    this.funcName = funcName;
  }

  @Override
  public void check() {
    Identifier funcID = this.symtab.lookupAll(funcName);

    if (funcID == null) {
      System.out.println(funcName + " is not defined.");
    } else {
      if (funcID instanceof FuncID) {
        List<TypeID> params = ((FuncID) funcID).getParams();
        int paramSize = params.size();
        int childrenSize = children.size();
        if (paramSize != childrenSize) {
          System.out.println(funcName + " expected " + paramSize + " arguments but got " + childrenSize);
        } else {
          for (int i = 0; i < paramSize; i ++) {
            TypeID currParam = params.get(i);
            Node currChild = children.get(i);
            String paramType = currParam.getTypeName();
            String childType = currChild.getIdentifier().getType().getTypeName();
            if (!paramType.equals(childType)) {
              System.out.println(funcName + " argument " + i + " expected type " + paramType + " but got " + childType);
            }
          }
        }
      } else {
        System.out.println(funcName + " is not a function!");
      }
    }

  }
}
