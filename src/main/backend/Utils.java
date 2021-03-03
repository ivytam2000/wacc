package backend;

import backend.instructions.*;
import frontend.symboltable.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {

  private static final int TEN_BITS = 1024;
  private static final String INT_MSG = "%d\\0";
  private static final String STRING_MSG = "%.*s\\0";
  private static final String LN_MSG = "\\0";
  private static final String TRUE_MSG = "true\\0";
  private static final String FALSE_MSG = "false\\0";
  private static final String OVERFLOW_MSG
      = "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n";
  private static final String DIV_BY_ZERO_MSG
      = "DivideByZeroError: divide or modulo by zero\\n\\0";
  private static final String NEG_INDEX_MSG
      = "ArrayIndexOutOfBoundsError: negative index\\n\\0";
  private static final String BIG_INDEX_MSG
      = "ArrayIndexOutOfBoundsError: index too large\\n\\0";
  private static final String NULL_MSG
      = "NullReferenceError: dereference a null reference\\n\\0";

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

  public static List<Instr> getStartRoutine(SymbolTable symtab) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    int size = symtab.getSize();
    if (size > 0) {
      // if greater than 10 bits
      while (size > TEN_BITS) {
        size = size - TEN_BITS;
        instrs.add(new SUB(false, false, Instr.SP, "#" + TEN_BITS));
      }
      instrs.add(new SUB(false, false, Instr.SP, "#" + size));
    }
    return instrs;
  }

  // end-routine function - not sure if this is valid
  // ADD sp, sp, #stackSize
  // LDR r0 =0
  // POP {pc}
  public static List<Instr> getEndRoutine(SymbolTable symtab) {
    List<Instr> instrs = new ArrayList<>();
    int stackSize = symtab.getSize();
    if (stackSize > 0) {
      // if greater than 10 bits
      while (stackSize > TEN_BITS ) {
        stackSize = stackSize - TEN_BITS;
        instrs.add(new ADD(false, Instr.SP, Instr.SP, "#" + TEN_BITS));
      }
      instrs.add(new ADD(false, Instr.SP, Instr.SP, "#" + stackSize));
    }
    instrs.add(new LDR(Instr.R0, "0"));
    instrs.add(new POP(Instr.PC));
    instrs.add(new LTORG());
    return instrs;
  }

  public static BRANCH getPrintBranch(TypeID type) {
    BRANCH brInstr = null;
    if (type instanceof IntID) {
      BackEndGenerator.addToPreDefFuncs("p_print_int");
      brInstr = new BRANCH(true, "", "p_print_int");
    } else if (type instanceof BoolID) {
      BackEndGenerator.addToPreDefFuncs("p_print_bool");
      brInstr = new BRANCH(true, "", "p_print_bool");
    } else if (type instanceof CharID) {
      BackEndGenerator.addToPreDefFuncs("putchar");
      brInstr = new BRANCH(true, "", "putchar");
    } else if (type instanceof StringID) {
      BackEndGenerator.addToPreDefFuncs("p_print_string");
      brInstr = new BRANCH(true, "", "p_print_string");
    } else if (type instanceof PairID || type instanceof ArrayID) {
      BackEndGenerator.addToPreDefFuncs("p_print_reference");
      brInstr = new BRANCH(true, "", "p_print_reference");
    }
    // brInstr should not be null
    return brInstr;
  }


  // ------ PRE-DEFINED FUNCTIONS ------

  public static Map<String, List<Instr>> getPreDefFunc(List<String> pdfs) {
    // Keep track of unique preDefFunc that has already been added
    Set<String> pdfTracker = new HashSet<>();
    Map<String, List<Instr>> preDefFuncInstrs = new HashMap<>();

    for (int i = 0; i < pdfs.size(); i++) {
      String f = pdfs.get(i);
      // Prevent duplicates
      if (!pdfTracker.contains(f)) {
        // Get method by name instead of a huge if-else / switch case
        try {
          Method method = Utils.class.getDeclaredMethod(f, Map.class);
          method.invoke(null, preDefFuncInstrs);
          pdfTracker.add(f);
        } catch (NoSuchMethodException e) {
          // Helps with debugging (which preDefFuncs have not been defined yet)
          System.err.println("No such predefined method! " + f);
        } catch (IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }

    return preDefFuncInstrs;
  }

  private static void p_print_string(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new LDR(Instr.R1, Instr.R0, 0));
    instrs.add(new ADD(false, Instr.R2, Instr.R0, "#4"));
    instrs.add(new LDR(Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(STRING_MSG)));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, "#4"));
    instrs.add(new BRANCH(true, "", "printf"));
    instrs.add(new MOV("", Instr.R0, "#0"));
    instrs.add(new BRANCH(true, "", "fflush"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_print_string", instrs);
  }

  private static void p_throw_overflow_error(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new LDR(Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(OVERFLOW_MSG)));
    BackEndGenerator.addToPreDefFuncs("p_throw_runtime_error");
    instrs.add(new BRANCH(true, "", "p_throw_runtime_error"));

    pdf.put("p_throw_overflow_error", instrs);
  }

  private static void p_throw_runtime_error(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    BackEndGenerator.addToPreDefFuncs("p_print_string");
    instrs.add(new BRANCH(true, "", "p_print_string"));
    instrs.add(new MOV("", Instr.R0, "#-1"));
    instrs.add(new BRANCH(true, "", "exit"));

    pdf.put("p_throw_runtime_error", instrs);
  }

  private static void p_print_int(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new MOV("", Instr.R1, Instr.R0));
    instrs.add(new LDR(Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(INT_MSG)));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, "#4"));
    instrs.add(new BRANCH(true, "", "printf"));
    instrs.add(new MOV("", Instr.R0, "#0"));
    instrs.add(new BRANCH(true, "", "fflush"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_print_int", instrs);
  }

  private static void p_print_ln(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new LDR(Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(LN_MSG)));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, "#4"));
    instrs.add(new BRANCH(true, "", "puts"));
    instrs.add(new MOV("", Instr.R0, "#0"));
    instrs.add(new BRANCH(true, "", "fflush"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_print_ln", instrs);
  }

  private static void p_print_bool(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, "#0"));
    instrs.add(new LDR(4, "NE", Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(TRUE_MSG)));
    instrs.add(new LDR(4, "EQ", Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(FALSE_MSG)));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, "#4"));
    instrs.add(new BRANCH(true, "", "printf"));
    instrs.add(new MOV("", Instr.R0, "#0"));
    instrs.add(new BRANCH(true, "", "fflush"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_print_bool", instrs);
  }

  private static void p_check_divide_by_zero(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R1, "#0"));
    instrs.add(new LDR(4, "EQ", Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(DIV_BY_ZERO_MSG)));
    BackEndGenerator.addToPreDefFuncs("p_throw_runtime_error");
    instrs.add(new BRANCH(true, "EQ", "p_throw_runtime_error"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_check_divide_by_zero", instrs);
  }

  private static void p_check_array_bounds(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, "#0"));
    instrs.add(new LDR(4, "LT", Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(NEG_INDEX_MSG)));
    BackEndGenerator.addToPreDefFuncs("p_throw_runtime_error");
    instrs.add(new BRANCH(true,"LT", "p_throw_runtime_error"));
    instrs.add(new LDR(Instr.R1, Instr.R1,0));
    instrs.add(new CMP(Instr.R0, Instr.R1));
    instrs.add(new LDR(4, "CS", Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(BIG_INDEX_MSG)));
    instrs.add(new BRANCH(true,"CS", "p_throw_runtime_error"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_check_array_bounds", instrs);
  }

  private static void p_check_null_pointer(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, "#0"));
    instrs.add(new LDR(4, "EQ", Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(NULL_MSG)));
    BackEndGenerator.addToPreDefFuncs("p_throw_runtime_error");
    instrs.add(new BRANCH(true,"EQ", "p_throw_runtime_error"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_check_null_pointer", instrs);
  }

  private static void p_read_int(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new MOV("", Instr.R1, Instr.R0));
    instrs.add(new LDR(Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(INT_MSG)));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, "#4"));
    instrs.add(new BRANCH(true,"", "scanf"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_read_int", instrs);
  }

  private static void p_free_pair(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, "#0"));
    instrs.add(new LDR(4, "EQ", Instr.R0, "msg_" + BackEndGenerator.addToDataSegment(NULL_MSG)));
    BackEndGenerator.addToPreDefFuncs("p_throw_runtime_error");
    instrs.add(new BRANCH(true, "EQ", "p_throw_runtime_error"));
    instrs.add(new PUSH(Instr.R0));
    instrs.add(new LDR(Instr.R0, Instr.R0, 0));
    instrs.add(new BRANCH(true,"", "free"));
    instrs.add(new LDR(Instr.R0, Instr.SP, 0));
    instrs.add(new LDR(Instr.R0, Instr.R0, 4));
    instrs.add(new BRANCH(true,"", "free"));
    instrs.add(new POP(Instr.R0));
    instrs.add(new BRANCH(true,"", "free"));
    instrs.add(new POP(Instr.PC));

    pdf.put("p_free_pair", instrs);
  }

}
