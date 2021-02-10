package frontend;

import frontend.abstractsyntaxtree.AST;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.PairTypeAST;
import frontend.abstractsyntaxtree.statements.SequenceAST;
import frontend.abstractsyntaxtree.statements.VarDecAST;
import frontend.symboltable.BoolID;
import frontend.symboltable.CharID;
import frontend.symboltable.IntID;
import frontend.symboltable.PairID;
import frontend.symboltable.StringID;
import frontend.symboltable.TypeID;
import java.io.IOException;
import org.junit.Test;

public class TypeSemanticsTests {

  //Creates a simple pair of type int and bool
  @Test
  public void validSimplePair() throws IOException {
    AST ast =
        TestUtilities.buildAST("simplePair.wacc");
    VarDecAST varDecAST = (VarDecAST) ast.getStatAST();;

    Node typeAST = varDecAST.getTypeAST();
    assert (typeAST instanceof PairTypeAST);
    PairTypeAST pairTypeAST = (PairTypeAST) typeAST;
    assert (pairTypeAST.getFst().getIdentifier().getType() instanceof IntID);
    assert (pairTypeAST.getSnd().getIdentifier().getType() instanceof BoolID);

    TypeID rhsType = varDecAST.getAssignRHS().getIdentifier().getType();
    assert (rhsType instanceof PairID);
    PairID pairID = (PairID) rhsType;
    assert (pairID.getFstType() instanceof IntID);
    assert (pairID.getSndType() instanceof BoolID);
  }

  //Creates a simple pair of type int and bool
	@Test
  public void validNestedPair() throws IOException {
    AST ast =
        TestUtilities.buildAST("nestedPair.wacc");
    Node statAST = ast.getStatAST();
    assert (statAST instanceof SequenceAST);
    VarDecAST varDecAST = (VarDecAST) ((SequenceAST) statAST).getStatements().get(1);

    Node typeAST = varDecAST.getTypeAST();
    assert (typeAST instanceof PairTypeAST);

    //LHS : Check outer pair
    PairTypeAST pairTypeAST = (PairTypeAST) typeAST;
    TypeID fstTypeLHS = pairTypeAST.getFst().getIdentifier().getType();
    TypeID sndTypeLHS = pairTypeAST.getSnd().getIdentifier().getType();
    assert (fstTypeLHS instanceof PairID);
    assert (sndTypeLHS instanceof PairID);

    //LHS : Check inner pair
    PairID fstPairLHS = (PairID) fstTypeLHS.getType();
    PairID sndPairLHS = (PairID) sndTypeLHS.getType();

    //RHS : Check outer pair
    TypeID rhsType = varDecAST.getAssignRHS().getIdentifier().getType();
    assert (rhsType instanceof PairID);
    PairID pairRHS = (PairID) rhsType;
    TypeID fstTypeRHS = pairRHS.getFstType();
    TypeID sndTypeRHS = pairRHS.getSndType();
    assert (fstTypeRHS instanceof PairID);
    assert (sndTypeRHS instanceof PairID);

    //RHS : Check inner pair
    PairID fstPairRHS = (PairID) fstTypeRHS.getType();
    PairID sndPairRHS = (PairID) sndTypeRHS.getType();
    assert (fstPairRHS.getFstType() instanceof IntID);
    assert (fstPairRHS.getSndType() instanceof BoolID);
    assert (sndPairRHS.getFstType() instanceof CharID);
    assert (sndPairRHS.getSndType() instanceof StringID);
  }
}
