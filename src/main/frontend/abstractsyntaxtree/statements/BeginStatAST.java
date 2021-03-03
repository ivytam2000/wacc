package frontend.abstractsyntaxtree.statements;

import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class BeginStatAST extends Node {

  private final Node stat;
  private final SymbolTable symtab;

  public BeginStatAST(Node stat, SymbolTable symtab) {
    super(stat.getIdentifier());
    this.stat = stat;
    this.symtab = symtab;
  }

  @Override
  public void check() {}

  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    // make space on the stack with the number of variables sizes
    if (symtab.getSize() > 0) {
      String stackSize = "#" + symtab.getSize();
      instrs.add(new SUB(false, false, Instr.SP, Instr.SP, stackSize));
      stat.toAssembly();
      instrs.add(new ADD(false, Instr.SP, Instr.SP, stackSize));
    }
    addToCurLabel(instrs);
  }

  public Node getStat() {
    return stat;
  }
}
