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
  }

  public void add(String name, Identifier astNode) {
    dictionary.put(name, astNode);
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
}
