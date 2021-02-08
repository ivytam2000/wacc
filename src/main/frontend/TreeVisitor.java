package frontend;

import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;
import frontend.abstractsyntaxtree.*;
import frontend.abstractsyntaxtree.expressions.*;
import frontend.abstractsyntaxtree.statements.*;
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
  public Node visitIntLiter(IntLiterContext ctx) {
    Node intLiterAST =
        new IntLiterAST(currSymTab, ctx.MINUS() == null,
            ctx.INTEGER().toString());
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
    /* TODO: Check how check for assignment of pairLiter is implemented
    Eg are we checking that pairLiterAST.getIdentifier().getType() is null? */
    return new PairLiterAST();
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
    assert(operation != null);
    Node binOpExprAST = new BinOpExprAST(currSymTab, 2,operation, eL, eR);
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
    assert(operation != null);
    Node binOpExprAST = new BinOpExprAST(currSymTab, 3,operation, eL, eR);
    binOpExprAST.check();
    return binOpExprAST;
  }

  @Override
  public Node visitAndExpr(AndExprContext ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    Node binOpExprAST = new BinOpExprAST(currSymTab, 1,"AND", eL, eR);
    binOpExprAST.check();
    return binOpExprAST;
  }

  @Override
  public Node visitOrExpr(OrExprContext ctx) {
    Node eL = visit(ctx.expr(0));
    Node eR = visit(ctx.expr(1));
    Node binOpExprAST = new BinOpExprAST(currSymTab, 1,"OR", eL, eR);
    binOpExprAST.check();
    return binOpExprAST;
  }
  //----------------------------------------------------------------------------

  //TODO: Do we need these visit rules?
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
    String arrayName = ctx.IDENT().toString();
    //Check that the IDENT is an array. If not, no point moving forward.
    Identifier identifier = currSymTab.lookupAll(arrayName);
    if (!(identifier instanceof ArrayID)) {
      // TODO: Fail?
      System.err.println(arrayName + " is not an array");
    }
    List<Node> indexes = new ArrayList<>();
    List<ExprContext> expressions = ctx.expr();
    for (ExprContext e : expressions) {
      Node exprAST = visit(e);
      if (!(exprAST.getIdentifier().getType() instanceof IntID)) {
        // TODO: Fail?
        System.err.println("Array index not of type int");
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
    return super.visitArrayLiter(ctx);
  }

  @Override
  public Node visitProg(ProgContext ctx) {
    return super.visitProg(ctx);
  }
}
