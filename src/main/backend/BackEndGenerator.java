package backend;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.AST;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BackEndGenerator {
  private static final List<String> dataSegment = new ArrayList<>();
  private static final Set<String> preDefFunc = new HashSet<>();

  private Map<String, List<Instr>> userDefFunc;
  private final List<Instr> main = new ArrayList<>();
  private final AST ast;

  public BackEndGenerator(AST ast) {
    dataSegment.clear();
    preDefFunc.clear();
    this.ast = ast;
  }

  public List<Instr> generate() {
    this.userDefFunc = ast.generateFunc();
//    return ast.toAssembly();
    //Processing
    return null;
  }

  public static int addToDataSegment(String msg) {
    dataSegment.add(msg);
    return dataSegment.indexOf(msg);
  }

  public static void addToPreDefFunc(String func) {
    preDefFunc.add(func);
  }

}