package frontend.symboltable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

  private final Map<String, IdentifierST> dictionary = new HashMap<>();
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

  public void add(TypeST type) {
    dictionary.put(type.getTypeName(), type);
  }

  public void add(String name, IdentifierST identifier) {
    dictionary.put(name, identifier);
  }

  public IdentifierST lookup(String name) {
    return dictionary.get(name);
  }

  public IdentifierST lookupAll(String name) {
    SymbolTable temp = this;
    IdentifierST node;
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
    symbolTable.add(new IntST());
    symbolTable.add(new BoolST());
    symbolTable.add(new CharST());
    // TODO: Add strings, arrays and pairs
  }
}
