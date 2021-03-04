package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignLHSContext;
import backend.BackEndGenerator;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

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
  public void toAssembly() {
    lhs.toAssembly();
    List<Instr> instrs = new ArrayList<>();
    MOV movInstr = new MOV("", Instr.R0, AddrMode.buildReg(Instr.R4));
    instrs.add(movInstr);
    String label = lhsType instanceof IntID ? "p_read_int" : "p_read_char";
    BackEndGenerator.addToPreDefFuncs(label);
    BRANCH brInstr = new BRANCH(true, "", label);
    instrs.add(brInstr);
    addToCurLabel(instrs);
  }
}
