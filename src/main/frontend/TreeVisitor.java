package frontend;

import antlr.WaccParser;
import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;
import frontend.abstractsyntaxtree.*;
import frontend.abstractsyntaxtree.statements.*;
import frontend.abstractsyntaxtree.assignments.AssignCallAST;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.expressions.*;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    ParamListContext s = ctx.paramList();
    ParamListAST params = ctx.paramList() == null ? new ParamListAST()
        : (ParamListAST) visitParamList(ctx.paramList());

    Node returnType = visitType(ctx.type());
    FuncID funcID = new FuncID(returnType, params.convertToParamIDs(), currSymTab);

    FuncAST funcAST = new FuncAST(funcID, currSymTab, ctx.IDENT().getText(), params);
    funcAST.check();

    // Swap back symbol table
    currSymTab = encScope;
    return funcAST;
  }

  // Visit functions for statements

  @Override
  public Node visitPrint_stat(Print_statContext ctx) {
    Node expr = visit(ctx.expr());
    return new PrintAST(expr);
  }

  @Override
  public Node visitAssign_stat(Assign_statContext ctx) {
    AssignLHSAST lhs = (AssignLHSAST) visitAssignLHS(ctx.assignLHS());
    AssignRHSAST rhs = (AssignRHSAST) visit(ctx.assignRHS());

    AssignStatAST assignStatAST = new AssignStatAST(lhs, rhs, currSymTab);
    assignStatAST.check();
    currSymTab.add(lhs.getIdentName(), lhs.getIdentifier());
    return assignStatAST;
  }

  @Override
  public Node visitPrintln_stat(Println_statContext ctx) {
    Node expr = visit(ctx.expr());
    return new PrintlnAST(expr);
  }

  @Override
  public Node visitReturn_stat(Return_statContext ctx) {
    Node expr = visit(ctx.expr());
    Node returnAST = new ReturnAST(currSymTab, expr);
    returnAST.check();
    return returnAST;
  }

  @Override
  public Node visitExit_stat(Exit_statContext ctx) {
    Node expr = visit(ctx.expr());
    ExitAST exitAST = new ExitAST(expr);
    exitAST.check();
    return exitAST;
  }

  @Override
  public Node visitSkip_stat(Skip_statContext ctx) {
    return new SkipAST();
  }

  @Override
  public Node visitFree_stat(Free_statContext ctx) {
    // TODO: do we need a ExprAST class?
    Node expr = visit(ctx.expr());
    FreeAST freeAST = new FreeAST(expr);
    freeAST.check();
    return freeAST;
  }

  @Override
  public Node visitVar_decl_stat(Var_decl_statContext ctx) {
    AssignRHSAST assignRHS = (AssignRHSAST) visit(ctx.assignRHS());
    Node typeAST = visit(ctx.type());
    VarDecAST varDec =
        new VarDecAST(currSymTab, typeAST.getIdentifier().getType().getTypeName(),
            ctx.IDENT().getText(), assignRHS);
    varDec.check();
    return varDec;
  }

  @Override
  public Node visitWhile_stat(While_statContext ctx) {
    // New scope
    SymbolTable encScope = currSymTab;
    currSymTab = new SymbolTable(encScope);

    Node expr = visit(ctx.expr());
    Node stat = visit(ctx.stat());

    WhileAST whileAST = new WhileAST(expr, stat);
    whileAST.check();
    // Swap back to parent scope
    currSymTab = encScope;
    return visitChildren(ctx);
  }

  @Override
  public Node visitIf_stat(If_statContext ctx) {
    Node expr = visit(ctx.expr());
    SymbolTable encScope = currSymTab;
    // Each branch is executed in its own scope
    currSymTab = new SymbolTable(encScope);
    Node thenStat = visit(ctx.stat(0));
    currSymTab = new SymbolTable(encScope);
    Node elseStat = visit(ctx.stat(1));
    // Swap back to parent scope
    currSymTab = encScope;
    IfAST ifAST = new IfAST(expr, thenStat, elseStat);
    ifAST.check();
    return ifAST;
  }

  @Override
  public Node visitBegin_stat(WaccParser.Begin_statContext ctx) {
    SymbolTable encScope = currSymTab;
    // Create new scope
    currSymTab = new SymbolTable(encScope);
    Node stat = visit(ctx.stat());
    BeginStatAST beginAST = new BeginStatAST(stat);
    // Swap back to parent scope
    currSymTab = encScope;
    return beginAST;
  }

  @Override
  public Node visitSequence_stat(Sequence_statContext ctx) {
    List<StatContext> statCtxs = ctx.stat();
    // TODO: Should I create an abstract statement node class?
    List<Node> statASTs = new ArrayList<>();
    for (StatContext s : statCtxs) {
      statASTs.add(visit(s));
    }
    return new SequenceAST(statASTs);
  }

  @Override
  public Node visitRead_stat(Read_statContext ctx) {
    AssignLHSAST assignLHSAST = (AssignLHSAST) visit(ctx.assignLHS());
    ReadAST readAST = new ReadAST(assignLHSAST);
    readAST.check();
    return assignLHSAST;
  }

  @Override
  public Node visitParamList(ParamListContext ctx) {
    List<ParamAST> paramASTs = new ArrayList<>();
    for (ParamContext paramCtx : ctx.param()) {
      paramASTs.add((ParamAST) visitParam(paramCtx));
    }

    ParamListAST paramListAST = new ParamListAST(paramASTs);
    paramListAST.check();

    return paramListAST;
  }

  @Override
  public Node visitParam(ParamContext ctx) {
    Identifier paramID = new ParamID(visitType(ctx.type()).getIdentifier().getType());
    String varName = ctx.IDENT().getText();
    return new ParamAST(paramID, currSymTab, varName);
  }

  @Override
  public Node visitAssignLHS(AssignLHSContext ctx) {
    if (ctx.IDENT() != null) {
      String assignName = ctx.IDENT().getText();
      return new AssignLHSAST(currSymTab, assignName);
    } else if (ctx.pairElem() != null) {
      PairElemAST pairElem = (PairElemAST) visitPairElem((ctx.pairElem()));
      return new AssignLHSAST(currSymTab, pairElem.getName());
    } else {  // array elem
      ArrayElemAST arrayElem = (ArrayElemAST) visitArrayElem((ctx.arrayElem()));
      return new AssignLHSAST(currSymTab, arrayElem.getName());
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
    PairID pairID =
        new PairID(firstExprAST.getIdentifier().getType(), secondExprAST.getIdentifier().getType());
    return new AssignRHSAST(pairID, currSymTab, children);
  }

  @Override
  public Node visitPairElem_assignRHS(PairElem_assignRHSContext ctx) {
    List<Node> children = new ArrayList<>();
    Node pairElemAST = visitPairElem(ctx.pairElem());
    children.add(pairElemAST);
    // Don't need to check the since creating the pairElem will call check
    return new AssignRHSAST(pairElemAST.getIdentifier().getType(), currSymTab, children);
  }

  @Override
  public Node visitCall_assignRHS(Call_assignRHSContext ctx) {
    List<Node> params = new ArrayList<>();
    String funcName = ctx.IDENT().getText();
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
    // Don't need to check the since creating the exprASTs will call check
    return new ArgListAST(currSymTab, expressions);
  }

  @Override
  public Node visitPairElem(PairElemContext ctx) {
    Node exprAST = visit(ctx.expr());
    Identifier ident = exprAST.getIdentifier();

    // Don't need to check the since creating the exprAST will call check
    if (ctx.FST() != null) {
      return new PairElemAST(ident, currSymTab, true, exprAST);
    } else {
      return new PairElemAST(ident, currSymTab, false, exprAST);
    }
  }

  @Override
  public Node visitBaseType(BaseTypeContext ctx) {
    TypeID baseTypeID;

    if (ctx.INT() != null) {
      baseTypeID = currSymTab.lookupAll(ctx.INT().getText()).getType();
    } else if (ctx.BOOL() != null) {
      baseTypeID = currSymTab.lookupAll(ctx.BOOL().getText()).getType();
    } else if (ctx.CHAR() != null) {
      baseTypeID = currSymTab.lookupAll(ctx.CHAR().getText()).getType();
    } else {
      baseTypeID = currSymTab.lookupAll(ctx.STRING().getText()).getType();
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
    assert (ctx.PAIR() != null);

    Node fst = visitPairElemType(ctx.pairElemType(0));
    Node snd = visitPairElemType(ctx.pairElemType(1));
    TypeID pairID = new PairID(fst.getIdentifier().getType(), snd.getIdentifier().getType());

    PairTypeAST pairTypeAST = new PairTypeAST(pairID, currSymTab, fst, snd);
    pairTypeAST.check();

    return pairTypeAST;
  }

  @Override
  public Node visitPairElemType(PairElemTypeContext ctx) {
    if (ctx.baseType() != null) {
      return visitBaseType(ctx.baseType());
    }
    if (ctx.arrayType() != null) {
      return visitArrayType(ctx.arrayType());
    }

    Identifier pairGenericID = new PairID();
    PairElemTypeAST pairElemTypeAST = new PairElemTypeAST(pairGenericID, currSymTab);

    return pairElemTypeAST;
  }

  @Override
  public Node visitIntLiter(IntLiterContext ctx) {
    Node intLiterAST =
        new IntLiterAST(currSymTab, ctx.MINUS() == null,
            ctx.INTEGER().getText());
    intLiterAST.check();
    return intLiterAST;
  }

  @Override
  public Node visitBoolLiter(BoolLiterContext ctx) {
    return new BoolLiterAST(currSymTab, ctx.FALSE() == null);
  }

  @Override
  public Node visitCharLiter(CharLiterContext ctx) {
    String s = ctx.CHAR_LITER().getText();
    // Length will be 3 accounting for quotes or 4 if it's an escape char
    assert ((s.charAt(1) == '\\' && s.length() == 4) || s.length() == 3);
    char val = s.charAt(0);
    return new CharLiterAST(currSymTab, val);
  }

  @Override
  public Node visitStrLiter(StrLiterContext ctx) {
    String val = ctx.STR_LITER().getText();
    assert (val != null);
    return new StrLiterAST(currSymTab, val);
  }

  @Override
  public Node visitType(TypeContext ctx) {
    if (ctx.baseType() != null) {
      return visitBaseType(ctx.baseType());
    }
    if (ctx.arrayType() != null) {
      return visitArrayType(ctx.arrayType());
    }
    return visitPairType(ctx.pairType());
  }

  @Override
  public Node visitPairLiter(PairLiterContext ctx) {
    return new PairLiterAST();
  }

  @Override
  public Node visitIdentExpr(IdentExprContext ctx) {
    String val = ctx.IDENT().getText();
    assert (val != null);
    Node identExprAST = new IdentExprAST(currSymTab, val);
    identExprAST.check();
    return identExprAST;
  }

  @Override
  public Node visitArrElemExpr(ArrElemExprContext ctx) {
    return visit(ctx.arrayElem());
  }

  @Override
  public Node visitParanExpr(ParanExprContext ctx) {
    return visit(ctx.expr());
  }

  @Override
  public Node visitUnOpExpr(UnOpExprContext ctx) {
    Node exprAST = visit(ctx.expr());
    Node unOpAST = new UnOpExprAST(currSymTab, ctx.unaryOper(), exprAST);
    unOpAST.check();
    return unOpAST;
  }

  @Override
  public Node visitArithOpExpr_1(ArithOpExpr_1Context ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    ArithmeticOper1Context arithCtx = ctx.arithmeticOper1();
    String operation = null;
    if (arithCtx.MULT() != null) {
      operation = "MULT";
    } else if (arithCtx.DIV() != null) {
      operation = "DIV";
    } else if (arithCtx.MOD() != null) {
      operation = "MOD";
    }
    assert (operation != null);
    Node arithOpExprAST = new ArithOpExprAST(currSymTab, operation, eL, eR);
    arithOpExprAST.check();
    return arithOpExprAST;
  }

  @Override
  public Node visitArithOpExpr_2(ArithOpExpr_2Context ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    ArithmeticOper2Context arithCtx = ctx.arithmeticOper2();
    String operation = null;
    if (arithCtx.PLUS() != null) {
      operation = "PLUS";
    } else if (arithCtx.MINUS() != null) {
      operation = "MINUS";
    }
    assert (operation != null);
    Node arithOpExprAST = new ArithOpExprAST(currSymTab, operation, eL, eR);
    arithOpExprAST.check();
    return arithOpExprAST;
  }

  @Override
  public Node visitBinOpExpr_1(BinOpExpr_1Context ctx) {
    //Int or char only
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    BinaryOper1Context binOp1Ctx = ctx.binaryOper1();
    String operation = null;
    if (binOp1Ctx.GT() != null) {
      operation = "GT";
    } else if (binOp1Ctx.GTE() != null) {
      operation = "GTE";
    } else if (binOp1Ctx.LT() != null) {
      operation = "LT";
    } else if (binOp1Ctx.LTE() != null) {
      operation = "LTE";
    }
    assert (operation != null);
    Node binOpExprAST = new BinOpExprAST(currSymTab, 2, operation, eL, eR);
    binOpExprAST.check();
    return binOpExprAST;
  }

  @Override
  public Node visitBinOpExpr_2(BinOpExpr_2Context ctx) {
    //Defined for all types
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    BinaryOper2Context binOp2Ctx = ctx.binaryOper2();
    String operation = null;
    if (binOp2Ctx.EQ() != null) {
      operation = "EQ";
    } else if (binOp2Ctx.NE() != null) {
      operation = "NE";
    }
    assert (operation != null);
    Node binOpExprAST = new BinOpExprAST(currSymTab, 3, operation, eL, eR);
    binOpExprAST.check();
    return binOpExprAST;
  }

  @Override
  public Node visitAndExpr(AndExprContext ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    Node binOpExprAST = new BinOpExprAST(currSymTab, 1, "AND", eL, eR);
    binOpExprAST.check();
    return binOpExprAST;
  }

  @Override
  public Node visitOrExpr(OrExprContext ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    Node binOpExprAST = new BinOpExprAST(currSymTab, 1, "OR", eL, eR);
    binOpExprAST.check();
    return binOpExprAST;
  }
  //----------------------------------------------------------------------------

  // TODO: Do we need these visit rules?
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
  public Node visitBinaryOper1(BinaryOper1Context ctx) {
    return super.visitBinaryOper1(ctx);
  }

  @Override
  public Node visitBinaryOper2(BinaryOper2Context ctx) {
    return super.visitBinaryOper2(ctx);
  }
  //END TODO
  //----------------------------------------------------------------------------

  @Override
  public Node visitArrayElem(ArrayElemContext ctx) {
    //TODO: Does the implementation handle nested arrays?
    // int[][] a = [[1, 2], [3,4]];
    // int y = a[0][1];
    String arrayName = ctx.IDENT().getText();
    //Check that the IDENT is an array. If not, no point moving forward.
    Identifier identifier = currSymTab.lookupAll(arrayName);
    if (!(identifier instanceof ArrayID)) {
      SemanticErrorCollector.addError(arrayName + " is not an array");
    }
    List<Node> indexes = new ArrayList<>();
    List<ExprContext> expressions = ctx.expr();
    for (ExprContext e : expressions) {
      Node exprAST = visit(e);
      if (!(exprAST.getIdentifier().getType() instanceof IntID)) {
        SemanticErrorCollector.addError("Array index not of type int");
      } else {
        indexes.add(exprAST);
      }
    }
    ArrayElemAST arrayElemAST =
        new ArrayElemAST(identifier, arrayName, indexes);
    arrayElemAST.check();
    return arrayElemAST;

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

  // TODO: Delete debugger
  @Override
  public Node visitProg(ProgContext ctx) {
    return super.visitProg(ctx);
  }
}
