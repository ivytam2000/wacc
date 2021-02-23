package frontend.symboltable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final Map<String, Identifier> dictionary = new HashMap<>();
  private int size = 0;
  private final Map<String, Integer> varOffsets = new HashMap<>();
  private final SymbolTable parent;

  public SymbolTable(SymbolTable parent) {
    this.parent = parent;
  }

  /**
   * Defines a top-level symbol table, pre-loaded with default types.
   */
  public SymbolTable() {
    this(null);
    loadTopLevelSymbolTable(this);
  }

  /**
   * Loads a top-level symbol table for the global scope.
   */
  private void loadTopLevelSymbolTable(SymbolTable symbolTable) {
    symbolTable.add(new IntID());
    symbolTable.add(new BoolID());
    symbolTable.add(new CharID());
    symbolTable.add(new StringID());
    symbolTable.add(new NullID());
  }

  /**
   * Checks if this symbol table is a top-level one, i.e. of global scope.
   */
  public boolean isTopLevel() {
    return parent == null;
  }

  // For types only
  public void add(TypeID type) {
    dictionary.put(type.getTypeName(), type);
  }

  public void add(String name, Identifier identifier) {
    dictionary.put(name, identifier);
  }

  /**
   * Finds identifiers within this symbol table only.
   */
  public Identifier lookup(String name) {
    return dictionary.get(name);
  }

  /**
   * Finds identifiers in this symbol table and any of its parent symbol
   * tables.
   */
  public Identifier lookupAll(String name) {
    SymbolTable temp = this;
    Identifier node;
    while (temp != null) {
      node = temp.lookup(name);
      if (node != null) {
        if (node instanceof ParamID) {
          return node.getType();
        }
        return node;
      }
      temp = temp.parent;
    }
    return null;
  }

  public void incrementSize(int val) {
    size += val;
  }

  public int getStackOffset(String var) {
    SymbolTable temp = this;
    int innerOffset = 0;

    while (!temp.varOffsets.containsKey(var)) {
      innerOffset += temp.size;
      temp = temp.parent;
    }

    return innerOffset + temp.varOffsets.get(var);
  }
}
