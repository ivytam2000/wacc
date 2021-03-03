package frontend.abstractsyntaxtree.functions;

import antlr.WaccParser.FuncContext;
import backend.instructions.Instr;
import backend.instructions.LTORG;
import backend.instructions.MOV;
import backend.instructions.POP;
import backend.instructions.PUSH;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;
import java.util.ArrayList;
import java.util.List;

import static backend.BackEndGenerator.addToUsrDefFuncs;
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

    int offset = 0;
    for (Node paramAST : params.paramASTs) {
      String varName = ((ParamAST) paramAST).getName();
      offset += paramAST.getIdentifier().getType().getBytes();
      globalScope.addOffset(varName, offset);
    }

    List<Instr> instructions = new ArrayList<>();
    addToCurLabel(new PUSH(Instr.LR));

    statements.toAssembly();

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
