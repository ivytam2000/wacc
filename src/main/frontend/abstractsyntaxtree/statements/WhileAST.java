package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.Utils;
import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import frontend.symboltable.UnknownID;

import static backend.instructions.Instr.*;

public class WhileAST extends Node {
  private final Node expr;
  private final Node stat;
  private final ExprContext exprCtx;
  private final SymbolTable symtab;

  public WhileAST(Node expr, Node stat, ExprContext exprCtx,
      SymbolTable symtab) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.stat = stat;
    this.exprCtx = exprCtx;
    this.symtab = symtab;
  }

  public Node getStat() {
    return stat;
  }

  @Override
  public void check() {
    if (expr.getIdentifier() != null) {
      TypeID exprType = expr.getIdentifier().getType();
      if (!(exprType instanceof UnknownID || exprType instanceof BoolID)) {
        SemanticErrorCollector.addIncompatibleType(
            "bool",
            exprType.getTypeName(),
            exprCtx.getText(),
            exprCtx.getStart().getLine(),
            exprCtx.getStart().getCharPositionInLine());
      }
    }
  }

  @Override
  public void toAssembly() {
    // Create a new label with instructions which evaluate the while
    // condition expression and the statements after the while loop
    String nextStatLabel = getNextLabel();
    // Create a new label with instructions for the body of the loop
    String bodyLabel = getNextLabel();

    // Branch to this the nextStat label
    addToCurLabel(new BRANCH(false, Condition.NO_CON, nextStatLabel));

    setCurLabel(nextStatLabel);
    expr.toAssembly();
    // Test if the expression is true if it is we branch the bodyLabel
    addToCurLabel(new CMP(Instr.R4, AddrMode.buildImm(1)));
    addToCurLabel(new BRANCH(false, Condition.EQ, bodyLabel));

    setCurLabel(bodyLabel);
    // Body label should be printed before nextStatLabel
    addToLabelOrder(bodyLabel);
    // Make space on the stack for while's new scope
    addToCurLabel(Utils.getStartRoutine(symtab, false));
    stat.toAssembly();
    addToCurLabel(Utils.getEndRoutine(symtab, false));

    setCurLabel(nextStatLabel);
    addToLabelOrder(nextStatLabel);
  }
}
