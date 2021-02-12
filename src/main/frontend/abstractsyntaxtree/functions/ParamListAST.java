package frontend.abstractsyntaxtree.functions;

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

  public List<TypeID> convertToParamIDs() {
    return paramASTs.stream().map(paramAST -> paramAST.getIdentifier().getType())
        .collect(Collectors.toList());
  }
}
