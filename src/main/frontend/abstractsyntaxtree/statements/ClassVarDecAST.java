package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.Class_var_decl_statContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ClassID;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class ClassVarDecAST extends Node {

  private final String className;
  private final String varName;
  private final SymbolTable symtab;
  private final AssignRHSAST assignRHS;
  private final Class_var_decl_statContext ctx;

  public ClassVarDecAST(String className, String varName,
      AssignRHSAST assignRHS, SymbolTable symtab, Class_var_decl_statContext ctx) {
    super(symtab.lookupAll("class " + className));
    this.className = className;
    this.varName = varName;
    this.assignRHS = assignRHS;
    this.symtab = symtab;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    if (identifier == null || !(identifier instanceof ClassID)) {
      SemanticErrorCollector.addClassNotDefined(
          className, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      return;
    }

    int line = ctx.getStart().getLine();
    int pos = ctx.getStart().getCharPositionInLine();

    ClassID classID = (ClassID) identifier;
    TypeID rhsType = assignRHS.getIdentifier().getType();

    Identifier variable = symtab.lookup(varName);

    // Check if var is already declared unless it is a function name
    if (variable != null && !(variable instanceof FuncID)) {
      SemanticErrorCollector.addSymbolAlreadyDefined(varName, line, pos);
    }

    if (!Utils.typeCompat(classID, rhsType)) {
      SemanticErrorCollector.addIncompatibleType(
          className, rhsType.getTypeName(), varName, line, pos);
    }

    symtab.add(varName, classID);
  }

  @Override
  public void toAssembly() {

  }

}
