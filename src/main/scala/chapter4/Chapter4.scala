package chapter4

object Chapter4 {
  def parseInt(str: String): Option[Int] =
    scala.util.Try(str.toInt).toOption

  def divide(a: Int, b: Int): Option[Int] =
    if (b == 0) None else Some(a / b)

  def stringDivideBy(aStr: String, bStr: String): Option[Int] =
    parseInt(aStr).flatMap { aNum =>
      parseInt(bStr).flatMap { bNum =>
        divide(aNum, bNum)
      }
    }

  def stringDivideByFor(aStr: String, bStr: String): Option[Int] =
    for {
      aNum <- parseInt(aStr)
      bNum <- parseInt(bStr)
      ans <- divide(aNum, bNum)
    } yield ans

  // Ex 4.1.2
  trait Monad[F[_]] {
    def pure[A](a: A): F[A]

    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def map[A, B](fa: F[A])(f: A => B): F[B] =
      flatMap(fa)(a => pure(f(a)))
  }

  object MonadInstance {
    implicit def monadOption[A] =
      new Monad[Option] {
        override def pure[F](a: F): Option[F] = Option(a)
        override def flatMap[F, B](fa: Option[F])(func: F => Option[B]): Option[B] =
          fa match {
            case Some(a) => func(a)
            case None    => Option.empty[B]
          }
      }
  }

  object Monad {
    def apply[F[_]](implicit monad: Monad[F]) = monad
  }

  import cats.implicits._

  def sumSquares[F[_]: cats.Monad](a: F[Int], b: F[Int]): F[Int] =
    a.flatMap(x => b.map(y => x * x + y * y))

  // Ex 4.3.1
  object IdMonad {

    import cats.Id

    def pure[A](a: A): Id[A] = a
    def flatMap[A, B](id: Id[A])(f: A => Id[B]): Id[B] = f(id)
    def map[A, B](id: Id[A])(f: A => B): Id[B] = f(id)
  }

  def countPositive(nums: List[Int]) =
    nums.foldLeft(0.asRight[String]) { (accumulator, num) =>
      if (num > 0)
        accumulator.map(_ + 1)
      else
        Left("Negative. Stopping1")
    }

  // Eval
  import cats.Eval

  val now = Eval.now(math.random() + 1000)
  val later = Eval.later(math.random() + 1000)
  val always = Eval.always(math.random() + 1000)

  def factorialStackSafe(n: BigInt): Eval[BigInt] =
    if (n == 1)
      Eval.now(n)
    else
      Eval.defer(factorialStackSafe(n - 1).map(_ * n))

  // Ex 4.6.5
  def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
    as match {
      case head :: tail =>
        fn(head, foldRight(tail, acc)(fn))
      case Nil =>
        acc
    }

  def foldRightEval[A, B](as: List[A], acc: Eval[B])(fn: (A, Eval[B]) => Eval[B]): Eval[B] =
    as match {
      case head :: tail =>
        Eval.defer(fn(head, foldRightEval(tail, acc)(fn)))
      case Nil =>
        acc
    }

  def foldRight2[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
    foldRightEval(as, Eval.now(acc))((a, b) => b.map(fn(a, _))).value

  import cats.data.Writer

  type Logged[A] = Writer[Vector[String], A]

  Vector("msg1", "msg2").tell

  def slowly[A](body: => A) =
    try body
    finally Thread.sleep(100)

  // Ex 4.7.3
  def factorial(n: Int): Logged[Int] =
    for {
      ans <-
        if (n == 0)
          1.pure[Logged]
        else
          slowly(factorial(n - 1).map(_ * n))
      _ <- Vector(s"fact $n $ans").tell
    } yield ans

  import cats.data.Reader

  case class Cat(name: String, favoriteFood: String)

  val catName: Reader[Cat, String] =
    Reader(cat => cat.name)

  catName.run(Cat("Akshay", "Chicken"))

  val greetKitty: Reader[Cat, String] =
    catName.map(name => s"Hello $name")

  val feedKitty: Reader[Cat, String] =
    Reader(cat => s"Have a nice bowl of ${cat.favoriteFood}")

  val greetAndFeed: Reader[Cat, String] =
    for {
      greet <- greetKitty
      feed <- feedKitty
    } yield (s"$greet $feed")

  // Ex 4.8.3
  object DbReader {
    final case class Db(
      usernames: Map[Int, String],
      passwords: Map[String, String]
    )

    type DbReader[A] = Reader[Db, A]

    def findUsername(userId: Int): DbReader[Option[String]] =
      Reader(db => db.usernames.get(userId))

    def checkPassword(username: String, password: String): DbReader[Boolean] =
      Reader(db => db.passwords.get(username).contains(password))

    def checkLogin(userId: Int, password: String): DbReader[Boolean] =
      for {
        username <- findUsername(userId)
        validPassword <-
          username
            .map { username =>
              checkPassword(username, password)
            }
            .getOrElse(false.pure[DbReader])
      } yield validPassword
  }

  import cats.data.State

  val a = State[Int, String] { state =>
    (state, s"The state is $state")
  }

  val (state, result) = a.run(10).value
  val state2 = a.runS(10).value
  val result2 = a.runA(10).value

  // Ex 4.9.3
  object PostOrderCalculator {
    import cats.data.State

    type CalcState[A] = State[List[Int], A]

    def evalOne(sym: String): CalcState[Int] =
      sym match {
        case "+" => operator(_ + _)
        case "-" => operator(_ - _)
        case "*" => operator(_ * _)
        case "/" => operator(_ / _)
        case num => operand(num.toInt)
      }

    def operand(num: Int): CalcState[Int] =
      State[List[Int], Int] { stack =>
        (num :: stack, num)
      }

    def operator(func: (Int, Int) => Int): CalcState[Int] =
      State[List[Int], Int] {
        case b :: a :: tail =>
          val ans = func(a, b)
          (ans :: tail, ans)

        case _ =>
          sys.error("Fail!")
      }

    def evalAll(input: List[String]): CalcState[Int] =
      input.foldLeft(0.pure[CalcState]) { (a, b) =>
        a.flatMap(_ => evalOne(b))
      }

    def evalInput(input: String): Int =
      evalAll(input.split(" ").toList).runA(Nil).value

  }

  // Ex 4.10.1
  sealed trait Tree[+A]
  final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  final case class Leaf[A](value: A) extends Tree[A]

  object TreeMonad {
    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] = Branch(left, right)

    def leaf[A](value: A): Tree[A] =
      Leaf(value)

    implicit val treeMonad: cats.Monad[Tree] = new cats.Monad[Tree] {
      def pure[A](x: A): Tree[A] = leaf(x)

      def flatMap[A, B](fa: Tree[A])(f: A => Tree[B]): Tree[B] =
        fa match {
          case Branch(l, r) => Branch(flatMap(l)(f), flatMap(r)(f))
          case Leaf(v)      => f(v)
        }

      def tailRecM[A, B](a: A)(f: A => Tree[Either[A, B]]): Tree[B] =
        flatMap(f(a)) {
          case Right(b) => leaf(b)
          case Left(a)  => tailRecM(a)(f)
        }
    }
  }

}
