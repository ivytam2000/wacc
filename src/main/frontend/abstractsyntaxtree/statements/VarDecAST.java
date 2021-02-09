package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import frontend.symboltable.VariableID;

import static frontend.abstractsyntaxtree.statements.AssignStatAST.typeCompat;

public class VarDecAST extends Node {

  private final SymbolTable symtab;
  private final String typeName;
  private final String varName;
  private final AssignRHSAST assignRHS;

  public VarDecAST(SymbolTable symtab, String typeName, String varName, AssignRHSAST assignRHS) {
    super();
    this.symtab = symtab;
    this.typeName = typeName;
    this.varName = varName;
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {
    Identifier typeID = symtab.lookupAll(typeName);
    Identifier variable = symtab.lookup(varName);

    if (typeID == null) {
      // check if the identifier returned is a known type/identifier
      SemanticErrorCollector.addError("Unknown type " + typeName);
    } else if (!(typeID instanceof TypeID)) {
      // check if the identifier is a type
      SemanticErrorCollector.addError(typeName + "is not a type");
    } else if (variable != null) {
      SemanticErrorCollector.addError(varName + "is already declared");
    } else {
      TypeID rhsType = assignRHS.getIdentifier().getType();
      if (typeCompat((TypeID) typeID, rhsType)) {
        // create variable identifier
        VariableID varID = new VariableID((TypeID) typeID, varName);
        // add to symbol table
        symtab.add(varName, varID);
        setIdentifier(varID);
      }
    }
  }
}
