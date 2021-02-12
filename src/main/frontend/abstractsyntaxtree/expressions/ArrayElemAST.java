package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.ArrayElemContext;
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
  private final ArrayElemContext ctx;

  public ArrayElemAST(Identifier currIdentifier, String val, List<Node> exprs, ArrayElemContext ctx) {
    super();
    this.currIdentifier = currIdentifier;
    this.val = val;
    this.exprs = exprs;
    this.ctx = ctx;
  }

  public String getName() {
    return this.val;
  }

  @Override
  public void check() {
    for (Node e : exprs) {
      //We cannot index further into something that's not an array
      if (!(currIdentifier instanceof ArrayID)) {
        SemanticErrorCollector.addCannotBeIndexed(ctx.getStart().getLine(),
            ctx.getStart().getCharPositionInLine(), val);
        break;
      } else {
        currIdentifier = ((ArrayID) currIdentifier).getElemType();
      }
    }
    setIdentifier(currIdentifier);
  }
}
