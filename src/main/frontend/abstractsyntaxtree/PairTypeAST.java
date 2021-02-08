package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class PairTypeAST extends Node {

  private final String typeName;
  private final Node first;
  private final Node second;
  private TypeID pairTypeObj;
  private SymbolTable symtab;

  public PairTypeAST(Identifier identifier, SymbolTable symtab, Node first, Node second) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
    this.first = first;
    this.second = second;
  }

  @Override
  public void check() {
    first.check();
    second.check();

    pairTypeObj = (TypeID) identifier;
  }
}
