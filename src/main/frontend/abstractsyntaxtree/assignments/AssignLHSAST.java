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

  @Override
  public void check() {
    Identifier ident = symtab.lookupAll(assignName);

    if (ident == null) {
      System.out.println(assignName + " is not defined.");
    } else {
      if (!ident.getType().getTypeName().equals(identifier.getType().getTypeName())) {
        System.out.println(assignName + " expected " + identifier.getType().getTypeName()
            + " but got " + ident.getType().getTypeName());
      }
    }

  }
}
