package frontend.abstractsyntaxtree;

import frontend.symboltable.SymbolTable;
import java.util.List;

public class AST extends Node {

  private final List<Node> funcASTs;
  private final Node statAST;

  public AST(List<Node> funcASTs, Node statAST) {
    super();
    this.funcASTs = funcASTs;
    this.statAST = statAST;
  }

  @Override
  public void check() {

  }

//  FOR DEBUGGING
  public List<Node> getFuncASTs() {
    return funcASTs;
  }

  public Node getStatAST() {
    return statAST;
  }
}
