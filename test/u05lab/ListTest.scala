package u05lab

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import u05lab.code._
class ListTest {
  val list = 10 :: 20 :: 30 :: 40 :: Nil()
  @Test
  def testIncremental() {
    assert(true)
  }

  @Test
  def testZipRight() {
    val l = List("a", "b", "c", "d")
    assertEquals(List.nil, List.nil.zipRight)
    assertEquals(List(("a", 0), ("b", 1),("c", 2), ("d", 3)), l.zipRight)
    assertEquals(List((10,0), (20,1), (30,2), (40,3)),list.zipRight)
  }

  @Test
  def testPartition() {
    val l = List(1, 2, 3, 4)
    val finalList1=List(2,3,4)
    val finalList2=List(1)
    val finalList3 = List(20,30,40)
    val finalList4 = List(10)
    assertEquals((finalList1,finalList2), l.partition(_>1))
    assertEquals((finalList3,finalList4) ,list.partition(_>15))
  }

  //Non chiara la differenza tra span e partition
  //assertEquals(List.nil, List.nil.span(_>15))  Perchè fallisce
  @Test
  def testSpan() {
    assertEquals((List(10), List(20,30,40)), list.span(_<15))
    assertEquals((List(10, 20,30,40),Nil()), list.span(_>5))
  }
  /*
    Problema:
    Implementandolo senza la funzione interna riesce a prendere i primi n elementi della lista,
    ma io devo restituire gli ultimi n, quindi sembra logico utilizzare la reverse per risolvere il problema.
    Tuttavia, ovviamente non potendo passare in input la lista a cui è stata applicata la reverse bisogna farlo
    internamente, ma on mi è chiaro come funzioni; io creo un metodo di appoggio che prende la lista a cui è
    già stata applicata l'operazione di reverse si occupi di prendere solo gli ultimi n elementi, ma non funziona.
    La ricorsione io chiamo su _takeRight eppure aggiungendo le stampe appare evidente che la ricorsione
    avvenga su tutta la funzione takeRight.
    E quindi il risultato finale risulta essere: Cons(40,Cons(10,Nil()))
    Non capisco come fare a far sì che la reverse venga chiamata una sola volta all'inizio e poi _takeRight processi
    solo la lista iniziale a cui è applicato la funzione reverse senza riapplicare l'operazione ogni volta.

  list before reversing Cons(40,Cons(30,Cons(20,Cons(10,Nil()))))
  list after reversing Cons(10,Cons(20,Cons(30,Cons(40,Nil()))))
  list before reversing Cons(20,Cons(30,Cons(40,Nil())))
  list after reversing Cons(40,Cons(30,Cons(20,Nil())))
  list before reversing Cons(30,Cons(20,Nil()))
  list after reversing Cons(20,Cons(30,Nil()))
     */
  @Test
  def testTakeRight(): Unit ={
    val resultlist = 30 :: 40 :: Nil()
    assertEquals(list, list.takeRight(0))
    assertEquals(resultlist, list.takeRight(2))

  }

  //Lancia l'eccezione in ogni caso; non so cosa io stia sbagliando
  //non so come testare il lancio delle eccezioni
  @Test
  def testReduce(): Unit ={
    val list = 10 :: 20 :: 30 :: 40 :: Nil()
    val finalValue = 100
    assertEquals(finalValue, list.reduce(_+_))

  }

  //Non implementato non so da che parte partire
  @Test
  def testCollection(): Unit={
    val list = 10 :: 20 :: 30 :: 40 :: Nil()
    val finalValue = 9 :: 39 :: Nil()
    val p1 = new PartialFunction[Int, Int] {
      def isDefinedAt(x: Int) = x < 15 || x > 35
      def apply(x:Int) = x - 1
    }
    /* Errore p1
    org.opentest4j.AssertionFailedError:
    Expected :Cons(9,Cons(39,Nil()))
    Actual   :Cons(9,Cons(19,Cons(29,Cons(39,Nil()))))
     */
    val p2 : PartialFunction[Int, Int] = {
      case x if(x < 15 || x > 35) => x-1
     }
  /* errore p2
  scala.MatchError: 20 (of class java.lang.Integer)
   */
    assertEquals(finalValue, list.collect(p2))
  }

  @Test
  def testSequence(): Unit={

  }
}