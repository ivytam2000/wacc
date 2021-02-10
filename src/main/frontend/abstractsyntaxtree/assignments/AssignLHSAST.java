package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class AssignLHSAST extends Node {

  private SymbolTable symtab;
  private String assignName;

  public AssignLHSAST(SymbolTable symtab, Node assignNode) {
    super(assignNode.getIdentifier());
    this.symtab = symtab;
    this.assignName = assignNode.getIdentifier().getType().getTypeName();
  }

  public AssignLHSAST(SymbolTable symtab, String assignName) {
    super(symtab.lookupAll(assignName));
    this.symtab = symtab;
    this.assignName = assignName;
  }

  public String getIdentName() {
    return assignName;
  }

  @Override
  public void check() {
  }
}
