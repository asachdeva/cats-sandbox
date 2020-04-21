package chapter1

object Chapter1 {

  trait Printable[A] {
    def format(value: A): String
  }

  object PrintableInstances {
    implicit val stringPrintable: Printable[String] =
      new Printable[String] {
        def format(value: String): String =
          value
      }

    implicit val intPrintable: Printable[Int] =
      new Printable[Int] {
        def format(value: Int): String =
          value.toString
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
  import cats.implicits._

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

}
