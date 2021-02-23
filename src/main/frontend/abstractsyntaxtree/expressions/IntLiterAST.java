package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class IntLiterAST extends Node {

  private final boolean positive; //For backend
  private final String val; //For backend

  public IntLiterAST(SymbolTable symtab, boolean positive, String val) {
    super(symtab.lookupAll("int"));
    this.positive = positive;
    this.val = val;
  }

  @Override
  public void check() {

  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
