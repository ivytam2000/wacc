package backend;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.AST;
import frontend.abstractsyntaxtree.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BackEndGenerator {

  private static final List<String> dataSegmentStrings = new ArrayList<>();

  private static final Set<String> preDefFuncs = new HashSet<>();
  private static Map<String, List<Instr>> usrDefFuncs;

  private static List<Instr> mainInstructions;
  private final AST ast;

  public BackEndGenerator(AST ast) {
    this.ast = ast;

    dataSegmentStrings.clear();
    preDefFuncs.clear();
    usrDefFuncs = generateFuncInstructions();
    mainInstructions = generateMainInstructions();
  }

  public String run() {
    StringBuilder output = new StringBuilder();

    // Writes the data segment
    output.append(".data\n\n");

    int msgIndex = 0;
    for (String dataSegmentString : dataSegmentStrings) {
      output.append("msg_").append(msgIndex).append(":\n");
      output.append("\t.word ").append(dataSegmentString.length()).append("\n");
      output.append("\t.ascii \"").append(dataSegmentString).append("\"\n");
      msgIndex++;
    }

    // Writes the text segment
    output.append("\n.text\n\n.global main\n");

    // Writes all the user-defined functions
    for (Map.Entry<String, List<Instr>> function : usrDefFuncs.entrySet()) {
      String sectionName = function.getKey();
      List<Instr> sectionInstructions = function.getValue();
      output.append(writeTextSection(sectionName, sectionInstructions));
    }

    // Writes all the main instructions
    output.append(writeTextSection("main", mainInstructions));

    // TODO: Write pre-defined functions.

    return output.toString();
  }

  private Map<String, List<Instr>> generateFuncInstructions() {
    Map<String, List<Instr>> defFunc = new HashMap<>();

    for (Node funcAST : ast.getFuncASTs()) {
      defFunc.put(funcAST.getIdentifier().toString(), funcAST.toAssembly());
    }

    return defFunc;
  }

  private List<Instr> generateMainInstructions() {
    return ast.toAssembly();
  }

  /**
   * Uses fileWriter to write a .text section of instructions, named
   * sectionName, in assembly format.
   */
  private String writeTextSection(String sectionName,
      List<Instr> instructions) {
    StringBuilder output = new StringBuilder();
    output.append(sectionName).append(":\n");
    for (Instr instruction : instructions) {
      output.append("\t").append(instruction.getInstruction()).append("\n");
    }
    return output.toString();
  }

  public static int addToDataSegment(String msg) {
    dataSegmentStrings.add(msg);
    return dataSegmentStrings.indexOf(msg);
  }

  public static void addToPreDefFunc(String func) {
    preDefFuncs.add(func);
  }
}