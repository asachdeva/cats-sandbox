package chapter3

object Chapter3 {

  object Tree {
    import cats.Functor
    // 3.5.4
    sealed trait Tree[+A]
    final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
    final case class Leaf[A](value: A) extends Tree[A]

    implicit val treeFunctor: Functor[Tree] =
      new Functor[Tree] {
        def map[A, B](tree: Tree[A])(f: A => B): Tree[B] =
          tree match {
            case Branch(left, right) =>
              Branch(map(left)(f), map(right)(f))
            case Leaf(value) =>
              Leaf(f(value))
          }
      }

    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
      Branch(left, right)

    def leaf[A](value: A): Tree[A] =
      Leaf(value)
  }

  // 3.6.1.1
  trait Printable[A] {
    self =>
    def format(value: A): String

    def contramap[B](func: B => A): Printable[B] =
      new Printable[B] {
        def format(value: B): String =
          self.format(func(value))
      }
  }

  final case class Box[A](value: A)

  object PrintableInstances {
    implicit val stringPrintable: Printable[String] =
      new Printable[String] {
        def format(value: String): String =
          s"$value"
      }

    implicit val booleanPrintable: Printable[Boolean] =
      new Printable[Boolean] {
        def format(value: Boolean): String =
          if (value) "true" else "false"
      }

    implicit val intPrintable: Printable[Int] =
      new Printable[Int] {
        def format(value: Int): String =
          value.toString
      }

    implicit def boxPrintable[A](implicit printable: Printable[A]): Printable[Box[A]] =
      printable.contramap(box => box.value)

    implicit val longPrintable = intPrintable.contramap((l: Long) => l.toInt)
  }

  object PrintableSyntax {
    implicit class PrintableOps[A](a: A) {
      def format(implicit printable: Printable[A]) = printable.format(a)
    }
  }

  // Ex 3.6.2.1
  trait Codec[A] {
    self =>
    def encode(value: A): String
    def decode(value: String): A
    def imap[B](dec: A => B, enc: B => A): Codec[B] =
      new Codec[B] {
        def encode(value: B): String = self.encode(enc(value))
        def decode(value: String): B = dec(self.decode(value))
      }
  }

  object Codec {
    implicit val codecString: Codec[String] =
      new Codec[String] {
        def encode(value: String) = value
        def decode(value: String) = value
      }

    implicit val codecDouble = codecString.imap[Double](_.toDouble, _.toString)
    implicit def codecBox[A](implicit c: Codec[A]) = c.imap[Box[A]](Box(_), _.value)
    def encode[A](value: A)(implicit c: Codec[A]): String = c.encode(value)
    def decode[A](value: String)(implicit c: Codec[A]): A = c.decode(value)
  }

}
