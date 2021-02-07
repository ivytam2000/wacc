package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class AssignArrayLiterAST extends AssignRHSAST {

  public AssignArrayLiterAST(Identifier identifier,
      SymbolTable symtab,
      List<Node> children) {
    super(identifier, symtab, children);
  }

  @Override
  public void check() {

  }
}
