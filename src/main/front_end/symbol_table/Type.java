package main.front_end.symbol_table;

public abstract class Type extends Identifier {

  private String typeName;

  public Type(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return this.typeName;
  }

}
