package frontend.abstractsyntaxtree.functions;

import static backend.instructions.Instr.LR;
import static backend.instructions.Instr.PC;

import antlr.WaccParser.FuncContext;
import backend.instructions.Instr;
import backend.instructions.POP;
import backend.instructions.PUSH;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;
import java.util.ArrayList;
import java.util.List;

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
  public List<Instr> toAssembly() {
    List<Instr> instructions = new ArrayList<>();
    instructions.add(new PUSH(LR));
    instructions.addAll(statements.toAssembly());
    // TODO: Link parameters with their addresses in the stack.
    instructions.add(new POP(PC));
    instructions.add(new POP(PC));
    return instructions;
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
