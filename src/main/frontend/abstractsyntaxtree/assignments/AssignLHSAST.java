package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class AssignLHSAST extends Node {

  private SymbolTable symtab;
  private String assignName;

  public AssignLHSAST(SymbolTable symtab, String assignName) {
    super(symtab.lookupAll(assignName));
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
