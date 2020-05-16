package chapter4

import Chapter4.MonadInstance._
import cats.implicits._

class Chapter4Suite extends munit.FunSuite {
  test("Monad test -- getting func-y") {
    assert(Chapter4.Monad[Option].map(Option(6))(a => a + 4) == Option(10))
    assert(Chapter4.sumSquares(Option(2), Option(3)) == Option(13))
    assert(Chapter4.sumSquares(List(1, 2), List(3, 4)) == List(10, 17, 13, 20))
    assert(Chapter4.sumSquares(3: cats.Id[Int], 4: cats.Id[Int]) == 25)

    val tempList = List(1, 2, 3)
    assert(Chapter4.countPositive(tempList) == 3.asRight[String])
  }

  test("4.6.5 FoldRightEvalTest") {
    assert(Chapter4.foldRight2((1 to 100000 toList), 0L)(_ + _) == 5000050000L)
  }

  test("4.7.3 WriterMonadTest") {
    import scala.concurrent._
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global
    import Chapter4._

    val result = Await.result(
      Future.sequence(
        Vector(
          Future(factorial(3).run),
          Future(factorial(6).run)
        )
      ),
      30.seconds
    )

    assert(
      result == Vector(
        (Vector("fact 0 1", "fact 1 1", "fact 2 2", "fact 3 6"), 6),
        (
          Vector(
            "fact 0 1",
            "fact 1 1",
            "fact 2 2",
            "fact 3 6",
            "fact 4 24",
            "fact 5 120",
            "fact 6 720"
          ),
          720
        )
      )
    )
  }

  test("4.8.3 ReaderMonadtest") {
    import Chapter4.DbReader._

    val users = Map(
      1 -> "dade",
      2 -> "kate",
      3 -> "margo"
    )

    val passwords = Map(
      "dade" -> "zerocool",
      "kate" -> "acidburn",
      "margo" -> "secret"
    )
    val db = Db(users, passwords)

    assert(checkLogin(1, "zerocool").run(db) == true)
    assert(checkLogin(2, "akshay").run(db) == false)
  }
}
