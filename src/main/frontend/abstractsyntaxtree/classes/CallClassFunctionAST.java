package frontend.abstractsyntaxtree.classes;

import static backend.instructions.Instr.SP;
import static backend.instructions.Instr.addToCurLabel;

import antlr.WaccParser.CallClassFuncContext;
import backend.instructions.ADD;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Condition;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.Label;
import backend.instructions.MOV;
import backend.instructions.STR;
import backend.instructions.SUB;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ClassID;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.ArrayList;
import java.util.List;

public class CallClassFunctionAST extends AssignRHSAST {

  private final String varName;
  private final String funcName;
  private final SymbolTable symtab;
  private final CallClassFuncContext ctx;
  private final ArgListAST args;

  public CallClassFunctionAST(
      String varName, String funcName, ArgListAST args, SymbolTable symtab, CallClassFuncContext ctx) {
    super(symtab.lookupAll(varName), symtab);
    this.varName = varName;
    this.funcName = funcName;
    this.symtab = symtab;
    this.ctx = ctx;
    this.args = args;
  }

  @Override
  public void check() {
    int line = ctx.getStart().getLine();
    int position = ctx.getStart().getCharPositionInLine();

    // Variable is not defined in the symbol table yet
    if (identifier == null) {
      SemanticErrorCollector.addVariableUndefined(
          varName, line, position);
      return;
    }

    // Variable defined is not a class instance
    if (!(identifier instanceof ClassID)) {
      SemanticErrorCollector.addVariableIsNotAnInstanceOfAClass(
          varName, line, position);
      return;
    }

    ClassID classID = (ClassID) identifier;
    SymbolTable classSymtab = classID.getSymtab();

    Identifier functionIdent = classSymtab.lookup("func " + funcName);
    String className = classID.getTypeName();

    // Function does not exist in the class
    if (functionIdent == null) {
      SemanticErrorCollector.addClassDoesNotHaveFunction(
          className, funcName, line, position);
      return;
    }

    if (!(functionIdent instanceof FuncID)) {
      // Given function name is not actually a function type
      SemanticErrorCollector.addIsNotClassFuncError(
          line, position, className, funcName, functionIdent.getType().getTypeName());
      setIdentifier(functionIdent.getType());
      return;
    }

    // Class attribute is not private
    if (functionIdent.getVisibility() == Visibility.PRIVATE) {
      SemanticErrorCollector.addClassFunctionIsPrivate(
          className, funcName, line, position);
      setIdentifier(functionIdent.getType());
      return;
    }

    // Check if function has the correct param
    List<TypeID> params = ((FuncID) functionIdent).getParams();
    List<Node> argsAST = args.getArguments();
    int paramSize = params.size();
    int argsSize = argsAST.size();

    int pos = (argsSize == 0) ? ctx.getStart().getCharPositionInLine():
        ctx.argList().getStart().getCharPositionInLine();
    // If given number of arguments are not the same as the number of params
    if (paramSize != argsSize) {
      SemanticErrorCollector.addClassFuncInconsistentArgsError(
          line,
          pos,
          className,
          funcName,
          paramSize,
          argsSize);
    } else {
      for (int i = 0; i < paramSize; i++) {
        TypeID currParam = params.get(i);
        Node currArg = argsAST.get(i);
        TypeID argType = currArg.getIdentifier().getType();
        if (!(argType instanceof NullID)) {

          // If argument and param types don't match
          if (currParam.getClass() != argType.getClass()) {
            SemanticErrorCollector.addClassFuncInconsistentArgTypeError(
                line,
                ctx.argList().expr(i).getStart().getCharPositionInLine(),
                className,
                funcName,
                i,
                currParam.getTypeName(),
                argType.getTypeName());
          }
        }
      }
    }
    setIdentifier(functionIdent.getType());
  }

  @Override
  public void toAssembly() {
    List<Instr> instructions = new ArrayList<>();

    int varNameOffset = symtab.getStackOffset(varName);
//    instructions.add(new ADD(false, Instr.SP, Instr.SP, AddrMode.buildImm(varNameOffset)));

    int accOffset = 0;
    String transferReg = Instr.getTargetReg();

    // Allocate in reverse so that first argument directly on top of LR
    for (int i = args.getArguments().size() - 1; i >= 0; i--) {
      Node argNode = args.getArguments().get(i);

      // Puts the next argument into the transfer register
      argNode.toAssembly();

      // Record total offset to destroy stack after
      int offset = argNode.getIdentifier().getType().getBytes();
      accOffset += offset;

      // Temporary offset so that arguments are accessed correctly
      symtab.incrementFuncOffset(offset);

      // Add to stack
      Instr.addToCurLabel(new STR(offset,
          Condition.NO_CON, transferReg,
          AddrMode.buildAddrWithWriteBack(Instr.SP, -offset)));
    }
    //addToCurLabel(new LDR(transferReg, AddrMode.buildAddrWithOffset(Instr.SP
    //    , varNameOffset)));
    addToCurLabel(new ADD(false, transferReg, Instr.SP,
        AddrMode.buildImm(varNameOffset + accOffset)));
    addToCurLabel(new STR(transferReg, AddrMode.buildAddrWithWriteBack(Instr.SP, -(4))));

    // Function call
    instructions
        .add(new BRANCH(true, Condition.NO_CON, Label.CLASS_FUNC_HEADER + funcName));

//    instructions.add(new SUB(false, false, Instr.SP, Instr.SP, AddrMode.buildImm(varNameOffset)));
    // Destroy stack - STACK ALWAYS HAS TO BE DESTROYED AS IT WILL ALWAYS
    // CONTAIN THE INSTANCE'S ADDRESS
    instructions.add(
          new ADD(false, Instr.SP, Instr.SP, AddrMode.buildImm(accOffset + 4)));

    // Reset temporary offset
    symtab.resetFuncOffset();
    // Move result
    instructions.add(
        new MOV(Condition.NO_CON, transferReg, AddrMode.buildReg(Instr.R0)));

    addToCurLabel(instructions);
  }

}
