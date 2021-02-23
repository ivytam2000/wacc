package frontend.abstractsyntaxtree.assignments;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class AssignLHSAST extends Node {

  private final String assignName;

  public AssignLHSAST(Node assignNode, String assignName) {
    super(assignNode.getIdentifier());
    this.assignName = assignName;
  }

  public AssignLHSAST(SymbolTable symtab, String assignName) {
    super(symtab.lookupAll(assignName));
    this.assignName = assignName;
  }

  public String getIdentName() {
    return assignName;
  }

  @Override
  public void check() {}

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
