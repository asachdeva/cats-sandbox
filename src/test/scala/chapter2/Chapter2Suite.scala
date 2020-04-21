package chapter2

import Chapter2.BooleanMonoid
import Chapter2._

class Chapter2Suite extends munit.FunSuite {

  def identityLaw[A](x: A)(implicit m: Monoid[A]): Boolean =
    (m.combine(x, m.empty) == x) && (m.combine(m.empty, x) == x)

  def associativeLaw[A](x: A, y: A, z: A)(implicit m: Monoid[A]): Boolean =
    m.combine(x, m.combine(y, z)) == m.combine(m.combine(x, y), z)

  def testIdentityLaws[A](values: List[A])(implicit m: Monoid[A]): Unit =
    for {
      x <- values
    } assert(identityLaw(x), s"Identity failed for $x")

  def testAssociativityLaws[A](values: List[A])(implicit m: Monoid[A]): Unit =
    for {
      x <- values
      y <- values
      z <- values
    } assert(associativeLaw(x, y, z), s"Associativity Laws failed for $x, $y, $z")

  test("Ex 2.3 andMonoid") {
    import BooleanMonoid.andMonoid
    val booleanValues = List(true, false)
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

  test("Ex 2.3 orMonoid") {
    import BooleanMonoid.orMonoid
    val booleanValues = List(true, false)
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

  test("Ex 2.3 booleanEitherMonoid") {
    import BooleanMonoid.booleanEitherMonoid
    val booleanValues = List(true, false)
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

  test("Ex 2.3 booleanXnorMonoid") {
    import BooleanMonoid.booleanXnorMonoid
    val booleanValues = List(true, false)
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

}
