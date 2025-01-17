package u05lab.code

sealed trait List[A] {
  def head: Option[A]

  def tail: Option[List[A]]

  def append(list: List[A]): List[A]

  def foreach(consumer: (A) => Unit): Unit

  def get(pos: Int): Option[A]

  def filter(predicate: (A) => Boolean): List[A]

  def map[B](fun: (A) => B): List[B]

  def toSeq: Seq[A]

  def foldLeft[B](acc: B)(f: (B,A)=>B): B

  def foldRight[B](acc: B)(f: (A,B)=>B): B

  def flatMap[B](f: A => List[B]): List[B]

  def reverse(): List[A]

  def zipRight: List[(A,Int)]

  def partition(pred: A => Boolean): (List[A],List[A])

  def span(pred: A => Boolean): (List[A],List[A])

  def reduce(op: (A,A)=>A): A

  def takeRight(n: Int): List[A]

  def collect[A,B](partialFunction: PartialFunction[A,B]): List[B]

  def sequence[A](a: List[Option[A]]): Option[List[A]]
  // right-associative construction: 10 :: 20 :: 30 :: Nil()
  def ::(head: A): List[A] = Cons(head,this)
}

// defining concrete implementations based on the same template

case class Cons[A](_head: A, _tail: List[A])
  extends ListImplementation[A]

case class Nil[A]()
  extends ListImplementation[A]

// enabling pattern matching on ::

object :: {
  def unapply[A](l: List[A]): Option[(A,List[A])] = l match {
    case Cons(h,t) => Some((h,t))
    case _ => None
  }
}

// List algorithms
trait ListImplementation[A] extends List[A] {

  override def head: Option[A] = this match {
    case h :: t => Some(h)
    case _ => None
  }

  override def tail: Option[List[A]] = this match {
    case h :: t => Some(t)
    case _ => None
  }

  override def append(list: List[A]): List[A] = this match {
    case h :: t => h :: (t append list)
    case _ => list
  }

  override def foreach(consumer: (A) => Unit): Unit = this match {
    case h :: t => {
      consumer(h); t foreach consumer
    }
    case _ => None
  }

  override def get(pos: Int): Option[A] = this match {
    case h :: t if pos == 0 => Some(h)
    case h :: t if pos > 0 => t get (pos - 1)
    case _ => None
  }

  override def filter(predicate: (A) => Boolean): List[A] = this match {
    case h :: t if (predicate(h)) => h :: (t filter predicate)
    case _ :: t => (t filter predicate)
    case _ => Nil()
  }

  override def map[B](fun: (A) => B): List[B] = this match {
    case h :: t => fun(h) :: (t map fun)
    case _ => Nil()
  }

  override def toSeq: Seq[A] = this match {
    case h :: t => h +: t.toSeq // using method '+:' in Seq..
    case _ => Seq()
  }

  override def foldLeft[B](acc: B)(f: (B, A) => B): B = this match {
    case Cons(h, t) => t.foldLeft(f(acc, h))(f)
    case Nil() => acc
  }

  override def foldRight[B](acc: B)(f: (A, B) => B): B = //la foldright processa gli ultimi elementi della lista prima
  //la foldLeft invece procede in ordine naturale da sx a dx
    this.reverse().foldLeft(acc)((acc, elem) => f(elem, acc))

  override def reverse(): List[A] =
    this.foldLeft(Nil[A].asInstanceOf[List[A]])((acc, elem) => Cons(elem, acc))

  override def flatMap[B](f: A => List[B]): List[B] = this match {
    case Cons(h, t) => f(h).append(t.flatMap(f))
    case Nil() => Nil()
  }

  //EX1 :
  override def zipRight: List[(A, Int)] = {
    var k = -1
    this.map(e => {
      k += 1; (e, k)
    }) //RICORDA la map mi da in output un numero di elementi pari a quello preso in ingresso nella lista, cambiati secondo il filtro passato, la flatMap no
  }

  private def neg[A](predicate: A => Boolean): A => Boolean = !predicate(_)

  //EX2 :
  override def partition(pred: A => Boolean): (List[A], List[A]) = {
    (this.filter(pred), this.filter(neg(pred)))
  }

  private def filterR(predicate: (A) => Boolean): List[A] = this match {
    case h :: t if (predicate(h)) => h :: (t filter predicate)
    case _ :: _ => Nil()
  }

