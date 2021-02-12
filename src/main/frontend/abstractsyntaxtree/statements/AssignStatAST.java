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
import frontend.symboltable.TypeID;

public class AssignStatAST extends Node {

  private final AssignRHSAST rhs;
  private final AssignLHSAST lhs;
  private SymbolTable symtab;
  private final WaccParser.AssignRHSContext rhsCtx;
  private final WaccParser.AssignLHSContext lhsCtx;

  public AssignStatAST(
      WaccParser.AssignLHSContext lhsCtx, WaccParser.AssignRHSContext rhsCtx,
      AssignLHSAST lhs, AssignRHSAST rhs, SymbolTable symtab) {
    this.rhs = rhs;
    this.lhs = lhs;
    this.lhsCtx = lhsCtx;
    this.rhsCtx = rhsCtx;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    String varName = lhs.getIdentName();
    Identifier var = symtab.lookupAll(varName);

    int lhsLine = lhsCtx.getStart().getLine();
    int lhsPos = lhsCtx.getStart().getCharPositionInLine();

    if (var == null) { //Undefined variable
      SemanticErrorCollector
          .addVariableUndefined(varName, lhsLine, lhsPos);
    } else if (var instanceof FuncID) { //Trying to assign to a function
      SemanticErrorCollector
          .addAssignToFuncError(lhsLine, lhsPos, varName);
    } else {
      TypeID lhsType = lhs.getIdentifier().getType();
      TypeID rhsType = rhs.getIdentifier().getType();

      if (!Utils.typeCompat(lhsType, rhsType)) { //types don't match
        SemanticErrorCollector
            .addIncompatibleType(lhsType.getTypeName(), rhsType.getTypeName(),
                varName, lhsLine, rhsCtx.getStart().getCharPositionInLine());
      }
    }
  }
}
