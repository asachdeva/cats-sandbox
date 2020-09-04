package sandbox

import cats._
import cats.syntax.all._

object Main extends App {
  println("Hello " |+| "Cats!")
  println("Hello " |+| "World")

  // Chapter -- 1
  // First part of the type class is the trait
  // with at least one type param and represents
  // some functionality we would like to implement

  sealed trait Json
  final case class JsString(get: String) extends Json
  final case class JsObject(get: Map[String, Json]) extends Json
  final case class JsNumber(get: Double) extends Json
  final case object JsNull extends Json

  trait JsonWriter[A] {
    def write(value: A): Json
  }

  // Second part of the typeclass is the Type class Instance
  // This provides implementations for the types we care about
  // in the below example we care about Employee Type

  final case class Person(name: String, email: String)

  //final case class Person(name: String, email: String)

  object JsonWriterInstances {
    implicit val stringWriter: JsonWriter[String] =
      new JsonWriter[String] {
        def write(value: String): Json =
          JsString(value)
      }

    implicit val personWriter: JsonWriter[Person] =
      new JsonWriter[Person] {
        def write(value: Person): Json =
          JsObject(
            Map(
              "name" -> JsString(value.name),
              "email" -> JsString(value.email)
            )
          )
      }
  }

  final case class Employee(name: String, age: Int, email: String)

  object JsonWriterInstances2 {
    implicit val employeeWriter: JsonWriter[Employee] =
      new JsonWriter[Employee] {
        def write(value: Employee): Json =
          JsObject(
            Map(
              "name" -> JsString(value.name),
              "age" -> JsNumber(value.age),
              "email" -> JsString(value.email)
            )
          )
      }
  }

  // Third Part of the typeclass is the Interface we expose
  // to external users:  This can be of 2 forms
  // Interface Objects or Interface Syntax
  // MethodA: Interface Object -- Singletonobject exposing functionality

  object Json {
    def toJson[A](value: A)(implicit w: JsonWriter[A]): Json =
      w.write(value)
  }

  // To use Import the Type class instances and
  // the InterfaceObject
  // Import the implicit type class instances
  import JsonWriterInstances._
  import JsonWriterInstances2._
  println(Json.toJson(Person("Akshay Sachdeva", "asachdeva@fp.com")))
  println(Json.toJson(Employee("Kristen Massey", 39, "kmassey22@yahoo.com")))

  // MethodB: Interface Syntax
  // In this case we need to import the Type class instances
  // as well as the Interface Syntax
  object JsonSyntax {
    implicit class JsonWriterOps[A](value: A) {
      def toJson(implicit w: JsonWriter[A]): Json =
        w.write(value)
    }
  }
  import JsonSyntax._
  import JsonWriterInstances._
  println(Person("Miles Sabin", "miles@sabin.com").toJson)
  println(Person("papa", "papa@papa.com").toJson(personWriter))

  // Ex 1.3 Printable Library
  // Part1
  trait Printable[A] {
    self =>
    def format(input: A): String

    def contramap[B](func: B => A): Printable[B] =
      new Printable[B] {
        def format(value: B): String =
          self.format(func(value))
      }
  }

  // Part 2
  object PrintableInstances {
    implicit val stringPrintable: Printable[String] =
      new Printable[String] {
        def format(input: String) = "\"" + input + "\""
      }

    implicit val intPrintable: Printable[Int] =
      new Printable[Int] {
        def format(input: Int) = input.toString()
      }

    implicit val booleanPrintable: Printable[Boolean] =
      new Printable[Boolean] {
        def format(input: Boolean): String =
          if (input) "yes" else "no"
      }
  }
  // Part 3
  object Printable {
    def format[A](value: A)(implicit p: Printable[A]): String =
      p.format(value)
    def print[A](value: A)(implicit p: Printable[A]): Unit =
      println(format(value))
  }

