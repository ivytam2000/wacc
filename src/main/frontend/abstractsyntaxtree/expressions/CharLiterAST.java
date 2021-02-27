package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class CharLiterAST extends Node {

  private final String val; //For backend

  public CharLiterAST(SymbolTable symtab, String val) {
    super(symtab.lookupAll("char"));
    this.val = val;
  }

  public String getVal() {
    return "#" + "'" + val + "'" ;
  }

  @Override
  public void check() {}

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
