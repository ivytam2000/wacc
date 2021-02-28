package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.NullID;
import java.util.ArrayList;
import java.util.List;

public class PairLiterAST extends Node {

  public PairLiterAST() {
    super(new NullID());
  }

  @Override
  public void check() {}

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    //TODO: MOV or LDR?
    instrs.add(new MOV("", Instr.getTargetReg(), "#0"));
    return instrs;
  }
}
