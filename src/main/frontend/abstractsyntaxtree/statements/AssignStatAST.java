package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
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

  private final AssignRHSAST rhs;
  private final AssignLHSAST lhs;
  private SymbolTable symtab;
  private Identifier assignObj;
  private final WaccParser.AssignRHSContext rhsCtx;
  private final WaccParser.AssignLHSContext lhsCtx;
  private final boolean funcCheck;

  public AssignStatAST(WaccParser.AssignLHSContext lhsCtx,
      WaccParser.AssignRHSContext rhsCtx, AssignLHSAST lhs,
      AssignRHSAST rhs, SymbolTable symtab, boolean funcCheck) {
    this.rhs = rhs;
    this.lhs = lhs;
    this.lhsCtx = lhsCtx;
    this.rhsCtx = rhsCtx;
    this.symtab = symtab;
    this.funcCheck = funcCheck;
  }

  @Override
  public void check() {
    String varName = lhs.getIdentName();
    Identifier var;
    String errmsg = "Variable " + varName + " is undefined";
    if (funcCheck) {
      var = symtab.lookup(varName);
      errmsg += " in this scope";
    } else {
      var = symtab.lookupAll(varName);
    }
    if (var == null) {
      SemanticErrorCollector.addError(errmsg);
    } else if (Utils.typeCompat(lhsCtx, rhsCtx, lhs, rhs)) {
      setIdentifier(lhs.getIdentifier().getType());
    }
  }
}

//int x = 1;
// x = 2