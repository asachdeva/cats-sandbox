package chapter6

object Chapter6 {

  import cats.Semigroupal
  import cats.implicits._

  Semigroupal[Option].product(Option(123), Option("abc"))
  (Option(123), Option("abc")).tupled

  case class Cat(name: String, age: Int, food: String)

  (
    Option("Akshay"),
    Option(47),
    Option("chicken")
  ).mapN(Cat.apply)

  // ex 6.3.1.1
  import cats.Monad

  def product[M[_]: Monad, A, B](x: M[A], y: M[B]): M[(A, B)] =
    for {
      a <- x
      b <- y
    } yield (a, b)

  val temp = 123.valid[List[String]]
  val temp2 = List("Badness").invalid[Int]

  // Ex 6.4.4
  object FormValidation {
    case class User(name: String, age: Int)

    import cats.data.Validated

    type FormData = Map[String, String]
    type FailFast[A] = Either[List[String], A]
    type FailSlow[A] = Validated[List[String], A]
    type NumFmtExn = NumberFormatException

    def getValue(name: String)(data: FormData): FailFast[String] =
      data.get(name).toRight(List(s"$name field not specified"))

    def parseInt(name: String)(data: String): FailFast[Int] =
      Either.catchOnly[NumFmtExn](data.toInt).leftMap(_ => List(s"$name must be an integer"))

    def nonBlank(name: String)(data: String): FailFast[String] =
      Right(data).ensure(List(s"$name cannot be blank"))(_.nonEmpty)

    def nonNegative(name: String)(data: Int): FailFast[Int] =
      Right(data).ensure(List(s"$name must be non-negative"))(_ >= 0)

    def readName(data: FormData): FailFast[String] = getValue("name")(data).flatMap(nonBlank("name"))

    def readAge(data: FormData): FailFast[Int] =
      getValue("age")(data).flatMap(nonBlank("age")).flatMap(parseInt("age")).flatMap(nonNegative("age"))

    def readUser(data: FormData): FailSlow[User] =
      (readName(data).toValidated, readAge(data).toValidated).mapN(User.apply)

  }
}
