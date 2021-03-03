package frontend.abstractsyntaxtree.pairs;

import antlr.WaccParser.PairElemContext;
import backend.BackEndGenerator;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.ArrayElemAST;
import frontend.abstractsyntaxtree.expressions.IdentExprAST;
import frontend.abstractsyntaxtree.expressions.PairLiterAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static backend.instructions.Instr.addToCurLabel;

public class PairElemAST extends Node {

  private final SymbolTable symtab;
  private final Identifier childIdentifier;
  private final boolean first;
  private final Node child;
  private String identName;
  private final PairElemContext ctx;

  public PairElemAST(
      Identifier childIdentifier,
      SymbolTable symtab,
      boolean first,
      Node child,
      PairElemContext ctx) {
    super();
    this.childIdentifier = childIdentifier;
    this.symtab = symtab;
    this.first = first;
    this.child = child;
    this.identName = ctx.getStart().getText();
    this.ctx = ctx;
  }

  public String getName() {
    return identName;
  }

  public boolean getFirst() {
    return first;
  }


  @Override
  public void check() {
    // Checking expr has compatible type
    if (child instanceof IdentExprAST) {
      identName = ((IdentExprAST) child).getName();
      if (!(symtab.lookupAll(identName) instanceof PairID)) {
        addIncompatibleTypeSemanticError();
      }

    } else if (child instanceof ArrayElemAST) {
      if (!(child.getIdentifier().getType() instanceof PairID)) {
        addIncompatibleTypeSemanticError();
      }

    } else if (child instanceof PairLiterAST) {
      SemanticErrorCollector.addIncompatibleType(
          "pair",
          "null",
          identName,
          ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());

    } else {
      addIncompatibleTypeSemanticError();
    }

    // Set identifier
    if (childIdentifier instanceof NullID) {
      setIdentifier(childIdentifier);
    } else {
      PairID childIDAsPair =
          childIdentifier instanceof ParamID
              ? ((PairID) (childIdentifier).getType())
              : ((PairID) childIdentifier);
      Identifier childIDPairElem = first ? childIDAsPair.getFstType() : childIDAsPair.getSndType();

      setIdentifier(Objects.requireNonNullElseGet(childIDPairElem, NullID::new));
    }
  }

  private void addIncompatibleTypeSemanticError() {
    SemanticErrorCollector.addIncompatibleType(
        "pair",
        child.getIdentifier().getType().getTypeName(),
        identName,
        ctx.getStart().getLine(),
        ctx.getStart().getCharPositionInLine());
  }

  @Override
  public void toAssembly() {
    List<Instr> instructions = new ArrayList<>();

    //    instructions.add(new STR(1, "", Instr.R5, Instr.R0, 0));
    //    instructions.add(new STR(Instr.R0, Instr.R4, 4));
    //    // use symbol table to get offset and then add the extra inner offset
    int stackPointerOffset = symtab.getStackOffset(identName);
    //
    //    stackPointerOffset += childIdentifier.getType().getBytes();
    //
    //    instructions.add(new STR(Instr.R4, Instr.SP, stackPointerOffset));

    instructions.add(new LDR(Instr.R4, Instr.SP, stackPointerOffset));

    instructions.add(new MOV("", Instr.R0, Instr.R4));
    BackEndGenerator.addToPreDefFuncs("p_check_null_pointer");
    instructions.add(new BRANCH(true, "", "p_check_null_pointer"));

    PairID type = (PairID) childIdentifier.getType();
    TypeID elem_type;

    if (first) {
      elem_type = type.getFstType();
      instructions.add(new LDR(Instr.R4, Instr.R4, 0));
    } else {
      elem_type = type.getSndType();
      instructions.add(new LDR(Instr.R4, Instr.R4, 4));
    }

    instructions.add(new LDR(elem_type.getBytes(), "", Instr.R4, Instr.R4, 0));
    //    instructions.add(new STR(Instr.R4, Instr.SP, 0));

    addToCurLabel(instructions);
  }
}
