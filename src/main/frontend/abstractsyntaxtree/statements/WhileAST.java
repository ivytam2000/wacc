package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;

public class WhileAST extends Node {
  private final Node expr;
  private final Node stat;

  public WhileAST(Node expr, Node stat) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.stat = stat;
  }

  @Override
  public void check() {
    // TODO: Check if all expr have a identifier?
    if (expr == null || expr.getIdentifier() == null) {
      SemanticErrorCollector.addError("Invalid condition expression");
    } else {
      TypeID exprType = expr.getIdentifier().getType();
      if (!(exprType instanceof BoolID)) {
        SemanticErrorCollector.addError("Condition is not of type bool ");
      }
    }
  }
}
