package frontend.abstractsyntaxtree;

import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.List;

public class ArrayLiterAST extends Node {

  private SymbolTable symtab;
  private List<Node> children;

  public ArrayLiterAST(SymbolTable symtab, List<Node> children) {
    this.symtab = symtab;
    this.children = children;
  }

  @Override
  public void check() {
    String type = children.get(0).getIdentifier().getType().getTypeName();
    for (int i = 1; i < children.size(); i++) {
      Node child = children.get(i);
      String childType = child.getIdentifier().getType().getTypeName();
      if (!childType.equals(type)) {
        SemanticErrorCollector.addError("Array doesn't have consistent types, index " + i +
            " has type " + childType + " but expected " + type);
      }
    }
  }
}
