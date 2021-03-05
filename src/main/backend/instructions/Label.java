package backend.instructions;

public abstract class Label {

  // Main program label
  public static final String MAIN = "main";

  // Pre defined functions
  public static final String P_PRINT_INT = "p_print_int";
  public static final String P_PRINT_BOOL = "p_print_bool";
  public static final String P_PRINT_STRING = "p_print_string";
  public static final String P_PRINT_REFERENCE = "p_print_reference";
  public static final String P_THROW_RUNTIME_ERROR = "p_throw_runtime_error";
  public static final String P_READ_INT = "p_read_int";
  public static final String P_CHECK_NULL_POINTER = "p_check_null_pointer";
  public static final String P_FREE_PAIR = "p_free_pair";
  public static final String P_FREE_ARRAY = "p_free_array";
  public static final String P_READ_CHAR = "p_read_char";
  public static final String P_CHECK_ARRAY_BOUNDS = "p_check_array_bounds";
  public static final String P_CHECK_DIVIDE_BY_ZERO = "p_check_divide_by_zero";
  public static final String P_THROW_OVERFLOW_ERROR = "p_throw_overflow_error";
  public static final String P_PRINT_LN = "p_print_ln";

  // Library functions
  public static final String PUTCHAR = "putchar";
  public static final String PRINTF = "printf";
  public static final String FFLUSH = "fflush";
  public static final String PUTS = "puts";
  public static final String SCANF = "scanf";
  public static final String FREE = "free";
  public static final String MALLOC = "malloc";
  public static final String EXIT = "exit";
  public static final String DIV_FUNC = "__aeabi_idiv";
  public static final String MOD_FUNC = "__aeabi_idivmod";
}