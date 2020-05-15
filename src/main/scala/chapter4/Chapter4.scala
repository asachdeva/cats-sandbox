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

}
