package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.IdentExprContext;
import backend.instructions.*;
import backend.BackEndGenerator;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ClassAttributeID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.UnknownID;
import frontend.symboltable.VarID;
import java.util.ArrayList;
import java.util.List;

public class IdentExprAST extends Node {

  private final SymbolTable currsymtab;
  private final IdentExprContext ctx;
  private boolean check;
  // For dynamic variables
  private int dynamicTypeNeeded;
  private boolean allTypesSupported;

  public IdentExprAST(SymbolTable currsymtab, IdentExprContext ctx) {
    super();
    this.currsymtab = currsymtab;
    this.ctx = ctx;
    this.check = false;
  }

  public void setCheck() {
    this.check = true;
  }

  public String getName() {
    return ctx.getText();
  }

  public void setDynamicTypeNeeded(int dynamicTypeNeeded) {
    this.dynamicTypeNeeded = dynamicTypeNeeded;
  }

  public void setAllTypes() {
    allTypesSupported = true;
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
    Identifier identifier = currsymtab.lookupAll(getName());
    int offset;

    if (identifier instanceof ClassAttributeID) {
      offset = currsymtab.getStackOffset("object_addr");
      int attributeOffset = currsymtab.getStackOffset(getName());
      // loading the
      String targetReg = Instr.getTargetReg();
      // Storing the instance's address into a register
      Instr.addToCurLabel(
          new LDR(identifier.getType().getBytes(), Condition.NO_CON,
              targetReg, AddrMode.buildAddrWithOffset(Instr.SP, offset)));
      // Adding the offset of the attribute to the regeister which holds the
      // instance's address
      if (attributeOffset > 0) {
        Instr.addToCurLabel(new ADD(false, targetReg, targetReg,
            AddrMode.buildImm(attributeOffset)));
      }
      Instr.addToCurLabel(
          new LDR(targetReg, AddrMode.buildAddrWithOffset(targetReg
              , 0)));
    } else {
      offset = currsymtab.getStackOffset(getName());
      Instr loadVar = new LDR(identifier.getType().getBytes(), Condition.NO_CON,
          Instr.getTargetReg(), AddrMode.buildAddrWithOffset(Instr.SP, offset));
      instrs.add(loadVar);

      if (identifier instanceof VarID) {
        if (check && !allTypesSupported) {
          // TODO: Can we just freely use R0 and R1? Need to save?

          // Get addr into R0
          instrs.add(
              new ADD(false, Instr.R0, Instr.SP, AddrMode.buildImm(offset)));
          // Load typeNumber (byte) from "box" into R0
          instrs.add(new LDR(-Instr.BYTE_SIZE, Condition.NO_CON, Instr.R0,
              AddrMode.buildAddrWithOffset(Instr.R0, Instr.WORD_SIZE)));
          // Load actual typeNumber needed
          instrs.add(new MOV(Condition.NO_CON, Instr.R1,
              AddrMode.buildImm(dynamicTypeNeeded)));
          // Jump to dynamic type check
          BackEndGenerator.addToPreDefFuncs(Label.P_DYNAMIC_TYPE_CHECK);
          instrs.add(
              new BRANCH(true, Condition.NO_CON, Label.P_DYNAMIC_TYPE_CHECK));
        }

      }

      Instr.addToCurLabel(instrs);
    }
  }
}
