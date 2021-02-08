package frontend;

import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;
import frontend.abstractsyntaxtree.*;
import frontend.abstractsyntaxtree.expressions.*;
import frontend.abstractsyntaxtree.statements.*;
import frontend.symboltable.*;
import java.util.List;

public class TreeVisitor extends WaccParserBaseVisitor<Node> {

  private SymbolTable currSymTab;

  public TreeVisitor() {
    this.currSymTab = new SymbolTable(); // Initialised with top level symtab
  }

  @Override
  public Node visitProgram(ProgramContext ctx) {
    assert (ctx.BEGIN() != null);
    assert (ctx.END() != null);
    assert (ctx.EOF() != null);

    List<FuncContext> funcContexts = ctx.func();
    for (FuncContext FC : funcContexts) {
      visitFunc(FC); // Return func node
      // Add to AST
    }
    // visit stat
    visit(ctx.stat());

    return null;
  }

  @Override
  public Node visitFunc(FuncContext ctx) {
    // create symbol table
    SymbolTable encScope = currSymTab;
    currSymTab = new SymbolTable(encScope);

    // Swap back symbol table
    currSymTab = encScope;
    return super.visitFunc(ctx);
  }

  /* Visit Statement Functions */
  @Override
  public Node visitPrint_stat(Print_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitAssign_stat(Assign_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitPrintln_stat(Println_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitReturn_stat(Return_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitExit_stat(Exit_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitSkip_stat(Skip_statContext ctx) {
    System.out.println("SKIP found");
    return visitChildren(ctx);
  }

  @Override
  public Node visitFree_stat(Free_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitVar_decl_stat(Var_decl_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitWhile_stat(While_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitNew_scope_stat(New_scope_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitIf_stat(If_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitSequence_stat(Sequence_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitRead_stat(Read_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitParamList(ParamListContext ctx) {
    return super.visitParamList(ctx);
  }

  @Override
  public Node visitParam(ParamContext ctx) {
    return super.visitParam(ctx);
  }

  @Override
  public Node visitAssignLHS(AssignLHSContext ctx) {
    return super.visitAssignLHS(ctx);
  }

  @Override
  public Node visitAssignRHS(AssignRHSContext ctx) {
    return super.visitAssignRHS(ctx);
  }

  @Override
  public Node visitArgList(ArgListContext ctx) {
    return super.visitArgList(ctx);
  }

  @Override
  public Node visitPairElem(PairElemContext ctx) {
    return super.visitPairElem(ctx);
  }

  @Override
  public Node visitTypeBaseType(TypeBaseTypeContext ctx) {
    return visitBaseType(ctx.baseType());
  }

  @Override
  public Node visitTypeArrayType(TypeArrayTypeContext ctx) {
    return visitArrayType(ctx.arrayType());
  }

  @Override
  public Node visitTypePairType(TypePairTypeContext ctx) {
    return visitPairType(ctx.pairType());
  }

  @Override
  public Node visitBaseType(BaseTypeContext ctx) {
    return super.visitBaseType(ctx);
  }

  @Override
  public Node visitArrayType(ArrayTypeContext ctx) {
    return super.visitArrayType(ctx);
  }

  @Override
  public Node visitPairType(PairTypeContext ctx) {
    return super.visitPairType(ctx);
  }

  @Override
  public Node visitPairElemType(PairElemTypeContext ctx) {
    return super.visitPairElemType(ctx);
  }

  @Override
  public Node visitArrayElem(ArrayElemContext ctx) {
    return super.visitArrayElem(ctx);
  }

  @Override
  public Node visitIntLiter(IntLiterContext ctx) {
    Node intLiterAST =
        new IntLiterAST(currSymTab, ctx.MINUS() == null, ctx.INTEGER().toString());
    intLiterAST.check();
    return intLiterAST;
  }

  @Override
  public Node visitBoolLiter(BoolLiterContext ctx) {
    return new BoolLiterAST(currSymTab, ctx.FALSE() == null);
  }

  @Override
  public Node visitCharLiter(CharLiterContext ctx) {
    String s = ctx.CHAR_LITER().toString();
    //Length will be 3 accounting for quotes or 4 if it's an escape char
    assert ((s.charAt(1) == '\\' && s.length() == 4) || s.length() == 3);
    char val = s.charAt(0);
    return new CharLiterAST(currSymTab, val);
  }

  @Override
  public Node visitStrLiter(StrLiterContext ctx) {
    String val = ctx.STR_LITER().toString();
    assert (val != null);
    return new StrLiterAST(currSymTab, val);
  }

  @Override
  public Node visitPairLiter(PairLiterContext ctx) {
    return super.visitPairLiter(ctx);
  }

  @Override
  public Node visitIdentExpr(IdentExprContext ctx) {
    String val = ctx.IDENT().toString();
    assert (val != null);
    Node identExprAST = new IdentExprAST(currSymTab, val);
    identExprAST.check();
    return identExprAST;
  }

  @Override
  public Node visitArrElemExpr(ArrElemExprContext ctx) {
    //TODO: The ast needs a getname method for the ident
    return super.visitArrElemExpr(ctx);
  }

  @Override
  public Node visitParanExpr(ParanExprContext ctx) {
    return super.visitParanExpr(ctx);
  }

  @Override
  public Node visitUnOpExpr(UnOpExprContext ctx) {
    return super.visitUnOpExpr(ctx);
  }

  @Override
  public Node visitArithOpExpr_1(ArithOpExpr_1Context ctx) {
    return super.visitArithOpExpr_1(ctx);
  }

  @Override
  public Node visitArithOpExpr_2(ArithOpExpr_2Context ctx) {
    return super.visitArithOpExpr_2(ctx);
  }

  @Override
  public Node visitBinOpExpr(BinOpExprContext ctx) {
    return super.visitBinOpExpr(ctx);
  }

  @Override
  public Node visitUnaryOper(UnaryOperContext ctx) {
    return super.visitUnaryOper(ctx);
  }

  @Override
  public Node visitArithmeticOper1(ArithmeticOper1Context ctx) {
    return super.visitArithmeticOper1(ctx);
  }

  @Override
  public Node visitArithmeticOper2(ArithmeticOper2Context ctx) {
    return super.visitArithmeticOper2(ctx);
  }

  @Override
  public Node visitBinaryOper(BinaryOperContext ctx) {
    return super.visitBinaryOper(ctx);
  }

  @Override
  public Node visitArrayLiter(ArrayLiterContext ctx) {
    return super.visitArrayLiter(ctx);
  }

  @Override
  public Node visitProg(ProgContext ctx) {
    return super.visitProg(ctx);
  }
}
