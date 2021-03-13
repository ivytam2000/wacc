package frontend.abstractsyntaxtree.classes;

import antlr.WaccParser.ClassAttrContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ClassID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class AccessClassAttributeAST extends AssignLHSAST {

  private final String varName;
  private final String attributeName;
  private final SymbolTable symtab;
  private final ClassAttrContext ctx;

  public AccessClassAttributeAST(String varName, String attributeName,
      SymbolTable symtab, ClassAttrContext ctx) {
    super(symtab, varName);
    this.attributeName = attributeName;
    this.varName = varName;
    this.symtab = symtab;
    this.ctx = ctx;
  }

  @Override
  public void check() {

    int line = ctx.getStart().getLine();
    int position = ctx.getStart().getCharPositionInLine();

    // Variable is not defined in the symbol table yet
    if (identifier == null) {
      SemanticErrorCollector.addVariableUndefined(
          varName, line, position);
      return;
    }

    // Variable defined is not a class instance
    if (!(identifier instanceof ClassID)) {
      SemanticErrorCollector.addVariableIsNotAnInstanceOfAClass(
          varName, line, position);
      return;
    }

    ClassID classID = (ClassID) identifier;
    SymbolTable classSymtab = classID.getSymtab();

    Identifier attributeIdent = classSymtab.lookup(attributeName);
    String className = classID.getTypeName();

    // Attribute does not exist in the class
    if (attributeIdent == null) {
      SemanticErrorCollector.addClassDoesNotHaveAttributeName(
          className, attributeName, line, position);
      return;
    }

    // Class attribute is not private
    if (attributeIdent.getVisibility() == Visibility.PRIVATE) {
      SemanticErrorCollector.addClassAttributeIsPrivate(
          className, attributeName, line, position);
    }

    setIdentifier(attributeIdent);
  }

  @Override
  public void toAssembly() {
  }
}
