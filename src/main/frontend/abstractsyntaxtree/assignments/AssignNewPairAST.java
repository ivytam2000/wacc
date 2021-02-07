package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class AssignNewPairAST extends AssignRHSAST {

  public AssignNewPairAST(Identifier identifier,
      SymbolTable symtab,
      List<Node> children) {
    super(identifier, symtab, children);
  }

  @Override
  public void check() {

  }
}
