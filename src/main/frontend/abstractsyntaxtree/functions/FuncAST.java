package frontend.abstractsyntaxtree.functions;

import antlr.WaccParser.FuncContext;
import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;
import java.util.ArrayList;
import java.util.List;

import static backend.BackEndGenerator.addToUsrDefFuncs;
import static backend.Utils.getEndRoutine;
import static backend.Utils.getStartRoutine;
import static backend.instructions.Instr.addToCurLabel;
import static backend.instructions.Instr.addToLabelOrder;
import static backend.instructions.Instr.setCurLabel;

public class FuncAST extends Node {

  private final String funcName;
  private final ParamListAST params;

  private SymbolTable globalScope;
  private Node statements;

  private FuncContext ctx;

  public FuncAST(Identifier identifier, SymbolTable globalScope,
      String funcName, ParamListAST params, FuncContext ctx) {
    super(identifier);
    this.funcName = funcName;
    this.params = params;
    this.globalScope = globalScope;
    this.ctx = ctx;
  }

  public void setStatements(Node statements) {
    this.statements = statements;
  }

  @Override
  public void check() {
    // Return type of body
    TypeID bodyReturnType = Utils
        .inferFinalReturnType(statements, ctx.getStart().getLine());
    // Declared return type
    TypeID funcReturnType = identifier.getType();

    // Body can just exit and match any return type
    if (!(bodyReturnType instanceof ExitID || Utils
        .typeCompat(funcReturnType, bodyReturnType))) {
      SemanticErrorCollector
          .addIncompatibleReturnTypes(funcReturnType.getTypeName(),
              bodyReturnType.getTypeName(), "function " + funcName,
              ctx.getStart().getLine(),
              ctx.getStart().getCharPositionInLine());
    }
  }

  @Override
  public void toAssembly() {
    String labelName = "f_" + funcName;
    setCurLabel(labelName);
    addToLabelOrder(labelName);

    SymbolTable funcsymtab = ((FuncID) identifier).getSymtab();
    int offset = 4 + funcsymtab.getSize();
    for (Node paramAST : params.paramASTs) {
      String varName = ((ParamAST) paramAST).getName();
      funcsymtab.addOffset(varName, offset);
      offset += paramAST.getIdentifier().getType().getBytes();
    }

    List<Instr> instructions = new ArrayList<>();
    addToCurLabel(new PUSH(Instr.LR));
    addToCurLabel(getStartRoutine(funcsymtab, false));

    funcsymtab.setFuncContext(true);
    statements.toAssembly();
    funcsymtab.setFuncContext(false);

    addToCurLabel(getEndRoutine(funcsymtab, false));

    instructions.add(new POP(Instr.PC));
    instructions.add(new POP(Instr.PC));
    instructions.add(new LTORG());
    addToCurLabel(instructions);
    setCurLabel("main");
  }

  public void addFuncToGlobalScope() {
    Identifier f = globalScope.lookupAll("func " + funcName);

    // f already defined
    if (f != null) {
      SemanticErrorCollector.addSymbolAlreadyDefined(
          funcName, ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
      return;
    }

    globalScope.add("func " + funcName, identifier);
  }
}
