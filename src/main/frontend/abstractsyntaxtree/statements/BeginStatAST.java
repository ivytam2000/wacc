package frontend.abstractsyntaxtree.statements;

import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

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
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    // make space on the stack with the number of variables sizes
    String stackSize = "#" + symtab.getSize();
    instrs.add(new SUB(false, false, Instr.SP, stackSize));
    instrs.addAll(stat.toAssembly());
    // TODO: what about in the while case because we terminate in a branch?
    instrs.add(new ADD(false, Instr.SP, Instr.SP, stackSize));
    instrs.add(new LDR(4,"", Instr.R0, "=0"));
    instrs.add(new POP(Instr.PC));
    return instrs;
  }

  public Node getStat() {
    return stat;
  }
}
