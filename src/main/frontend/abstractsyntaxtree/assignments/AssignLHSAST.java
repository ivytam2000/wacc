package frontend.abstractsyntaxtree.assignments;

import backend.BackEndGenerator;
import backend.instructions.ADD;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.ArrayElemAST;
import frontend.abstractsyntaxtree.pairs.PairElemAST;
import frontend.symboltable.CharID;
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
    this.symtab = symtab;
    this.assignName = assignName;
  }

  public String getIdentName() {
    return assignName;
  }

  public Node getAssignNode() {
    return assignNode;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
    assert (symtab != null);

    if (assignNode == null) { // Variable
      Instr.addToCurLabel(new ADD(false, Instr.R4, Instr.SP,
          AddrMode.buildImm(symtab.getStackOffset(assignName))));
      return;
    }

    List<Instr> instrs = new ArrayList<>();
    if (assignNode instanceof ArrayElemAST) {
      String fstReg = Instr.getTargetReg();
      // Get pointer to array
      Instr.addToCurLabel(new ADD(false, fstReg, Instr.SP,
          AddrMode.buildImm(symtab.getStackOffset(assignName))));
      String sndReg = Instr.incDepth();

      List<Node> exprs = ((ArrayElemAST) assignNode).getExprs();
      for (Node e : exprs) {
        // Evaluate the index
        e.toAssembly();
        // Get the size of array
        instrs.add(new LDR(fstReg, AddrMode.buildAddr(fstReg)));
        // Check size
        instrs.add(new MOV("", Instr.R0, AddrMode.buildReg(sndReg)));
        instrs.add(new MOV("", Instr.R1, AddrMode.buildReg(fstReg)));
        BackEndGenerator.addToPreDefFuncs("p_check_array_bounds");
        instrs.add(new BRANCH(true, "", "p_check_array_bounds"));
        // Go to first element (0th element is size)
        int size;
        if (assignNode instanceof ArrayElemAST) {
          if (assignNode.getIdentifier().getType() instanceof CharID) {
            size = 4;
          } else {
            size = assignNode.getIdentifier().getType().getBytes();
          }
        } else {
          size = assignNode.getIdentifier().getType().getBytes();
        }
        // Index to the target element
        instrs.add(new ADD(false, fstReg, fstReg, AddrMode.buildImm(size)));
        if (size > 1) {
          instrs.add(new ADD(false, fstReg, fstReg, AddrMode.buildReg(sndReg),
              AddrMode.buildImmWithLSL(size / 2)));
        } else {
          instrs.add(new ADD(false, fstReg, fstReg, AddrMode.buildReg(sndReg)));
        }
      }

      Instr.decDepth();
    } else { // Pair
      String reg = Instr.getTargetReg();
      instrs.add(new LDR(reg, AddrMode
          .buildAddrWithOffset(Instr.SP, symtab.getStackOffset(assignName))));
      instrs.add(new MOV("", Instr.R0, AddrMode.buildReg(reg)));
      BackEndGenerator.addToPreDefFuncs("p_check_null_pointer");
      instrs.add(new BRANCH(true, "", "p_check_null_pointer"));
      instrs.add(new LDR(reg, AddrMode.buildAddrWithOffset(reg,
          ((PairElemAST) assignNode).getFirst() ? 0 : 4)));
    }
    Instr.addToCurLabel(instrs);
  }
}
