package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.Identifier;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class AssignStatAST extends Node {

  private AssignRHSAST rhs;
  private AssignLHSAST lhs;
  private SymbolTable symtab;
  private Identifier assignObj;

  public AssignStatAST(AssignLHSAST lhs, AssignRHSAST rhs, SymbolTable symtab) {
    this.rhs = rhs;
    this.lhs = lhs;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    if (Utils.typeCompat(lhs, rhs)) {
      setIdentifier(lhs.getIdentifier().getType());
    }
  }
}
