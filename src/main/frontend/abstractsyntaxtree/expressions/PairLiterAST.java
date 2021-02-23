package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.NullID;
import java.util.List;

public class PairLiterAST extends Node {

  public PairLiterAST() {
    super(new NullID());
  }

  @Override
  public void check() {}

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
