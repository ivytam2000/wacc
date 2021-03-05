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

import static backend.instructions.Instr.addToCurLabel;

public class VarDecAST extends Node {

  private final SymbolTable symtab;
  private final Node typeAST;
  private final String varName;
  private final AssignRHSAST assignRHS;
  private final Var_decl_statContext ctx;
  private final AssignRHSContext rhsCtx;

  public VarDecAST(
      SymbolTable symtab,
      Node typeAST,
      String varName,
      AssignRHSAST assignRHS,
      Var_decl_statContext ctx) {
    super();
    this.symtab = symtab;
    this.typeAST = typeAST;
    this.varName = varName;
    this.ctx = ctx;
    this.rhsCtx = ctx.assignRHS();
    this.assignRHS = assignRHS;
  }

  public Node getTypeAST() {
    return typeAST;
  }

  public AssignRHSAST getAssignRHS() {
    return assignRHS;
  }

  @Override
  public void check() {
    TypeID decType = typeAST.getIdentifier().getType();
    symtab.incrementSize(decType.getBytes());

    TypeID rhsType = assignRHS.getIdentifier().getType();

    int line = rhsCtx.getStart().getLine();
    int pos = rhsCtx.getStart().getCharPositionInLine();

    Identifier variable = symtab.lookup(varName);

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

    symtab.add(varName, typeAST.getIdentifier().getType());
    setIdentifier(typeAST.getIdentifier().getType());
  }



  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    TypeID decType = typeAST.getIdentifier().getType();

    // Generate the offset of the variable
    int offset = symtab.getSmallestOffset() - decType.getBytes();

    // Add the offset to the symbol table's hashmap of variables' offsets
    symtab.addOffset(varName, offset);
    assignRHS.toAssembly();

    // Stores the value in the offset stack address
    STR strInstr = new STR(decType.getBytes(), Condition.NO_CON, Instr.R4,
                           AddrMode.buildAddrWithOffset(Instr.SP, offset));

    instrs.add(strInstr);
    addToCurLabel(instrs);
  }
}
