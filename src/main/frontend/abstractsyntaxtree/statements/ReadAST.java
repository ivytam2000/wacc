package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignLHSContext;
import backend.instructions.ADD;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

import java.util.ArrayList;
import java.util.List;

public class ReadAST extends Node {

  private final AssignLHSAST lhs;
  private final AssignLHSContext ctx;
  private final Identifier lhsType;

  public ReadAST(AssignLHSAST assignLHS, AssignLHSContext ctx) {
    super();
    this.lhs = assignLHS;
    this.ctx = ctx;
    lhsType = lhs.getIdentifier();
  }

  @Override
  public void check() {
    int line = ctx.getStart().getLine();
    int pos = ctx.getStart().getCharPositionInLine();

    if (!(lhsType instanceof IntID) && !(lhsType instanceof CharID)) {
      SemanticErrorCollector.addIncompatibleType(
          "int or char", lhsType.getType().getTypeName(), ctx.getText(),
          line, pos);
    }
  }

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    // TODO: how to get the variable name ??
    // offset = symtab.getStackOffset(expVarName)
    //ADD addInstr = new ADD(false,Instr.R4,Instr.SP, offset);
    MOV movInstr = new MOV("", Instr.R0, Instr.R4);
    instrs.add(movInstr);
    String label = lhsType instanceof IntID ? "p_read_int" : "p_read_char";
    BRANCH brInstr = new BRANCH(true, "", label);
    instrs.add(brInstr);
    return instrs;
  }
}
