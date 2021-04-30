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

  @Test
  def testSpan() {
    assertEquals((List(10), List(20,30,40)), list.span(_<15))
    assertEquals((List(10, 20,30,40),Nil()), list.span(_>5))
  }

  @Test
  def testTakeRight(): Unit ={
    val resultlist = 30 :: 40 :: Nil()
    assertEquals(list, list.takeRight(0))
    assertEquals(resultlist, list.takeRight(2))

  }

  @Test
  def testReduce(): Unit ={
    val list = 10 :: 20 :: 30 :: 40 :: Nil()
    val finalValue = 100
    assertEquals(finalValue, list.reduce(_+_))

  }

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