  private def filterL(predicate: (A) => Boolean): List[A] = this match {
    case h :: t if (predicate(h)) => t filter predicate
    case h :: t => h :: t
  }

  //EX3 :
  override def span(pred: A => Boolean): (List[A], List[A]) = {
    (this.filterR(pred), this.filterL(pred))
  }

  //EX4:
  /**
    *
    * @throws UnsupportedOperationException if the list is empty
    */
  override def reduce(op: (A, A) => A): A = this match {
    case Cons(h, t) if (t == Nil()) => h
    case Cons(h, t) => op(h, t.reduce(op))
    case _ => throw new UnsupportedOperationException()
  }

  //EX5:
  override def takeRight(n: Int): List[A] = {
    def _takeRight(n: Int, list: List[A]): List[A] = list match { //Ricorda di fare il match su list e non su this, se no lui matcha sempre sulla lista originale
      case Cons(h, t) if (n > 0) => Cons(h, _takeRight(n - 1, t))
      case _ => Nil()
    }
    _takeRight(n, this.reverse).reverse()
  }

  override def collect[A,B](partialFunction: PartialFunction[A,B]):List[B] = {
    val list: ListImplementation[A] = this.asInstanceOf[ListImplementation[A]]
    var list2: ListImplementation[B]= Nil()
     def _collect( list: ListImplementation[A], partialFunction: PartialFunction[A,B]): ListImplementation[B]= list match{
       case h::t => list2 = list2.append(Cons(partialFunction(h), Nil())).asInstanceOf[ListImplementation[B]];
                    _collect ( t.asInstanceOf[ListImplementation[A]] , partialFunction);
       case _=> list2
  }
    _collect(list, partialFunction)

  }

  //es 4: O
  override def sequence[A](a: List[Option[A]]): Option[List[A]]= ???
}
// Factories
object List {

// Smart constructors
def nil[A]: List[A] = Nil()
def cons[A](h: A, t: List[A]): List[A] = Cons(h,t)

def apply[A](elems: A*): List[A] = {
var list: List[A] = Nil()
for (i <- elems.length-1 to 0 by -1) list = elems(i) :: list
list
}

def of[A](elem: A, n: Int): List[A] =
if (n==0) Nil() else elem :: of(elem,n-1)
}

object ListsTest extends App {

import List._  // Working with the above lists
println(List(10,20,30,40))
val l: List[Int] = 10 :: 20 :: 30 :: 40 :: Nil() // same as above
println(l.head) // 10
println(l.tail) // 20,30,40
println(l append l) // 10,20,30,40,10,20,30,40
println(l append l toSeq) // as a list: 10,20,30,40,10,20,30,40
println(l get 2) // 30
println(of("a",10)) // a,a,a,..,a
println(l filter (_<=20) map ("a"+_) ) // a10, a20

assert(List(1,2,3) == List(1,2,3))

println("partition" +scala.collection.immutable.List(10,20,30,40).partition(_>15))
println("span" + scala.collection.immutable.List(10,20,30,40).span(_>15))

// Ex. 1: zipRight
println("zip"+l.zipRight.toSeq) // List((10,0), (20,1), (30,2), (40,3))

// Ex. 2: partition
println("partition"+l.partition(_>15)) // ( Cons(20,Cons(30,Cons(40,Nil()))), Cons(10,Nil()) )

// Ex. 3: span
println("span" +l.span(_>15)) // ( Nil(), Cons(10,Cons(20,Cons(30,Cons(40,Nil())))) )
println("span" +l.span(_<15)) // ( Cons(10,Nil()), Cons(20,Cons(30,Cons(40,Nil()))) )

// Ex. 4: reduce
println("reduce " +l.reduce(_+_))// 100
try { List[Int]().reduce(_+_); assert(false) } catch { case _:UnsupportedOperationException => }

// Ex. 5: takeRight
println("take right" + l.takeRight(2)) // Cons(30,Cons(40,Nil()))

// Ex. 6: collect
  val p = new PartialFunction[Int, Int] {
    def apply(x: Int): Int = x - 1
    def isDefinedAt(x: Int): Boolean = x < 15 || x > 35
  }
//println(l.collect { case x if x<15 || x>35 => x-1 }) // Cons(9, Cons(39, Nil()))
  println(l.collect(p))
}