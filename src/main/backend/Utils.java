package backend;

import backend.instructions.*;
import backend.instructions.ADD;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.POP;
import backend.instructions.PUSH;
import frontend.symboltable.*;

import java.util.ArrayList;
import java.util.List;

public class Utils {

  public static String getAssignValue(Identifier identifier, String value) {
    if (identifier instanceof IntID || identifier instanceof StringID) {
      return "=" + value;
    } else {
      return "#" + value;
    }
  }

  public static String getEffectiveAddr(String reg, int offset) {
    StringBuilder output = new StringBuilder("[").append(reg);
    String offsetTag =
        offset == 0 ? "" : new StringBuilder(", #").append(offset).toString();
    output.append(offsetTag).append("]");
    return output.toString();
  }

  // end-routine function - not sure if this is valid
  // ADD sp, sp, #stackSize
  // LDR r0 =0
  // POP {pc}
  public static List<Instr> getEndRoutine(SymbolTable symtab) {
    List<Instr> instrs = new ArrayList<>();
    String stackSize = "#" + symtab.getSize();
    instrs.add(new ADD(false, Instr.SP, Instr.SP, stackSize));
    instrs.add(new LDR(4, "", Instr.R0, "=0"));
    instrs.add(new POP(Instr.PC));
    return instrs;
  }

  public static BRANCH getPrintBranch(TypeID type) {
    BRANCH brInstr = null;
    if (type instanceof IntID) {
      brInstr = new BRANCH(true, "", "p_print_int");
    } else if (type instanceof BoolID) {
      brInstr = new BRANCH(true, "", "p_print_bool");
    } else if (type instanceof CharID) {
      brInstr = new BRANCH(true, "", "putchar");
    } else if (type instanceof StringID) {
      brInstr = new BRANCH(true, "", "p_print_string");
    } else if (type instanceof PairID || type instanceof ArrayID) {
      brInstr = new BRANCH(true, "", "p_print_reference");
    }
    // brInstr should not be null
    return brInstr;
  }

//  public static List<Instr> getPreDefFunc(String func) {
//    List<Instr> instrs = new ArrayList<>();
//    if (func.equals("p_print_string")) {
//      instrs.add(new PUSH(Instr.LR));
//      instrs.add(new POP(Instr.PC));
//    }
//    return instrs;
//  }

}
