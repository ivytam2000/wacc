package frontend.abstractsyntaxtree.assignments;

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

  public AssignRHSAST(Identifier identifier, SymbolTable symtab,
      List<Node> children) {
    super(identifier);
    this.symtab = symtab;
    this.children = children;
  }

  public AssignRHSAST(Identifier identifier, SymbolTable symtab) {
    super(identifier);
    this.symtab = symtab;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
   // List<Instr> instructions = new ArrayList<>();
    if (identifier instanceof PairID) {
      // malloc pair struct
      addToCurLabel(new LDR(Instr.R0, "8"));
      addToCurLabel(new BRANCH(true, "", "malloc"));
      addToCurLabel(new MOV("", Instr.R4, Instr.R0));
      // Need to increment register as we using R4
      Instr.incDepth();

      // first pair elem
      children.get(0).toAssembly();
      TypeID child_1 = children.get(0).getIdentifier().getType();
      addToCurLabel(new LDR(Instr.R0, String.valueOf(child_1.getBytes())));
      addToCurLabel(new BRANCH(true, "", "malloc"));
      addToCurLabel(new STR(child_1.getBytes(), "", Instr.R5, Instr.R0, 0));
      addToCurLabel(new STR(Instr.R0, Instr.R4, 0));

      // second pair elem
      // if children is NOT an array of pairs elem
      if(children.size() == 2){
        children.get(1).toAssembly();
        TypeID child_2 = children.get(1).getIdentifier().getType();
        addToCurLabel(new LDR(Instr.R0, String.valueOf(child_2.getBytes())));
        addToCurLabel(new BRANCH(true, "", "malloc"));
        addToCurLabel(new STR(child_2.getBytes(),"", Instr.R5, Instr.R0, 0));
        addToCurLabel(new STR(Instr.R0, Instr.R4, 4));
      }

      //addToCurLabel(instructions);

    } else {
      for (Node expr: children) {
        expr.toAssembly();
      }
    }
  }
}
