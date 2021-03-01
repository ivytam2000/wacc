package frontend.abstractsyntaxtree.functions;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.TypeID;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParamListAST extends Node {

  protected List<ParamAST> paramASTs;

  public ParamListAST() {
    this.paramASTs = Collections.emptyList();
  }

  public ParamListAST(List<ParamAST> paramASTs) {
    this.paramASTs = paramASTs;
  }

  @Override
  public void check() {
    for (ParamAST paramAST : paramASTs) {
      paramAST.check();
    }
  }

  @Override
  public void toAssembly() {}

  // Used in the visitor to convert ASTs to IDs
  public List<TypeID> convertToParamIDs() {
    return paramASTs.stream()
        .map(paramAST -> paramAST.getIdentifier().getType())
        .collect(Collectors.toList());
  }
}
