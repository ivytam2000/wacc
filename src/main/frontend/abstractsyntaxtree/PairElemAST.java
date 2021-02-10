package frontend.abstractsyntaxtree;

import frontend.abstractsyntaxtree.expressions.ArrayElemAST;
import frontend.abstractsyntaxtree.expressions.IdentExprAST;
import frontend.abstractsyntaxtree.expressions.PairLiterAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
import frontend.symboltable.PairID;
import frontend.symboltable.ParamID;
import frontend.symboltable.SymbolTable;

public class PairElemAST extends Node {

  private SymbolTable symtab;
//  private String pairName;
  private final Identifier childIdentifier;
  private boolean first;
  private Node child;
  private String identName;

  public PairElemAST(Identifier childIdentifier, SymbolTable symtab, boolean first, Node child) {
    super();
    this.childIdentifier = childIdentifier;
    this.symtab = symtab;
    this.first = first;
    this.child = child;
    this.identName = "";
  }

  public String getName() {
    return identName;
  }

  @Override
  public void check() {
    //Checking expr has compatible type
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
    } else if (child instanceof PairLiterAST) {
      SemanticErrorCollector.addError("Null");
    } else {
      SemanticErrorCollector.addError("Unexpected Type : does not have type "
          + "pair");
    }
    if (childIdentifier instanceof NullID) {
      setIdentifier(childIdentifier);
    } else if (first) {
      if (childIdentifier instanceof ParamID) {
        setIdentifier(((PairID) ((ParamID) childIdentifier).getType()).getFstType());
      } else {
        setIdentifier(((PairID) childIdentifier).getFstType());
      }
    } else {
      if (childIdentifier instanceof ParamID) {
        setIdentifier(((PairID) ((ParamID) childIdentifier).getType()).getSndType());
      } else {
        setIdentifier(((PairID) childIdentifier).getSndType());
      }
    }
  }
}
