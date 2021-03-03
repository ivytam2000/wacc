package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.NullID;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class PairLiterAST extends Node {

  public PairLiterAST() {
    super(new NullID());
  }

  @Override
  public void check() {}

  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    //TODO: MOV or LDR? I think LDR
    instrs.add(new LDR(Instr.getTargetReg(), "0"));
   // instrs.add(new MOV("", Instr.getTargetReg(), "#0"));
    addToCurLabel(instrs);
  }
}
