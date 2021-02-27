package backend;

import frontend.abstractsyntaxtree.AST;
import frontend.symboltable.Identifier;
import frontend.symboltable.IntID;

public class Utils {

  public static String getAssignValue(Identifier identifier, String value) {
    if (identifier instanceof IntID) {
      return "=" + value;
    } else {
      return "#" + value;
    }
  }

}
