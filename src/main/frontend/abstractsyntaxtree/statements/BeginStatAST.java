package frontend.abstractsyntaxtree.statements;

import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

import static backend.Utils.getEndRoutine;
import static backend.Utils.getStartRoutine;
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
    addToCurLabel(getStartRoutine(symtab, false));
    stat.toAssembly();
    addToCurLabel(getEndRoutine(symtab,false));
  }

  public Node getStat() {
    return stat;
  }
}
