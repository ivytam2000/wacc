package frontend.abstractsyntaxtree.assignments;

import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import backend.instructions.STR;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class AssignRHSAST extends Node {

  protected final SymbolTable symtab;
  protected List<Node> children;
  private final boolean isNewpair;

  public AssignRHSAST(Identifier identifier, SymbolTable symtab,
      List<Node> children, boolean isNewpair) {
    super(identifier);
    this.symtab = symtab;
    this.children = children;
    this.isNewpair = isNewpair;
  }

  public AssignRHSAST(Identifier identifier, SymbolTable symtab) {
    super(identifier);
    this.symtab = symtab;
    this.isNewpair = false;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
   // List<Instr> instructions = new ArrayList<>();
    if (identifier instanceof PairID) {
      // We only malloc if it's newpair
      // malloc pair struct
      if (isNewpair) {
        addToCurLabel(new LDR(Instr.R0, AddrMode.buildVal(8)));
        addToCurLabel(new BRANCH(true, "", "malloc"));
        addToCurLabel(new MOV("", Instr.R4, AddrMode.buildReg(Instr.R0)));
        // Need to increment register as we using R4
        Instr.incDepth();
      }

      // First pair elem
      children.get(0).toAssembly();
      if (isNewpair) {
        Instr.decDepth();

        TypeID child_1 = children.get(0).getIdentifier().getType();
        addToCurLabel(new LDR(Instr.R0, AddrMode.buildVal(child_1.getBytes())));
        addToCurLabel(new BRANCH(true, "", "malloc"));
        addToCurLabel(new STR(child_1.getBytes(), "", Instr.R5, AddrMode.buildAddr(Instr.R0)));
        addToCurLabel(new STR(Instr.R0, AddrMode.buildAddr(Instr.R4)));

        Instr.incDepth();
      }
      // Second pair elem
      // If children is NOT an array of pairs elem
      if(children.size() == 2){
        children.get(1).toAssembly();
        if (isNewpair) {
          Instr.decDepth();
          TypeID child_2 = children.get(1).getIdentifier().getType();
          addToCurLabel(new LDR(Instr.R0, AddrMode.buildVal(child_2.getBytes())));
          addToCurLabel(new BRANCH(true, "", "malloc"));
          addToCurLabel(new STR(child_2.getBytes(), "", Instr.R5, AddrMode.buildAddr(Instr.R0)));
          addToCurLabel(new STR(Instr.R0, AddrMode.buildAddrWithOffset(Instr.R4, 4)));
        }
      }

    } else {
      for (Node expr: children) {
        expr.toAssembly();
      }
    }
  }
}
