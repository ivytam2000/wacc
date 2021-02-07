package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class AssignExprAST extends AssignRHSAST {

  public AssignExprAST(Identifier identifier,
      SymbolTable symtab,
      List<Node> children) {
    super(identifier, symtab, children);
  }

  @Override
  public void check() {

  }
}
