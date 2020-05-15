package chapter4

import Chapter4.MonadInstance._
import cats.implicits._

class Chapter4Suite extends munit.FunSuite {
  test("Monad test -- getting func-y") {
    assert(Chapter4.Monad[Option].map(Option(6))(a => a + 4) == Option(10))
    assert(Chapter4.sumSquares(Option(2), Option(3)) == Option(13))
    assert(Chapter4.sumSquares(List(1, 2), List(3, 4)) == List(10, 17, 13, 20))
  }
}
