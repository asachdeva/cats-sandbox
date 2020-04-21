package chapter1

import cats._
import cats.implicits._

import munit.FunSuite

class Chapter1Suite  extends FunSuite {
  import Chapter1.PrintableInstances._
  import Chapter1._
  test("Ex 1.3")  {
    assert(Printable.format("100") == "100")
    assert(Printable.format(100) == "100")

    val akshayCat = Cat("Akshay", 46, "black")
    assert(Printable.format(akshayCat) == "Akshay is a 46 year-old black cat.")
  }

  test("Ex 1.4.6") {
   implicit val catShow: Show[Cat] =
     Show.show{cat =>
         val name = cat.name.show
         val age = cat.age.show
         val color = cat.color.show

         s"$name is a $age year-old $color cat."
  }

  val niobe = Cat("Niobe", 9, "black")
  assert(niobe.show == "Niobe is a 9 year-old black cat.")
  }

  test("Ex 1.5.5") {
    implicit val catEq: Eq[Cat] =
      Eq.instance[Cat] {(cat1, cat2) =>
        cat1.name === cat2.name &&
        cat1.age === cat2.age &&
        cat1.color === cat2.color
      }

      val cat1 = Cat("akshay", 10, "black")
      val cat2 = Cat("akshay", 10, "black")
      val cat3 = Cat("akshay", 9, "black")

      assert(cat1 === cat2)
      assert(cat1 =!= cat3)

  }
}
