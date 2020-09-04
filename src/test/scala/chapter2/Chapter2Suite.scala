package chapter2

import Chapter2.BooleanMonoid
import Chapter2._

class Chapter2Suite extends munit.FunSuite {
  val booleanValues = List(true, false)
  val setValues = List(Set(1, 2, 3), Set(3, 4, 5), Set(4, 5, 6))

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
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

  test("Ex 2.3 orMonoid") {
    import BooleanMonoid.orMonoid
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

  test("Ex 2.3 booleanEitherMonoid") {
    import BooleanMonoid.booleanEitherMonoid
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

  test("Ex 2.3 booleanXnorMonoid") {
    import BooleanMonoid.booleanXnorMonoid
    testIdentityLaws(booleanValues)
    testAssociativityLaws(booleanValues)
  }

  test("Ex 2.4 setUnionMonoid") {
    implicit val setIntMonoid = SetMonoid.setUnionMonoid[Int]

    testIdentityLaws(setValues)
    testAssociativityLaws(setValues)
  }

  test("Ex 2.4 setSymDiffMonoid") {
    implicit val setIntMonoid = SetMonoid.symDiffMonoid[Int]

    testIdentityLaws(setValues)
    testAssociativityLaws(setValues)
  }

  test("Ex 2.5.4 -- SuperAdder test add3 test for ints Option[Int] and Orders") {

    import SuperAdder._

    val ints = List(1, 2, 3, 4, 5)
    assert(add3(ints) == 15)

    val optionInts = List(Option(1), Option(2))
    assert(add3(optionInts) == Option(3))

    val listOrders = List(Order(1.0, 2.0), Order(2.0, 3.0), Order(3.0, 4.0))
    assert(add3(listOrders) == Order(6.0, 9.0))

  }

}
