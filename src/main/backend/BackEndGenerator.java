package backend;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.AST;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static backend.instructions.Instr.addToCurLabel;
import static backend.instructions.Instr.addToLabelOrder;
import static backend.instructions.Instr.setCurLabel;

public class BackEndGenerator {

  private static final List<String> dataSegmentStrings = new ArrayList<>();
  private static int dataSegmentIndex = 0;

  private static final List<String> preDefFuncs = new ArrayList<>();

  private final AST ast;

  public BackEndGenerator(AST ast) {
    this.ast = ast;
    dataSegmentStrings.clear();
    preDefFuncs.clear();
  }

  public String run() {
    StringBuilder output = new StringBuilder();
    // Generates the main body of instructions, e.g.: main, L0, L1 etc.
    generateMainInstructions();

    Map<String, List<Instr>> preDefFuncInstrs = Utils
        .getPreDefFunc(preDefFuncs);
    for (Map.Entry<String, List<Instr>> pdf : preDefFuncInstrs.entrySet()) {
      String pdfName = pdf.getKey();
      List<Instr> pdfInstrs = pdf.getValue();

      setCurLabel(pdfName);
      addToLabelOrder(pdfName);
      addToCurLabel(pdfInstrs);
    }

    // Writes the data segment
    if (dataSegmentStrings.size() > 0) {
      output.append(".data\n\n");
      int msgIndex = 0;
      for (String dataSegmentString : dataSegmentStrings) {
        output.append("msg_").append(msgIndex).append(":\n");
        int limit = dataSegmentString.length();
        int len = limit;
        for (int i = 0; i < limit; ) {
          if (dataSegmentString.charAt(i) == '\\') {
            len--;
            i++;
          }
          i++;
        }
        output.append("\t.word ").append(len).append("\n");
        output.append("\t.ascii \"").append(dataSegmentString).append("\"\n");
        msgIndex++;
      }
      output.append("\n");
    }

    // Writes the text segment
    output.append(".text\n\n.global main\n");

    // Writes every labelled section and their corresponding instructions
    // Sections include user-defined functions, main etc.
    for (String label : Instr.getLabelOrder()) {
      output.append(writeTextSection(label, Instr.getLabels().get(label)));
    }

    return output.toString();
  }

  private void generateMainInstructions() {
    addToCurLabel(Utils.getStartRoutine(ast.getSymtab(),true));
    ast.toAssembly();
    addToCurLabel(Utils.getEndRoutine(ast.getSymtab(),true));
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
      output.append("\t").append(instruction.translateToArm()).append("\n");
    }
    return output.toString();
  }

  public static int addToDataSegment(String msg) {
    dataSegmentStrings.add(msg);
    // If we use indexOf method, always returns first occurence
    return dataSegmentIndex++;
  }

  public static void addToPreDefFuncs(String func) {
    preDefFuncs.add(func);
  }
}
