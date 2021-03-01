package frontend.abstractsyntaxtree.pairs;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.PairLiterAST;
import frontend.symboltable.Identifier;
import java.util.List;

public class PairTypeAST extends Node {

  private final Node fst;
  private final Node snd;

  public PairTypeAST(Identifier identifier, Node fst, Node snd) {
    super(identifier);
    // Assigns its children to a NullID if its types are not specified
    this.fst = fst == null ? new PairLiterAST() : fst;
    this.snd = snd == null ? new PairLiterAST() : snd;
  }

  public Node getFst() {
    return fst;
  }

  public Node getSnd() {
    return snd;
  }

  @Override
  public void check() {
    fst.check();
    snd.check();
  }

  @Override
  public void toAssembly() {}
}
