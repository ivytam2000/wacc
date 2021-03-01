package frontend.abstractsyntaxtree.assignments;

import backend.BackEndGenerator;
import backend.instructions.ADD;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.ArrayElemAST;
import frontend.abstractsyntaxtree.pairs.PairElemAST;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class AssignLHSAST extends Node {

  private Node assignNode = null;
  private SymbolTable symtab = null;
  private final String assignName;

  public AssignLHSAST(SymbolTable symtab, Node assignNode, String assignName) {
    super(assignNode.getIdentifier());
    this.symtab = symtab;
    this.assignNode = assignNode;
    this.assignName = assignName;
  }

  public AssignLHSAST(SymbolTable symtab, String assignName) {
    super(symtab.lookupAll(assignName));
    this.assignName = assignName;
  }

  public String getIdentName() {
    return assignName;
  }

  public Node getAssignNode() {
    return assignNode;
  }

  @Override
  public void check() {}

  @Override
  public List<Instr> toAssembly() {
    assert (assignNode != null);
    assert (symtab != null);

    List<Instr> instrs = new ArrayList<>();
    if (assignNode instanceof ArrayElemAST) {
      String fstReg = Instr.getTargetReg();
      // Get pointer to array
      instrs.add(new ADD(false, fstReg, Instr.SP, "#" + symtab.getStackOffset(assignName)));
      String sndReg = Instr.incDepth();

      List<Node> exprs = ((ArrayElemAST) assignNode).getExprs();
      for (Node e : exprs) {
        // Evaluate the index
        instrs.addAll(e.toAssembly());
        // Get the size of array
        instrs.add(new LDR(fstReg, fstReg, 0));
        // Check size
        instrs.add(new MOV("", Instr.R0, sndReg));
        instrs.add(new MOV("", Instr.R1, fstReg));
        BackEndGenerator.addToPreDefFunc("p_check_array_bounds");
        instrs.add(new BRANCH(true, "", "p_check_array_bounds"));
        // Go to first element (0th element is size)
        int size = assignNode.getIdentifier().getType().getBytes();
        instrs.add(new ADD(false, fstReg, fstReg, "#" + size));
        // Index to the target element
        instrs.add(new ADD(false, fstReg, fstReg, sndReg, size > 1 ? "LSL #" + size / 2 : ""));
      }

      Instr.decDepth();
    } else { //Pair
      String reg = Instr.getTargetReg();
      instrs.add(new LDR(reg, Instr.SP, symtab.getStackOffset(assignName)));
      instrs.add(new MOV("", Instr.R0, reg));
      BackEndGenerator.addToPreDefFunc("p_check_null_pointer");
      instrs.add(new BRANCH(true, "", "p_check_null_pointer"));
      instrs.add(new LDR(reg, reg, ((PairElemAST) assignNode).getFirst() ? 0 : assignNode.getIdentifier().getType().getBytes()));
    }
    return instrs;
  }
}