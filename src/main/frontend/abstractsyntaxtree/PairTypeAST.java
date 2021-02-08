package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class PairTypeAST extends Node {

  private final String typeName;
  private final PairElemTypeAST first;
  private final PairElemTypeAST second;
  private TypeID pairTypeObj;
  private SymbolTable symtab;

  public PairTypeAST(Identifier identifier, SymbolTable symtab, PairElemTypeAST first,
      PairElemTypeAST second) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
    this.first = first;
    this.second = second;
  }

  @Override
  public void check() {
    Identifier t = symtab.lookupAll(typeName);

    if (t == null) {
      System.out.println("Unknown type " + typeName);
      return;
    }

    first.check();
    second.check();

    pairTypeObj = (PairID) identifier;
  }
}
