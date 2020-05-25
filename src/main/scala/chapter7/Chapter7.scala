package chapter7

object Chapter7 {

  object Foldable {
    def show[A](list: List[A]): String =
      list.foldLeft("nil")((accum, item) => s"$item then $accum")

    // Ex 7.1.2
    List(1, 2, 3).foldLeft(List.empty[Int])((a, i) => i :: a)
    List(1, 2, 3).foldRight(List.empty[Int])((i, a) => i :: a)

    // Ex 7.1.3
    def map[A, B](list: List[A])(f: A => B): List[B] =
      list.foldRight(List.empty[B]) { (item, accum) =>
        f(item) :: accum
      }

    def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] =
      list.foldRight(List.empty[B]) { (item, accum) =>
        f(item) ::: accum
      }

    def filter[A](list: List[A])(f: A => Boolean): List[A] =
      list.foldRight(List.empty[A]) { (item, accum) =>
        if (f(item)) item :: accum else accum
      }

    import scala.math.Numeric

    def sumWithNumric[A](list: List[A])(implicit numeric: Numeric[A]): A =
      list.foldRight(numeric.zero)(numeric.plus)

    import cats._

    def sumWithMonoid[A](list: List[A])(implicit m: Monoid[A]): A =
      list.foldRight(m.empty)(m.combine)
  }

  object Traversable {

    import scala.concurrent._
    import scala.concurrent.ExecutionContext.Implicits.global

    val hostnames = List(
      "alpha.example.com",
      "beta.example.com",
      "gamma.demo.com"
    )

    def getUptime(hostname: String): Future[Int] =
      Future(hostname.length * 60)

    val allUptimes: Future[List[Int]] =
      hostnames.foldLeft(Future(List.empty[Int])) { (accum, host) =>
        val uptime = getUptime(host)
        for {
          accum <- accum
          uptime <- uptime
        } yield accum :+ uptime
      }

    val allUptimesUsingTraverse: Future[List[Int]] =
      Future.traverse(hostnames)(getUptime)

    // Ex 7.2.2.1
    import cats.implicits._
    import cats._

    def listTraverse[F[_]: Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
      list.foldLeft(List.empty[B].pure[F]) { (accum, item) =>
        (accum, func(item)).mapN(_ :+ _)
      }

    def listSequence[F[_]: Applicative, B](list: List[F[B]]): F[List[B]] =
      listTraverse(list)(identity)
  }
}
