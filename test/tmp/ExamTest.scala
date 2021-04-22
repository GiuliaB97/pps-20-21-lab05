package tmp

import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertThrows, assertTrue}
import org.junit.jupiter.api.Test
import u05lab.code.ExamsManagerTest.{ExamsManager, _}

import java.util.Optional
class ExamTest {
  val erf = ExamResultFactoryImpl()
  val em = ExamsManager()

  @Test
  def testExamResultBasicBehaviour() {
    //esame fallito, non c'è voto
    assertEquals(erf.failed().getKind,  Kind.FAILED)
    assertFalse(erf.failed().getEvaluation.isDefined)
    assertFalse(erf.failed().cumLaude)
		// lo studente si è ritirato, non c'è voto
    assertEquals(erf.retired().getKind, Kind.RETIRED)
    assertFalse(erf.retired().getEvaluation.isDefined)
    assertFalse(erf.retired().cumLaude);
		// 30L
    assertEquals(erf.succeededCumLaude().getKind(), Kind.SUCCEEDED)
    assertEquals(erf.succeededCumLaude().getEvaluation, Option.apply(30))
    assertTrue(erf.succeededCumLaude().cumLaude())

    // esame superato, ma non con lode
    assertEquals(erf.succeeded(28).getKind(), Kind.SUCCEEDED)
    assertEquals(erf.succeeded(28).getEvaluation, Option.apply(28))
    assertFalse(erf.succeeded(28).cumLaude)
	}


	// verifica eccezione in ExamResultFactory
	@Test def optionalTestEvaluationCantBeGreaterThan30(): Unit ={
		assertThrows(classOf[IllegalArgumentException], () => erf.succeeded(32))
    }

	// verifica eccezione in ExamResultFactory
	@Test def optionalTestEvaluationCantBeSmallerThan18() {
		assertThrows(classOf[IllegalArgumentException], () => erf.succeeded(17))
    }


	// metodo di creazione di una situazione di risultati in 3 appelli

	def prepareExams() {
		em.createNewCall("gennaio");
		em.createNewCall("febbraio");
		em.createNewCall("marzo");

    em.addStudentResult("gennaio", "rossi",
      erf.failed()); // rossi -> fallito
		em.addStudentResult("gennaio", "bianchi",
      erf.retired()); // bianchi -> ritirato
		em.addStudentResult("gennaio", "verdi", erf.succeeded(28)); // verdi -> 28
		em.addStudentResult("gennaio", "neri", erf.succeededCumLaude()); // neri -> 30L

		em.addStudentResult("febbraio", "rossi", erf.failed()); // etc..
		em.addStudentResult("febbraio", "bianchi", erf.succeeded(20));
		em.addStudentResult("febbraio", "verdi", erf.succeeded(30));

		em.addStudentResult("marzo", "rossi", erf.succeeded(25));
		em.addStudentResult("marzo", "bianchi", erf.succeeded(25));
		em.addStudentResult("marzo", "viola", erf.failed());
	}

	// verifica base della parte obbligatoria di ExamManager
	@Test
    def testExamsManagement() {
		prepareExams();
		// partecipanti agli appelli di gennaio e marzo
		assertEquals(em.getAllStudentsFromCall("gennaio"),Set("rossi","bianchi","verdi","neri"));
		assertEquals(em.getAllStudentsFromCall("marzo"),Set("rossi","bianchi","viola"));

		// promossi di gennaio con voto
		assertEquals(em.getEvaluationsMapFromCall("gennaio").toSeq.size,2);
		assertEquals(em.getEvaluationsMapFromCall("gennaio").get("verdi"),28);
		assertEquals(em.getEvaluationsMapFromCall("gennaio").get("neri"),30);
		// promossi di febbraio con voto
		assertEquals(em.getEvaluationsMapFromCall("febbraio").toSeq.size,2);
		assertEquals(em.getEvaluationsMapFromCall("febbraio").get("bianchi"),20);
		assertEquals(em.getEvaluationsMapFromCall("febbraio").get("verdi"),30);

		// tutti i risultati di rossi (attenzione ai toString!!)
		assertEquals(em.getResultsMapFromStudent("rossi").toSeq.size,3);
		assertEquals(em.getResultsMapFromStudent("rossi").get("gennaio"),"FAILED");
		assertEquals(em.getResultsMapFromStudent("rossi").get("febbraio"),"FAILED");
		assertEquals(em.getResultsMapFromStudent("rossi").get("marzo"),"SUCCEEDED(25)");
		// tutti i risultati di bianchi
		assertEquals(em.getResultsMapFromStudent("bianchi").toSeq.size,3);
		assertEquals(em.getResultsMapFromStudent("bianchi").get("gennaio"),"RETIRED");
		assertEquals(em.getResultsMapFromStudent("bianchi").get("febbraio"),"SUCCEEDED(20)");
		assertEquals(em.getResultsMapFromStudent("bianchi").get("marzo"),"SUCCEEDED(25)");
		// tutti i risultati di neri
		assertEquals(em.getResultsMapFromStudent("neri").toSeq.size,1);
		assertEquals(em.getResultsMapFromStudent("neri").get("gennaio"),"SUCCEEDED(30L)");

	}

	// verifica del metodo ExamManager.getBestResultFromStudent
	@Test
    def optionalTestExamsManagement() {
		this.prepareExams();
		// miglior voto acquisito da ogni studente, o vuoto..
		assertEquals(em.getBestResultFromStudent("rossi"),Optional.of(25));
		assertEquals(em.getBestResultFromStudent("bianchi"),Optional.of(25));
		assertEquals(em.getBestResultFromStudent("neri"),Optional.of(30));
		assertEquals(em.getBestResultFromStudent("viola"),Optional.empty());
	}


	@Test def optionalTestCantCreateACallTwice() {
		this.prepareExams();
		assertThrows(classOf[IllegalArgumentException], () => em.createNewCall("marzo"))
    }

	@Test def optionalTestCantRegisterAnEvaluationTwice() {
		this.prepareExams();
		assertThrows(classOf[IllegalArgumentException], () => em.addStudentResult("gennaio", "verdi", erf.failed()))
    }

}