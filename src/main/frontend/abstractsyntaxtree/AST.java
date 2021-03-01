package frontend.abstractsyntaxtree;

import backend.instructions.Instr;
import frontend.symboltable.SymbolTable;
import java.util.List;
import java.util.Map;

/**
 * A top-level AST node for the whole program.
 */
public class AST extends Node {

  /**
   * A list of all the functions within the program scanned in the first pass.
   */
  private final List<Node> funcASTs;
  private final Node statAST;
  private final SymbolTable symtab;

  public AST(List<Node> funcASTs, Node statAST, SymbolTable symtab) {
    super();
    this.funcASTs = funcASTs;
    this.statAST = statAST;
    this.symtab = symtab;
  }

  public List<Node> getFuncASTs() {
    return funcASTs;
  }

  public Node getStatAST() {
    return statAST;
  }

  public SymbolTable getSymtab() {
    return symtab;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
     statAST.toAssembly();
  }
}
