package frontend.abstractsyntaxtree;

import backend.instructions.Instr;
import java.util.List;
import java.util.Map;

/**
 * A top-level AST node for the whole program.
 */
public class AST extends Node {

  // A list of all the functions within the program scanned in the first pass
  private final List<Node> funcASTs;
  private final Node statAST;

  public AST(List<Node> funcASTs, Node statAST) {
    super();
    this.funcASTs = funcASTs;
    this.statAST = statAST;
  }

  // For future use in code generation
  public List<Node> getFuncASTs() {
    return funcASTs;
  }

  public Node getStatAST() {
    return statAST;
  }

  @Override
  public void check() {
  }

  public Map<String, List<Instr>> generateFunc() {
    //TODO
    return null;
  }
}
