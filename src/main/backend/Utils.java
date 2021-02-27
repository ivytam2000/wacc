package backend;

import backend.instructions.ADD;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.POP;
import frontend.abstractsyntaxtree.AST;
import frontend.symboltable.Identifier;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class Utils {

  public static String getAssignValue(Identifier identifier, String value) {
    if (identifier instanceof IntID) {
      return "=" + value;
    } else {
      return "#" + value;
    }
  }

  // end-routine function - not sure if this is valid
  // ADD sp, sp, #stackSize
  // LDR r0 =0
  // POP {pc}
  public List<Instr> getEndRoutine(SymbolTable symtab){
    List<Instr> instrs = new ArrayList<>();
    String stackSize = "#" + symtab.getSize();
    instrs.add(new ADD(false, Instr.SP,Instr.SP, stackSize));
    instrs.add(new LDR(4,"", Instr.R0, "=0"));
    instrs.add(new POP(Instr.PC));
    return instrs;
  }

}
