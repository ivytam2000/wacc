package frontend.abstractsyntaxtree.pairs;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.PairLiterAST;
import frontend.symboltable.Identifier;

public class PairTypeAST extends Node {

  private final Node fst;
  private final Node snd;

  public PairTypeAST(Identifier identifier, Node fst, Node snd) {
    super(identifier);
    if (fst == null) {
      this.fst = new PairLiterAST();
    } else {
      this.fst = fst;
    }
    if (snd == null) {
      this.snd = new PairLiterAST();
    } else {
      this.snd = snd;
    }
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
}
