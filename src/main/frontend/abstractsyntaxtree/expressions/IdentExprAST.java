package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.IdentExprContext;
import backend.instructions.AddrMode;
import backend.instructions.Instr;
import backend.instructions.LDR;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.UnknownID;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class IdentExprAST extends Node {

  private final SymbolTable currsymtab;
  private final IdentExprContext ctx;

  public IdentExprAST(SymbolTable currsymtab, IdentExprContext ctx) {
    super();
    this.currsymtab = currsymtab;
    this.ctx = ctx;
  }

  public String getName() {
    return ctx.getText();
  }

  @Override
  public void check() {
    String varName = getName();
    Identifier identifier = currsymtab.lookupAll(varName);

    if (identifier == null) { //Unknown variable
      SemanticErrorCollector
          .addVariableUndefined(varName, ctx.getStart().getLine(),
              ctx.getStart().getCharPositionInLine());
      setIdentifier(new UnknownID());
    } else {
      setIdentifier(identifier);
    }
  }

  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Load from (SP + offset) into target register
    int offset = currsymtab.getStackOffset(getName());
    instrs.add(new LDR(identifier.getType().getBytes(), "",
        Instr.getTargetReg(), AddrMode.buildAddrWithOffset(Instr.SP, offset)));
//    instrs.add(new LDR(identifier.getType().getBytes(), "",
//        Instr.getTargetReg(), Instr.SP, offset));

    addToCurLabel(instrs);
  }
}
