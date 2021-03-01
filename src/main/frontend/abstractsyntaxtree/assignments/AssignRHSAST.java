package frontend.abstractsyntaxtree.assignments;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class AssignRHSAST extends Node {

  protected final SymbolTable symtab;
  protected List<Node> children;

  public AssignRHSAST(Identifier identifier, SymbolTable symtab,
      List<Node> children) {
    super(identifier);
    this.symtab = symtab;
    this.children = children;
  }

  public AssignRHSAST(Identifier identifier, SymbolTable symtab) {
    super(identifier);
    this.symtab = symtab;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
    for (Node expr: children) {
      expr.toAssembly();
    }
  }
}
