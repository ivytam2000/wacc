package frontend.symboltable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final Map<String, Identifier> dictionary = new HashMap<>();
  private final SymbolTable parent;

  public SymbolTable(SymbolTable parent) {
    this.parent = parent;
  }

  /**
   * Top-level symbol table.
   */
  public SymbolTable() {
    this(null);
    loadTopLevelSymbolTable(this);
  }

  public void add(Type type) {
    dictionary.put(type.getTypeName(), type);
  }

  public void add(String name, Identifier identifier) {
    dictionary.put(name, identifier);
  }

  public Identifier lookup(String name) {
    return dictionary.get(name);
  }

  public Identifier lookupAll(String name) {
    SymbolTable temp = this;
    Identifier node;
    while (temp != null) {
      node = temp.lookup(name);
      if (node != null) {
        return node;
      }
      temp = temp.parent;
    }
    return null;
  }

  private void loadTopLevelSymbolTable(SymbolTable symbolTable) {
    symbolTable.add(new Int());
    symbolTable.add(new Bool());
    symbolTable.add(new Char());
    // TODO: Add strings, arrays and pairs
  }
}
