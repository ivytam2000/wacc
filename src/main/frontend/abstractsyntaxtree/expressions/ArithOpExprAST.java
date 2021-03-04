package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.ArithOpExpr_1Context;
import antlr.WaccParser.ArithOpExpr_2Context;
import antlr.WaccParser.ExprContext;
import backend.BackEndGenerator;
import backend.instructions.ADD;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.CMP;
import backend.instructions.Instr;
import backend.instructions.MOV;
import backend.instructions.MUL;
import backend.instructions.SUB;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import frontend.symboltable.UnknownID;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class ArithOpExprAST extends Node {

  private final String op;
  private final Node eL;
  private final Node eR;
  private final ExprContext ctx;

  public ArithOpExprAST(SymbolTable symtab, String op, Node eL, Node eR,
      ExprContext ctx) {
    super(symtab.lookupAll("int"));
    this.op = op;
    this.eL = eL;
    this.eR = eR;
    assert (ctx instanceof ArithOpExpr_1Context
        || ctx instanceof ArithOpExpr_2Context);
    this.ctx = ctx;
  }

  @Override
  public void check() {
    int fstStatPosition = 0;
    int sndStatPosition = 2;

    TypeID eLType = eL.getIdentifier().getType();
    TypeID eRType = eR.getIdentifier().getType();

    //Both eL and eR must be of type int to do arithmetic

    if (!(eLType instanceof IntID || eLType instanceof UnknownID)) {
      SemanticErrorCollector
          .addIncompatibleType("int (For " + op + ")", eLType.getTypeName(),
              ctx.children.get(fstStatPosition).getText(),
              ctx.getStart().getLine(),
              ctx.getStart().getCharPositionInLine());
    }

    if (!(eRType instanceof IntID || eRType instanceof UnknownID)) {
      SemanticErrorCollector
          .addIncompatibleType("int (For " + op + ")", eRType.getTypeName(),
              ctx.children.get(sndStatPosition).getText(),
              ctx.getStart().getLine(),
              ctx.getStop().getCharPositionInLine());
    }
  }

  @Override
  public void toAssembly() {

    eL.toAssembly();

    String sndReg = Instr.incDepth();
    eR.toAssembly();
    String fstReg = Instr.decDepth();

    boolean regsOnStack = Instr.regsOnStack();
    String targetReg = regsOnStack ? sndReg : fstReg;

    List<Instr> instrs = new ArrayList<>();

    boolean addOverflow = true;
    switch (op) {
      case "+":
        instrs.add(new ADD(true, targetReg, fstReg, AddrMode.buildReg(sndReg)));
        instrs.add(new BRANCH(true, "VS", "p_throw_overflow_error"));
        break;
      case "-":
        instrs.add(new SUB(false, true, targetReg, fstReg, sndReg));
        instrs.add(new BRANCH(true, "VS", "p_throw_overflow_error"));
        break;
      case "*":
        instrs.add(new MUL(fstReg, sndReg, regsOnStack));
        if (regsOnStack) {
          instrs.add(new CMP(fstReg, AddrMode.buildReg(sndReg), AddrMode.buildArithmeticShiftL(31)));
        } else {
          instrs.add(new CMP(sndReg,  AddrMode.buildReg(fstReg), AddrMode.buildArithmeticShiftL(31)));
        }
        instrs.add(new BRANCH(true, "NE", "p_throw_overflow_error"));
        break;
      case "/":
        addOverflow = false;
        instrs.add(new MOV("", Instr.R0, fstReg));
        instrs.add(new MOV("", Instr.R1, sndReg));
        instrs.add(new BRANCH(true, "", "p_check_divide_by_zero"));
        instrs.add(new BRANCH(true, "", "__aeabi_idiv"));
        instrs.add(new MOV("", Instr.getTargetReg(), Instr.R0));
        break;
      case "%":
        addOverflow = false;
        instrs.add(new MOV("", Instr.R0, fstReg));
        instrs.add(new MOV("", Instr.R1, sndReg));
        instrs.add(new BRANCH(true, "", "p_check_divide_by_zero"));
        instrs.add(new BRANCH(true, "", "__aeabi_idivmod"));
        instrs.add(new MOV("", Instr.getTargetReg(), Instr.R1));
        break;
      default:
        assert(false); //UNREACHABLE
    }

    if (addOverflow) {
      BackEndGenerator.addToPreDefFuncs("p_throw_overflow_error");
    } else {
      BackEndGenerator.addToPreDefFuncs("p_check_divide_by_zero");
    }

    addToCurLabel(instrs);
  }
}
