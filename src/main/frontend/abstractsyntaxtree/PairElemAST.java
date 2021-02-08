package frontend.abstractsyntaxtree;

import frontend.abstractsyntaxtree.expressions.ArrayElemAST;
import frontend.abstractsyntaxtree.expressions.IdentExprAST;
import frontend.errorlistener.SemanticErrorCollector;
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
    if (child instanceof IdentExprAST) {
      identName = ((IdentExprAST) child).getName();
      if (!(symtab.lookupAll(identName) instanceof PairID)) {
        SemanticErrorCollector.addError(identName + " does not have type Pair.");
      }
    } else if (child instanceof ArrayElemAST) {
      if (!(child.getIdentifier().getType() instanceof PairID)) {
        SemanticErrorCollector.addError(
            "Expected type pair but got " + child.getIdentifier().getType().getTypeName());
      }
    } else {
      SemanticErrorCollector.addError("Unexpected Type : does not have type "
          + "pair");
    }
  }
}
