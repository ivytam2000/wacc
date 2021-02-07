package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;

public class IntLiterAST extends Node {

  private final boolean positive;
  private final String val;

  public IntLiterAST(SymbolTable symtab, boolean positive, String val) {
    super(symtab.lookupAll("int"));
    assert (super.identifier != null);
    this.positive = positive;
    this.val = val;
  }

  @Override
  public void check() {
    long valTemp = Long.parseLong(val);
    if ((positive && IntID.MAX < valTemp) || IntID.MIN > valTemp) {
      System.err.println("Int assignment overflow!");
      System.exit(200);
    }
  }
}
