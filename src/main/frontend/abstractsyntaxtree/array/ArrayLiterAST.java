package frontend.abstractsyntaxtree.array;

import antlr.WaccParser.ArrayLiterContext;
import backend.BackEndGenerator;
import backend.Utils;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import backend.instructions.STR;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.BoolLiterAST;
import frontend.abstractsyntaxtree.expressions.CharLiterAST;
import frontend.abstractsyntaxtree.expressions.IdentExprAST;
import frontend.abstractsyntaxtree.expressions.IntLiterAST;
import frontend.abstractsyntaxtree.expressions.StrLiterAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

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

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instructions = new ArrayList<>();

    String lengthOfArray = "=" + children.size();
    Identifier childrenType = children.get(0).getIdentifier();
    int bytesNeeded = childrenType.getType().getBytes();
    String byteOfArray = "=" + (4 + children.size() * bytesNeeded);
    instructions.add(new LDR(Instr.R0, byteOfArray, true));

    // add malloc branch to allocate memory, should be called in var dec
    instructions.add(new BRANCH(true, "", "malloc"));

    // mov r4 to r0
    instructions.add(new MOV("", Instr.R4, Instr.R0));

    for (int i = 0; i < children.size(); i++) {
      Node curr_expr = children.get(i);
      Identifier curr_ident = curr_expr.getIdentifier();

      String value = "";
      if (curr_expr instanceof IntLiterAST) {
        value = ((IntLiterAST) curr_expr).getVal();
      } else if (curr_expr instanceof CharLiterAST) {
        value = ((CharLiterAST) curr_expr).getVal();
      } else if (curr_expr instanceof StrLiterAST) {
        curr_expr.toAssembly();
        int messageIndex = BackEndGenerator.addToDataSegment(((StrLiterAST) curr_expr).getVal());
        value = "msg" + messageIndex;
      } else if (curr_expr instanceof BoolLiterAST) {
        value = ((BoolLiterAST) curr_expr).getVal();
      } else if (curr_expr instanceof IdentExprAST) {
        // contains array and pairs
        String identName = ((IdentExprAST) curr_expr).getName();
        // get stack offset from symbol table
        value = Integer.toString(symtab.getStackOffset(identName));
      }
      value = Utils.getAssignValue(curr_ident, value);
      instructions.add(new LDR(curr_ident.getType().getBytes(), "", Instr.R5, value, 0,true));
      int offset = (i + 1) * bytesNeeded;
      instructions.add(new STR(curr_ident.getType().getBytes(), "", Instr.R5, Instr.R4, offset));
    }

    instructions.add(new LDR(Instr.R5, lengthOfArray, true));
    instructions.add(new STR(Instr.R5, Instr.R4));

    return instructions;
  }
}
