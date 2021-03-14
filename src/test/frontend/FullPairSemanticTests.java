package frontend;

import static frontend.TestUtilities.singleExitsWith;

import java.io.IOException;
import org.junit.Test;

public class FullPairSemanticTests {

  String testDir = "src/test/examples/extensions/fullPairs/";

  // ****** INVALID TESTS ******

  @Test
  public void invalidAssignmentTest() throws IOException {
    singleExitsWith(testDir + "invalidAssign.wacc", 200);
  }

  @Test
  public void nullDecAndBadReassignTest() throws IOException {
    singleExitsWith(testDir + "nullDecBadReassign.wacc", 200);
  }

  @Test
  public void assignNullBadReassignTest() throws IOException {
    singleExitsWith(testDir + "assignNullBadReassign.wacc", 200);
  }

  @Test
  public void nestedNullBadReassignTest() throws IOException {
    singleExitsWith(testDir + "nestedNullBadReassign.wacc", 200);
  }

  @Test
  public void badRepeatNewPairTest() throws IOException {
    singleExitsWith(testDir + "badRepeatNewPair.wacc", 200);
  }

  @Test
  public void deepNestedPairsTest() throws IOException {
    singleExitsWith(testDir + "deepNestedPairs.wacc", 200);
  }

  // ****** VALID TESTS ******

  @Test
  public void nullDecAndCorrectReassignTest() throws IOException {
    singleExitsWith(testDir + "nullDec.wacc", 0);
  }

  @Test
  public void assignNullReassignTest() throws IOException {
    singleExitsWith(testDir + "assignNullReassign.wacc", 0);
  }

  @Test
  public void nestedNullReassignTest() throws IOException {
    singleExitsWith(testDir + "nestedNullReassign.wacc", 0);
  }

  @Test
  public void repeatNewPairTest() throws IOException {
    singleExitsWith(testDir + "repeatNewPair.wacc", 0);
  }
}
