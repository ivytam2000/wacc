package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import frontend.symboltable.VariableID;

import static frontend.abstractsyntaxtree.statements.AssignStatAST.typeCompat;

public class VarDecAST extends Node {

  private SymbolTable symtab;
  private String typeName;
  private String varName;
  private AssignRHSAST assignRHS;

  public VarDecAST(SymbolTable symtab, String typeName, String varName, AssignRHSAST assignRHS) {
    super();
    this.symtab = symtab;
    this.typeName = typeName;
    this.varName = varName;
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {
    Identifier typeID = symtab.lookup(typeName);
    Identifier variable = symtab.lookup(varName);
    // check if the identifier returned is a known type
    if (typeID == null) {
      System.out.println("Unknown type " + typeName);
    } else if (!(typeID instanceof TypeID)) {
      // check if the type is an identifier
      System.out.println(typeName + "is not a type");
    } else if (variable != null) {
      System.out.println(varName + "is already declared");
    } else {
      TypeID rhsType = assignRHS.getIdentifier().getType();
      if (typeCompat((TypeID) typeID, rhsType)) {
        // create variable identifier
        VariableID varID = new VariableID((TypeID) typeID, varName);
        // add to symbol table
        symtab.add(varName, varID);
      }
    }
  }
}
