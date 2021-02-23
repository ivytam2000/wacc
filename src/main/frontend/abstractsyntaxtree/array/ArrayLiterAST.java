package frontend.abstractsyntaxtree.array;

import antlr.WaccParser.ArrayLiterContext;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import java.util.List;

public class ArrayLiterAST extends Node {

  private final List<Node> children;
  private final ArrayLiterContext ctx;

  public ArrayLiterAST(
      Identifier identifier, List<Node> children, ArrayLiterContext ctx) {
    super(identifier);
    this.children = children;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    if (!children.isEmpty()) {
      String type = children.get(0).getIdentifier().getType().getTypeName();
      int line = ctx.getStart().getLine();
      for (int i = 1; i < children.size(); i++) {
        Node child = children.get(i);
        String childType = child.getIdentifier().getType().getTypeName();
        if (!childType.equals(type)) {
          SemanticErrorCollector.addArrayInconsistentTypes(
              line, ctx.expr(i).getStart().getCharPositionInLine(), i, type,
              childType);
        }
      }
    }
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
