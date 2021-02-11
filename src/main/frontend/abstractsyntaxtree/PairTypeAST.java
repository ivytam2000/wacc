package frontend.abstractsyntaxtree;

import frontend.abstractsyntaxtree.expressions.PairLiterAST;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class PairTypeAST extends Node {

  private final String typeName;
  private final Node fst;
  private final Node snd;
  private SymbolTable symtab;

  public PairTypeAST(Identifier identifier, SymbolTable symtab, Node fst, Node snd) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
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
