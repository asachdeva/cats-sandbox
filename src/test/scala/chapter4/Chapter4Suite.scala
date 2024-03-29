package chapter4

import Chapter4.MonadInstance._
import cats.syntax.all._

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
    assert(Chapter4.foldRight2(1 to 100000 toList, 0L)(_ + _) == 5000050000L)
  }

  test("4.7.3 WriterMonadTests") {
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

  test("4.8.3 ReaderMonadTests") {
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

  test("4.9.3 StateMonadTests") {
    import Chapter4.PostOrderCalculator._

    assert(evalOne("42").runA(Nil).value == 42)
    assert(evalAll(List("1", "2", "+", "3", "*")).runA(Nil).value == 9)
    assert(evalInput("1 2 + 3 *") == 9)

    val caught = intercept[RuntimeException] {
      evalInput("1 - 3")
    }
    assert(caught.getMessage() == "Fail!")
  }

  test("4.10.1 TreeMonadTests") {
    import Chapter4.Tree
    import Chapter4.TreeMonad._

    assert(10.pure[Tree] == leaf(10))

    val tree = branch(leaf(100), leaf(200)).flatMap(x => branch(leaf(x - 1), leaf(x + 1)))
    assert(tree == branch(branch(leaf(99), leaf(101)), branch(leaf(199), leaf(201))))

    val result =
      20.tailRecM[Tree, String](a => if (a == 20) leaf(Right(a.toString())) else leaf(Left(a + 1)))

    assert(result == leaf("20"))

    val result2 =
      10.tailRecM[Tree, String](a => if (a == 20) leaf(Right(a.toString())) else leaf(Left(a + 1)))

    assert(result2 == leaf("20"))
  }
}
