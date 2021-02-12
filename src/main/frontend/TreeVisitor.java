package frontend;

import antlr.WaccParser;
import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;
import frontend.abstractsyntaxtree.*;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.abstractsyntaxtree.functions.FuncAST;
import frontend.abstractsyntaxtree.functions.ParamAST;
import frontend.abstractsyntaxtree.functions.ParamListAST;
import frontend.abstractsyntaxtree.statements.*;
import frontend.abstractsyntaxtree.assignments.AssignCallAST;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.expressions.*;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;
import java.util.ArrayList;
import java.util.List;

public class TreeVisitor extends WaccParserBaseVisitor<Node> {

  private SymbolTable currSymTab;

  public TreeVisitor() {
    this.currSymTab = new SymbolTable(); // Initialised with top level symtab
  }

  //Root of parse tree
  @Override
  public Node visitProgram(ProgramContext ctx) {
    //Functions analysis
    List<Node> fs = new ArrayList<>();
    List<FuncContext> funcContexts = ctx.func();
    //Add all functions to global scope first in order to support recursion
    for (FuncContext fc : funcContexts) {
      fs.add(visitFunc(fc));
    }
    //Iterates through functions and checks its body
    for (int i = 0; i < fs.size(); i++) {
      visitFuncWrapper(funcContexts.get(i), (FuncAST) fs.get(i));
    }

    // visit stat
    Node statAST = visit(ctx.stat());

    return new AST(fs, statAST);
  }

  //Sets up FuncAST and adds to global scope
  @Override
  public Node visitFunc(FuncContext ctx) {
    // Create symbol table
    SymbolTable globalScope = currSymTab;
    currSymTab = new SymbolTable(globalScope);

    Node returnType = visitType(ctx.type());

    //Initialises empty ParamListAST if no params
    ParamListAST params = ctx.paramList() == null ? new ParamListAST()
        : (ParamListAST) visitParamList(ctx.paramList());

    FuncID funcID = new FuncID(returnType, params.convertToParamIDs(),
        currSymTab);
    String funcName = ctx.IDENT().getText();

    FuncAST funcAST = new FuncAST(funcID, globalScope, funcName, params, ctx);
    funcAST.addFuncToGlobalScope();

    // Swap back symbol table
    currSymTab = globalScope;

    return funcAST;
  }

  //verifies function body
  private void visitFuncWrapper(FuncContext ctx, FuncAST funcAST) {
    FuncID funcID = (FuncID) currSymTab.lookupAll(ctx.IDENT().getText());

    SymbolTable globalScope = currSymTab;
    //set correct scope
    currSymTab = funcID.getSymtab();

    Node stat = visit(ctx.stat());
    funcAST.setStatements(stat);
    funcAST.check();

    //swap back to global scope
    currSymTab = globalScope;
  }

  //PRINT expr
  @Override
  public Node visitPrint_stat(Print_statContext ctx) {
    Node expr = visit(ctx.expr());
    return new PrintAST(expr);
  }

  //assignLHS ASSIGN assignRHS
  @Override
  public Node visitAssign_stat(Assign_statContext ctx) {
    AssignLHSAST lhs = (AssignLHSAST) visit(ctx.assignLHS());
    AssignRHSAST rhs = (AssignRHSAST) visit(ctx.assignRHS());

    AssignStatAST assignStatAST = new AssignStatAST(ctx.assignLHS(),
        ctx.assignRHS(), lhs, rhs, currSymTab);
    assignStatAST.check();
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
    Node returnAST = new ReturnAST(currSymTab, expr, ctx);
    returnAST.check();
    return returnAST;
  }

  @Override
  public Node visitExit_stat(Exit_statContext ctx) {
    Node expr = visit(ctx.expr());
    ExitAST exitAST = new ExitAST(expr, ctx);
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
    FreeAST freeAST = new FreeAST(expr, ctx.expr());
    freeAST.check();
    return freeAST;
  }

