package frontend.abstractsyntaxtree.assignments;

import antlr.WaccParser.Call_assignRHSContext;
import backend.Utils;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import backend.instructions.STR;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.ArrayList;
import java.util.List;

public class AssignCallAST extends AssignRHSAST {

  private final String funcName;
  private final ArgListAST args;
  private final Call_assignRHSContext ctx;

  public AssignCallAST(
      String funcName, SymbolTable symtab, ArgListAST args,
      Call_assignRHSContext ctx) {
    super(symtab.lookupAll("func " + funcName), symtab);
    this.funcName = funcName;
    this.args = args;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    Identifier funcID = getIdentifier();
    int line = ctx.getStart().getLine();
    int identPos = ctx.IDENT().getSymbol().getCharPositionInLine();

    // Function not defined yet
    if (funcID == null) {
      SemanticErrorCollector.addFunctionUndefined(funcName, line, identPos);
    } else {
      if (funcID instanceof FuncID) {
        List<TypeID> params = ((FuncID) funcID).getParams();
        List<Node> argsAST = args.getArguments();
        int paramSize = params.size();
        int argsSize = argsAST.size();

        // If given number of arguments are not the same as the number of params
        if (paramSize != argsSize) {
          SemanticErrorCollector.addFuncInconsistentArgsError(
              line,
              ctx.argList().getStart().getCharPositionInLine(),
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
                SemanticErrorCollector.addFuncInconsistentArgTypeError(
                    line,
                    ctx.argList().expr(i).getStart().getCharPositionInLine(),
                    funcName,
                    i,
                    currParam.getTypeName(),
                    argType.getTypeName());
              }
            }
          }
        }
      } else {
        // Given function name is not actually a function type
        SemanticErrorCollector.addIsNotFuncError(
            line, identPos, funcName, funcID.getType().getTypeName());
      }
    }
  }

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instructions = new ArrayList<>();

    String transferReg = Instr.getTargetReg();
    for (int i = 0; i < args.getArguments().size(); i++) {
      Node argNode = args.getArguments().get(i);
      int argOffset = symtab.getStackOffset(argNode.getIdentifier().toString());
      instructions.add(new MOV("", transferReg,
          Utils.getEffectiveAddr(Instr.SP, argOffset)));

      TypeID paramID = ((FuncID) getIdentifier()).getParams().get(i);
      int paramOffset = symtab.getStackOffset(paramID.toString());
      instructions.add(new STR(transferReg, Utils.getEffectiveAddr(Instr.SP, paramOffset), 0));
    }

    instructions.add(new BRANCH(true, "", "f_" + funcName));

    // TODO: How to move the return value back into the LHS?
    return null;
  }
}
