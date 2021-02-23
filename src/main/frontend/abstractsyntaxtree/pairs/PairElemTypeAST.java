package frontend.abstractsyntaxtree.pairs;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import java.util.List;

public class PairElemTypeAST extends Node {

  public PairElemTypeAST(Identifier identifier) {
    super(identifier);
  }

  @Override
  public void check() {
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
