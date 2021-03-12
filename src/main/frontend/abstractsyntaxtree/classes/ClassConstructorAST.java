package frontend.abstractsyntaxtree.classes;

import antlr.WaccParser.ConstructorContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.functions.ParamAST;
import frontend.abstractsyntaxtree.functions.ParamListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.HashSet;
import java.util.Set;

public class ClassConstructorAST extends Node {

  private final SymbolTable classScope;
  private final String className;
  private final ParamListAST paramList;
  private final ConstructorContext ctx;

  public ClassConstructorAST(Identifier identifier, SymbolTable classScope,
      String className, ParamListAST paramList, ConstructorContext ctx) {
    // identifier will be the classID
    super(identifier);
    this.classScope = classScope;
    this.className = className;
    this.paramList = paramList;
    this.ctx = ctx;
  }

  public String getClassName() { return className; }

  @Override
  public void check() {
    // need to check if the attributes are all initialised
    // we know that attributeList gets checked first already

    // checks that all attributes are defined in the params
    Set<String> attributes = classScope.getAllIdent();
    Set<String> attributeChecker = new HashSet<>(attributes);

    for (ParamAST param: paramList.getParamList()) {
      String varName = param.getName();
      Identifier currIdent = classScope.lookup(varName);
      if (currIdent == null) {
        SemanticErrorCollector.addVariableUndefined(
            varName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return;
      }

      TypeID expected = currIdent.getType();
      TypeID actual = param.getIdentifier().getType();

      // checks that the types are the same
      if (!Utils.typeCompat(expected, actual)) {
        SemanticErrorCollector.addIncompatibleTypeInConstructor(expected.getTypeName(), actual.getTypeName(),
            className, varName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return;
      }
      attributeChecker.remove(varName);
    }

    // doesn't contain the same amount of param as the attributes, throw error
    if (attributeChecker.size() != 0) {
      for (String attribute: attributes) {
        SemanticErrorCollector.addInconsistentNumberOfParamInConstructor(attribute,
            classScope.lookup(attribute).getType().getTypeName(), className,
            ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      }
    }

    // adds the constructor into the scope
    classScope.add(className, identifier);
  }

  @Override
  public void toAssembly() {

  }
}
