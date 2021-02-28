package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import backend.instructions.LDR;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class IntLiterAST extends Node {

  private final boolean positive; //For backend
  private final String val; //For backend

  public IntLiterAST(SymbolTable symtab, boolean positive, String val) {
    super(symtab.lookupAll("int"));
    this.positive = positive;
    this.val = val;
  }

  public String getVal() {
    return val;
  }

  @Override
  public void check() {

  }

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Load value directly into target register
    instrs.add(new LDR(4, "", Instr.getTargetReg(), val));

    return instrs;
  }
}
