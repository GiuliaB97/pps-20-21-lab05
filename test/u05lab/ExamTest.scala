package u05lab
import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertTrue}
import org.junit.jupiter.api.Test
import u05lab.code.ExamsManagerTest._
import u05lab.code.ExamsManagerTest.ExamsManager

import java.util.Optional
class ExamTest {
  val erf = ExamResultFactoryImpl()
  val em = ExamsManager()

  @Test
  def testExamResultBasicBehaviour() {
    //esame fallito, non c'è voto
    assertEquals(erf.failed().getKind(),  Kind.FAILED)
    //assertFalse(erf.failed().getEvaluation().isPresent())
    //assertFalse(erf.failed().cumLaude())
    // lo studente si è ritirato, non c'è voto
    assertEquals(erf.retired().getKind(), Kind.RETIRED)
    //assertFalse(erf.retired().getEvaluation().isPresent())
    //assertFalse(erf.retired().cumLaude());
    // 30L
    assertEquals(erf.succeededCumLaude().getKind(), Kind.SUCCEEDED)
    assertEquals(erf.succeededCumLaude().getEvaluation(), Optional.of(30))
    assertTrue(erf.succeededCumLaude().cumLaude())

    // esame superato, ma non con lode
    assertEquals(erf.succeeded(28).getKind(), Kind.SUCCEEDED)
    assertEquals(erf.succeeded(28).getEvaluation(), Optional.of(28))
    assertFalse(erf.succeeded(28).cumLaude())
  }

}