package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;

public class PairElemAST extends Node {

  private SymbolTable symtab;
  private String pairName;
  private boolean first;
  private Node child;
  private String identName;

  public PairElemAST(Identifier identifier, SymbolTable symtab, boolean first, Node child) {
    super(identifier);
    this.first = first;
    this.child = child;
    this.identName = "";
  }

  public String getName() {
    return identName;
  }

  @Override
  public void check() {
    if (child instanceof IdentAST) {
      identName = IdentAST.getname();
      if (!(symtab.lookupAll(identName) instanceof PairID)) {
        System.out.println(identName + " does not have type Pair.");
      }
    } else if (child instanceof ArrayElemAST) {
      if (!(child.getIdentifier().getType() instanceof PairID)) {
        System.out.println("Expected type pair but got " + child.getIdentifier().getType().getTypeName());
      }
    } else {
      System.out.println();
    }

  }

}
