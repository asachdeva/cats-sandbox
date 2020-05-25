package chapter7

class Chapter7Suite extends munit.FunSuite {
  test("Ex 7.1.1 + Ex 7.1.2 Foldable Tests") {

    import Chapter7.Foldable._
    assert(show(Nil) == "nil")
    assert(show(List(1, 2, 3)) == "3 then 2 then 1 then nil")

    assert(List(1, 2, 3).foldLeft(0)(_ - _) == -6)
    assert(List(1, 2, 3).foldRight(0)(_ - _) == 2)

    assert(List(1, 2, 3).foldLeft(List.empty[Int])((a, i) => i :: a) == List(3, 2, 1))
    assert(List(1, 2, 3).foldRight(List.empty[Int])((i, a) => i :: a) == List(1, 2, 3))

    assert(map(List(1, 2, 3))(_ * 2) == List(2, 4, 6))
    assert(flatMap(List(1, 2, 3))(a => List(a, a * 10, a * 100)) == List(1, 10, 100, 2, 20, 200, 3, 30, 300))
    assert(filter(List(1, 2, 3))(_ % 2 == 1) == List(1, 3))

  }
}
