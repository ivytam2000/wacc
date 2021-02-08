package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.Identifier;
import frontend.symboltable.IntID;
import java.util.List;

public class ArrayElemAST extends Node {

  private Identifier currIdentifier;
  private final String val;
  private final List<Node> exprs;

  public ArrayElemAST(Identifier currIdentifier, String val, List<Node> exprs) {
    super();
    this.currIdentifier = currIdentifier;
    this.val = val;
    this.exprs = exprs;
  }

  public String getName() {
    return this.val;
  }

  @Override
  public void check() {
    for (Node e : exprs) {
      assert (e.getIdentifier().getType() instanceof IntID);
      if (!(currIdentifier instanceof ArrayID)) {
        SemanticErrorCollector.addError("Tried to index a non-array");
      } else {
        currIdentifier = ((ArrayID) currIdentifier).getElemType();
      }
    }
    setIdentifier(currIdentifier);
  }
}
