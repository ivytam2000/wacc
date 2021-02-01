package main.front_end.symbol_table;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final Map<String, Identifier> currSymbolTable;
  private final SymbolTable parent;

  public SymbolTable(SymbolTable parent) {
    this.parent = parent;
    currSymbolTable = new HashMap<>();
  }

  // first symbol table
  public SymbolTable() {
    this(null);
  }

  public void add(String name, Identifier astNode) {
    currSymbolTable.put(name, astNode);
  }

  public Identifier lookup(String name) {
    return currSymbolTable.get(name);
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
