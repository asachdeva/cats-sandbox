package chapter3

import cats.syntax.all._

class Chapter3Suite extends munit.FunSuite {
  test("Ex 3.5.4 -- Tree Functor test") {
    import Chapter3.Tree._
    val treeBranch = branch(branch(leaf(1), leaf(2)), branch(leaf(3), branch(leaf(4), leaf(5))))
    assert(
      treeBranch.map(_ + 1) ==
        branch(branch(leaf(2), leaf(3)), branch(leaf(4), branch(leaf(5), leaf(6))))
    )
  }

  test("Ex 3.6.1.1 -- Printable Contramap test") {
    import Chapter3.PrintableSyntax._
    import Chapter3.PrintableInstances._
    import Chapter3.Box

    assert(100L.format == "100")
    assert(Box(true).format == "true")
    assert(Box(100).format == "100")
  }

  test("Ex 3.6.2.1 -- Codec test") {
    import Chapter3.Box
    import Chapter3.Codec

    assert(Codec.encode(1234.5) == "1234.5")
    assert(Codec.decode[Double]("1234.5") == 1234.5)
    assert(Codec.encode(Box(1234.5)) == "1234.5")
    assert(Codec.decode[Box[Double]]("1234.5") == Box(1234.5))
  }
}
