package sandbox

import cats.implicits._
import cats._

object Main extends App {
  println("Hello " |+| "Cats!")
  println("Akshay" |+| " Loves" |+| " FP")

  println("Akshay" |+| " Loves" |+| " FP")

  // Chapter 1
  // Data Type
  sealed trait Json
  final case class JsObject(get: Map[String, Json]) extends Json
  final case class JsString(get: String) extends Json
  final case class JsNumber(get: Double) extends Json
  case object JsNull extends Json

  // Type class
  trait JsonWriter[A] {
    def write(value: A): Json
  }

  final case class Employee(name: String, age: Int, email: String)

  object JsonWriterInstances2 {
    implicit val employeeWriter: JsonWriter[Employee] =
      new JsonWriter[Employee] {
        def write(value: Employee): Json =
          JsObject(Map(
            "name"  -> JsString(value.name),
            "age"   -> JsNumber(value.age),
            "email" -> JsString(value.email)
          ))
      }
  }



  // Type class Instances
  case class Person(name: String, email: String)

  object JsonWriterInstances {
    implicit val stringWriter: JsonWriter[String] =
      new JsonWriter[String] {
        def write(value: String): Json =
          JsString(value)
      }

    implicit val personWriter: JsonWriter[Person] =
      new JsonWriter[Person] {
        def write(value: Person): Json =
          JsObject(Map(
            "name"  -> JsString(value.name),
            "email" -> JsString(value.email)
          ))
      }
  }

  // MethodA: Interface Object -- Singletonobject exposing functionality
  object Json {
    def toJson[A](value: A)(implicit w: JsonWriter[A]): Json =
      w.write(value)
  }
  // Import the implicit type class instances
  import JsonWriterInstances._
  import JsonWriterInstances2._
  println(Json.toJson(Person("Akshay Sachdeva","asachdeva@fp.com")))
  println(Json.toJson(Employee("Kristen Massey", 39, "kmassey22@yahoo.com")))

  // MethodB: Interface Syntax
  object JsonSyntax {
    implicit class JsonWriterOps[A](value: A) {
      def toJson(implicit w: JsonWriter[A]): Json =
        w.write(value)
    }
  }
  import JsonWriterInstances._
  import JsonSyntax._
  println(Person("Miles Sabin", "miles@sabin.com").toJson)

  println(Person("papa", "papa@papa.com").toJson(personWriter))

  // Ex 1.3 Printable Library
  // Part1
  trait Printable[A] {
    def format(value: A): String
  }

  // Part 2
  object PrintableInstances {
    implicit val stringPrintable: Printable[String] =
      new Printable[String] {
        def format(value: String) = value
      }

    implicit val intPrintable: Printable[Int] =
      new Printable[Int] {
        def format(value: Int) = value.toString
      }
  }

  // Part 3
  object Printable {
    def format[A](value: A)(implicit p: Printable[A]): String =
      p.format(value)

    def print[A](value: A)(implicit p: Printable[A]): Unit =
      println(format(value))
  }

  //Using the Library
  final case class Cats(name: String, age: Int, color: String)

  import PrintableInstances._

  implicit val catPrintable: Printable[Cats] =
    new Printable[Cats] {
      def format(input: Cats) = {
        val name = Printable.format(input.name)
        val age = Printable.format(input.age)
        val color = Printable.format(input.color)
        s"$name is a $age year-old $color cat."
      }
    }

    val cat = Cats("Akshay", 46, "brown")

    Printable.print(cat)

    object PrintableSyntax {
      implicit class PrintableOps[A](input: A) {
        def format(implicit p: Printable[A]): String =
          p.format(input)

        def print(implicit p: Printable[A]): Unit =
          println(p.format(input))
      }
    }

    import PrintableSyntax._

    Cats("Peter", 33, "black").print


    val showInt: Show[Int] = Show.apply[Int]
    val showString: Show[String] = Show.apply[String]

    val intAsString: String = showInt.show(123)

    println(intAsString)

    val shownInt = 123.show
    println(shownInt)

    println("Akshay".show)

    implicit val catShow: Show[Cats] =
      Show.show(cat => s"${cat.name} is a ${cat.age} year-old ${cat.color} cat.")

    println(Cats("Hello", 66, "blue").show)

    val eqInt = Eq[Int]

    println(eqInt.eqv(123,124))
    println(eqInt.eqv(123,123))

    println(123 === 123)

    (Some(1): Option[Int]) === (None: Option[Int])
    println(1.some === 1.some)
    println(1.some === none[Int])

    implicit val catEq: Eq[Cats] =
      Eq.instance[Cats] {(cat1, cat2) =>
        cat1.name === cat2.name &&
        cat1.age === cat2.age &&
        cat1.color === cat2.color
      }

    val cat1 = Cats("A", 12, "b")
    val cat2 = Cats("A", 12, "c")
    val cat3 = Cats("A", 12, "c")

    println(cat1.eqv(cat2))
    println(cat2.eqv(cat3))

    val optionCat1 = Option(cat1)
    val optionCat3 = Option.empty[Cats]

    println(optionCat1 === optionCat3)


}
