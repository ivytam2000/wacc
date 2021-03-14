package frontend.abstractsyntaxtree.classes;

import antlr.WaccParser.ConstructorContext;
import backend.instructions.Instr;
import backend.instructions.LTORG;
import backend.instructions.Label;
import backend.instructions.POP;
import backend.instructions.PUSH;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.functions.ParamAST;
import frontend.abstractsyntaxtree.functions.ParamListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ConstructorID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static backend.Utils.getStartRoutine;
import static backend.instructions.Instr.WORD_SIZE;
import static backend.instructions.Instr.addToCurLabel;
import static backend.instructions.Instr.addToLabelOrder;
import static backend.instructions.Instr.setCurLabel;

public class ClassConstructorAST extends Node {

  private final SymbolTable classScope;
  private final String className;
  private final ParamListAST paramList;
  private final ConstructorContext ctx;

  public ClassConstructorAST(Identifier identifier, SymbolTable classScope,
      String className, ParamListAST paramList, ConstructorContext ctx) {
    // identifier will be the classID
    super(identifier);
    this.classScope = classScope;
    this.className = className;
    this.paramList = paramList;
    this.ctx = ctx;
  }

  public String getClassName() { return className; }

  @Override
  public void check() {
    // need to check if the attributes are all initialised
    // we know that attributeList gets checked first already

    // checks that all attributes are defined in the params
    Set<String> attributes = classScope.getAllIdent();
    Set<String> attributeChecker = new HashSet<>(attributes);

    for (ParamAST param: paramList.getParamList()) {
      String varName = param.getName();
      ((ConstructorID) identifier).addParamName(varName);
      Identifier currIdent = classScope.lookup(varName);
      if (currIdent == null) {
        SemanticErrorCollector.addVariableUndefined(
            varName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return;
      }

      TypeID expected = currIdent.getType();
      TypeID actual = param.getIdentifier().getType();

      // checks that the types are the same
      if (!Utils.typeCompat(expected, actual)) {
        SemanticErrorCollector.addIncompatibleTypeInConstructor(expected.getTypeName(), actual.getTypeName(),
            className, varName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return;
      }
      attributeChecker.remove(varName);
    }

    // doesn't contain the same amount of param as the attributes, throw error
    if (attributeChecker.size() != 0) {
      for (String attribute: attributes) {
        SemanticErrorCollector.addInconsistentNumberOfParamInConstructor(attribute,
            classScope.lookup(attribute).getType().getTypeName(), className,
            ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      }
    }

    // adds the constructor into the scope
    classScope.add(className, identifier);
  }

  @Override
  public void toAssembly() {
    String labelName = Label.CLASS_HEADER + className;
    setCurLabel(labelName);
    addToLabelOrder(labelName);

    // Calculate the size of stack frame that needs to be allocated
    SymbolTable classSymtab = ((ConstructorID) identifier).getSymtab();
    int offset = WORD_SIZE;
    boolean skipLR = false;
    for (Node paramAST : paramList.getParamList()) {
      skipLR = true;
      String varName = ((ParamAST) paramAST).getName();
      classSymtab.addOffset(varName, offset);
      offset += paramAST.getIdentifier().getType().getBytes();
    }

    List<Instr> instructions = new ArrayList<>();
    addToCurLabel(new PUSH(Instr.LR));
    addToCurLabel(getStartRoutine(classSymtab, false));

    // If there is at least one parameter, we need to account for LR on the stack
    if (skipLR) {
      classSymtab.setSkipLR();
    }

    // Clean up routine
    instructions.add(new POP(Instr.PC));
    instructions.add(new LTORG());
    addToCurLabel(instructions);

    setCurLabel(Label.MAIN);
  }
}
