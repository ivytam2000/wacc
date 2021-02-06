package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.ParamID;
import frontend.symboltable.SymbolTable;

public class ParamAST extends Node {

  private final String typeName;
  private final String varName;
  private ParamID paramObj;

  protected ParamAST(Identifier identifier, SymbolTable symtab, Node parent, String varName) {
    super(identifier, symtab, parent);
    this.typeName = identifier.getType().getTypeName();
    this.varName = varName;
  }

  @Override
  public void check() {
    Identifier t = symtab.lookupAll(typeName);

    if (t == null) {
      System.out.println("Unknown type " + typeName);
      return;
    }

    if (!(identifier instanceof ParamID)) {
      System.out.println(typeName + " is not a type");
      return;
    }

    paramObj = (ParamID) identifier;
    symtab.add(varName, paramObj);
  }
}
