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
    long val = Long.parseLong(ctx.INTEGER().toString());
    if (ctx.MINUS() != null) {
      val = -val;
    }
    IntLiterAST intLiterAST =
        new IntLiterAST(currSymTab.lookupAll("int"), val);
    intLiterAST.check();
    return intLiterAST;
  }

  @Override
  public Node visitBoolLiter(BoolLiterContext ctx) {
    return new BoolLiterAST(currSymTab.lookupAll("bool"));
  }

  @Override
  public Node visitCharLiter(CharLiterContext ctx) {
    return super.visitCharLiter(ctx);
  }

  @Override
  public Node visitStrLiter(StrLiterContext ctx) {
    return super.visitStrLiter(ctx);
  }

  @Override
  public Node visitPairLiter(PairLiterContext ctx) {
    return super.visitPairLiter(ctx);
  }

  @Override
  public Node visitIdentExpr(IdentExprContext ctx) {
    return super.visitIdentExpr(ctx);
  }

  @Override
  public Node visitArrElemExpr(ArrElemExprContext ctx) {
    return super.visitArrElemExpr(ctx);
  }

  @Override
  public Node visitUnOpExpr(UnOpExprContext ctx) {
    return super.visitUnOpExpr(ctx);
  }

  @Override
  public Node visitParanExpr(ParanExprContext ctx) {
    return super.visitParanExpr(ctx);
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
