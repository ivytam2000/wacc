package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
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
    this.fst = fst;
    this.snd = snd;
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
