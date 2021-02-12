package frontend.abstractsyntaxtree.pairs;

import antlr.WaccParser.PairElemContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.ArrayElemAST;
import frontend.abstractsyntaxtree.expressions.IdentExprAST;
import frontend.abstractsyntaxtree.expressions.PairLiterAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
import frontend.symboltable.PairID;
import frontend.symboltable.ParamID;
import frontend.symboltable.SymbolTable;

import java.util.Objects;

public class PairElemAST extends Node {

  private final SymbolTable symtab;
  private final Identifier childIdentifier;
  private final boolean first;
  private final Node child;
  private String identName;
  private final PairElemContext ctx;

  public PairElemAST(Identifier childIdentifier, SymbolTable symtab, boolean first, Node child,
      PairElemContext ctx) {
    super();
    this.childIdentifier = childIdentifier;
    this.symtab = symtab;
    this.first = first;
    this.child = child;
    this.identName = ctx.getStart().getText();
    this.ctx = ctx;
  }

  public String getName() {
    return identName;
  }

  @Override
  public void check() {
    // Checking expr has compatible type
    if (child instanceof IdentExprAST) {
      identName = ((IdentExprAST) child).getName();
      if (!(symtab.lookupAll(identName) instanceof PairID)) {
        addIncompatibleTypeSemanticError();
      }

    } else if (child instanceof ArrayElemAST) {
      if (!(child.getIdentifier().getType() instanceof PairID)) {
        addIncompatibleTypeSemanticError();
      }

    } else if (child instanceof PairLiterAST) {
      SemanticErrorCollector
          .addIncompatibleType("pair",
              "null", identName,
              ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());

    } else {
      addIncompatibleTypeSemanticError();
    }

    // set identifier
    if (childIdentifier instanceof NullID) {
      setIdentifier(childIdentifier);
    } else {
      PairID childIDAsPair =
          childIdentifier instanceof ParamID ? ((PairID) (childIdentifier).getType())
              : ((PairID) childIdentifier);
      Identifier childIDPairElem = first ? childIDAsPair.getFstType()
          : childIDAsPair.getSndType();

      setIdentifier(Objects.requireNonNullElseGet(childIDPairElem, NullID::new));
    }
  }

  private void addIncompatibleTypeSemanticError() {
    SemanticErrorCollector
        .addIncompatibleType("pair",
            child.getIdentifier().getType().getTypeName(), identName,
            ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
  }
}