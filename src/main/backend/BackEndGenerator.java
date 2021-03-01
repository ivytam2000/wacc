package backend;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.AST;
import frontend.abstractsyntaxtree.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static backend.instructions.Instr.addToCurLabel;

public class BackEndGenerator {

  private static final List<String> dataSegmentStrings = new ArrayList<>();

  private static final List<String> preDefFuncs = new ArrayList<>();
  private static Map<String, List<Instr>> usrDefFuncs;

  // private static List<Instr> mainInstructions;
  private final AST ast;

  public BackEndGenerator(AST ast) {
    this.ast = ast;
    dataSegmentStrings.clear();
    preDefFuncs.clear();
    usrDefFuncs = new HashMap<>();
    // mainInstructions = generateMainInstructions();
  }

  public String run() {
    StringBuilder output = new StringBuilder();
    // generates the main body of instructions (main, L0, L1 etc)
    generateMainInstructions();

    Map<String, List<Instr>> preDefFuncInstrs = Utils.getPreDefFunc(preDefFuncs);

    // Writes the data segment
    if (dataSegmentStrings.size() > 0) {
      output.append(".data\n\n");
      int msgIndex = 0;
      for (String dataSegmentString : dataSegmentStrings) {
        output.append("msg_").append(msgIndex).append(":\n");
        output.append("\t.word ").append(dataSegmentString.length() - 1).append("\n");
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

    // Writes all the labels
    for (String label : Instr.getLabelOrder()) {
      output.append(writeTextSection(label, Instr.getLabels().get(label)));
    }

    // output.append(writeTextSection("main", mainInstructions));

    for (Map.Entry<String, List<Instr>> pdf : preDefFuncInstrs.entrySet()) {
      String pdfName = pdf.getKey();
      List<Instr> pdfInstrs = pdf.getValue();
      output.append(writeTextSection(pdfName, pdfInstrs));
    }

    return output.toString();
  }

  /*  private Map<String, List<Instr>> generateFuncInstructions() {
    Map<String, List<Instr>> defFunc = new HashMap<>();

    for (Node funcAST : ast.getFuncASTs()) {
      defFunc.put(funcAST.getIdentifier().toString(), funcAST.toAssembly());
    }

    return defFunc;
  }*/

  private void generateMainInstructions() {
    addToCurLabel(Utils.getStartRoutine(ast.getSymtab()));
    ast.toAssembly();
    addToCurLabel(Utils.getEndRoutine(ast.getSymtab()));
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
}