  // Using the Library
  final case class Cats(name: String, age: Int, color: String)

  import PrintableInstances._

  implicit val catPrintable: Printable[Cats] =
    new Printable[Cats] {
      def format(input: Cats) = {
        val name = Printable.format(input.name)
        val age = Printable.format(input.age)
        val color = Printable.format(input.color)
        s"$name is a $age year-old $color cat."
      }
    }

  val cat = Cats("Akshay", 46, "brown")

  Printable.print(cat)

  object PrintableSyntax {
    implicit class PrintableOps[A](input: A) {
      def format(implicit p: Printable[A]): String =
        p.format(input)

      def print(implicit p: Printable[A]): Unit =
        println(p.format(input))
    }
  }

  import PrintableSyntax._

  final case class Box[A](value: A)

  implicit def boxPrintable[A](implicit p: Printable[A]) =
    p.contramap[Box[A]](_.value)

  println(Printable.format(Box("hello Akshay")))
  println(Printable.format(Box(true)))

  Cats("Peter", 33, "black").print

  println(Printable.format("hello yo"))
  println(Printable.format(true))
  println(Printable.format(false))

  // Cats -- Show
  val showInt: Show[Int] = Show.apply[Int]
  val showString: Show[String] = Show.apply[String]

  val intAsString: String = showInt.show(123)
  println(intAsString)

  // Interface Syntax
  val shownInt = 123.show
  println(shownInt)
  println("Akshay".show)

  implicit val catShow: Show[Cats] =
    Show.show(cat => s"${cat.name} is a ${cat.age} year-old ${cat.color} cat.")
  println(Cats("Hello", 66, "blue").show)

  import java.util.Date
  implicit val dateShow: Show[Date] =
    new Show[Date] {
      def show(date: Date): String =
        s"${date.getTime}ms since the epoch"
    }

  val date = new Date()
  println(date.show)

  // Cats -- Eq Type class
  val eqInt = Eq[Int]
  println(eqInt.eqv(123, 124))
  println(eqInt.eqv(123, 123))
  println(123 === 123)

  (Some(1): Option[Int]) === (None: Option[Int])
  println(1.some === 1.some)
  println(1.some === none[Int])

  implicit val catEq: Eq[Cats] =
    Eq.instance[Cats] { (cat1, cat2) =>
      cat1.name === cat2.name &&
      cat1.age === cat2.age &&
      cat1.color === cat2.color
    }

  val cat1 = Cats("A", 12, "b")
  val cat2 = Cats("A", 12, "c")
  val cat3 = Cats("A", 12, "c")

  println(cat1.eqv(cat2))
  println(cat2.eqv(cat3))

  val optionCat1 = Option(cat1)
  val optionCat3 = Option.empty[Cats]

  println(optionCat1 === optionCat3)

  implicit val dateEq: Eq[Date] =
    Eq.instance[Date]((date1, date2) => date1.getTime === date2.getTime)

  // Chapter 2 -- Monoids and Semigroups
  // Monoids and Semigroup allow us to add
  // and combine values
  //
  // Three properties of addition
  //
  // 1] Addition of 2 Ints is a closed operation meaning
  // that adding 2 ints always produces another Int
  // 2] There is also an identity element 0 with the property
  // that a + 0 == 0 + a == a for any Int
  // 3] Associativity -- order does not matter
  // (1 + 2) + 3 == 1 + (2 + 3)
  //
  // For String we can apply the addition laws using
  // string concatenation where the identity string
  // is the empty string or ""
  //
  // Formally a monoid for a type A is
  // 1] an operation combine with type (A, A) => A
  // 2] an element empty (identity) of type A
  //
  // Monoids must obey laws in the
  // associative and identity laws

//      trait Monoid[A] {
//        def combine(x: A, y: A): A
//        def empty: A
//      }
//
//
  // Semigroup is just the combine part of Monoids
  // This applies to types where there cannot be an
  // empty element for example NonEmptyListhttps://github.com/asachdeva/vim_setup/blob/master/init.vim

