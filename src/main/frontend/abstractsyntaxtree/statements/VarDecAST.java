package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
import frontend.abstractsyntaxtree.ArrayLiterAST;
import frontend.abstractsyntaxtree.ArrayTypeAST;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.PairTypeAST;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;
import java.lang.reflect.Type;
import jdk.jshell.execution.Util;
import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.reflect.Array;
import org.antlr.v4.runtime.misc.Pair;

public class VarDecAST extends Node {

  private final SymbolTable symtab;
  private final Node typeAST;
  private final String varName;
  private final AssignRHSAST assignRHS;
  private final WaccParser.Var_decl_statContext ctx;

  public VarDecAST(SymbolTable symtab, Node typeAST,
      WaccParser.Var_decl_statContext ctx, AssignRHSAST assignRHS) {
    super();
    this.symtab = symtab;
    this.typeAST = typeAST;
    this.varName = ctx.IDENT().getText();
    this.ctx = ctx;
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {

    if (typeAST instanceof PairTypeAST) {
      TypeID fstType = ((PairTypeAST) typeAST).getFst().getIdentifier().getType();
      TypeID sndType = ((PairTypeAST) typeAST).getSnd().getIdentifier().getType();
      TypeID assignType = assignRHS.getIdentifier().getType();
      if (assignType instanceof PairID) {
        PairID assignPair = (PairID) assignType;

        if (fstType instanceof PairID) {
          //Grab the first identifier of assignPair
          if (!(assignPair.getFstType() instanceof PairTypes)) {
            SemanticErrorCollector.addError("First of pair : Expected pair");
          }
        } else if (fstType instanceof ArrayID) {
          if (!Utils.compareArrayTypes(fstType, assignPair.getFstType())) {
            SemanticErrorCollector.addError("First of pair : Expected array");
          }
        } else {
          if (fstType != assignPair.getFstType()) {
            SemanticErrorCollector.addError("First of pair : Types mismatch");
          }
        }
        TypeID temp1 = assignPair.getFstType();

        if (sndType instanceof PairID) {
          //Grab the first identifier of assignPair
          if (!(assignPair.getSndType() instanceof PairTypes)) {
            SemanticErrorCollector.addError("Second of pair : Expected pair");
          }
        } else if (sndType instanceof ArrayID) {
          if (!Utils.compareArrayTypes(sndType, assignPair.getSndType())) {
            SemanticErrorCollector.addError("Second of pair : Expected array");
          }
        } else {
          if (sndType != assignPair.getSndType()) {
            SemanticErrorCollector.addError("Second of pair : Types mismatch");
          }
        }
        TypeID temp2 = assignPair.getSndType();

        PairID lhsType = new PairID(temp1, temp2);
        typeAST.setIdentifier(lhsType);
      } else if (!(assignType instanceof NullID)) {
        SemanticErrorCollector.addError(
            "Incompatible types: expected pair, " + "but actual:" + assignType.getTypeName());
      }
    } else if (typeAST instanceof ArrayTypeAST) {
      TypeID assignType = assignRHS.getIdentifier().getType();
      if (assignType instanceof ArrayID) {
        if (!Utils.compareArrayTypes(typeAST.getIdentifier().getType(), assignType)) {
          SemanticErrorCollector.addError("Not same array type");
        }
      }
    } else {
      String typeName = typeAST.getIdentifier().getType().getTypeName();
      Identifier typeID = symtab.lookupAll(typeName);
      Identifier variable = symtab.lookup(varName);

      if (typeID == null) {
        // check if the identifier returned is a known type/identifier
        SemanticErrorCollector.addError("Unknown type " + typeName);
      } else if (variable != null && !(variable instanceof FuncID)) {
        SemanticErrorCollector.addError(varName + " is already declared");
      }else if (!(typeID instanceof TypeID)) {
        // check if the identifier is a type
        SemanticErrorCollector.addError(typeName + "is not a type");
      }
      //Boolean return not used
      Utils.typeCompat(ctx.type(),ctx.assignRHS(),typeAST, assignRHS);
    }

    symtab.add(varName, typeAST.getIdentifier().getType());
    setIdentifier(typeAST.getIdentifier().getType());
  }

  //  FOR DEBUGGING
  public Node getTypeAST() {
    return typeAST;
  }

  public AssignRHSAST getAssignRHS() {
    return assignRHS;
  }
}
