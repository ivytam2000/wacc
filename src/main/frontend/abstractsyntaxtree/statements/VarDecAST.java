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

import static backend.instructions.AddrMode.buildAddrWithOffset;
import static backend.instructions.Condition.NO_CON;
import static backend.instructions.Instr.WORD_SIZE;
import static backend.instructions.Instr.addToCurLabel;

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
    symtab.incrementSize(decType.getBytes());

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
    int offset = symtab.getSmallestOffset() - decType.getBytes();

    // Add the offset to the symbol table's hashmap of variables' offsets
    symtab.addOffset(varName, offset);
    assignRHS.toAssembly();

    // Malloc dynamic variable "box", set tag and store variable
    if (symtab.lookup(varName) instanceof VarID) {

      // Malloc dynamic variable "box"
      instrs.add(new LDR(Instr.R0, AddrMode.buildImm(DYNAMIC_BOX_SIZE)));
      instrs.add(new BRANCH(true, NO_CON, Label.MALLOC));
      instrs.add(new MOV(NO_CON, Instr.R5, AddrMode.buildReg(Instr.R0)));

      // Data stored in first 4 bytes
      TypeID rhsType = assignRHS.getIdentifier().getType();
      instrs.add(new STR(rhsType.getBytes(), NO_CON, Instr.R4, AddrMode.buildAddr(Instr.R5)));

      // Store type number at (malloc-ed address + 4)
      instrs.add(new LDR(Instr.R4, AddrMode.buildImm(Utils.getTypeNumber(rhsType))));
      instrs.add(new STR(Instr.R4, AddrMode.buildAddrWithOffset(Instr.R5, WORD_SIZE)));

      // Move actual address to r4
      instrs.add(new MOV(NO_CON, Instr.R4, AddrMode.buildReg(Instr.R5)));

    }

    // Stores the value in the offset stack address
    STR strInstr = new STR(decType.getBytes(), NO_CON, Instr.R4,
        buildAddrWithOffset(Instr.SP, offset));
    instrs.add(strInstr);
    addToCurLabel(instrs);
  }

  public TypeID getDecType() {
    return decType;
  }

  public AssignRHSAST getAssignRHS() {
    return assignRHS;
  }
}
