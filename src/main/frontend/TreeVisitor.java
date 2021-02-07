package frontend;

import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;
import frontend.abstractsyntaxtree.*;
import frontend.abstractsyntaxtree.statements.AssignStatAST;
import frontend.abstractsyntaxtree.assignments.AssignCallAST;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.expressions.*;
import frontend.abstractsyntaxtree.statements.ReadAST;
import frontend.abstractsyntaxtree.statements.VarDecAST;
import frontend.symboltable.*;
import java.util.ArrayList;
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
    assert (ctx.IDENT() != null);

    // Create symbol table
    SymbolTable encScope = currSymTab;
    currSymTab = new SymbolTable(encScope);

    ParamListAST params = (ParamListAST) visitParamList(ctx.paramList());

    TypeID returnTypeID = currSymTab.lookupAll(ctx.type().toString()).getType();
    FuncID funcID = new FuncID(returnTypeID, params.convertToParamIDs(), currSymTab);

    FuncAST funcAST = new FuncAST(funcID, currSymTab, ctx.IDENT().toString(), params);
    funcAST.check();

    // Swap back symbol table
    currSymTab = encScope;
    return funcAST;
  }

  // Visit functions for statements

  @Override
  public Node visitPrint_stat(Print_statContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  public Node visitAssign_stat(Assign_statContext ctx) {
    AssignLHSAST lhs = (AssignLHSAST) visitAssignLHS(ctx.assignLHS());
    AssignRHSAST rhs = (AssignRHSAST) visit(ctx.assignRHS());

    AssignStatAST assignStatAST = new AssignStatAST(lhs, rhs, currSymTab);
    assignStatAST.check();
    return assignStatAST;
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
    // Might need to check if it is the instance before downcasting?
    // TODO : need to create a typeAST node?
    // TypeAST typeAST = visit(ctx.type());
    AssignRHSAST assignRHS = (AssignRHSAST) visit(ctx.assignRHS());
    VarDecAST varDec = new VarDecAST(currSymTab, ctx.type().toString(),
        ctx.IDENT().getText(), assignRHS);
    varDec.check();
    return varDec;
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
    AssignLHSAST assignLHSAST = (AssignLHSAST) visit(ctx.assignLHS());
    ReadAST readAST = new ReadAST(currSymTab,assignLHSAST);
    readAST.check();
    return assignLHSAST;
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
    if (ctx.IDENT() != null) {
      String assignName = ctx.IDENT().toString();
      return new AssignLHSAST(currSymTab.lookupAll(assignName), currSymTab, assignName);
    } else if (ctx.pairElem() != null) {
      Node pairElem = visitPairElem((ctx.pairElem()));
      return new AssignLHSAST(pairElem.getIdentifier().getType(), currSymTab);
    } else {  // array elem
      Node arrayElem = visitArrayElem((ctx.arrayElem()));
      return new AssignLHSAST(arrayElem.getIdentifier().getType(), currSymTab);
    }
  }

  @Override
  public Node visitExpr_assignRHS(Expr_assignRHSContext ctx) {
    List<Node> children = new ArrayList<>();
    Node exprAST = visit(ctx.expr());
    children.add(exprAST);
    // don't need to check the since creating the exprAST will call check
    return new AssignRHSAST(exprAST.getIdentifier().getType(), currSymTab, children);
  }

  @Override
  public Node visitArrayLiter_assignRHS(ArrayLiter_assignRHSContext ctx) {
    List<Node> children = new ArrayList<>();
    Node arrayLiterAST = visitArrayLiter(ctx.arrayLiter());
    children.add(arrayLiterAST);
    // don't need to check the since creating the arrayLiterAST will call check
    return new AssignRHSAST(arrayLiterAST.getIdentifier().getType(), currSymTab, children);
  }

  @Override
  public Node visitNewPair_assignRHS(NewPair_assignRHSContext ctx) {
    Node firstExprAST = visit(ctx.expr(0));
    Node secondExprAST = visit(ctx.expr(1));
    List<Node> children = new ArrayList<>();
    children.add(firstExprAST);
    children.add(secondExprAST);
    // don't need to check the since creating the exprASTs will call check
    PairID pairID = new PairID(firstExprAST.getIdentifier().getType(),
        secondExprAST.getIdentifier().getType());
    return new AssignRHSAST(pairID, currSymTab, children);
  }

  @Override
  public Node visitPairElem_assignRHS(PairElem_assignRHSContext ctx) {
    List<Node> children = new ArrayList<>();
    Node pairElemAST = visitPairElem(ctx.pairElem());
    children.add(pairElemAST);
    // don't need to check the since creating the pairElem will call check
    return new AssignRHSAST(pairElemAST.getIdentifier().getType(), currSymTab, children);
  }

  @Override
  public Node visitCall_assignRHS(Call_assignRHSContext ctx) {
    List<Node> params = new ArrayList<>();
    String funcName = ctx.IDENT().toString();
    Node argListAST = visitArgList(ctx.argList());
    params.add(argListAST);
    AssignCallAST assignCallAST = new AssignCallAST(funcName, currSymTab, params);
    assignCallAST.check();
    return assignCallAST;
  }

  @Override
  public Node visitArgList(ArgListContext ctx) {
    List<Node> expressions = new ArrayList<>();
    for (ExprContext exprContext : ctx.expr()) {
      expressions.add(visitChildren(exprContext));
    }
    // don't need to check the since creating the exprASTs will call check
    return new ArgListAST(currSymTab, expressions);
  }

  @Override
  public Node visitPairElem(PairElemContext ctx) {
    Node exprAST = visit(ctx.expr());
    Identifier ident = exprAST.getIdentifier();
    // don't need to check the since creating the exprAST will call check
    if (ctx.FST() != null) {
      return new PairElemAST(ident, currSymTab, true, exprAST);
    } else {
      return new PairElemAST(ident, currSymTab, false, exprAST);
    }
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
    TypeID baseTypeID;

    if (ctx.INT() != null) {
      baseTypeID = currSymTab.lookupAll(ctx.INT().toString()).getType();
    } else if (ctx.BOOL() != null) {
      baseTypeID = currSymTab.lookupAll(ctx.BOOL().toString()).getType();
    } else if (ctx.CHAR() != null) {
      baseTypeID = currSymTab.lookupAll(ctx.CHAR().toString()).getType();
    } else {
      baseTypeID = currSymTab.lookupAll(ctx.STRING().toString()).getType();
    }

    BaseTypeAST baseTypeAST = new BaseTypeAST(baseTypeID, currSymTab);
    baseTypeAST.check();

    return baseTypeAST;
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
  public Node visitPairElemTypeBaseType(PairElemTypeBaseTypeContext ctx) {
    return visitBaseType(ctx.baseType());
  }

  @Override
  public Node visitPairElemTypeArrayType(PairElemTypeArrayTypeContext ctx) {
    return visitArrayType(ctx.arrayType());
  }

  @Override
  public Node visitPairElemTypePairToken(PairElemTypePairTokenContext ctx) {
    return super.visitPairElemTypePairToken(ctx);
  }

  @Override
  public Node visitArrayElem(ArrayElemContext ctx) {
    return super.visitArrayElem(ctx);
  }

  @Override
  public Node visitIntLiter(IntLiterContext ctx) {
    Identifier identifier = currSymTab.lookupAll("int");
    IntLiterAST intLiterAST;
    long val = Long.parseLong(ctx.INTEGER().toString());
    if (ctx.MINUS() != null) {
      val = -val;
    }
    intLiterAST = new IntLiterAST(identifier, val);
    intLiterAST.check();
    return super.visitIntLiter(ctx);
  }

  @Override
  public Node visitBoolLiter(BoolLiterContext ctx) {
    return super.visitBoolLiter(ctx);
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
  public Node visitIdentExpr(IdentExprContext ctx) {
    return super.visitIdentExpr(ctx);
  }

  @Override
  public Node visitPairLiter(PairLiterContext ctx) {
    return super.visitPairLiter(ctx);
  }

  @Override
  public Node visitArrElemExpr(ArrElemExprContext ctx) {
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
    List<Node> children = new ArrayList<>();

    for (ExprContext expr : ctx.expr()) {
      Node exprAST = visit(expr);
      children.add(exprAST);
    }

    ArrayLiterAST arrayLiterAST = new ArrayLiterAST(currSymTab, children);
    arrayLiterAST.check();
    return arrayLiterAST;
  }

  @Override
  public Node visitProg(ProgContext ctx) {
    return super.visitProg(ctx);
  }
}
