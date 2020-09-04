package chapter1

object Chapter1 {

  trait Printable[A] {
    def format(value: A): String
  }

  object PrintableInstances {
    implicit val stringPrinatble: Printable[String] =
      new Printable[String] {
        def format(value: String): String =
          value
      }

    implicit val intPrinntable: Printable[Int] =
      new Printable[Int] {
        def format(value: Int): String =
          value.toString()
      }
  }

  object Printable {
    def format[A](value: A)(implicit p: Printable[A]): String =
      p.format(value)

    def print[A](value: A)(implicit p: Printable[A]): Unit =
      println(format(value))
  }

  case class Cat(name: String, age: Int, color: String)

  import PrintableInstances._

  implicit val catPrintable: Printable[Cat] =
    new Printable[Cat] {
      def format(value: Cat): String = {
        val name = Printable.format(value.name)
        val age = Printable.format(value.age)
        val color = Printable.format(value.color)
        s"$name is a $age year-old $color cat."
      }
    }

  object PrintableSyntax {
    implicit class PrintableOps[A](value: A) {
      def format(implicit p: Printable[A]): String =
        p.format(value)

      def print(implicit p: Printable[A]): Unit =
        println(p.format(value))
    }
  }

  import cats._
  import cats.syntax.all._

  val showInt: Show[Int] = Show.apply[Int]
  val showBoolean: Show[Boolean] = Show.apply[Boolean]

  val intAsString: String = showInt.show(123)

  val booleanAsString: String = showBoolean.show(true)
  val shownInt = 123.show

  import java.util.Date

  implicit val dateShow: Show[Date] =
    new Show[Date] {
      def show(date: Date): String =
        s"${date.getTime} ms since the epoch"
    }

  implicit val dateShow2: Show[Date] =
    Show.show(date => s"${date.getTime} ms since the epoch")

  implicit val catShow: Show[Cat] =
    Show.show(cat => s"${cat.name} is a ${cat.age} year-old ${cat.color} color cat.")

  // VAriance
  abstract class Animal {
    def name: String
  }

  case class Lion(name: String) extends Animal
  case class Puma(name: String) extends Animal

  def printAnimalNames(animals: List[Animal]): Unit =
    animals.foreach { animal =>
      println(animal.name)
    }

  val lions: List[Lion] = List(Lion("foo"), Lion("bar"))
  val pumas: List[Puma] = List(Puma("papa"), Puma("makai"))

  printAnimalNames(lions)
  printAnimalNames(pumas)
}
