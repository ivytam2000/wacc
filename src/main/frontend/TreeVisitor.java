package frontend;

import antlr.WaccParser.ArgListContext;
import antlr.WaccParser.ArrayElemContext;
import antlr.WaccParser.ArrayElemExprContext;
import antlr.WaccParser.ArrayLiterContext;
import antlr.WaccParser.ArrayTypeContext;
import antlr.WaccParser.AssignLHSContext;
import antlr.WaccParser.AssignRHSContext;
import antlr.WaccParser.BaseTypeContext;
import antlr.WaccParser.BinOpExprContext;
import antlr.WaccParser.BinaryOperContext;
import antlr.WaccParser.BoolLiterContext;
import antlr.WaccParser.CharLiterContext;
import antlr.WaccParser.FuncContext;
import antlr.WaccParser.IdentExprContext;
import antlr.WaccParser.IntLiterContext;
import antlr.WaccParser.PairElemContext;
import antlr.WaccParser.PairElemTypeContext;
import antlr.WaccParser.PairLiterContext;
import antlr.WaccParser.PairTypeContext;
import antlr.WaccParser.ParamContext;
import antlr.WaccParser.ParamListContext;
import antlr.WaccParser.ParanthesesExprContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.ProgramContext;
import antlr.WaccParser.StatContext;
import antlr.WaccParser.StrLiterContext;
import antlr.WaccParser.TypeArrayTypeContext;
import antlr.WaccParser.TypeBaseTypeContext;
import antlr.WaccParser.TypePairTypeContext;
import antlr.WaccParser.UnOpExprContext;
import antlr.WaccParser.UnaryOperContext;
import antlr.WaccParserBaseVisitor;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class TreeVisitor extends WaccParserBaseVisitor<Void> {

  private SymbolTable currSymTab;

  public TreeVisitor() {
    this.currSymTab = new SymbolTable(); //Initialised with top level symtab
  }

  @Override
  public Void visitProgram(ProgramContext ctx) {
    assert (ctx.BEGIN() != null);
    assert (ctx.END() != null);
    assert (ctx.EOF() != null);

    List<FuncContext> funcContexts = ctx.func();
    for (FuncContext FC : funcContexts) {
      visitFunc(FC); //Return func node
      //Add to AST
    }
    visitStat(ctx.stat());
    return null;
  }

  @Override
  public Void visitFunc(FuncContext ctx) {
    //create symbol table
    SymbolTable encScope = currSymTab;
    currSymTab = new SymbolTable(encScope);

    //Swap back symbol table
    currSymTab = encScope;
    return super.visitFunc(ctx);
  }

  @Override
  public Void visitParamList(ParamListContext ctx) {
    return super.visitParamList(ctx);
  }

  @Override
  public Void visitParam(ParamContext ctx) {
    return super.visitParam(ctx);
  }

  @Override
  public Void visitStat(StatContext ctx) {
    System.out.println(ctx.SKIP_LITER());
    return null;
  }

  @Override
  public Void visitAssignLHS(AssignLHSContext ctx) {
    return super.visitAssignLHS(ctx);
  }

  @Override
  public Void visitAssignRHS(AssignRHSContext ctx) {
    return super.visitAssignRHS(ctx);
  }

  @Override
  public Void visitArgList(ArgListContext ctx) {
    return super.visitArgList(ctx);
  }

  @Override
  public Void visitPairElem(PairElemContext ctx) {
    return super.visitPairElem(ctx);
  }

  @Override
  public Void visitTypeBaseType(TypeBaseTypeContext ctx) {
    return visitBaseType(ctx.baseType());
  }

  @Override
  public Void visitTypeArrayType(TypeArrayTypeContext ctx) {
    return visitArrayType(ctx.arrayType());
  }

  @Override
  public Void visitTypePairType(TypePairTypeContext ctx) {
    return visitPairType(ctx.pairType());
  }

  @Override
  public Void visitBaseType(BaseTypeContext ctx) {
    return super.visitBaseType(ctx);
  }

  @Override
  public Void visitArrayType(ArrayTypeContext ctx) {
    return super.visitArrayType(ctx);
  }

  @Override
  public Void visitPairType(PairTypeContext ctx) {
    return super.visitPairType(ctx);
  }

  @Override
  public Void visitPairElemType(PairElemTypeContext ctx) {
    return super.visitPairElemType(ctx);
  }

  @Override
  public Void visitArrayElem(ArrayElemContext ctx) {
    return super.visitArrayElem(ctx);
  }

  @Override
  public Void visitIntLiter(IntLiterContext ctx) {
    return super.visitIntLiter(ctx);
  }

  @Override
  public Void visitBoolLiter(BoolLiterContext ctx) {
    return super.visitBoolLiter(ctx);
  }

  @Override
  public Void visitCharLiter(CharLiterContext ctx) {
    return super.visitCharLiter(ctx);
  }

  @Override
  public Void visitStrLiter(StrLiterContext ctx) {
    return super.visitStrLiter(ctx);
  }

  @Override
  public Void visitPairLiter(PairLiterContext ctx) {
    return super.visitPairLiter(ctx);
  }

  @Override
  public Void visitIdentExpr(IdentExprContext ctx) {
    return super.visitIdentExpr(ctx);
  }

  @Override
  public Void visitArrayElemExpr(ArrayElemExprContext ctx) {
    return super.visitArrayElemExpr(ctx);
  }

  @Override
  public Void visitUnOpExpr(UnOpExprContext ctx) {
    return super.visitUnOpExpr(ctx);
  }

  @Override
  public Void visitBinOpExpr(BinOpExprContext ctx) {
    return super.visitBinOpExpr(ctx);
  }

  @Override
  public Void visitParanthesesExpr(ParanthesesExprContext ctx) {
    return super.visitParanthesesExpr(ctx);
  }

  @Override
  public Void visitArrayLiter(ArrayLiterContext ctx) {
    return super.visitArrayLiter(ctx);
  }

  @Override
  public Void visitProg(ProgContext ctx) {
    return super.visitProg(ctx);
  }
}
