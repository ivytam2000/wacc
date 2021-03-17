package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignRHSContext;
import antlr.WaccParser.Var_decl_statContext;
import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

import java.util.ArrayList;
import java.util.List;

public class VarDecAST extends Node {

  private static final int DYNAMIC_BOX_SIZE = 5;

  private final SymbolTable symtab;
  private TypeID decType;
  private final String varName;
  private final AssignRHSAST assignRHS;
  private final Var_decl_statContext ctx;
  private final AssignRHSContext rhsCtx;

  public VarDecAST(
      SymbolTable symtab,
      TypeID decType,
      String varName,
      AssignRHSAST assignRHS,
      Var_decl_statContext ctx) {
    super();
    this.symtab = symtab;
    this.decType = decType;
    this.varName = varName;
    this.ctx = ctx;
    this.rhsCtx = ctx.assignRHS();
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {
    symtab.incrementSize(decType instanceof VarID ? DYNAMIC_BOX_SIZE : decType.getBytes());

    // if function is not defined or class is not defined
    if (assignRHS.getIdentifier() == null) {
      return;
    }

    TypeID rhsType = assignRHS.getIdentifier().getType();

    int line = rhsCtx.getStart().getLine();
    int pos = rhsCtx.getStart().getCharPositionInLine();

    Identifier variable = symtab.lookup(varName);

    // VarDec of nested pairs
    if (decType instanceof PairID && rhsType instanceof PairID) {
      PairID pairDecType = (PairID) decType;
      PairID pairRhsType = (PairID) rhsType;

      // Fst is a pair, set type based on RHS if not null
      if (pairDecType.getFstType() instanceof PairID) {
        TypeID pairRhsTypeFst = pairRhsType.getFstType();
        if (!(pairRhsTypeFst instanceof NullID)) {
          pairDecType.setFst(pairRhsTypeFst);
        }
      }

      // Snd is a pair, set type based on RHS if not null
      if (pairDecType.getSndType() instanceof PairID) {
        TypeID pairRhsTypeSnd = pairRhsType.getSndType();
        if (!(pairRhsTypeSnd instanceof NullID)) {
          pairDecType.setSnd(pairRhsTypeSnd);
        }
      }
    }

    // Check if var is already declared unless it is a function name
    if (variable != null && !(variable instanceof FuncID)) {
      SemanticErrorCollector.addSymbolAlreadyDefined(varName, line, pos);
    }

    // Check if types are compatible
    if (!Utils.typeCompat(decType, rhsType)) {
      SemanticErrorCollector.addIncompatibleType(
          decType.getTypeName(), rhsType.getTypeName(), rhsCtx.getText(), line,
          pos);
    }

    symtab.add(varName, decType);
    setIdentifier(decType);
  }


  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    // Generate the offset of the variable
    int offset = symtab.getSmallestOffset() -
        (decType instanceof VarID ? DYNAMIC_BOX_SIZE : decType.getBytes());

    // Add the offset to the symbol table's hashmap of variables' offsets
    symtab.addOffset(varName, offset);
    assignRHS.toAssembly();

    // Stores the value in the offset stack address
    TypeID rhsType = assignRHS.getIdentifier().getType();
    STR strInstr = new STR(rhsType.getBytes(), Condition.NO_CON, Instr.R4,
        AddrMode.buildAddrWithOffset(Instr.SP, offset));
    instrs.add(strInstr);

    // If dynamic variable, set type number in "box" (5th byte)
    Identifier type = symtab.lookup(varName);
    if (type instanceof VarID) {
      ((VarID) type).setTypeSoFar(rhsType);

      // Get addr of variable
      instrs.add(new ADD(false, Instr.R4, Instr.SP, AddrMode.buildImm(offset)));
      // Load the type number
      List<TypeID> types = new ArrayList<>();
      types.add(rhsType);
      instrs.add(new MOV(Condition.NO_CON, Instr.R5, AddrMode.buildImm(Utils.getTypeNumber(types))));
      // Store (byte) into "box"
      instrs.add(new STR(Instr.BYTE_SIZE, Condition.NO_CON, Instr.R5,
          AddrMode.buildAddrWithOffset(Instr.R4, Instr.WORD_SIZE)));
    }

    Instr.addToCurLabel(instrs);
  }

  public TypeID getDecType() {
    return decType;
  }

  public AssignRHSAST getAssignRHS() {
    return assignRHS;
  }
}
