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

  // Size constant for stack growth
  private static final int TEN_BITS = 1024;

  // String constants for messages
  private static final String INT_MSG = "%d\\0";
  private static final String TRUE_MSG = "true\\0";
  private static final String FALSE_MSG = "false\\0";
  private static final String CHAR_MSG
      = " %c\\0";
  private static final String PTR_MSG
      = "%p\\0";
  private static final String STRING_MSG = "%.*s\\0";
  private static final String LN_MSG = "\\0";
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
  private static final String INCOMPATIBLE_DYN_VAR_MSG
      = "DynamicTypeError: dynamic variable has incompatible type\\n\\0";

  // Generates start routine instructions
  public static List<Instr> getStartRoutine(SymbolTable symtab,
      boolean backEndGenerator) {
    List<Instr> instrs = new ArrayList<>();

    // push LR
    if (backEndGenerator) {
      instrs.add(new PUSH(Instr.LR));
    }

    // stack growth (limited to 10 bits at a time)
    int size = symtab.getSize();
    if (size > 0) {
      // If greater than 10 bits
      while (size > TEN_BITS) {
        size = size - TEN_BITS;
        instrs.add(new SUB(false, false, Instr.SP, Instr.SP,
            AddrMode.buildImm(TEN_BITS)));
      }
      instrs.add(
          new SUB(false, false, Instr.SP, Instr.SP, AddrMode.buildImm(size)));
    }

    return instrs;
  }

  // Returns the end routine instructions for every scope
  public static List<Instr> getEndRoutine(SymbolTable symtab,
      boolean backEndGenerator) {
    List<Instr> instrs = new ArrayList<>();

    int stackSize = 0;
    // If returning from function, need to destroy parent stacks
    if (symtab.getFuncContext()) {
      SymbolTable temp = symtab;
      while (!temp.isTopLevel()) {
        stackSize += temp.getSize();
        temp = temp.getParent();
      }
    } else {
      stackSize = symtab.getSize();
    }

    // Destroy stack (limited to 10 bits at a time)
    if (stackSize > 0) {
      // If greater than 10 bits
      while (stackSize > TEN_BITS) {
        stackSize = stackSize - TEN_BITS;
        instrs.add(
            new ADD(false, Instr.SP, Instr.SP, AddrMode.buildImm(TEN_BITS)));
      }
      instrs.add(
          new ADD(false, Instr.SP, Instr.SP, AddrMode.buildImm(stackSize)));
    }

    // End routine (LDR r0 =0; POP {pc}; .ltorg;)
    if (backEndGenerator) {
      instrs.add(new LDR(Instr.R0, AddrMode.buildVal(0)));
      instrs.add(new POP(Instr.PC));
      instrs.add(new LTORG());
    }

    return instrs;
  }

  /**
   * Adds functions to the list of pre-defined functions that need to be in the
   * assembly file depending on the type of identifier we are dealing with, e.g.
   * integers need to print in case of an overflow.
   */
  public static BRANCH getPrintBranch(TypeID type) {
    BRANCH brInstr = null;
    if (type instanceof IntID) {
      BackEndGenerator.addToPreDefFuncs(Label.P_PRINT_INT);
      brInstr = new BRANCH(true, Condition.NO_CON, Label.P_PRINT_INT);
    } else if (type instanceof BoolID) {
      BackEndGenerator.addToPreDefFuncs(Label.P_PRINT_BOOL);
      brInstr = new BRANCH(true, Condition.NO_CON, Label.P_PRINT_BOOL);
    } else if (type instanceof CharID) {
      brInstr = new BRANCH(true, Condition.NO_CON, Label.PUTCHAR);
    } else if (type instanceof StringID) {
      BackEndGenerator.addToPreDefFuncs(Label.P_PRINT_STRING);
      brInstr = new BRANCH(true, Condition.NO_CON, Label.P_PRINT_STRING);
    } else if (type instanceof OptionalPairID) {
      BackEndGenerator.addToPreDefFuncs(Label.P_PRINT_REFERENCE);
      brInstr = new BRANCH(true, Condition.NO_CON, Label.P_PRINT_REFERENCE);
    } else if (type instanceof ArrayID) {
      if (((ArrayID) type).getElemType() instanceof CharID) {
        BackEndGenerator.addToPreDefFuncs(Label.P_PRINT_STRING);
        brInstr = new BRANCH(true, Condition.NO_CON, Label.P_PRINT_STRING);
      } else {
        BackEndGenerator.addToPreDefFuncs(Label.P_PRINT_REFERENCE);
        brInstr = new BRANCH(true, Condition.NO_CON, Label.P_PRINT_REFERENCE);
      }
    }
    // brInstr should not be null
    return brInstr;
  }

  //****** METHODS FOR PRE DEFINED FUNCTIONS ******

  /**
   * Returns a map of every pre-defined function and their instructions.
   */
  public static Map<String, List<Instr>> getPreDefFunc(List<String> pdfs) {
    // Keep track of unique preDefFunc that has already been added
    Set<String> pdfTracker = new HashSet<>();
    Map<String, List<Instr>> preDefFuncInstrs = new HashMap<>();

    for (int i = 0; i < pdfs.size(); i++) {
      String f = pdfs.get(i);
      // Prevent duplicates
      if (!pdfTracker.contains(f)) {
        // Get method by name through reflection instead of a huge if-else / switch case
        try {
          Method method = Utils.class.getDeclaredMethod(f, Map.class);
          method.invoke(null, preDefFuncInstrs);
          pdfTracker.add(f);
        } catch (NoSuchMethodException e) {
          // Helps with debugging (for preDefFuncs which have not been defined yet)
          System.err.println("No such predefined method! " + f);
        } catch (IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }

    return preDefFuncInstrs;
  }

  private static List<Instr> printf() {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new ADD(false, Instr.R0, Instr.R0, AddrMode.buildImm(4)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.PRINTF));
    instrs.add(new MOV(Condition.NO_CON, Instr.R0, AddrMode.buildImm(0)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.FFLUSH));
    return instrs;
  }

  private static void p_print_string(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new LDR(Instr.R1, AddrMode.buildAddr(Instr.R0)));
    instrs.add(new ADD(false, Instr.R2, Instr.R0, AddrMode.buildImm(4)));
    instrs.add(new LDR(Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(STRING_MSG))));
    instrs.addAll(printf());
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_PRINT_STRING, instrs);
  }

  private static void p_throw_overflow_error(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new LDR(Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(OVERFLOW_MSG))));
    BackEndGenerator.addToPreDefFuncs(Label.P_THROW_RUNTIME_ERROR);
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.P_THROW_RUNTIME_ERROR));

    pdf.put(Label.P_THROW_OVERFLOW_ERROR, instrs);
  }

  private static void p_throw_runtime_error(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    BackEndGenerator.addToPreDefFuncs(Label.P_PRINT_STRING);
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.P_PRINT_STRING));
    instrs.add(new MOV(Condition.NO_CON, Instr.R0, AddrMode.buildImm(-1)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.EXIT));

    pdf.put(Label.P_THROW_RUNTIME_ERROR, instrs);
  }

  private static void p_print_int(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new MOV(Condition.NO_CON, Instr.R1, AddrMode.buildReg(Instr.R0)));
    instrs.add(new LDR(Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(INT_MSG))));
    instrs.addAll(printf());
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_PRINT_INT, instrs);
  }

  private static void p_print_ln(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new LDR(Instr.R0,
        AddrMode.buildStringVal(BackEndGenerator.addToDataSegment(LN_MSG))));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, AddrMode.buildImm(4)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.PUTS));
    instrs.add(new MOV(Condition.NO_CON, Instr.R0, AddrMode.buildImm(0)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.FFLUSH));
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_PRINT_LN, instrs);
  }

  private static void p_print_bool(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, AddrMode.buildImm(0)));
    instrs.add(new LDR(4, Condition.NE, Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(TRUE_MSG))));
    instrs.add(new LDR(4, Condition.EQ, Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(FALSE_MSG))));
    instrs.addAll(printf());
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_PRINT_BOOL, instrs);
  }

  private static void p_check_divide_by_zero(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R1, AddrMode.buildImm(0)));
    instrs.add(new LDR(4, Condition.EQ, Instr.R0, AddrMode.buildStringVal(
        BackEndGenerator.addToDataSegment(DIV_BY_ZERO_MSG))));
    BackEndGenerator.addToPreDefFuncs(Label.P_THROW_RUNTIME_ERROR);
    instrs.add(new BRANCH(true, Condition.EQ, Label.P_THROW_RUNTIME_ERROR));
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_CHECK_DIVIDE_BY_ZERO, instrs);
  }

  private static void p_check_array_bounds(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, AddrMode.buildImm(0)));
    instrs.add(new LDR(4, Condition.LT, Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(NEG_INDEX_MSG))));
    BackEndGenerator.addToPreDefFuncs(Label.P_THROW_RUNTIME_ERROR);
    instrs.add(new BRANCH(true, Condition.LT, Label.P_THROW_RUNTIME_ERROR));
    instrs.add(new LDR(Instr.R1, AddrMode.buildAddr(Instr.R1)));
    instrs.add(new CMP(Instr.R0, AddrMode.buildReg(Instr.R1)));
    instrs.add(new LDR(4, Condition.CS, Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(BIG_INDEX_MSG))));
    instrs.add(new BRANCH(true, Condition.CS, Label.P_THROW_RUNTIME_ERROR));
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_CHECK_ARRAY_BOUNDS, instrs);
  }

  private static void p_check_null_pointer(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, AddrMode.buildImm(0)));
    instrs.add(new LDR(4, Condition.EQ, Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(NULL_MSG))));
    BackEndGenerator.addToPreDefFuncs(Label.P_THROW_RUNTIME_ERROR);
    instrs.add(new BRANCH(true, Condition.EQ, Label.P_THROW_RUNTIME_ERROR));
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_CHECK_NULL_POINTER, instrs);
  }

  private static void p_read_int(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new MOV(Condition.NO_CON, Instr.R1, AddrMode.buildReg(Instr.R0)));
    instrs.add(new LDR(Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(INT_MSG))));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, AddrMode.buildImm(4)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.SCANF));
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_READ_INT, instrs);
  }

  private static void p_free_pair(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new CMP(Instr.R0, AddrMode.buildImm(0)));
    instrs.add(new LDR(4, Condition.EQ, Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(NULL_MSG))));
    BackEndGenerator.addToPreDefFuncs(Label.P_THROW_RUNTIME_ERROR);
    instrs.add(new BRANCH(true, Condition.EQ, Label.P_THROW_RUNTIME_ERROR));
    instrs.add(new PUSH(Instr.R0));
    instrs.add(new LDR(Instr.R0, AddrMode.buildAddr(Instr.R0)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.FREE));
    instrs.add(new LDR(Instr.R0, AddrMode.buildAddr(Instr.SP)));
    instrs.add(new LDR(Instr.R0, AddrMode.buildAddrWithOffset(Instr.R0, 4)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.FREE));
    instrs.add(new POP(Instr.R0));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.FREE));
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_FREE_PAIR, instrs);
  }

  private static void p_read_char(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new MOV(Condition.NO_CON, Instr.R1, AddrMode.buildReg(Instr.R0)));
    instrs.add(new LDR(Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(CHAR_MSG))));
    instrs.add(new ADD(false, Instr.R0, Instr.R0, AddrMode.buildImm(4)));
    instrs.add(new BRANCH(true, Condition.NO_CON, Label.SCANF));
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_READ_CHAR, instrs);
  }

  private static void p_print_reference(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));
    instrs.add(new MOV(Condition.NO_CON, Instr.R1, AddrMode.buildReg(Instr.R0)));
    instrs.add(new LDR(Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(PTR_MSG))));
    instrs.addAll(printf());
    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_PRINT_REFERENCE, instrs);
  }

  private static void p_dynamic_type_check(Map<String, List<Instr>> pdf) {
    List<Instr> instrs = new ArrayList<>();
    instrs.add(new PUSH(Instr.LR));

    instrs.add(new AND(Instr.R0, AddrMode.buildReg(Instr.R1)));
    instrs.add(new CMP(Instr.R0, AddrMode.buildImm(0)));
    instrs.add(new LDR(4, Condition.EQ, Instr.R0, AddrMode
        .buildStringVal(BackEndGenerator.addToDataSegment(INCOMPATIBLE_DYN_VAR_MSG))));
    BackEndGenerator.addToPreDefFuncs(Label.P_THROW_RUNTIME_ERROR);
    instrs.add(new BRANCH(true, Condition.EQ, Label.P_THROW_RUNTIME_ERROR));

    instrs.add(new POP(Instr.PC));

    pdf.put(Label.P_DYNAMIC_TYPE_CHECK, instrs);
  }
}
