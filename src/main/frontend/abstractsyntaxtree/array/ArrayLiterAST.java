package frontend.abstractsyntaxtree.array;

import antlr.WaccParser.ArrayLiterContext;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import backend.instructions.STR;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class ArrayLiterAST extends Node {

  private final List<Node> children;
  private final ArrayLiterContext ctx;
  private final SymbolTable symtab;

  public ArrayLiterAST(
      SymbolTable symtab, Identifier identifier, List<Node> children, ArrayLiterContext ctx) {
    super(identifier);
    this.children = children;
    this.ctx = ctx;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    if (!children.isEmpty()) {
      String type = children.get(0).getIdentifier().getType().getTypeName();
      int line = ctx.getStart().getLine();
      for (int i = 1; i < children.size(); i++) {
        Node child = children.get(i);
        String childType = child.getIdentifier().getType().getTypeName();
        if (!childType.equals(type)) {
          SemanticErrorCollector.addArrayInconsistentTypes(
              line, ctx.expr(i).getStart().getCharPositionInLine(), i, type,
              childType);
        }
      }
    }
  }

  //TODO: If we had nested arrays, this fails
  @Override
  public void toAssembly() {
    String lengthOfArray = "" + children.size();
    int bytesNeeded = 0;

    if (!children.isEmpty()) {
      Identifier childrenType = children.get(0).getIdentifier();
      bytesNeeded = childrenType.getType().getBytes();
    }

    String byteOfArray = "" + (4 + children.size() * bytesNeeded);
    addToCurLabel(new LDR(Instr.R0, byteOfArray));

    // add malloc branch to allocate memory, should be called in var dec
    addToCurLabel(new BRANCH(true, "", "malloc"));

    // mov r4 to r0
    addToCurLabel(new MOV("", Instr.R4, Instr.R0));

    for (int i = 0; i < children.size(); i++) {
      Node curr_expr = children.get(i);
      Identifier curr_ident = curr_expr.getIdentifier();

      String sndReg = Instr.incDepth();
      curr_expr.toAssembly();
      String fstReg = Instr.decDepth();

      int offset = (i + 1) * bytesNeeded;
      addToCurLabel(new STR(curr_ident.getType().getBytes(), "", sndReg, fstReg, offset));
    }

    addToCurLabel(new LDR(Instr.R5, lengthOfArray));
    addToCurLabel(new STR(Instr.R5, Instr.R4, 0));
  }
}
