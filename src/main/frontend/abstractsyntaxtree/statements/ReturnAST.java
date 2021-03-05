package frontend.abstractsyntaxtree.statements;

import static backend.instructions.AddrMode.buildReg;
import static backend.instructions.Condition.NO_CON;
import static backend.instructions.Instr.PC;
import static backend.instructions.Instr.addToCurLabel;

import antlr.WaccParser.Return_statContext;
import backend.Utils;
import backend.instructions.Instr;
import backend.instructions.MOV;
import backend.instructions.POP;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class ReturnAST extends Node {

  private final SymbolTable symtab;
  private final Node expr;
  private final Return_statContext ctx;

  public ReturnAST(SymbolTable symtab, Node expr, Return_statContext ctx) {
    super(expr.getIdentifier());
    this.symtab = symtab;
    symtab.setFuncContext();
    this.expr = expr;
    this.ctx = ctx;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {
    if (symtab.isTopLevel()) {
      SemanticErrorCollector.addGlobalReturnError(
          ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
  }

  @Override
  public void toAssembly() {
    // Evaluate the expression
    expr.toAssembly();
    List<Instr> instructions = new ArrayList<>();
    instructions.add(new MOV(NO_CON, Instr.R0, buildReg(Instr.getTargetReg())));
    addToCurLabel(instructions);
    // Restore the stack pointer by incrementing the function's stack
    addToCurLabel(Utils.getEndRoutine(symtab, false));
    addToCurLabel(new POP(PC));
  }
}
