package backend;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.AST;
import frontend.abstractsyntaxtree.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackEndGenerator {

  private static final List<String> dataSegmentStrings = new ArrayList<>();

  private static final List<String> preDefFuncs = new ArrayList<>();
  private static Map<String, List<Instr>> usrDefFuncs;
  private static Map<String, List<Instr>> branchLabels = new HashMap<>();

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

    Map<String, List<Instr>> preDefFuncInstrs = Utils.getPreDefFunc(preDefFuncs);

    // Writes the data segment

    if (dataSegmentStrings.size() > 0) {
      output.append(".data\n\n");
      int msgIndex = 0;
      for (String dataSegmentString : dataSegmentStrings) {
        output.append("msg_").append(msgIndex).append(":\n");
        //TODO: Length wrong when ending w \n and/or \0
        output.append("\t.word ").append(dataSegmentString.length()).append("\n");
        output.append("\t.ascii \"").append(dataSegmentString).append("\"\n");
        msgIndex++;
      }
      output.append("\n");
    }

    // Writes the text segment
    output.append(".text\n\n.global main\n");

    // Writes all the user-defined functions
    for (Map.Entry<String, List<Instr>> function : usrDefFuncs.entrySet()) {
      String sectionName = function.getKey();
      List<Instr> sectionInstructions = function.getValue();
      output.append(writeTextSection(sectionName, sectionInstructions));
    }

    // Writes all the main instructions
    output.append(writeTextSection("main", mainInstructions));

    for (Map.Entry<String, List<Instr>> pdf : preDefFuncInstrs.entrySet()) {
      String pdfName = pdf.getKey();
      List<Instr> pdfInstrs = pdf.getValue();
      output.append(writeTextSection(pdfName, pdfInstrs));
    }

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
    List<Instr> instrs = new ArrayList<>(Utils.getStartRoutine(ast.getSymtab()));
    instrs.addAll(ast.toAssembly());
    //TODO: End routine not always here
    instrs.addAll(Utils.getEndRoutine(ast.getSymtab()));
    return instrs;
  }

  /**
   * Uses fileWriter to write a .text section of instructions, named sectionName, in assembly
   * format.
   */
  private String writeTextSection(String sectionName, List<Instr> instructions) {
    StringBuilder output = new StringBuilder();
    output.append(sectionName).append(":\n");
    for (Instr instruction : instructions) {
      output.append("\t").append(instruction.translateToArm()).append("\n");
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

  public static void addToUsrDefFuncs(String label, List<Instr> instrs) {
    usrDefFuncs.put(label, instrs);
  }

  public static void addToBranchLabels(String label, List<Instr> instrs) {
    branchLabels.put(label, instrs);
  }

  public List<Instr> getLabelInstrs(String label){
    return branchLabels.get(label);
  }
}
