package frontend.abstractsyntaxtree.functions;

import antlr.WaccParser.FuncContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

public class FuncAST extends Node {

  private final String funcName;
  private final ParamListAST params;

  private SymbolTable symtab;
  private Node statements;

  private FuncContext ctx;

  public FuncAST(Identifier identifier, SymbolTable currSymTab, String funcName,
      ParamListAST params, FuncContext ctx) {
    super(identifier);
    this.funcName = funcName;
    this.params = params;
    this.symtab = currSymTab;
    this.ctx = ctx;
  }

  public void setStatements(Node statements) {
    this.statements = statements;
  }

  @Override
  public void check() {

    ((FuncID) identifier).setSymtab(symtab);

    TypeID returnType = Utils.inferFinalReturnType(statements);
    TypeID funcReturnType = identifier.getType();

    if (funcReturnType != null) {
      if(funcReturnType instanceof PairID){
        if(returnType instanceof PairID){
          boolean isEqual =
              ((PairID) funcReturnType).getFstType().getClass() ==
                  ((PairID) returnType).getFstType().getClass();
          isEqual =
              isEqual && (((PairID) funcReturnType).getSndType().getClass() == ((PairID) returnType).getSndType().getClass());
          if(!isEqual){
            System.out.println(((PairID) funcReturnType).getSndType());
            System.out.println(((PairID) returnType).getSndType());
            SemanticErrorCollector.addError("HELLO");
          }
        }else{
          SemanticErrorCollector.addError("BYE");
        }
      }else{
        if(returnType == null){
          SemanticErrorCollector.addError("Got null return type");
        } else {
          if (!(funcReturnType.getTypeName().equals(returnType.getTypeName()))){
            SemanticErrorCollector.addError("Incompatible types");
          }
        }
      }
    }

  }

  public void checkFunctionNameAndReturnType() {
    Identifier f = symtab.lookupAll(funcName);

    if (f != null) {
      SemanticErrorCollector.addSymbolAlreadyDefined(funcName, ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
      return;
    }

    symtab.add(funcName, identifier);
  }
}
