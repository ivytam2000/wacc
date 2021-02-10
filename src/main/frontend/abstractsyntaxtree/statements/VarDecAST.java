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
import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.reflect.Array;

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
        boolean correctArr = true;
        if (fstType instanceof ArrayID) {
          correctArr = Utils.compareArrayTypes(fstType, assignPair.getFstType());
        }
        if (sndType instanceof ArrayID) {
          correctArr = correctArr && Utils.compareArrayTypes(sndType, assignPair.getSndType());
        }
        if (!correctArr) {
          SemanticErrorCollector.addError("Expected array");
        } else {
          if (!(fstType.getTypeName().equals(assignPair.getFstType().getTypeName())
              && sndType.getTypeName().equals(assignPair.getSndType().getTypeName()))) {
            SemanticErrorCollector.addError("Pair types do not match with rhs");
          }
        }

      } else {
        SemanticErrorCollector.addError(
            "Incompatible types: expected pair, " + "but actual:" + assignType.getTypeName());
      }
      PairID pairType = new PairID(fstType, sndType);
      symtab.add(varName, pairType);
    } else if (typeAST instanceof ArrayTypeAST) {
      TypeID assignType = assignRHS.getIdentifier().getType();
      if (assignType instanceof ArrayID) {
        if (!Utils.compareArrayTypes(typeAST.getIdentifier().getType(), assignType)) {
          SemanticErrorCollector.addError("Not same array type");
        }
      }
      symtab.add(varName, typeAST.getIdentifier().getType());

    } else {
      String typeName = typeAST.getIdentifier().getType().getTypeName();
      Identifier typeID = symtab.lookupAll(typeName);
      Identifier variable = symtab.lookup(varName);

      if (typeID == null) {
        // check if the identifier returned is a known type/identifier
        SemanticErrorCollector.addError("Unknown type " + typeName);
      } else if (!(typeID instanceof TypeID)) {
        // check if the identifier is a type
        SemanticErrorCollector.addError(typeName + "is not a type");
      } else if (variable != null) {
        SemanticErrorCollector.addError(varName + "is already declared");
      } else {
        if (Utils.typeCompat(ctx.type(),ctx.assignRHS(),typeAST, assignRHS)) {
          symtab.add(varName, typeID);
          setIdentifier(typeID);
        }
      }
    }
  }

  //  FOR DEBUGGING
  public Node getTypeAST() {
    return typeAST;
  }

  public AssignRHSAST getAssignRHS() {
    return assignRHS;
  }
}
