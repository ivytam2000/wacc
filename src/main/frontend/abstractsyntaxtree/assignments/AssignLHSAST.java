package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class AssignLHSAST extends Node {

  private SymbolTable symtab;
  private String assignName;

  public AssignLHSAST(Identifier identifier, SymbolTable symtab, String assignName) {
    super(identifier);
    this.symtab = symtab;
    this.assignName = assignName;
  }

  public AssignLHSAST(Identifier identifier, SymbolTable symtab) {
    super(identifier);
    this.symtab = symtab;
    this.assignName = "";
  }

  public String getIdentName() {
    return assignName;
  }

  @Override
  public void check() {

  }
}
