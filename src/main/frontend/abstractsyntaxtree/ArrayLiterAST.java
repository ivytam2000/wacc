package frontend.abstractsyntaxtree;

import antlr.WaccParser.ArrayLiterContext;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.List;

public class ArrayLiterAST extends Node {

  private final SymbolTable symtab;
  private final List<Node> children;
  private final ArrayLiterContext ctx;

  public ArrayLiterAST(Identifier identifier, SymbolTable symtab, List<Node> children, ArrayLiterContext ctx) {
    super(identifier);
    this.symtab = symtab;
    this.children = children;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    if (!children.isEmpty()) {
      String type = children.get(0).getIdentifier().getType().getTypeName();
      for (int i = 1; i < children.size(); i++) {
        Node child = children.get(i);
        String childType = child.getIdentifier().getType().getTypeName();
        if (!childType.equals(type)) {
          String errorMsg = String.format("line %d:%d -- Array doesn't have consistent types, "
              + "index %d has expected type: %s but got actual type: %s", ctx.getStart().getLine(),
              ctx.expr(i).getStart().getCharPositionInLine(), i, type, childType);
          SemanticErrorCollector.addError(errorMsg);
        }
      }
    }
  }
}
