package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.ExprContext;
import backend.instructions.ADD;
import backend.instructions.AND;
import backend.instructions.AddrMode;
import backend.instructions.CMP;
import backend.instructions.Instr;
import backend.instructions.MOV;
import backend.instructions.ORR;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.CharID;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import frontend.symboltable.UnknownID;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class BinOpExprAST extends Node {

  private final String op;
  private final int expectedExprTypes;
  private final Node eL;
  private final Node eR;
  private final ExprContext ctx;

  public BinOpExprAST(SymbolTable symtab, int expectedExprTypes,
      String op, Node eL, Node eR, ExprContext ctx) {
    super(symtab.lookupAll("bool")); //BinOpExpr always has bool return type
    this.op = op;
    this.expectedExprTypes = expectedExprTypes;
    this.eL = eL;
    this.eR = eR;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    TypeID eLType = eL.getIdentifier().getType();
    TypeID eRType = eR.getIdentifier().getType();

    //Different binOps are compatible w different types

    if (expectedExprTypes == Utils.ALL_TYPES) { //Defined for all types
      if (!Utils.typeCompat(eLType, eRType)) {
        SemanticErrorCollector
            .addTypeMismatch(ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(), op);
      }
    } else {
      //Check for left and right expr of binOp separated to pin point error

      boolean errorL = false;
      boolean errorR = false;
      String expectedTypes = "";

      if (expectedExprTypes == Utils.INT_CHAR) { //Defined for int and char
        errorL = !(eLType instanceof IntID || eLType instanceof CharID
            || eLType instanceof UnknownID);
        errorR = !(eRType instanceof IntID || eRType instanceof CharID
            || eRType instanceof UnknownID);
        expectedTypes = "int or char";
      } else if (expectedExprTypes == Utils.BOOL) { //Defined for bool only
        errorL = !(eLType instanceof BoolID || eLType instanceof UnknownID);
        errorR = !(eRType instanceof BoolID || eRType instanceof UnknownID);
        expectedTypes = "bool";
      }

      if (errorL) {
        SemanticErrorCollector
            .addIncompatibleType(expectedTypes, eLType.getTypeName(),
                ctx.getText(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine());
      }

      if (errorR) {
        SemanticErrorCollector
            .addIncompatibleType(expectedTypes, eRType.getTypeName(),
                ctx.getText(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine());
      }
    }
  }

  @Override
  public void toAssembly() {
    String fstReg = Instr.getTargetReg();
    eL.toAssembly();
    List<Instr> instrs = new ArrayList<>();

    String sndReg = Instr.incDepth();
    eR.toAssembly();
    Instr.decDepth();

    if (op.equals("&&")) {
      instrs.add(new AND(fstReg, sndReg));
      addToCurLabel(instrs);
      return ;
    } else if (op.equals("||")) {
      instrs.add(new ORR(false, fstReg, AddrMode.buildReg(sndReg)));
      addToCurLabel(instrs);
      return ;
    }

    instrs.add(new CMP(fstReg, AddrMode.buildReg(sndReg)));

    String c1 = "";
    String c2 = "";
    switch (op) {
      case ">":
        c1 = "GT";
        c2 = "LE";
        break;
      case ">=":
        c1 = "GE";
        c2 = "LT";
        break;
      case "<":
        c1 = "LT";
        c2 = "GE";
        break;
      case "<=":
        c1 = "LE";
        c2 = "GT";
        break;
      case "==":
        c1 = "EQ";
        c2 = "NE";
        break;
      case "!=":
        c1 = "NE";
        c2 = "EQ";
        break;
      default:
        assert(false); // UNREACHABLE
    }

    instrs.add(new MOV(c1, fstReg, AddrMode.buildImm(1)));
    instrs.add(new MOV(c2, fstReg, AddrMode.buildImm(0)));

    addToCurLabel(instrs);
  }


}
