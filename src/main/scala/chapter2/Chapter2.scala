package chapter2

object Chapter2 {

  trait Semigroup[A] {
    def combine(a: A, b: A): A
  }

  trait Monoid[A] extends Semigroup[A] {
    def empty: A
  }

  object Monoid {
    def apply[A](implicit monoid: Monoid[A]) = monoid
  }

  // Ex 2.3

  object BooleanMonoid {
    implicit val andMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = true
      def combine(a: Boolean, b: Boolean) = a && b
    }

    implicit val orMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = false
      def combine(a: Boolean, b: Boolean) = a || b
    }

    implicit val booleanEitherMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = false
      def combine(a: Boolean, b: Boolean) = (a && !b) || (!a && b)
    }

    implicit val booleanXnorMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def empty: Boolean = true
      def combine(a: Boolean, b: Boolean) = (!a || b) && (a || !b)
    }
  }

}
