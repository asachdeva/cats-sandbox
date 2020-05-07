package chapter3

import cats.implicits._
import Chapter3.Tree._

class Chapter3Suite extends munit.FunSuite {
  test("Ex 3.5.4 -- Tree Functor test") {
    val treeBranch = branch(branch(leaf(1), leaf(2)), branch(leaf(3), branch(leaf(4), leaf(5))))
    assert(
      treeBranch.map(_ + 1) ==
        branch(branch(leaf(2), leaf(3)), branch(leaf(4), branch(leaf(5), leaf(6))))
    )
  }
}
