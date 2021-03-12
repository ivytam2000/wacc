package frontend.abstractsyntaxtree.classes;

import frontend.abstractsyntaxtree.Node;
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
    // calls check on child to see if the variable is defined
    for (ClassAttributeAST classAttrAST: classAttrASTs) {
      classAttrAST.check();
    }
  }

  @Override
  public void toAssembly() {
  }

}