  // Ex 2.3
  // There are 4 monoids for Booleans

  implicit val booleanAndMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(x: Boolean, y: Boolean): Boolean = x && y
      def empty = true
    }

  implicit val booleanOrMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(x: Boolean, y: Boolean): Boolean = x || y
      def empty = false
    }

  implicit val booleanEitherMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(x: Boolean, y: Boolean) =
        (!x && y) || (x && !y)
      def empty = false
    }

  implicit val booleanXnorMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(x: Boolean, y: Boolean) =
        (!x || y) && (x || !y)
      def empty = true
    }

  // Ex 2.4
  // Monoids for Sets

  // Note: The below is a def as opposed to a val
  // since we need to accept the type param A to
  // allow for Sets of any kind as in Int, String, Type
  implicit def setUnionMonoid[A]: Monoid[Set[A]] =
    new Monoid[Set[A]] {
      def combine(a: Set[A], b: Set[A]) = a.union(b)
      def empty = Set.empty[A]
    }

  val intSetMonoid = Monoid[Set[Int]]
  val stringSetMonoid = Monoid[Set[String]]

  println(intSetMonoid.combine(Set(1, 2, 3), Set(3, 4, 5)))
  println(stringSetMonoid.combine(Set("abc", "def"), Set("jkl", "abc", "def")))

  // Set Intersection is a semigroup since there is no Empty
  // Set complement and Set difference are not associative so they
  // cannot be considered for either Monoid or Semigroup
  // Symettric difference however is a Monoid
  // That is the union less the intersection
  //

  implicit def setIntersectionSemigroup[A]: Semigroup[Set[A]] =
    new Semigroup[Set[A]] {
      def combine(a: Set[A], b: Set[A]) = a.intersect(b)
    }

  println(setIntersectionSemigroup.combine(Set(1, 2, 3), Set(1, 2, 3, 4)))

  // Below example is how to combine Options using Monoid.combine
  // and syntax operator (on Semigroup)
  val oneOption = 1.some
  val twoOption = 2.some
  println(Monoid[Option[Int]].combine(oneOption, twoOption))
  println(oneOption |+| twoOption)

  println(Semigroup[List[Int]].combine(List(1, 2, 3), List(4, 5, 6)))

  val aMap = Map("foo" -> Map("bar" -> 5))
  val anotherMap = Map("foo" -> Map("bar" -> 6))
  val combineMap = aMap |+| anotherMap
  println(combineMap.get("foo"))
  println(Monoid[Map[String, Int]].combineAll(List(Map("a" -> 1, "b" -> 2), Map("a" -> 3))))
  println(Monoid[Map[String, Int]].combineAll(List()))

  // Ex 2.5.4
  def add(items: List[Int]): Int =
    items.foldLeft(0)(_ + _)

  println(add(List(1, 2, 3, 4)))

  // This is the context bound syntax from cats as opposed to writing
  // def combineAll[A](as: List[A])(implicit m: Monoid[A])
  def combineAll[A: Monoid](as: List[A]): A =
    as.foldLeft(Monoid[A].empty)(Monoid[A].combine)

  println(combineAll(List(1.some, 2.some, 3.some)))

  // Chapter 3 -- Functors
  //
  // Functors are abstractions that allows us to
  // represent sequences of operations within a context
  // such as a List, an Option.  A special case of functors
  // is monads or applicative functors
  //
  //
  // Internall a functor is anythign with a map method
  // so a List, an Option, an Either etc

  println(List(1, 2, 3).map(n => n + 1))
  println(List(1, 2, 3).map(n => n + 1).map(n => n + 2))

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  import scala.concurrent.{Await, Future}

  val future: Future[String] =
    Future(123).map(n => n + 1).map(n => n + 2).map(n => s"$n!")

  println(Await.result(future, 1.second))

  // Functions are also functors

  val func1: Int => Double =
    (x: Int) => x.toDouble

  val func2: Double => Double =
    (y: Double) => y * 2

  println(func1.map(func2)(1))
  println(func1.andThen(func2)(1))
  println(func2(func1(1)))

  val func =
    ((x: Int) => x.toDouble).map(x => x + 1).map(x => x * 2).map(x => s"${x}!")
  println("Akshay value of func is " + func(123))

  //trait Functor[F[_]] {
  //def map[A, B](fa: F[A])(f: A => B): F[B]
  //}

  val list1 = List(1, 2, 3)
  val list2 = Functor[List].map(list1)(_ * 2)
  val list3 = Functor[List].map(list1)(_ * 3)
  println(list2)
  println(list3)

  // Functor alsoe supplies the lift method

  val myfunc = (x: Int) => x + 77
  val liftedMyFunc = Functor[Option].lift(myfunc)
  println(liftedMyFunc(2.some))

  val func3 = (x: Int) => x + 1
  val liftedFunc = Functor[Option].lift(func3)
  println(liftedFunc(1.some))

  val f1 = (x: Int) => x + 1
  val f2 = (x: Int) => x + 2
  val f3 = (x: Int) => s"$x!"
  val f4 = f1.map(f2).map(f3)

  println(f4(123))

  def doMath[F[_]](start: F[Int])(implicit f: Functor[F]): F[Int] =
    start.map(n => n + 1 * 2)

  println(doMath(20.some))
  println(doMath(1.some))

  println(doMath(List(1, 2, 3)))

  def doMath2[F[_]: Functor](start: F[Int]): F[Int] =
    start.map(n => n + 1 * 2)

  println("DoMath2 " + doMath2(20.some))

  println("DoMath2 " + doMath2(List(1, 2, 3)))
  // Example to show Futures are not RT

  import scala.util.Random

  val future1 = {
    val r = new Random(0L)
    val x = Future(r.nextInt())
    for {
      a <- x
      b <- x
    } yield (a, b)
  }

  val future2 = {
    val r = new Random(0L)
    for {
      a <- Future(r.nextInt())
      b <- Future(r.nextInt())
    } yield (a, b)
  }

  val result1 = Await.result(future1, 1.second)
  val result2 = Await.result(future2, 1.second)
  println(result1)
  println(result2)

  // Ex 3.5.4
  sealed trait Tree[+A]
  final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  final case class Leaf[A](value: A) extends Tree[A]

  // Need smart constructors since the compiler cannot find Functor
  // instance for Branch or Leaf
  object Tree {
    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
      Branch(left, right)

    def leaf[A](value: A): Leaf[A] =
      Leaf(value)
  }

  implicit val treeFunctor: Functor[Tree] =
    new Functor[Tree] {
      def map[A, B](tree: Tree[A])(f: A => B): Tree[B] =
        tree match {
          case Branch(left, right) =>
            Branch(map(left)(f), map(right)(f))
          case Leaf(value) =>
            Leaf(f(value))
        }
    }

  Tree.branch(Tree.leaf(10), Tree.leaf(20)).map(_ * 2)

  trait Codec[A] {
    def encode(value: A): String
    def decode(value: String): A
    def imap[B](dec: A => B, enc: B => A): Codec[B] = {
      val self = this
      new Codec[B] {
        def encode(value: B): String =
          self.encode(enc(value))

        def decode(value: String): B =
          dec(self.decode(value))
      }
    }
  }

  def encode[A](value: A)(implicit c: Codec[A]): String =
    c.encode(value)

  def decode[A](value: String)(implicit c: Codec[A]): A =
    c.decode(value)

  implicit val stringCodec: Codec[String] =
    new Codec[String] {
      def encode(value: String): String = value
      def decode(value: String): String = value
    }

  implicit val intCodec: Codec[Int] =
    stringCodec.imap(_.toInt, _.toString)

  implicit val doubleCodec: Codec[Double] =
    stringCodec.imap(_.toDouble, _.toString)

  implicit val booleanCodec: Codec[Boolean] =
    stringCodec.imap(_.toBoolean, _.toString)

  println(encode(123.4))
  println(decode[Double]("123.4"))

  // Contramap in Cats
  val showSymbol = Contravariant[Show].contramap(showString)((sym: Symbol) => s"'${sym.name}")
  println(showSymbol.show(Symbol("dave")))

  // Chapter 4 Monads
  // Most common abstaction in Scala
  // Mechanism for seuqencing computations
  // informally anything with a constructor and a flatMap

  def parseInt(str: String): Option[Int] =
    scala.util.Try(str.toInt).toOption

  def divide(a: Int, b: Int): Option[Int] =
    if (b == 0) None else Some(a / b)

  def stringDivideBy(astr: String, bStr: String): Option[Int] =
    parseInt(astr).flatMap(aNum => parseInt(bStr).flatMap(bNum => divide(aNum, bNum)))

  def stringDivideByForComp(astr: String, bstr: String): Option[Int] =
    for {
      anum <- parseInt(astr)
      bnum <- parseInt(bstr)
      ans <- divide(anum, bnum)
    } yield ans

  println(stringDivideBy("8", "2"))
  println(stringDivideBy("8", "0"))
  println(stringDivideBy("zero", "3"))

  // Note: Every monad is also a functor
  // so we can use map and flatMap or for comprehensions
  //
  // trait Monad[F[_]] [
  //  def pure[A](value: A): F[A]
  //  def flatMap[A,B](value: F[A])(func: A => F[B]): F[B]
  //  def map[A,B](value: F[A])(func: A => B) : F[B] =
  //   flatmap(value)(a => pure(func(a)))

  def stringDivideByFor(aStr: String, bStr: String): Option[Int] =
    for {
      aNum <- parseInt(aStr)
      bNum <- parseInt(bStr)
      ans <- divide(aNum, bNum)
    } yield ans

  val opt1 = Monad[Option].pure(3)
  val opt2 = Monad[Option].flatMap(opt1)(x => Some(x + 3))

  println(opt2)

  val opt3 = Monad[Option].map(opt1)(x => 100 * x)
  println(opt3)

  val lista = Monad[List].pure(3)
  val listb = Monad[List].flatMap(List(1, 2, 3))(a => (List(a, a * 10)))
  println(listb)
  val listc = Monad[List].map(List(1, 2, 3))(a => a + 123)
  println(listc)

  val liste = 1.pure[List]
  val option1 = 1.pure[Option]
  val option77 = 77.pure[Option]

  def sumSquares2[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
    a.flatMap(x => b.map(y => x * x + y * y))

  def sumSquares[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
    for {
      x <- a
      y <- b
    } yield x * x + y * y

  println(sumSquares2(2.some, 3.some))
  println(sumSquares(List(1, 2, 3), List(3, 5)))

  println(sumSquares(3: Id[Int], 4: Id[Int]))

  val a = 3.asRight[String]
  val b = 4.asRight[String]

  println(a)

  def countPositive(numList: List[Int]): Either[String, Int] =
    numList.foldLeft(0.asRight[String]) { (accumulator, num) =>
      if (num > 0)
        accumulator.map(_ + 1)
      else
        Left("Negative Stopping")
    }

  def divZero(): Either[String, Int] =
    for {
      a <- 1.asRight[String]
      b <- 1.asRight[String]
      c <-
        if (b === 0) "DIV0".asLeft[Int]
        else (a / b).asRight[String]
    } yield c * 100

  println("DIV0 " + divZero())

  println(countPositive(List(1, 2, 3, 4)))
  println(countPositive(List(1, 2, -3, 4)))

  // Error Handling --
  // always define errors as ADT to be more explicit as opposed
  // to say using Throwable
  //
  //
  sealed trait LoginError extends Product with Serializable
  final case class UserNotFound(username: String) extends LoginError
  final case class PasswordIncorrect(username: String) extends LoginError
  case object UnexpectedError extends LoginError
  case class User(username: String, password: String)
  type LoginResult = Either[LoginError, User]

  def handleError(error: LoginError): Unit =
    error match {
      case UserNotFound(u) =>
        println(s"User Not found: $u")

      case PasswordIncorrect(u) =>
        println(s"Password is Incorrrect: $u")

      case UnexpectedError =>
        println(s"UnexpectedError")
    }

  // MonadError

  type ErrorOr[A] = Either[String, A]
  val monadError = MonadError[ErrorOr, String]

  val e = monadError.pure(42)
  val e2 = monadError.raiseError("Badness")

  // Eval Monad
  val now = Eval.now(math.random() + 1000)
  val later = Eval.later(math.random() + 1000)
  val always = Eval.always(math.random() + 1000)

  println(now.value)
  println(later.value)
  println(always.value)

  val greeting = Eval
    .always {
      println("Step 1"); "Hello"
    }
    .map { str => println("Step 2"); s"$str World" }

  println(greeting.value)

  val ans = for {
    a <- Eval.now { println("Calculating A"); 40 }
    b <- Eval.always { println("Calculating B"); 2 }
  } yield {
    println("Adding a and b")
    a + b
  }

  println(ans.value)
  println(ans.value)

  // This is not stack safe
  def factorial(n: BigInt): BigInt =
    if (n == 1) n else n * factorial(n - 1)

  def factorial2(n: BigInt): Eval[BigInt] =
    if (n == 1)
      Eval.now(n)
    else
      Eval.defer(factorial2(n - 1).map(_ * n))

  //println(factorial(50000))
  //println(factorial2(50000).value)

  // Writer Monad allows us to carry along a log
  // along with the computation
  import cats.data.Writer

  val w = Writer(Vector(
                   "It was the best of times",
                   "It was the worst of times"
                 ),
                 1859
  )

  type Logged[A] = Writer[Vector[String], A]

  println(123.pure[Logged])
  // If we have a log and no result we can use tell syntax
  Vector("msg1", "msg2").tell
  // Extract only the result using value or the log using written
  // or both using run

  val writ = 123.writer(Vector("msg1", "msg2"))
  println(writ.run)

  val writer1 = for {
    a <- 10.pure[Logged]
    _ <- Vector("a", "b", "c").tell
    b <- 32.writer(Vector("x", "y", "z"))
  } yield a + b

  println(writer1.run)
  println(writer1.mapWritten(_.map(_.toUpperCase)).run)
  // Log in a writer is preserved when we map and flatmap

  // Reader Monad
  // Sequence Operations that depend on some Input
  // One useful case is DI...chain together operations

  import cats.data.Reader
  case class Cat2(name: String, favoriteFood: String)
  val catName: Reader[Cat2, String] =
    Reader(cat => cat.name)
  val greetKitty: Reader[Cat2, String] =
    catName.map(name => s"Hello ${name}")
  val feedKitty: Reader[Cat2, String] =
    Reader(cat => s"Have a nice bowl of ${cat.favoriteFood}")
  val greetAndFeed: Reader[Cat2, String] =
    for {
      greet <- greetKitty
      feed <- feedKitty
    } yield s"$greet. $feed"

  println(catName.run(Cat2("Garfield", "Kibbles")))
  println(greetKitty.run(Cat2("Heathcliff", "junkfood")))
  println(greetAndFeed(Cat2("Buckles", "Softfood")))
  // Eval in foldright
  def foldRight[A, B](as: List[A], z: B)(fn: (A, B) => B): B =
    as match {
      case head :: tail =>
        fn(head, foldRight(tail, z)(fn))
      case Nil =>
        z
    }
}
