package chapter7

import cats.Foldable

class Chapter7Suite extends munit.FunSuite {
  test("Ex 7.1.1 + Ex 7.1.2 Foldable Tests") {

    import Chapter7.Foldable._
    import cats.implicits._

    assert(show(Nil) == "nil")
    assert(show(List(1, 2, 3)) == "3 then 2 then 1 then nil")

    assert(List(1, 2, 3).foldLeft(0)(_ - _) == -6)
    assert(List(1, 2, 3).foldRight(0)(_ - _) == 2)

    assert(List(1, 2, 3).foldLeft(List.empty[Int])((a, i) => i :: a) == List(3, 2, 1))
    assert(List(1, 2, 3).foldRight(List.empty[Int])((i, a) => i :: a) == List(1, 2, 3))

    assert(map(List(1, 2, 3))(_ * 2) == List(2, 4, 6))
    assert(flatMap(List(1, 2, 3))(a => List(a, a * 10, a * 100)) == List(1, 10, 100, 2, 20, 200, 3, 30, 300))
    assert(filter(List(1, 2, 3))(_ % 2 == 1) == List(1, 3))
    assert(sumWithNumric(List(1, 2, 3)) == 6)
    assert(sumWithMonoid(List(1, 2, 3)) == 6)

    val temp = List(1, 2, 3)
    assert(Foldable[List].foldLeft(temp, 0)(_ + _) == 6)
    assert(Foldable[List].combineAll(temp) == 6)
    assert(Foldable[List].foldMap(temp)(_.toString) == "123")
  }

  test("Traverable Tests") {
    import scala.concurrent._
    import scala.concurrent.duration._

    assert(
      Await.result(Chapter7.Traversable.allUptimes, 1.second) == Await
        .result(Chapter7.Traversable.allUptimesUsingTraverse, 1.second)
    )

    import Chapter7.Traversable._
    import cats.implicits._
    println(listSequence(List(Vector(1, 2), Vector(3, 4))))
  }
}