  @Override
  public Node visitVar_decl_stat(Var_decl_statContext ctx) {
    AssignRHSAST assignRHS = (AssignRHSAST) visit(ctx.assignRHS());
    Node typeAST = visit(ctx.type());
    String varName = ctx.IDENT().getText();
    VarDecAST varDec = new VarDecAST(currSymTab, typeAST, varName, assignRHS,
        ctx);
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

    WhileAST whileAST = new WhileAST(expr, stat, ctx.expr());
    whileAST.check();
    // Swap back to parent scope
    currSymTab = encScope;
    return whileAST;
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
    IfAST ifAST = new IfAST(expr, thenStat, elseStat, ctx);
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
    ReadAST readAST = new ReadAST(assignLHSAST, ctx.assignLHS());
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

  /**
   * Pre-condition: currSymTab already set to be the inner-scope symbol table
   */
  @Override
  public Node visitParam(ParamContext ctx) {
    Identifier paramID = new ParamID(
        visitType(ctx.type()).getIdentifier().getType());
    String varName = ctx.IDENT().getText();
    // Assume function sets currSymTab to be the inner-scope symbol table
    return new ParamAST(paramID, currSymTab, varName, ctx);
  }

  @Override
  public Node visitAssignLHS(AssignLHSContext ctx) {
    if (ctx.IDENT() != null) {
      String assignName = ctx.IDENT().getText();
      return new AssignLHSAST(currSymTab, assignName);
    } else if (ctx.pairElem() != null) {
      // LHS is of pair type
      PairElemAST pairElem = (PairElemAST) visitPairElem((ctx.pairElem()));
      return new AssignLHSAST(pairElem, pairElem.getName());
    } else {
      // LHS is of array type
      ArrayElemAST arrayElem = (ArrayElemAST) visitArrayElem((ctx.arrayElem()));
      return new AssignLHSAST(arrayElem, arrayElem.getName());
    }
  }

  /**
   * Pre-condition: ExprAST calls check()
   */
  @Override
  public Node visitExpr_assignRHS(Expr_assignRHSContext ctx) {
    List<Node> children = new ArrayList<>();
    Node exprAST = visit(ctx.expr());
    children.add(exprAST);
    return new AssignRHSAST(exprAST.getIdentifier().getType(), currSymTab,
        children);
  }

  /**
   * Pre-condition: ExprAST calls check()
   */
  @Override
  public Node visitArrayLiter_assignRHS(ArrayLiter_assignRHSContext ctx) {
    List<Node> children = new ArrayList<>();
    Node arrayLiterAST = visitArrayLiter(ctx.arrayLiter());
    children.add(arrayLiterAST);
    return new AssignRHSAST(arrayLiterAST.getIdentifier().getType(), currSymTab,
        children);
  }

  /**
   * Pre-condition: ExprAST calls check()
   */
  @Override
  public Node visitNewPair_assignRHS(NewPair_assignRHSContext ctx) {
    Node firstExprAST = visit(ctx.expr(0));
    Node secondExprAST = visit(ctx.expr(1));
    List<Node> children = new ArrayList<>();
    children.add(firstExprAST);
    children.add(secondExprAST);
    PairID pairID =
        new PairID(firstExprAST.getIdentifier().getType(),
            secondExprAST.getIdentifier().getType());
    return new AssignRHSAST(pairID, currSymTab, children);
  }

  /**
   * Pre-condition: ExprAST calls check()
   */
  @Override
  public Node visitPairElem_assignRHS(PairElem_assignRHSContext ctx) {
    List<Node> children = new ArrayList<>();
    Node pairElemAST = visitPairElem(ctx.pairElem());
    children.add(pairElemAST);
    return new AssignRHSAST(pairElemAST.getIdentifier().getType(), currSymTab,
        children);
  }

  @Override
  public Node visitCall_assignRHS(Call_assignRHSContext ctx) {
    String funcName = ctx.IDENT().getText();
    Node argListAST = visitArgList(ctx.argList());
    AssignCallAST assignCallAST = new AssignCallAST(funcName, currSymTab,
        (ArgListAST) argListAST,
        ctx);
    assignCallAST.check();
    return assignCallAST;
  }

  @Override
  public Node visitArgList(ArgListContext ctx) {
    List<Node> expressions = new ArrayList<>();
    if (ctx != null) {
      for (ExprContext exprContext : ctx.expr()) {
        expressions.add(visit(exprContext));
      }
    }
    // Don't need to check the since creating the exprASTs will call check
    return new ArgListAST(currSymTab, expressions);
  }

  @Override
  public Node visitPairElem(PairElemContext ctx) {
    Node exprAST = visit(ctx.expr());
    Identifier ident = exprAST.getIdentifier();

    PairElemAST pairElemAST;
    // Don't need to check the since creating the exprAST will call check
    if (ctx.FST() != null) {
      pairElemAST = new PairElemAST(ident, currSymTab, true, exprAST, ctx);
    } else {
      pairElemAST = new PairElemAST(ident, currSymTab, false, exprAST, ctx);
    }
    pairElemAST.check();
    return pairElemAST;
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

    BaseTypeAST baseTypeAST = new BaseTypeAST(baseTypeID, currSymTab, ctx);
    baseTypeAST.check();

    return baseTypeAST;
  }

  @Override
  public Node visitArrayType(ArrayTypeContext ctx) {
    int dimensions = ctx.OPEN_SQUARE_BRACKETS().size();

    if (ctx.baseType() != null) {
      Node baseTypeAST = visit(ctx.baseType());
      ArrayID nestedID = new ArrayID(baseTypeAST.getIdentifier().getType());
      for (int i = 1; i < dimensions; i++) {
        nestedID = new ArrayID(nestedID);
      }
      currSymTab.add(nestedID);
      return new ArrayTypeAST(nestedID, currSymTab, dimensions);
    } else {
      // Pair-type array
      Node pairTypeAST = visit(ctx.pairType());
      ArrayID pairArrayID = new ArrayID(pairTypeAST.getIdentifier().getType());
      currSymTab.add(pairArrayID);
      return new ArrayTypeAST(pairArrayID, currSymTab, dimensions);
    }
  }

  @Override
  public Node visitPairType(PairTypeContext ctx) {
    assert (ctx.PAIR() != null);

    Node fst = visitPairElemType(ctx.pairElemType(0));
    Node snd = visitPairElemType(ctx.pairElemType(1));
    TypeID pairID = new PairID(fst.getIdentifier().getType(),
        snd.getIdentifier().getType());

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
    PairElemTypeAST pairElemTypeAST = new PairElemTypeAST(pairGenericID,
        currSymTab);
    currSymTab.add(pairGenericID.getType().getTypeName(), pairGenericID);

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
    Node identExprAST = new IdentExprAST(currSymTab, ctx);
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
    Node unOpAST = new UnOpExprAST(currSymTab, exprAST, ctx.unaryOper());
    unOpAST.check();
    return unOpAST;
  }

  @Override
  public Node visitArithOpExpr_1(ArithOpExpr_1Context ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    ArithmeticOper1Context arithCtx = ctx.arithmeticOper1();
    String op = null;
    if (arithCtx.MULT() != null) {
      op = "*";
    } else if (arithCtx.DIV() != null) {
      op = "/";
    } else if (arithCtx.MOD() != null) {
      op = "%";
    }
    assert (op != null);
    Node arithOpExprAST = new ArithOpExprAST(currSymTab, op, eL, eR, ctx);
    arithOpExprAST.check();
    return arithOpExprAST;
  }

  @Override
  public Node visitArithOpExpr_2(ArithOpExpr_2Context ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    ArithmeticOper2Context arithCtx = ctx.arithmeticOper2();
    String op = null;
    if (arithCtx.PLUS() != null) {
      op = "+";
    } else if (arithCtx.MINUS() != null) {
      op = "-";
    }
    assert (op != null);
    Node arithOpExprAST = new ArithOpExprAST(currSymTab, op, eL, eR, ctx);
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
      operation = ">";
    } else if (binOp1Ctx.GTE() != null) {
      operation = ">=";
    } else if (binOp1Ctx.LT() != null) {
      operation = "<";
    } else if (binOp1Ctx.LTE() != null) {
      operation = "<=";
    }
    assert (operation != null);
    Node binOpExprAST = new BinOpExprAST(currSymTab, Utils.INT_CHAR, operation,
        eL, eR, ctx);
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
      operation = "==";
    } else if (binOp2Ctx.NE() != null) {
      operation = "!=";
    }
    assert (operation != null);
    Node binOpExprAST = new BinOpExprAST(currSymTab, Utils.ALL_TYPES, operation,
        eL, eR, ctx);
    binOpExprAST.check();
    return binOpExprAST;
  }

  @Override
  public Node visitAndExpr(AndExprContext ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    Node binOpExprAST = new BinOpExprAST(currSymTab, Utils.BOOL, "&&", eL, eR,
        ctx);
    binOpExprAST.check();
    return binOpExprAST;
  }

  @Override
  public Node visitOrExpr(OrExprContext ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    Node binOpExprAST = new BinOpExprAST(currSymTab, Utils.BOOL, "||", eL, eR,
        ctx);
    binOpExprAST.check();
    return binOpExprAST;
  }

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
        new ArrayElemAST(identifier, arrayName, indexes, ctx);
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
    ArrayID arrayID;
    if (children.isEmpty()) {
      arrayID = new ArrayID(new EmptyID());
    } else {
      arrayID = new ArrayID(children.get(0).getIdentifier().getType());
    }

    ArrayLiterAST arrayLiterAST = new ArrayLiterAST(arrayID, children, ctx);
    arrayLiterAST.check();
    return arrayLiterAST;
  }
}
