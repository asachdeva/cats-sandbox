package chapter7

class Chapter7Suite extends munit.FunSuite {
  test("foldLeft Tests") {

    import Chapter7.Foldable._
    assert(show(Nil) == "nil")
    assert(show(List(1, 2, 3)) == "3 then 2 then 1 then nil")

    assert(List(1, 2, 3).foldLeft(List.empty[Int])((a, i) => i :: a) == List(3, 2, 1))
    assert(List(1, 2, 3).foldRight(List.empty[Int])((i, a) => i :: a) == List(1, 2, 3))

  }
}
