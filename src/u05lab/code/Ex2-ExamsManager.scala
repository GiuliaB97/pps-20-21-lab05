package u05lab.code
import scala.collection.mutable
object ExamsManagerTest extends App {

  /* See: https://bitbucket.org/mviroli/oop2018-esami/src/master/a01b/e1/Test.java */
  trait ExamsManager{

    def createNewCall(call: String): Unit
    def addStudentResult(call: String, student: String, result: ExamResult): Unit
    def getAllStudentsFromCall(call: String): Set[String]
    def getEvaluationsMapFromCall(call: String): Map[String, Integer]
    def getResultsMapFromStudent(student: String): Map[String, String]
    def getBestResultFromStudent(student: String): Option[Integer]

  }
object ExamsManager{
  def apply(): ExamsManager = ExamsManagerImpl()
   case class ExamsManagerImpl() extends ExamsManager {
     var map:  mutable.Map[String, collection.mutable.HashMap[String, Int]] = mutable.Map()

     override def createNewCall(call: String): Unit =  {
      if(!map.contains(call)) {
        map.addOne(call,mutable.HashMap())
      }
    }
//per il problema è qui; nel senso che se non passa l'esame io non lo metto(credo perchè non sarpe che risultato mettere)
    override def addStudentResult(call: String, student: String, result: ExamResult): Unit = {
      if(!map.get(call).contains(student))
        map(call)+=(student-> result.getEvaluation().get)
      else
        throw new IllegalArgumentException
    }

    override def getAllStudentsFromCall(call: String): Set[String] = {
      val set: mutable.Set[String]= mutable.Set()
      map.foreach((entry)=>if(entry._1.contains(call)) entry._2.foreach(student=> set.add(student._1)))
      set.toSet
    }

    override def getEvaluationsMapFromCall(call: String): Map[String, Integer] = {
      val map2: mutable.HashMap[String, Integer]= mutable.HashMap()

      if(map.contains(call)){
        map.get(call).foreach((entry)=> entry.foreachEntry((student, result)=> map2.addOne(student, result)))
        map2.toMap
      } else{
        Map.empty
      }
    }

    override def getResultsMapFromStudent(student: String): Map[String, String] = {
      val sm:collection.mutable.Map[String, String]= collection.mutable.Map()
      map.foreach(entry=> if (entry._2.contains(student))sm.addOne((student, entry._2.get(student).toString)))
      sm.toMap
    }

    override def getBestResultFromStudent(student: String): Option[Integer] = {
      var tmp=0
      getResultsMapFromStudent(student).foreach(elem=> if(elem._2.toInt> tmp) tmp=elem._2.toInt)
      if(tmp!=0) {
        Option(tmp)
      }else{
        Option.empty
      }
    }
  }
}
  trait Kind
  object Kind {
    case object RETIRED extends Kind
    case object FAILED extends Kind
    case object SUCCEEDED extends Kind
  }

  // Define a new enumeration with a type alias and work with the full set of enumerated values
  trait ExamResult{
    def getKind(): Kind
    def getEvaluation(): Option[Integer]
    def cumLaude(): Boolean
  }

  trait ExamResultFactory{
    def failed(): ExamResult
    def retired(): ExamResult
    def succeededCumLaude():ExamResult
    def succeeded( evaluation: Int): ExamResult
  }

  case class ExamResultFactoryImpl() extends ExamResultFactory{
    private case class ExamResultAbs(kind: Kind, evaluation: Option[Integer], laude:Boolean) extends ExamResult{
      def getKind(): Kind= kind
      def getEvaluation(): Option[Integer]= evaluation
      def cumLaude(): Boolean = laude
    }
    override def failed(): ExamResult = ExamResultAbs(Kind.FAILED, Option.empty, false)

    override def retired(): ExamResult = ExamResultAbs(Kind.RETIRED, Option.empty, false)

    override def succeededCumLaude(): ExamResult = ExamResultAbs(Kind.SUCCEEDED, Option(30), true)

    override def succeeded(evaluation: Int): ExamResult = ExamResultAbs(Kind.SUCCEEDED, Option(evaluation), false)
}

}

