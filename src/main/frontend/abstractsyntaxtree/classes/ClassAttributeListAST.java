package frontend.abstractsyntaxtree.classes;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.ParamAST;
import java.util.Collections;
import java.util.List;

public class ClassAttributeListAST extends Node {

  protected List<ClassAttributeAST> classAttrASTs;

  public ClassAttributeListAST() {
    this.classAttrASTs = Collections.emptyList();
  }

  public ClassAttributeListAST(List<ClassAttributeAST> classAttrASTs) {
    this.classAttrASTs = classAttrASTs;
  }

  @Override
  public void check() {
    // TODO check if there are any duplicate attributes in the list
  }

  @Override
  public void toAssembly() {
  }

}
