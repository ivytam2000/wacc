package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class AssignStatAST extends Node {

  private final AssignRHSAST rhs;
  private final AssignLHSAST lhs;
  private SymbolTable symtab;
  private Identifier assignObj;
  private final WaccParser.AssignRHSContext rhsCtx;
  private final WaccParser.AssignLHSContext lhsCtx;

  public AssignStatAST(WaccParser.AssignLHSContext lhsCtx,
      WaccParser.AssignRHSContext rhsCtx, AssignLHSAST lhs,
      AssignRHSAST rhs, SymbolTable symtab) {
    this.rhs = rhs;
    this.lhs = lhs;
    this.lhsCtx = lhsCtx;
    this.rhsCtx = rhsCtx;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    String varName = lhs.getIdentName();
    Identifier var;
    var = symtab.lookupAll(varName);
    if (var == null) {
      SemanticErrorCollector.addError("Variable " + varName + " is undefined");
    } else if (var instanceof FuncID) {
      SemanticErrorCollector.addError("Can't assign to func " + varName);
    } else if (Utils.typeCompat(lhsCtx, rhsCtx, lhs, rhs)) {
      setIdentifier(lhs.getIdentifier().getType());
    }
  }
}